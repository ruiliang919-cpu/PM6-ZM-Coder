#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PM6-ZM-Coder 离线 Windows 自动化部署脚本

用法:
    python deploy.py start      启动所有服务
    python deploy.py stop       停止所有服务
    python deploy.py restart    重启所有服务
    python deploy.py status     查看服务状态
    python deploy.py init-db    初始化/重置数据库
    python deploy.py logs       查看部署日志
"""

import argparse
import configparser
import ctypes
import os
import re
import shutil
import socket
import subprocess
import sys
import time
from dataclasses import dataclass, field
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Tuple

# =============================================================================
# 常量定义
# =============================================================================

SCRIPT_DIR = Path(__file__).parent.resolve()
CONFIG_FILE = SCRIPT_DIR / "deploy-config.ini"
LOG_DIR = SCRIPT_DIR / "logs"
PID_DIR = SCRIPT_DIR / "pid"
TEMP_DIR = SCRIPT_DIR / "temp"

# 资源目录（基于脚本所在目录）
def _find_dir(name: str) -> Path:
    """查找目录，基于脚本所在目录。"""
    target = SCRIPT_DIR / name
    if not target.is_dir():
        # 模块加载阶段 logger 尚未初始化，使用 print 输出警告
        print(
            f"[WARN] 目录不存在: {target}\n"
            f"       如果从部署包 ZIP 解压运行，请确保完整解压所有文件到同一目录。"
        )
    return target

APP_DIR = _find_dir("app")
SQL_DIR = _find_dir("sql")

DEFAULT_PORTS = {
    "nginx": 80,
    "backend": 8081,
    "mysql": 3306,
    "redis": 6379,
    "mqtt": 1883,
}

EXIT_SUCCESS = 0
EXIT_ERROR = 1
EXIT_PORT_OCCUPIED = 2
EXIT_MYSQL_INIT_FAIL = 3
EXIT_DB_TIMEOUT = 4
EXIT_JAVA_FAIL = 5
EXIT_NGINX_CONFIG_ERROR = 6
EXIT_PYTHON_VERSION = 7
EXIT_USER_CANCEL = 10

# =============================================================================
# 颜色输出（Windows CMD 支持）
# =============================================================================

class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    CYAN = "\033[96m"
    RESET = "\033[0m"
    BOLD = "\033[1m"


def enable_ansi():
    """在 Windows CMD 中启用 ANSI 颜色（失败时静默忽略）"""
    if sys.platform == "win32":
        try:
            kernel32 = ctypes.windll.kernel32
            handle = kernel32.GetStdHandle(-11)
            mode = ctypes.c_uint32()
            if kernel32.GetConsoleMode(handle, ctypes.byref(mode)):
                kernel32.SetConsoleMode(handle, mode.value | 0x0004)
        except Exception:
            pass


def color_text(text: str, color: str) -> str:
    return f"{color}{text}{Colors.RESET}"


# =============================================================================
# 日志模块
# =============================================================================

class Logger:
    def __init__(self, log_dir: Path):
        self.log_dir = log_dir
        self.log_dir.mkdir(parents=True, exist_ok=True)
        self.log_file = self.log_dir / "deploy.log"
        self._file = None

    def _ensure_file(self):
        if self._file is None:
            self._file = open(self.log_file, "a", encoding="utf-8")

    def _write(self, level: str, message: str):
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        line = f"[{timestamp}] [{level}] {message}"
        self._ensure_file()
        self._file.write(line + "\n")
        self._file.flush()

    def info(self, message: str):
        self._write("INFO", message)
        print(color_text(f"[INFO] {message}", Colors.CYAN))

    def success(self, message: str):
        self._write("SUCCESS", message)
        print(color_text(f"[OK] {message}", Colors.GREEN))

    def warn(self, message: str):
        self._write("WARN", message)
        print(color_text(f"[WARN] {message}", Colors.YELLOW))

    def error(self, message: str):
        self._write("ERROR", message)
        print(color_text(f"[ERROR] {message}", Colors.RED))

    def close(self):
        if self._file:
            self._file.close()
            self._file = None


logger: Logger = None  # type: ignore


# =============================================================================
# 配置加载
# =============================================================================

class DeployConfig:
    def __init__(self, config_path: Path):
        self.config = configparser.ConfigParser()
        if config_path.exists():
            self.config.read(config_path, encoding="utf-8")
        else:
            self._create_default(config_path)

        self.ports = {
            "nginx": self.config.getint("ports", "nginx", fallback=80),
            "backend": self.config.getint("ports", "backend", fallback=8081),
            "mysql": self.config.getint("ports", "mysql", fallback=3306),
            "redis": self.config.getint("ports", "redis", fallback=6379),
            "mqtt": self.config.getint("ports", "mqtt", fallback=1883),
        }
        self.mysql_root_password = self.config.get("mysql", "root_password", fallback="root")
        self.mysql_app_username = self.config.get("mysql", "app_username", fallback="zm_root")
        self.mysql_app_password = self.config.get("mysql", "app_password", fallback="Huiyeda98315")
        self.mysql_database = self.config.get("mysql", "database", fallback="zm")

        self.paths = {
            "jdk": SCRIPT_DIR / self.config.get("paths", "jdk", fallback="packages/jdk"),
            "mysql": SCRIPT_DIR / self.config.get("paths", "mysql", fallback="packages/mysql"),
            "redis": SCRIPT_DIR / self.config.get("paths", "redis", fallback="packages/redis"),
            "nginx": SCRIPT_DIR / self.config.get("paths", "nginx", fallback="packages/nginx"),
            "mosquitto": SCRIPT_DIR / self.config.get("paths", "mosquitto", fallback="packages/mosquitto"),
        }

        self.jvm_heap_min = self.config.get("jvm", "heap_min", fallback="1024m")
        self.jvm_heap_max = self.config.get("jvm", "heap_max", fallback="2048m")
        self.jvm_metaspace_min = self.config.get("jvm", "metaspace_min", fallback="256m")
        self.jvm_metaspace_max = self.config.get("jvm", "metaspace_max", fallback="512m")

        self.auto_kill_ports = self.config.getboolean("auto", "kill_occupied_ports", fallback=False)
        self.auto_restart = self.config.getboolean("auto", "auto_restart", fallback=False)

    def _create_default(self, path: Path):
        self.config["ports"] = {k: str(v) for k, v in DEFAULT_PORTS.items()}
        self.config["mysql"] = {
            "root_password": "root",
            "app_username": "zm_root",
            "app_password": "Huiyeda98315",
            "database": "zm",
        }
        self.config["paths"] = {
            "jdk": "packages/jdk",
            "mysql": "packages/mysql",
            "redis": "packages/redis",
            "nginx": "packages/nginx",
            "mosquitto": "packages/mosquitto",
        }
        self.config["jvm"] = {
            "heap_min": "1024m",
            "heap_max": "2048m",
            "metaspace_min": "256m",
            "metaspace_max": "512m",
        }
        self.config["auto"] = {
            "kill_occupied_ports": "false",
            "auto_restart": "false",
        }
        with open(path, "w", encoding="utf-8") as f:
            self.config.write(f)


# =============================================================================
# 端口检测
# =============================================================================

def check_port(port: int) -> Tuple[bool, Optional[int]]:
    """检测端口是否被占用，返回 (是否占用, 占用进程PID)"""
    try:
        result = subprocess.run(
            "netstat -ano", capture_output=True, text=True, shell=True,
        )
        if result.returncode != 0:
            return False, None

        port_str = str(port)
        for line in result.stdout.split("\n"):
            parts = line.strip().split()
            if len(parts) < 5 or parts[0] != "TCP":
                continue
            local_addr = parts[1]
            addr_port = local_addr.rsplit(":", 1)[-1]
            if addr_port == port_str and parts[3] == "LISTENING":
                pid = int(parts[-1])
                return True, pid
        return False, None
    except Exception as e:
        logger.warn(f"检测端口 {port} 时出错: {e}")
        return False, None


def check_all_ports(config: DeployConfig) -> Dict[int, Tuple[bool, Optional[int]]]:
    """检测所有必需端口"""
    results = {}
    for name, port in config.ports.items():
        occupied, pid = check_port(port)
        results[port] = (occupied, pid)
        if occupied:
            logger.warn(f"端口 {port} ({name}) 已被占用 (PID={pid})")
        else:
            logger.info(f"端口 {port} ({name}) 可用")
    return results


def get_process_name(pid: int) -> str:
    """根据 PID 获取进程名"""
    try:
        result = subprocess.run(
            ["tasklist", "/FI", f"PID eq {pid}", "/FO", "CSV", "/NH"],
            capture_output=True,
            text=True,
            shell=True,
        )
        if result.returncode == 0 and result.stdout.strip():
            line = result.stdout.strip().split("\n")[0]
            # CSV 格式: "进程名","PID","会话名","会话#","内存使用"
            parts = line.split(",")
            if len(parts) >= 2 and parts[0].startswith('"'):
                return parts[0].strip('"')
    except Exception:
        pass
    return "未知进程"


def kill_process(pid: int) -> Tuple[bool, str]:
    """终止指定 PID 的进程，返回 (是否成功, 失败原因)"""
    try:
        result = subprocess.run(
            ["taskkill", "/F", "/PID", str(pid)],
            capture_output=True,
            text=True,
            shell=True,
        )
        if result.returncode == 0:
            return True, ""
        # PID 已不存在（进程已退出）
        err = result.stderr + result.stdout
        if "没有找到" in err or "not found" in err.lower():
            return True, ""
        # 权限不足
        if "拒绝访问" in err or "denied" in err.lower():
            return False, "需要管理员权限"
        return False, f"taskkill 失败: {err.strip()}"
    except Exception as e:
        return False, str(e)


def handle_port_conflicts(config: DeployConfig) -> bool:
    """处理端口冲突，返回是否继续部署"""
    results = check_all_ports(config)
    conflicts = {port: pid for port, (occupied, pid) in results.items() if occupied}

    if not conflicts:
        return True

    logger.warn(f"检测到 {len(conflicts)} 个端口冲突")
    for port, pid in conflicts.items():
        name = [k for k, v in config.ports.items() if v == port][0]
        proc_name = get_process_name(pid) if pid else "未知"
        print(f"  端口 {port} ({name}) 被 {proc_name} (PID={pid}) 占用")

    if config.auto_kill_ports:
        logger.info("自动终止占用进程...")
        if not _do_kill_conflicts(conflicts):
            return False
        return True

    print("\n是否终止这些进程并继续部署? (y/n): ", end="")
    try:
        choice = input().strip().lower()
    except (EOFError, KeyboardInterrupt):
        print()
        logger.info("非交互环境，自动终止冲突进程")
        return _do_kill_conflicts(conflicts)
    if choice != "y":
        logger.info("用户取消部署")
        return False

    return _do_kill_conflicts(conflicts)


def _do_kill_conflicts(conflicts: dict) -> bool:
    """执行终止冲突进程，杀完后验证端口状态"""
    need_admin = []
    for port, pid in conflicts.items():
        if not pid:
            continue
        ok, reason = kill_process(pid)
        if ok:
            logger.success(f"已终止 PID={pid} (端口 {port})")
        else:
            logger.warn(f"无法终止 PID={pid}: {reason}")
            if "管理员" in reason:
                need_admin.append(port)

    # 等待端口释放
    time.sleep(2)
    still_occupied = []
    for port in conflicts:
        occupied, _ = check_port(port)
        if occupied:
            still_occupied.append(port)
            # 再等一会处理 TIME_WAIT
            logger.warn(f"端口 {port} 仍在占用中，等待释放...")
            time.sleep(3)
            occupied, _ = check_port(port)
            if occupied:
                logger.error(f"端口 {port} 释放超时")

    if still_occupied:
        print()
        print(color_text("部分端口未能释放：", Colors.YELLOW))
        for port in still_occupied:
            name = [k for k, v in DEFAULT_PORTS.items() if v == port][0]
            print(f"  - 端口 {port} ({name})")
        if need_admin:
            print(color_text("\n提示：请以管理员身份运行此脚本（右键 → 以管理员身份运行）", Colors.YELLOW))
        print()
        print("是否忽略冲突继续部署? (y/n): ", end="")
        try:
            if input().strip().lower() != "y":
                return False
        except (EOFError, KeyboardInterrupt):
            print()
            logger.warn("非交互环境，跳过端口冲突继续部署")
            return True

    return True


# =============================================================================
# 服务管理基类
# =============================================================================

@dataclass
class ServiceStatus:
    name: str
    running: bool
    pid: Optional[int] = None
    port: Optional[int] = None
    message: str = ""


class Service:
    def __init__(self, name: str, port: int, config: DeployConfig):
        self.name = name
        self.port = port
        self.config = config
        self.pid_file = PID_DIR / f"{name}.pid"
        self.log_dir = LOG_DIR / name
        self.log_dir.mkdir(parents=True, exist_ok=True)

    def _read_pid(self) -> Optional[int]:
        if self.pid_file.exists():
            try:
                return int(self.pid_file.read_text().strip())
            except (ValueError, OSError):
                return None
        return None

    def _write_pid(self, pid: int):
        PID_DIR.mkdir(parents=True, exist_ok=True)
        self.pid_file.write_text(str(pid))

    def _remove_pid(self):
        if self.pid_file.exists():
            self.pid_file.unlink()

    def is_running(self) -> bool:
        pid = self._read_pid()
        if pid is None:
            return False
        try:
            result = subprocess.run(
                ["tasklist", "/FI", f"PID eq {pid}", "/NH"],
                capture_output=True,
                text=True,
                shell=True,
            )
            return str(pid) in result.stdout
        except Exception:
            return False

    def status(self) -> ServiceStatus:
        pid = self._read_pid()
        running = self.is_running()
        msg = f"PID={pid}" if running else "未运行"
        return ServiceStatus(self.name, running, pid, self.port, msg)

    def start(self) -> bool:
        raise NotImplementedError

    def stop(self) -> bool:
        pid = self._read_pid()
        if pid is None:
            logger.warn(f"{self.name} 未在运行")
            return True
        ok, _ = kill_process(pid)
        if ok:
            logger.success(f"{self.name} 已停止 (PID={pid})")
            self._remove_pid()
            return True
        else:
            logger.error(f"{self.name} 停止失败 (PID={pid})")
            return False


# =============================================================================
# MySQL 服务
# =============================================================================

class MySQLService(Service):
    def __init__(self, config: DeployConfig):
        super().__init__("MySQL", config.ports["mysql"], config)
        self.mysql_dir = config.paths["mysql"]
        self.data_dir = self.mysql_dir / "data"
        self.ini_file = SCRIPT_DIR / "config" / "my.ini"
        self.runtime_ini = TEMP_DIR / "my.ini"

    def _prepare_config(self):
        """生成运行时 my.ini（替换占位符为绝对路径）"""
        TEMP_DIR.mkdir(parents=True, exist_ok=True)
        content = self.ini_file.read_text(encoding="utf-8")
        content = content.replace("BASEDIR_PLACEHOLDER", str(self.mysql_dir).replace("\\", "/"))
        content = content.replace("DATADIR_PLACEHOLDER", str(self.data_dir).replace("\\", "/"))
        content = content.replace("LOGDIR_PLACEHOLDER", str(self.log_dir).replace("\\", "/"))
        self.runtime_ini.write_text(content, encoding="utf-8")

    def _initialize_data(self) -> bool:
        """初始化 MySQL data 目录"""
        if self.data_dir.exists() and any(self.data_dir.iterdir()):
            # 验证核心数据库目录是否完整
            required_dirs = ["mysql", "performance_schema", "sys"]
            missing = [d for d in required_dirs if not (self.data_dir / d).is_dir()]
            if not missing:
                logger.info("MySQL data 目录已存在且核心数据库完整，跳过初始化")
                return True
            else:
                logger.warn(f"MySQL data 目录不完整（缺少: {', '.join(missing)}），将重新初始化...")
                shutil.rmtree(self.data_dir, ignore_errors=True)

        logger.info("正在初始化 MySQL 数据目录...")
        mysqld_exe = self.mysql_dir / "bin" / "mysqld.exe"
        if not mysqld_exe.exists():
            logger.error(f"找不到 mysqld.exe: {mysqld_exe}")
            return False

        self._prepare_config()
        result = subprocess.run(
            [str(mysqld_exe), "--defaults-file=" + str(self.runtime_ini), "--initialize-insecure"],
            capture_output=True,
            text=True,
            cwd=str(self.mysql_dir),
        )
        if result.returncode != 0:
            logger.error(f"MySQL 初始化失败: {result.stderr}")
            return False
        logger.success("MySQL 数据目录初始化完成")
        return True

    def _show_mysql_error_log(self):
        """显示 MySQL 错误日志的最后几行，帮助诊断启动失败原因"""
        error_log = self.log_dir / "mysql_error.log"
        startup_log = self.log_dir / "mysql_startup.log"
        for log_path in [error_log, startup_log]:
            if log_path.exists():
                try:
                    content = log_path.read_text(encoding="utf-8", errors="replace")
                    lines = content.strip().splitlines()
                    tail = lines[-20:] if len(lines) > 20 else lines
                    if tail:
                        logger.error(f"MySQL 日志 ({log_path.name}) 最后几行:\n" + "\n".join(tail))
                except Exception as e:
                    logger.error(f"读取 MySQL 日志失败: {e}")

    def start(self) -> bool:
        if self.is_running():
            logger.info("MySQL 已在运行")
            return True

        if not self._initialize_data():
            return False

        self._prepare_config()
        mysqld_exe = self.mysql_dir / "bin" / "mysqld.exe"

        logger.info("正在启动 MySQL...")
        # 将 stderr 输出到日志文件，便于诊断启动失败
        mysql_startup_log = self.log_dir / "mysql_startup.log"
        self.log_dir.mkdir(parents=True, exist_ok=True)
        startup_log_file = open(mysql_startup_log, "w", encoding="utf-8")
        try:
            process = subprocess.Popen(
                [str(mysqld_exe), "--defaults-file=" + str(self.runtime_ini)],
                stdout=subprocess.DEVNULL,
                stderr=startup_log_file,
                cwd=str(self.mysql_dir),
                creationflags=subprocess.CREATE_NO_WINDOW if sys.platform == "win32" else 0,
            )
        except Exception as e:
            startup_log_file.close()
            logger.error(f"MySQL 进程启动失败: {e}")
            return False

        self._write_pid(process.pid)

        # 等待 MySQL 就绪
        for i in range(60):
            time.sleep(1)
            if check_port(self.port)[0]:
                startup_log_file.close()
                logger.success(f"MySQL 启动成功 (PID={process.pid})")
                return True
            # 检查进程是否已提前退出
            if process.poll() is not None:
                startup_log_file.close()
                logger.error(f"MySQL 进程已退出，退出码: {process.returncode}")
                self._show_mysql_error_log()
                return False

        startup_log_file.close()
        logger.error("MySQL 启动超时（60秒）")
        self._show_mysql_error_log()
        return False


# =============================================================================
# Redis 服务
# =============================================================================

class RedisService(Service):
    def __init__(self, config: DeployConfig):
        super().__init__("Redis", config.ports["redis"], config)
        self.redis_dir = config.paths["redis"]
        self.data_dir = self.redis_dir / "data"
        self.conf_file = SCRIPT_DIR / "config" / "redis.conf"
        self.runtime_conf = TEMP_DIR / "redis.conf"
        self.data_dir.mkdir(parents=True, exist_ok=True)

    def _prepare_config(self):
        TEMP_DIR.mkdir(parents=True, exist_ok=True)
        content = self.conf_file.read_text(encoding="utf-8")
        content = content.replace("DATADIR_PLACEHOLDER", str(self.data_dir).replace("\\", "/"))
        content = content.replace("LOGFILE_PLACEHOLDER", str(self.log_dir / "redis.log").replace("\\", "/"))
        self.runtime_conf.write_text(content, encoding="utf-8")

    def start(self) -> bool:
        if self.is_running():
            logger.info("Redis 已在运行")
            return True

        self._prepare_config()
        redis_exe = self.redis_dir / "redis-server.exe"
        if not redis_exe.exists():
            logger.error(f"找不到 redis-server.exe: {redis_exe}")
            return False

        logger.info("正在启动 Redis...")
        process = subprocess.Popen(
            [str(redis_exe), str(self.runtime_conf)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            cwd=str(self.redis_dir),
            creationflags=subprocess.CREATE_NEW_CONSOLE if sys.platform == "win32" else 0,
        )
        self._write_pid(process.pid)

        for i in range(30):
            time.sleep(0.5)
            if check_port(self.port)[0]:
                logger.success(f"Redis 启动成功 (PID={process.pid})")
                return True

        logger.error("Redis 启动超时")
        return False


# =============================================================================
# Mosquitto MQTT 服务
# =============================================================================

class MosquittoService(Service):
    def __init__(self, config: DeployConfig):
        super().__init__("Mosquitto", config.ports["mqtt"], config)
        self.mosq_dir = config.paths["mosquitto"]
        self.conf_file = SCRIPT_DIR / "config" / "mosquitto.conf"
        self.runtime_conf = TEMP_DIR / "mosquitto.conf"

    def _prepare_config(self):
        TEMP_DIR.mkdir(parents=True, exist_ok=True)
        content = self.conf_file.read_text(encoding="utf-8")
        content = content.replace("LOGDIR_PLACEHOLDER", str(self.log_dir).replace("\\", "/"))
        content = content.replace("PIDFILE_PLACEHOLDER", str(PID_DIR).replace("\\", "/"))

        # 创建 Mosquitto 密码文件
        # 注意: mosquitto_passwd.exe 不支持绝对路径，需要用相对路径 + cwd
        mqtt_user = self.config.config.get("mqtt", "username", fallback=os.environ.get("MQTT_USERNAME", "admin"))
        mqtt_pass = self.config.config.get("mqtt", "password", fallback=os.environ.get("MQTT_PASSWORD", "Hyd@qwe"))
        pwd_file = TEMP_DIR / "mosquitto.pwd"
        pwd_created = False
        if not pwd_file.exists():
            mosq_passwd_exe = self.mosq_dir / "mosquitto_passwd.exe"
            if mosq_passwd_exe.exists():
                result = subprocess.run(
                    [str(mosq_passwd_exe), "-c", "-b", "mosquitto.pwd", mqtt_user, mqtt_pass],
                    capture_output=True,
                    timeout=10,
                    cwd=str(TEMP_DIR),
                )
                pwd_created = (result.returncode == 0)

        if pwd_created or pwd_file.exists():
            content = content.replace("PWDFILE_PLACEHOLDER", str(pwd_file).replace("\\", "/"))
        else:
            # 密码文件生成失败，终止部署而非回退到匿名模式
            logger.error("Mosquitto 密码文件生成失败，无法保证安全认证，终止部署")
            raise RuntimeError("Mosquitto 密码文件生成失败: 无法创建认证文件，请检查 mosquitto_passwd.exe 是否存在且可执行")

        self.runtime_conf.write_text(content, encoding="utf-8")

    def start(self) -> bool:
        if self.is_running():
            logger.info("Mosquitto 已在运行")
            return True

        self._prepare_config()
        mosq_exe = self.mosq_dir / "mosquitto.exe"
        if not mosq_exe.exists():
            logger.error(f"找不到 mosquitto.exe: {mosq_exe}")
            return False

        logger.info("正在启动 Mosquitto MQTT Broker...")
        process = subprocess.Popen(
            [str(mosq_exe), "-c", str(self.runtime_conf)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            cwd=str(self.mosq_dir),
            creationflags=subprocess.CREATE_NEW_CONSOLE if sys.platform == "win32" else 0,
        )
        self._write_pid(process.pid)

        for i in range(30):
            time.sleep(0.5)
            if check_port(self.port)[0]:
                logger.success(f"Mosquitto 启动成功 (PID={process.pid})")
                return True

        logger.error("Mosquitto 启动超时")
        return False


# =============================================================================
# Java 后端服务
# =============================================================================

class JavaService(Service):
    def __init__(self, config: DeployConfig):
        super().__init__("JavaBackend", config.ports["backend"], config)
        self.jar_file = APP_DIR / "ruoyi-admin.jar"
        self.app_config = SCRIPT_DIR / "config" / "application.yml"
        self.runtime_config = TEMP_DIR / "application.yml"
        self.jdk_dir = config.paths["jdk"]

    def _prepare_config(self):
        TEMP_DIR.mkdir(parents=True, exist_ok=True)
        content = self.app_config.read_text(encoding="utf-8")
        content = content.replace("LOGDIR_PLACEHOLDER", str(self.log_dir).replace("\\", "/"))
        self.runtime_config.write_text(content, encoding="utf-8")

    def _show_java_log(self):
        """显示 Java 日志的最后几行，帮助诊断启动失败原因"""
        log_file = self.log_dir / "java_stdout.log"
        if log_file.exists():
            try:
                content = log_file.read_text(encoding="utf-8", errors="replace")
                lines = content.strip().splitlines()
                tail = lines[-30:] if len(lines) > 30 else lines
                if tail:
                    logger.error(f"Java 日志 (java_stdout.log) 最后几行:\n" + "\n".join(tail))
            except Exception as e:
                logger.error(f"读取 Java 日志失败: {e}")

    def start(self) -> bool:
        if self.is_running():
            logger.info("Java 后端已在运行")
            return True

        if not self.jar_file.exists():
            logger.error(f"找不到 JAR 文件: {self.jar_file}")
            return False

        java_exe = self.jdk_dir / "bin" / "java.exe"
        use_embedded_jdk = java_exe.exists()
        if not use_embedded_jdk:
            # 尝试系统 Java
            system_java = shutil.which("java")
            if system_java:
                java_exe = Path(system_java)
            else:
                logger.error(
                    f"找不到 Java 运行环境: "
                    f"嵌入式 JDK ({self.jdk_dir / 'bin' / 'java.exe'}) 不存在，"
                    f"系统 PATH 中也未找到 java 命令"
                )
                return False

        self._prepare_config()

        jvm_opts = (
            f'-Dname=ruoyi-admin.jar '
            f'-Duser.timezone=Asia/Shanghai '
            f'-Xms{self.config.jvm_heap_min} '
            f'-Xmx{self.config.jvm_heap_max} '
            f'-XX:MetaspaceSize={self.config.jvm_metaspace_min} '
            f'-XX:MaxMetaspaceSize={self.config.jvm_metaspace_max} '
            f'-XX:+HeapDumpOnOutOfMemoryError '
            f'-XX:+UseG1GC -XX:MaxGCPauseMillis=200'
        )

        logger.info("正在启动 Java 后端...")
        env = os.environ.copy()
        if use_embedded_jdk:
            env["JAVA_HOME"] = str(self.jdk_dir)
            env["PATH"] = str(self.jdk_dir / "bin") + os.pathsep + env.get("PATH", "")

        log_file = self.log_dir / "java_stdout.log"
        self.log_dir.mkdir(parents=True, exist_ok=True)

        # 保持日志文件在整个等待期间打开（与 MySQL 一致），
        # 避免 with 块提前关闭文件句柄
        log_out = open(log_file, "a", encoding="utf-8")
        try:
            process = subprocess.Popen(
                [str(java_exe)] + jvm_opts.split() + [
                    "-jar", str(self.jar_file),
                    f"--spring.config.additional-location=file:{str(self.runtime_config).replace(chr(92), '/')}",
                    "--spring.profiles.active=prod"
                ],
                stdout=log_out,
                stderr=subprocess.STDOUT,
                env=env,
                cwd=str(SCRIPT_DIR),
                # 使用 CREATE_NO_WINDOW 而非 CREATE_NEW_CONSOLE：
                # CREATE_NEW_CONSOLE 会弹出控制台窗口，用户点击窗口会触发
                # Windows QuickEdit 模式导致 Java 进程被暂停（挂起），
                # 表现为部署脚本"卡住"。CREATE_NO_WINDOW 无可见窗口，无此风险。
                creationflags=subprocess.CREATE_NO_WINDOW if sys.platform == "win32" else 0,
            )
        except Exception as e:
            log_out.close()
            logger.error(f"Java 进程启动失败: {e}")
            return False

        self._write_pid(process.pid)

        # 等待 Java 就绪（与 MySQL 一致的检测模式）
        for i in range(240):
            time.sleep(1)
            if check_port(self.port)[0]:
                log_out.close()
                logger.success(f"Java 后端启动成功 (PID={process.pid})")
                return True
            # 检查进程是否已提前退出（关键！缺少此检查会导致进程崩溃后傻等 240 秒）
            if process.poll() is not None:
                log_out.close()
                logger.error(f"Java 进程已退出，退出码: {process.returncode}")
                self._show_java_log()
                return False

        log_out.close()
        logger.error("Java 后端启动超时（240秒）")
        self._show_java_log()
        return False


# =============================================================================
# Nginx 服务
# =============================================================================

class NginxService(Service):
    def __init__(self, config: DeployConfig):
        super().__init__("Nginx", config.ports["nginx"], config)
        self.nginx_dir = config.paths["nginx"]
        self.conf_file = SCRIPT_DIR / "config" / "nginx.conf"
        self.runtime_conf = TEMP_DIR / "nginx.conf"
        self.html_dir = APP_DIR / "dist"

    def _prepare_config(self):
        TEMP_DIR.mkdir(parents=True, exist_ok=True)
        # Nginx 需要 temp 子目录，否则启动报错 CreateDirectory() failed
        for sub in ("client_body_temp", "proxy_temp", "fastcgi_temp", "uwsgi_temp", "scgi_temp"):
            (self.nginx_dir / "temp" / sub).mkdir(parents=True, exist_ok=True)
        content = self.conf_file.read_text(encoding="utf-8")
        content = content.replace("LOGDIR_PLACEHOLDER", str(self.log_dir).replace("\\", "/"))
        content = content.replace("PIDFILE_PLACEHOLDER", str(PID_DIR).replace("\\", "/"))
        content = content.replace("HTMLDIR_PLACEHOLDER", str(self.html_dir).replace("\\", "/"))
        content = content.replace("NGINXDIR_PLACEHOLDER", str(self.nginx_dir).replace("\\", "/"))
        content = content.replace("LISTEN_PORT_PLACEHOLDER", str(self.port))
        self.runtime_conf.write_text(content, encoding="utf-8")

    def _test_config(self) -> bool:
        nginx_exe = self.nginx_dir / "nginx.exe"
        result = subprocess.run(
            [str(nginx_exe), "-t", "-c", str(self.runtime_conf)],
            capture_output=True,
            text=True,
            cwd=str(self.nginx_dir),
        )
        if result.returncode != 0:
            logger.error(f"Nginx 配置测试失败:\n{result.stderr}")
            return False
        return True

    def start(self) -> bool:
        if self.is_running():
            logger.info("Nginx 已在运行")
            return True

        nginx_exe = self.nginx_dir / "nginx.exe"
        if not nginx_exe.exists():
            logger.error(f"找不到 nginx.exe: {nginx_exe}")
            return False

        if not self.html_dir.exists():
            logger.error(f"找不到前端 dist 目录: {self.html_dir}")
            return False

        self._prepare_config()

        if not self._test_config():
            return False

        logger.info("正在启动 Nginx...")
        process = subprocess.Popen(
            [str(nginx_exe), "-c", str(self.runtime_conf)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            cwd=str(self.nginx_dir),
            creationflags=subprocess.CREATE_NEW_CONSOLE if sys.platform == "win32" else 0,
        )
        self._write_pid(process.pid)

        for i in range(30):
            time.sleep(0.5)
            if check_port(self.port)[0]:
                logger.success(f"Nginx 启动成功 (PID={process.pid})")
                return True

        logger.error("Nginx 启动超时")
        return False

    def stop(self) -> bool:
        nginx_exe = self.nginx_dir / "nginx.exe"
        if nginx_exe.exists():
            self._prepare_config()
            subprocess.run(
                [str(nginx_exe), "-s", "stop", "-c", str(self.runtime_conf)],
                capture_output=True,
                cwd=str(self.nginx_dir),
            )
        return super().stop()


# =============================================================================
# 数据库初始化
# =============================================================================

def init_database(config: DeployConfig) -> bool:
    """初始化数据库（导入 zm.sql）"""
    mysql_dir = config.paths["mysql"]
    mysql_exe = mysql_dir / "bin" / "mysql.exe"
    sql_file = SQL_DIR / "zm.sql"

    if not mysql_exe.exists():
        logger.error(f"找不到 mysql.exe: {mysql_exe}")
        return False
    if not sql_file.exists():
        logger.error(f"找不到 SQL 文件: {sql_file}")
        return False

    # 等待 MySQL 就绪
    logger.info("等待 MySQL 就绪...")
    for i in range(60):
        if check_port(config.ports["mysql"])[0]:
            break
        time.sleep(1)
    else:
        logger.error("MySQL 未启动，无法初始化数据库")
        return False

    time.sleep(2)  # 额外等待服务完全就绪

    # MySQL 连接参数（使用配置的 root 密码）
    mysql_auth = [
        str(mysql_exe), "-h", "127.0.0.1", "-P", str(config.ports["mysql"]),
        "-u", "root", f"-p{config.mysql_root_password}",
    ]

    # 检查数据库是否已存在
    try:
        result = subprocess.run(
            mysql_auth + ["-e", f"SHOW DATABASES LIKE '{config.mysql_database}';"],
            capture_output=True,
            text=True,
            cwd=str(mysql_dir),
        )
        if config.mysql_database in result.stdout:
            logger.info(f"数据库 '{config.mysql_database}' 已存在，跳过初始化")
            return True
    except Exception as e:
        logger.warn(f"检查数据库存在性时出错: {e}")

    logger.info(f"正在导入数据库 {config.mysql_database}...")
    # 先创建数据库（zm.sql 不含 CREATE DATABASE 语句）
    result = subprocess.run(
        mysql_auth + ["-e", f"CREATE DATABASE IF NOT EXISTS `{config.mysql_database}` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"],
        capture_output=True,
        text=True,
        cwd=str(mysql_dir),
    )
    if result.returncode != 0:
        logger.error(f"创建数据库失败: {result.stderr}")
        return False
    # 导入 SQL 文件
    result = subprocess.run(
        mysql_auth + [f"-D{config.mysql_database}", "-e", f"source {str(sql_file).replace(chr(92), '/')}"],
        capture_output=True,
        text=True,
        cwd=str(mysql_dir),
    )
    if result.returncode != 0:
        logger.error(f"数据库导入失败: {result.stderr}")
        return False

    # 创建应用用户（同时支持 localhost 和 127.0.0.1 连接）
    for host in ("localhost", "127.0.0.1"):
        user_sql = (
            f"CREATE USER IF NOT EXISTS '{config.mysql_app_username}'@'{host}' "
            f"IDENTIFIED WITH mysql_native_password BY '{config.mysql_app_password}'; "
            f"GRANT ALL PRIVILEGES ON {config.mysql_database}.* TO '{config.mysql_app_username}'@'{host}'; "
            f"FLUSH PRIVILEGES;"
        )
        result = subprocess.run(
            mysql_auth + ["-e", user_sql],
            capture_output=True,
            text=True,
            cwd=str(mysql_dir),
        )
        if result.returncode != 0:
            logger.warn(f"创建应用用户 {config.mysql_app_username}@{host} 时出错（可能已存在）: {result.stderr}")

    logger.success("数据库初始化完成")
    return True


# =============================================================================
# 健康检查
# =============================================================================

def check_http(url: str, timeout: int = 5) -> Tuple[bool, str]:
    try:
        import urllib.request
        req = urllib.request.Request(url, method="HEAD")
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            return True, f"HTTP {resp.status}"
    except Exception as e:
        return False, str(e)


def health_check(config: DeployConfig) -> Dict[str, Tuple[bool, str]]:
    """执行完整健康检查"""
    results = {}

    # MySQL
    ok, _ = check_port(config.ports["mysql"])
    results["MySQL"] = (ok, "端口正常" if ok else "端口未监听")

    # Redis
    ok, _ = check_port(config.ports["redis"])
    results["Redis"] = (ok, "端口正常" if ok else "端口未监听")

    # Mosquitto MQTT
    ok, _ = check_port(config.ports["mqtt"])
    results["Mosquitto"] = (ok, "端口正常" if ok else "端口未监听")

    # Java Backend
    ok, msg = check_http(f"http://127.0.0.1:{config.ports['backend']}")
    results["JavaBackend"] = (ok, msg)

    # Nginx
    ok, msg = check_http(f"http://127.0.0.1:{config.ports['nginx']}")
    results["Nginx"] = (ok, msg)

    return results


def print_health_results(results: Dict[str, Tuple[bool, str]]):
    print("\n" + "=" * 50)
    print(color_text("  服务健康检查", Colors.BOLD))
    print("=" * 50)
    for name, (ok, msg) in results.items():
        status = color_text("[正常]", Colors.GREEN) if ok else color_text("[异常]", Colors.RED)
        print(f"  {status} {name:<15} {msg}")
    print("=" * 50)


# =============================================================================
# 主流程
# =============================================================================

def cmd_start(config: DeployConfig) -> int:
    """启动所有服务"""
    logger.info("=" * 50)
    logger.info("开始部署 PM6-ZM-Coder")
    logger.info("=" * 50)

    # 1. 检测端口
    if not handle_port_conflicts(config):
        return EXIT_PORT_OCCUPIED

    # 2. 创建必要目录
    LOG_DIR.mkdir(parents=True, exist_ok=True)
    PID_DIR.mkdir(parents=True, exist_ok=True)
    TEMP_DIR.mkdir(parents=True, exist_ok=True)

    services = [
        MySQLService(config),
        RedisService(config),
        MosquittoService(config),
        JavaService(config),
        NginxService(config),
    ]

    # 3. 启动服务
    for svc in services:
        if not svc.start():
            if isinstance(svc, MySQLService):
                return EXIT_MYSQL_INIT_FAIL
            elif isinstance(svc, JavaService):
                return EXIT_JAVA_FAIL
            elif isinstance(svc, NginxService):
                return EXIT_NGINX_CONFIG_ERROR
            return EXIT_ERROR

    # 4. 初始化数据库
    if not init_database(config):
        return EXIT_DB_TIMEOUT

    # 5. 健康检查
    time.sleep(2)
    results = health_check(config)
    print_health_results(results)

    all_ok = all(ok for ok, _ in results.values())
    if all_ok:
        logger.success("所有服务启动成功！")
        print(color_text(f"\n  访问地址: http://127.0.0.1:{config.ports['nginx']}", Colors.GREEN))
        print(color_text(f"  API 地址: http://127.0.0.1:{config.ports['backend']}", Colors.CYAN))
        return EXIT_SUCCESS
    else:
        logger.warn("部分服务未通过健康检查，请查看日志")
        return EXIT_ERROR


def cmd_stop(config: DeployConfig) -> int:
    """停止所有服务"""
    services = [
        NginxService(config),
        JavaService(config),
        MosquittoService(config),
        RedisService(config),
        MySQLService(config),
    ]

    for svc in services:
        svc.stop()

    logger.success("所有服务已停止")
    return EXIT_SUCCESS


def cmd_restart(config: DeployConfig) -> int:
    """重启所有服务"""
    cmd_stop(config)
    time.sleep(2)
    return cmd_start(config)


def cmd_status(config: DeployConfig) -> int:
    """查看服务状态"""
    services = [
        MySQLService(config),
        RedisService(config),
        MosquittoService(config),
        JavaService(config),
        NginxService(config),
    ]

    print("\n" + "=" * 50)
    print(color_text("  服务状态", Colors.BOLD))
    print("=" * 50)
    for svc in services:
        st = svc.status()
        status = color_text("[运行中]", Colors.GREEN) if st.running else color_text("[已停止]", Colors.RED)
        print(f"  {status} {st.name:<15} {st.message}")
    print("=" * 50)
    return EXIT_SUCCESS


def cmd_init_db(config: DeployConfig, force: bool = False) -> int:
    """初始化/重置数据库"""
    if not force:
        print(color_text("警告: 这将删除现有数据库并重新初始化！", Colors.RED))
        print("是否继续? (yes/no): ", end="")
        if input().strip().lower() != "yes":
            logger.info("操作已取消")
            return EXIT_USER_CANCEL

    mysql_dir = config.paths["mysql"]
    mysql_exe = mysql_dir / "bin" / "mysql.exe"

    # 删除并重新初始化 data 目录
    mysql_svc = MySQLService(config)
    mysql_svc.stop()
    time.sleep(2)

    data_dir = mysql_dir / "data"
    if data_dir.exists():
        shutil.rmtree(data_dir)
        logger.info("已删除旧的数据目录")

    if not mysql_svc.start():
        return EXIT_MYSQL_INIT_FAIL

    if not init_database(config):
        return EXIT_DB_TIMEOUT

    logger.success("数据库已重置")
    return EXIT_SUCCESS


def cmd_logs(config: DeployConfig) -> int:
    """查看日志"""
    if not LOG_DIR.exists():
        print("暂无日志文件")
        return EXIT_SUCCESS

    print("\n可用日志文件:")
    log_files = list(LOG_DIR.rglob("*.log"))
    for i, f in enumerate(log_files, 1):
        size = f.stat().st_size
        print(f"  [{i}] {f.relative_to(LOG_DIR)} ({size} bytes)")

    # 非交互环境直接返回
    if not sys.stdin.isatty():
        print("\n提示: 在非交互环境中运行，仅列出日志文件。使用交互式终端可查看内容。")
        return EXIT_SUCCESS

    print("\n输入编号查看日志 (0 退出): ", end="")
    try:
        choice = int(input().strip())
        if choice == 0 or choice > len(log_files):
            return EXIT_SUCCESS
        selected = log_files[choice - 1]
        print(f"\n--- {selected.name} ---\n")
        with open(selected, "r", encoding="utf-8", errors="ignore") as f:
            lines = f.readlines()
            print("".join(lines[-200:]))  # 显示最后 200 行
    except (ValueError, IndexError):
        pass
    except EOFError:
        print("\n非交互环境，跳过日志查看")

    return EXIT_SUCCESS


# =============================================================================
# 交互菜单
# =============================================================================

EXIT_MESSAGES = {
    EXIT_SUCCESS: ("完成", "命令执行成功"),
    EXIT_PORT_OCCUPIED: ("取消", "端口冲突未解决"),
    EXIT_MYSQL_INIT_FAIL: ("失败", "MySQL 初始化失败"),
    EXIT_DB_TIMEOUT: ("失败", "数据库连接超时"),
    EXIT_JAVA_FAIL: ("失败", "Java 后端启动失败"),
    EXIT_NGINX_CONFIG_ERROR: ("失败", "Nginx 配置错误"),
    EXIT_PYTHON_VERSION: ("失败", "Python 版本不兼容"),
    EXIT_USER_CANCEL: ("取消", "用户取消操作"),
}

MENU_ITEMS = [
    ("start", "启动所有服务"),
    ("stop", "停止所有服务"),
    ("restart", "重启所有服务"),
    ("status", "查看服务状态"),
    ("init-db", "初始化数据库"),
    ("logs", "查看日志"),
]


def show_menu(config: DeployConfig) -> int:
    """交互式菜单，返回退出码"""
    while True:
        print()
        print("  ============================================================")
        print("    PM6-ZM-Coder 离线部署工具")
        print("  ============================================================")
        print()
        for i, (cmd, desc) in enumerate(MENU_ITEMS, 1):
            print(f"    [{i}] {desc:<12} ({cmd})")
        print(f"    [7] 退 出")
        print()
        print("  ============================================================")

        try:
            choice = input("\n请输入选项编号: ").strip()
        except (EOFError, KeyboardInterrupt):
            print()
            return EXIT_USER_CANCEL

        if choice == "7":
            return EXIT_SUCCESS

        idx_map = {"1": 0, "2": 1, "3": 2, "4": 3, "5": 4, "6": 5}
        if choice not in idx_map:
            print(color_text("[错误] 无效选项", Colors.RED))
            input("按回车键继续...")
            print("\033[2J\033[H", end="")  # 清屏
            continue

        cmd_name = MENU_ITEMS[idx_map[choice]][0]
        print(f"\n  [信息] 执行命令: {cmd_name}\n")

        commands = {
            "start": cmd_start,
            "stop": cmd_stop,
            "restart": cmd_restart,
            "status": cmd_status,
            "init-db": cmd_init_db,
            "logs": cmd_logs,
        }

        try:
            exit_code = commands[cmd_name](config)
        except KeyboardInterrupt:
            logger.info("用户中断操作")
            exit_code = EXIT_USER_CANCEL
        except Exception as e:
            logger.error(f"执行命令时发生异常: {e}")
            exit_code = EXIT_ERROR

        print()
        if exit_code in EXIT_MESSAGES:
            tag, msg = EXIT_MESSAGES[exit_code]
        else:
            tag, msg = "失败", f"发生错误 (退出码: {exit_code})"

        print(f"  [{tag}] {msg}")
        print()
        input("按回车键返回菜单...")


# =============================================================================
# 入口
# =============================================================================

def main():
    global logger
    enable_ansi()

    if sys.version_info < (3, 7):
        print(color_text("错误: 需要 Python 3.7 或更高版本", Colors.RED))
        sys.exit(EXIT_PYTHON_VERSION)

    # 延迟初始化 logger，避免模块导入时因目录权限问题闪退
    try:
        logger = Logger(LOG_DIR)
    except Exception as e:
        print(color_text(f"[ERROR] 初始化日志失败: {e}", Colors.RED))
        sys.exit(EXIT_ERROR)

    parser = argparse.ArgumentParser(
        description="PM6-ZM-Coder 离线部署工具",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  deploy.bat                  进入交互菜单
  deploy.bat start            启动所有服务
  deploy.bat stop             停止所有服务
  deploy.bat restart          重启所有服务
  deploy.bat status           查看服务状态
  deploy.bat init-db          初始化/重置数据库
  deploy.bat logs             查看日志
        """
    )
    parser.add_argument(
        "command",
        nargs="?",
        choices=["start", "stop", "restart", "status", "init-db", "logs"],
        help="要执行的操作（不指定则进入交互菜单）"
    )
    parser.add_argument(
        "--force",
        action="store_true",
        help="跳过确认提示（用于 init-db 等命令）"
    )
    args = parser.parse_args()

    config = DeployConfig(CONFIG_FILE)

    # 无参数 → 交互菜单
    if args.command is None:
        try:
            exit_code = show_menu(config)
        finally:
            if logger:
                logger.close()
        sys.exit(exit_code)

    commands = {
        "start": cmd_start,
        "stop": cmd_stop,
        "restart": cmd_restart,
        "status": cmd_status,
        "init-db": cmd_init_db,
        "logs": cmd_logs,
    }

    try:
        if args.command == "init-db":
            exit_code = commands[args.command](config, force=args.force)
        else:
            exit_code = commands[args.command](config)
        print()
        if exit_code in EXIT_MESSAGES:
            tag, msg = EXIT_MESSAGES[exit_code]
            print(f"  [{tag}] {msg}")
        elif exit_code != 0:
            print(f"  [失败] 发生错误 (退出码: {exit_code})")
    except KeyboardInterrupt:
        logger.info("用户中断操作")
        exit_code = EXIT_USER_CANCEL
    except Exception as e:
        logger.error(f"执行命令时发生异常: {e}")
        exit_code = EXIT_ERROR
    finally:
        if logger:
            logger.close()

    sys.exit(exit_code)


if __name__ == "__main__":
    main()
