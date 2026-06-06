#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PM6-ZM-Coder 离线部署包打包脚本

核心原则：目标机器完全不联网，所有依赖必须预先打包。
任何一个组件获取失败，脚本立即退出，绝不生成不完整的部署包。

用法:
    python build-deploy-package.py [--version 1.2.10] [--skip-build] [--skip-download] [--with-python]
"""

import argparse
import hashlib
import os
import shutil
import subprocess
import sys
import tempfile
import time
import urllib.request
import zipfile
from pathlib import Path
from typing import List, Optional

SCRIPT_DIR = Path(__file__).parent.resolve()
PROJECT_ROOT = SCRIPT_DIR.parent.parent
DEPLOY_DIR = SCRIPT_DIR
OUTPUT_DIR = DEPLOY_DIR / "output"
PACKAGES_DIR = DEPLOY_DIR / "packages"

COMPONENTS = {
    "jdk": {
        "name": "OpenJDK 8 (Eclipse Temurin)",
        "filename": "OpenJDK8U-jdk_x64_windows_hotspot_8u392b08.zip",
        "urls": [
            "https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u392-b08/OpenJDK8U-jdk_x64_windows_hotspot_8u392b08.zip",
        ],
        "extract_dir": "jdk8u392-b08",
        "rename_to": "jdk",
        "checksum": None,
    },
    "mysql": {
        "name": "MySQL Community Server 8.0.35",
        "filename": "mysql-8.0.35-winx64.zip",
        "urls": [
            "https://cdn.mysql.com/archives/mysql-8.0/mysql-8.0.35-winx64.zip",
            "https://dev.mysql.com/get/Downloads/MySQL-8.0/mysql-8.0.35-winx64.zip",
        ],
        "extract_dir": "mysql-8.0.35-winx64",
        "rename_to": "mysql",
        "checksum": None,
    },
    "redis": {
        "name": "Redis 5.0.14.1 (Windows by tporadowski)",
        "filename": "Redis-x64-5.0.14.1.zip",
        "urls": [
            "https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip",
        ],
        "extract_dir": "",
        "rename_to": "redis",
        "checksum": None,
    },
    "nginx": {
        "name": "Nginx 1.24.0",
        "filename": "nginx-1.24.0.zip",
        "urls": [
            "https://nginx.org/download/nginx-1.24.0.zip",
        ],
        "extract_dir": "nginx-1.24.0",
        "rename_to": "nginx",
        "checksum": None,
    },
}

PYTHON_CONFIG = {
    "name": "Python 3.11.9 (Windows embeddable)",
    "filename": "python-3.11.9-embed-amd64.zip",
    "urls": [
        "https://www.python.org/ftp/python/3.11.9/python-3.11.9-embed-amd64.zip",
    ],
    "rename_to": "python",
}

MOSQUITTO_CONFIG = {
    "name": "Eclipse Mosquitto 2.1.2 (MQTT Broker)",
    "filename": "mosquitto-2.1.2-install-windows-x64.exe",
    "urls": [
        "https://mosquitto.org/files/binary/win64/mosquitto-2.1.2-install-windows-x64.exe",
    ],
    "rename_to": "mosquitto",
}


class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    CYAN = "\033[96m"
    RESET = "\033[0m"
    BOLD = "\033[1m"


def info(msg: str):
    print(f"{Colors.CYAN}[INFO] {msg}{Colors.RESET}")


def success(msg: str):
    print(f"{Colors.GREEN}[OK] {msg}{Colors.RESET}")


def warn(msg: str):
    print(f"{Colors.YELLOW}[WARN] {msg}{Colors.RESET}")


def error(msg: str):
    print(f"{Colors.RED}[ERROR] {msg}{Colors.RESET}")


class ComponentError(Exception):
    pass


def run_command(cmd: list, cwd: Optional[Path] = None, check: bool = True, timeout: int = 600) -> subprocess.CompletedProcess:
    cmd_str = " ".join(str(c) for c in cmd)
    info(f"执行: {cmd_str}")
    try:
        result = subprocess.run(
            cmd, cwd=cwd, capture_output=True, shell=True, timeout=timeout,
            encoding="utf-8", errors="replace"
        )
    except FileNotFoundError:
        error(f"命令未找到: {cmd[0]}")
        error("解决方案:")
        error(f"  1. 确认 {cmd[0]} 已安装并加入系统 PATH")
        error(f"  2. 在 PowerShell 中运行 'where {cmd[0]}' 确认路径")
        if cmd[0] == "mvn":
            error("  3. Maven 下载: https://maven.apache.org/download.cgi")
            error("  4. 设置环境变量: MAVEN_HOME 并将 %MAVEN_HOME%\\bin 加入 PATH")
        elif cmd[0] == "npm":
            error("  3. Node.js 下载: https://nodejs.org/")
            error("  4. 安装后 npm 自动加入 PATH")
        raise
    except subprocess.TimeoutExpired:
        error(f"命令执行超时 ({timeout}秒): {cmd_str}")
        error("解决方案:")
        error("  1. 检查网络连接（Maven 可能在下载依赖）")
        error("  2. 尝试手动执行命令确认问题")
        raise
    except OSError as e:
        error(f"系统错误: {e}")
        error("解决方案: 确认命令路径正确且有执行权限")
        raise

    if result.returncode != 0 and check:
        error(f"命令失败 (exit={result.returncode})")
        if result.stdout and result.stdout.strip():
            error(f"标准输出:\n{result.stdout[-2000:]}")
        if result.stderr and result.stderr.strip():
            error(f"错误输出:\n{result.stderr[-2000:]}")
        raise subprocess.CalledProcessError(result.returncode, cmd, output=result.stdout, stderr=result.stderr)
    return result


def check_prerequisites(skip_build: bool) -> None:
    """检查前置环境是否满足要求"""
    info("=" * 50)
    info("检查前置环境")
    info("=" * 50)

    issues = []

    if not skip_build:
        # 检查 Maven
        mvn_result = subprocess.run(
            "mvn --version", shell=True, capture_output=True,
            encoding="utf-8", errors="replace"
        )
        if mvn_result.returncode != 0:
            issues.append(
                "Maven 未安装或不在 PATH 中\n"
                "  解决方案:\n"
                "    1. 下载 Maven: https://maven.apache.org/download.cgi\n"
                "    2. 解压到任意目录（如 F:\\light\\apache-maven-3.9.13）\n"
                "    3. 将 bin 目录加入系统 PATH\n"
                "    4. 重新打开终端后验证: mvn --version"
            )
        else:
            success(f"Maven: {mvn_result.stdout.splitlines()[0].strip()}")

        # 检查 npm
        npm_result = subprocess.run(
            "npm --version", shell=True, capture_output=True,
            encoding="utf-8", errors="replace"
        )
        if npm_result.returncode != 0:
            issues.append(
                "npm 未安装或不在 PATH 中\n"
                "  解决方案:\n"
                "    1. 下载 Node.js: https://nodejs.org/\n"
                "    2. 安装时勾选 'Add to PATH'\n"
                "    3. 重新打开终端后验证: npm --version"
            )
        else:
            success(f"npm: v{npm_result.stdout.strip()}")

        # 检查 Java
        java_result = subprocess.run(
            "java -version", shell=True, capture_output=True,
            encoding="utf-8", errors="replace"
        )
        if java_result.returncode != 0:
            issues.append(
                "Java 未安装或不在 PATH 中\n"
                "  解决方案:\n"
                "    1. 下载 JDK 8: https://adoptium.net/\n"
                "    2. 设置 JAVA_HOME 环境变量\n"
                "    3. 将 %JAVA_HOME%\\bin 加入 PATH"
            )
        else:
            java_ver = java_result.stderr.splitlines()[0].strip() if java_result.stderr else "已安装"
            success(f"Java: {java_ver}")

    # 检查 pom.xml 和 package.json
    if not skip_build:
        server_pom = PROJECT_ROOT / "server" / "pom.xml"
        if not server_pom.exists():
            issues.append(f"找不到后端项目文件: {server_pom}")
        web_pkg = PROJECT_ROOT / "web" / "package.json"
        if not web_pkg.exists():
            issues.append(f"找不到前端项目文件: {web_pkg}")

    # 检查磁盘空间（至少需要 2GB）
    import shutil as _shutil
    total, used, free = _shutil.disk_usage(str(OUTPUT_DIR.drive or OUTPUT_DIR.anchor))
    free_gb = free / (1024**3)
    if free_gb < 2.0:
        issues.append(f"磁盘空间不足: 仅剩 {free_gb:.1f} GB（建议至少 2 GB）")
    else:
        success(f"磁盘空间: {free_gb:.1f} GB 可用")

    if issues:
        error("环境检查未通过，发现以下问题:")
        for i, issue in enumerate(issues, 1):
            error(f"\n问题 {i}: {issue}")
        error("\n请先解决上述问题后重新运行脚本。")
        sys.exit(1)

    success("环境检查全部通过")


def download_file(url: str, dest: Path, desc: str = "", timeout: int = 300) -> bool:
    if dest.exists():
        info(f"文件已存在，跳过下载: {dest.name}")
        return True

    info(f"正在下载 {desc or dest.name}...")
    info(f"  URL: {url}")

    try:
        dest.parent.mkdir(parents=True, exist_ok=True)
        req = urllib.request.Request(
            url,
            headers={"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
        )
        with urllib.request.urlopen(req, timeout=timeout) as response:
            with open(dest, "wb") as f:
                while True:
                    chunk = response.read(8192)
                    if not chunk:
                        break
                    f.write(chunk)
        success(f"下载完成: {dest.name} ({dest.stat().st_size / (1024*1024):.1f} MB)")
        return True
    except Exception as e:
        error(f"下载失败: {e}")
        if dest.exists():
            dest.unlink()
        return False


def try_download(urls: List[str], dest: Path, desc: str = "") -> bool:
    for i, url in enumerate(urls):
        source = "主站" if i == 0 else f"备用{i}"
        info(f"尝试从 {source} 下载...")
        if download_file(url, dest, desc):
            return True
    return False


def extract_zip(archive: Path, dest_dir: Path) -> None:
    info(f"正在解压: {archive.name}")
    try:
        with zipfile.ZipFile(archive, "r") as zf:
            zf.extractall(str(dest_dir))
        success(f"解压完成: {archive.name}")
    except Exception as e:
        raise ComponentError(f"解压失败 {archive.name}: {e}")


def sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            h.update(chunk)
    return h.hexdigest()


def prepare_component(key: str, cfg: dict) -> None:
    target_dir = PACKAGES_DIR / cfg["rename_to"]
    archive_path = PACKAGES_DIR / cfg["filename"]

    info("=" * 50)
    info(f"准备组件: {cfg['name']}")
    info("=" * 50)

    if target_dir.exists():
        success(f"{cfg['name']} 已存在（{target_dir}），跳过")
        return

    if archive_path.exists():
        info(f"发现本地压缩包: {archive_path.name}")
        _extract_and_move(archive_path, cfg)
        success(f"{cfg['name']} 从本地压缩包准备完成")
        return

    info(f"本地未找到，尝试下载...")
    if try_download(cfg["urls"], archive_path, cfg["name"]):
        if cfg.get("checksum"):
            actual = sha256_file(archive_path)
            if actual.lower() != cfg["checksum"].lower():
                archive_path.unlink()
                raise ComponentError(
                    f"{cfg['name']} 校验失败\n  期望: {cfg['checksum']}\n  实际: {actual}"
                )
        _extract_and_move(archive_path, cfg)
        success(f"{cfg['name']} 下载并准备完成")
        return

    _print_manual_download_guide(key, cfg)
    raise ComponentError(f"{cfg['name']} 获取失败：所有下载源均不可用，且本地无预置文件")


def _extract_and_move(archive_path: Path, cfg: dict) -> None:
    extract_dir = PACKAGES_DIR / "_extract"
    if extract_dir.exists():
        shutil.rmtree(extract_dir)
    extract_dir.mkdir(parents=True, exist_ok=True)

    extract_zip(archive_path, extract_dir)

    src_dir = extract_dir / cfg["extract_dir"] if cfg["extract_dir"] else extract_dir
    if not src_dir.exists():
        subdirs = [d for d in extract_dir.iterdir() if d.is_dir()]
        if len(subdirs) == 1:
            src_dir = subdirs[0]
        else:
            src_dir = extract_dir

    target_dir = PACKAGES_DIR / cfg["rename_to"]
    if target_dir.exists():
        shutil.rmtree(target_dir)

    if src_dir == extract_dir:
        shutil.copytree(str(src_dir), str(target_dir))
        shutil.rmtree(extract_dir)
    else:
        shutil.move(str(src_dir), str(target_dir))
        if extract_dir.exists():
            shutil.rmtree(extract_dir)


def _print_manual_download_guide(key: str, cfg: dict) -> None:
    error(f"{'='*50}")
    error(f"【{cfg['name']}】获取失败")
    error(f"{'='*50}")
    error("可能原因：")
    error("  1. 网络连接问题（国内访问 GitHub/MySQL 官网可能较慢）")
    error("  2. URL 已失效（官方更新了下载地址）")
    error("")
    error("解决方案（二选一）：")
    error("")
    error("方案 A：使用浏览器或下载工具手动下载")
    error(f"  文件名: {cfg['filename']}")
    for i, url in enumerate(cfg["urls"]):
        label = "主站" if i == 0 else f"备用{i}"
        error(f"  {label}: {url}")
    error("")
    error(f"  下载后放置到: {PACKAGES_DIR / cfg['filename']}")
    error("  然后重新运行打包脚本（会自动识别本地文件）")
    error("")
    error("方案 B：修改脚本中的 URL")
    error(f"  编辑文件: {Path(__file__)}")
    error(f"  找到 COMPONENTS['{key}']['urls']，替换为可用的镜像地址")
    error(f"{'='*50}")


def prepare_python(with_python: bool) -> None:
    if not with_python:
        return

    cfg = PYTHON_CONFIG
    target_dir = PACKAGES_DIR / cfg["rename_to"]
    archive_path = PACKAGES_DIR / cfg["filename"]

    info("=" * 50)
    info(f"准备内嵌 Python: {cfg['name']}")
    info("=" * 50)

    if target_dir.exists():
        success(f"内嵌 Python 已存在，跳过")
        return

    if archive_path.exists():
        info(f"发现本地压缩包: {archive_path.name}")
        extract_dir = PACKAGES_DIR / "_extract_py"
        if extract_dir.exists():
            shutil.rmtree(extract_dir)
        extract_dir.mkdir(parents=True, exist_ok=True)
        extract_zip(archive_path, extract_dir)
        if target_dir.exists():
            shutil.rmtree(target_dir)
        shutil.move(str(extract_dir), str(target_dir))
        success(f"内嵌 Python 从本地压缩包准备完成")
        return

    if try_download(cfg["urls"], archive_path, cfg["name"]):
        extract_dir = PACKAGES_DIR / "_extract_py"
        if extract_dir.exists():
            shutil.rmtree(extract_dir)
        extract_dir.mkdir(parents=True, exist_ok=True)
        extract_zip(archive_path, extract_dir)
        if target_dir.exists():
            shutil.rmtree(target_dir)
        shutil.move(str(extract_dir), str(target_dir))
        success(f"内嵌 Python 下载并准备完成")
        return

    _print_manual_download_guide("python", cfg)
    raise ComponentError(f"{cfg['name']} 获取失败")


def prepare_mosquitto() -> None:
    """准备 Mosquitto MQTT Broker（从 .exe 安装包静默安装提取文件）"""
    cfg = MOSQUITTO_CONFIG
    target_dir = PACKAGES_DIR / cfg["rename_to"]
    installer_path = PACKAGES_DIR / cfg["filename"]

    info("=" * 50)
    info(f"准备组件: {cfg['name']}")
    info("=" * 50)

    if target_dir.exists():
        success(f"{cfg['name']} 已存在（{target_dir}），跳过")
        return

    if installer_path.exists():
        info(f"发现本地安装包: {installer_path.name}")
        _extract_mosquitto_installer(installer_path, target_dir)
        success(f"{cfg['name']} 从本地安装包解压完成")
        return

    info(f"本地未找到，尝试下载...")
    if try_download(cfg["urls"], installer_path, cfg["name"]):
        _extract_mosquitto_installer(installer_path, target_dir)
        success(f"{cfg['name']} 下载并解压完成")
        return

    _print_manual_download_guide("mosquitto", cfg)
    raise ComponentError(f"{cfg['name']} 获取失败")


def _extract_mosquitto_installer(installer_path: Path, target_dir: Path) -> None:
    """通过静默安装方式提取 Mosquitto 文件"""
    temp_install_dir = Path(tempfile.mkdtemp(prefix="mosquitto_install_"))
    try:
        info("正在静默安装 Mosquitto 到临时目录...")
        result = subprocess.run(
            [str(installer_path), "/S", f"/D={str(temp_install_dir)}"],
            capture_output=True,
            text=True,
            timeout=180,
        )
        # 等待安装完成
        time.sleep(2)

        if not temp_install_dir.exists() or not any(temp_install_dir.iterdir()):
            raise ComponentError(
                f"Mosquitto 静默安装失败: 未生成任何文件\n"
                f"  返回码: {result.returncode}\n"
                f"  stderr: {result.stderr}"
            )

        # 将安装目录复制到 packages/mosquitto
        shutil.copytree(str(temp_install_dir), str(target_dir))
        success(f"Mosquitto 文件已提取到: {target_dir}")
    finally:
        if temp_install_dir.exists():
            shutil.rmtree(temp_install_dir, ignore_errors=True)


def build_backend() -> Path:
    server_dir = PROJECT_ROOT / "server"
    jar_path = server_dir / "ruoyi-admin" / "target" / "ruoyi-admin.jar"

    info("=" * 50)
    info("开始编译后端 (Maven)")
    info("=" * 50)

    if not (server_dir / "pom.xml").exists():
        raise FileNotFoundError(f"找不到 pom.xml: {server_dir / 'pom.xml'}")

    run_command(["mvn", "clean", "package", "-DskipTests"], cwd=server_dir)

    if not jar_path.exists():
        error("编译后找不到 JAR 文件，可能原因:")
        error("  1. Maven 编译成功但打包失败")
        error("  2. 模块名称或输出路径发生变化")
        error(f"  期望路径: {jar_path}")
        error("  解决方案: 手动执行 'cd server; mvn clean package -DskipTests' 检查输出")
        raise FileNotFoundError(f"编译后找不到 JAR 文件: {jar_path}")

    success(f"后端编译完成: {jar_path}")
    return jar_path


def build_frontend() -> Path:
    web_dir = PROJECT_ROOT / "web"
    dist_dir = web_dir / "dist"

    info("=" * 50)
    info("开始构建前端 (npm)")
    info("=" * 50)

    if not (web_dir / "package.json").exists():
        raise FileNotFoundError(f"找不到 package.json: {web_dir / 'package.json'}")

    info("安装前端依赖...")
    run_command(["npm", "install"], cwd=web_dir)

    info("执行生产构建...")
    # 高版本 Node.js 需要此选项以兼容旧版 OpenSSL 算法
    os.environ["NODE_OPTIONS"] = "--openssl-legacy-provider"
    run_command(["npm", "run", "build:prod"], cwd=web_dir)

    if not dist_dir.exists():
        error("构建后找不到 dist 目录，可能原因:")
        error("  1. npm run build:prod 执行失败")
        error("  2. vue.config.js 中 outputDir 配置异常")
        error(f"  期望路径: {dist_dir}")
        error("  解决方案: 手动执行 'cd web; npm run build:prod' 检查输出")
        raise FileNotFoundError(f"构建后找不到 dist 目录: {dist_dir}")

    success(f"前端构建完成: {dist_dir}")
    return dist_dir


def verify_components(with_python: bool) -> None:
    info("=" * 50)
    info("验证组件完整性")
    info("=" * 50)

    required = {
        "jdk": ["bin/java.exe"],
        "mysql": ["bin/mysqld.exe", "bin/mysql.exe"],
        "redis": ["redis-server.exe"],
        "nginx": ["nginx.exe"],
        "mosquitto": ["mosquitto.exe"],
    }

    all_ok = True
    for name, files in required.items():
        base = PACKAGES_DIR / name
        if not base.exists():
            error(f"缺少组件: {name}")
            all_ok = False
            continue
        for f in files:
            if not (base / f).exists():
                error(f"缺少文件: {name}/{f}")
                all_ok = False

    if with_python:
        py_exe = PACKAGES_DIR / "python" / "python.exe"
        if not py_exe.exists():
            error(f"缺少内嵌 Python: {py_exe}")
            all_ok = False

    if not all_ok:
        raise ComponentError("组件验证失败，请检查 packages/ 目录")

    success("所有组件验证通过")


def assemble_package(version: str, jar_path: Path, dist_dir: Path, with_python: bool) -> Path:
    info("=" * 50)
    info("组装部署包")
    info("=" * 50)

    pkg_name = f"PM6-ZM-Deploy-v{version}"
    pkg_dir = OUTPUT_DIR / pkg_name

    if pkg_dir.exists():
        shutil.rmtree(pkg_dir)
    pkg_dir.mkdir(parents=True, exist_ok=True)

    shutil.copy(DEPLOY_DIR / "deploy.bat", pkg_dir / "deploy.bat")
    shutil.copy(DEPLOY_DIR / "deploy.py", pkg_dir / "deploy.py")
    shutil.copy(DEPLOY_DIR / "deploy-config.ini", pkg_dir / "deploy-config.ini")

    config_dest = pkg_dir / "config"
    shutil.copytree(DEPLOY_DIR / "config", config_dest, dirs_exist_ok=True)

    app_dest = pkg_dir / "app"
    app_dest.mkdir(exist_ok=True)
    shutil.copy(jar_path, app_dest / "ruoyi-admin.jar")
    shutil.copytree(dist_dir, app_dest / "dist", dirs_exist_ok=True)

    sql_src = PROJECT_ROOT / "database" / "zm.sql"
    if sql_src.exists():
        sql_dest = pkg_dir / "sql"
        sql_dest.mkdir(exist_ok=True)
        shutil.copy(sql_src, sql_dest / "zm.sql")
    else:
        warn(f"找不到数据库脚本: {sql_src}")

    for key, cfg in COMPONENTS.items():
        src = PACKAGES_DIR / cfg["rename_to"]
        if src.exists():
            dest = pkg_dir / "packages" / cfg["rename_to"]
            if key == "mysql":
                # 排除 data 目录，避免将开发环境的数据文件打包进部署包
                shutil.copytree(src, dest, ignore=shutil.ignore_patterns('data'))
            else:
                shutil.copytree(src, dest)

    # Mosquitto (always included)
    mosq_src = PACKAGES_DIR / "mosquitto"
    if mosq_src.exists():
        mosq_dest = pkg_dir / "packages" / "mosquitto"
        shutil.copytree(mosq_src, mosq_dest)
    else:
        warn("Mosquitto 组件目录不存在，部署包将不含 MQTT Broker")

    if with_python:
        src = PACKAGES_DIR / "python"
        if src.exists():
            dest = pkg_dir / "packages" / "python"
            shutil.copytree(src, dest)
            # 修复 embeddable Python 的 ._pth 文件，启用 site 模块以支持完整标准库
            pth_file = dest / "python311._pth"
            if pth_file.exists():
                content = pth_file.read_text(encoding="utf-8")
                content = content.replace("#import site", "import site")
                pth_file.write_text(content, encoding="utf-8")
                info("已启用 embeddable Python 的 site 模块")
            info("已包含内嵌 Python")

    (pkg_dir / "logs").mkdir(exist_ok=True)
    (pkg_dir / "temp").mkdir(exist_ok=True)
    (pkg_dir / "pid").mkdir(exist_ok=True)

    success(f"部署包组装完成: {pkg_dir}")
    return pkg_dir


def create_zip(pkg_dir: Path) -> Path:
    info("=" * 50)
    info("打包为 zip")
    info("=" * 50)

    zip_path = OUTPUT_DIR / f"{pkg_dir.name}.zip"
    if zip_path.exists():
        zip_path.unlink()

    info(f"正在创建: {zip_path.name}")

    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as zf:
        for root, dirs, files in os.walk(pkg_dir):
            for file in files:
                file_path = Path(root) / file
                arcname = str(file_path.relative_to(pkg_dir))
                zf.write(file_path, arcname)

    size_mb = zip_path.stat().st_size / (1024 * 1024)
    success(f"zip 打包完成: {zip_path}")
    info(f"文件大小: {size_mb:.1f} MB")
    return zip_path


def print_summary(pkg_dir: Path, zip_path: Path, with_python: bool):
    print("\n" + "=" * 60)
    print(f"{Colors.GREEN}{Colors.BOLD}  打包完成！{Colors.RESET}")
    print("=" * 60)
    print(f"  部署包目录: {pkg_dir}")
    print(f"  zip 文件:   {zip_path}")
    print(f"  文件大小:   {zip_path.stat().st_size / (1024*1024):.1f} MB")
    if with_python:
        print(f"  {Colors.CYAN}已包含内嵌 Python，目标机器无需预装 Python{Colors.RESET}")
    else:
        print(f"  {Colors.YELLOW}目标机器需要预装 Python 3.7+{Colors.RESET}")
    print("\n  使用方式:")
    print(f"    1. 将 {zip_path.name} 拷贝到目标 Windows 机器")
    print("    2. 解压到任意目录")
    print("    3. 双击 deploy.bat 运行")
    print("=" * 60 + "\n")


def main():
    parser = argparse.ArgumentParser(
        description="PM6-ZM-Coder 离线部署包打包脚本",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python build-deploy-package.py
  python build-deploy-package.py --version 1.2.10
  python build-deploy-package.py --skip-build
  python build-deploy-package.py --with-python
        """
    )
    parser.add_argument("--version", default="1.0.0", help="部署包版本号 (默认: 1.0.0)")
    parser.add_argument("--skip-build", action="store_true", help="跳过前后端编译（使用现有产物）")
    parser.add_argument("--skip-download", action="store_true", help="跳过组件下载（仅使用本地预置）")
    parser.add_argument("--with-python", action="store_true", help="包含内嵌 Python（目标机器无需预装）")
    args = parser.parse_args()

    if sys.version_info < (3, 7):
        error("需要 Python 3.7 或更高版本")
        sys.exit(1)

    try:
        OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
        PACKAGES_DIR.mkdir(parents=True, exist_ok=True)
        check_prerequisites(skip_build=args.skip_build)

        if args.skip_build:
            info("跳过编译，使用现有产物")
            jar_path = PROJECT_ROOT / "server" / "ruoyi-admin" / "target" / "ruoyi-admin.jar"
            dist_dir = PROJECT_ROOT / "web" / "dist"
            if not jar_path.exists():
                raise FileNotFoundError(f"找不到 JAR 文件: {jar_path}")
            if not dist_dir.exists():
                raise FileNotFoundError(f"找不到 dist 目录: {dist_dir}")
        else:
            jar_path = build_backend()
            dist_dir = build_frontend()

        if not args.skip_download:
            for key, cfg in COMPONENTS.items():
                prepare_component(key, cfg)

            prepare_mosquitto()

            if args.with_python:
                prepare_python(with_python=True)
        else:
            info("跳过下载，仅使用本地预置")

        verify_components(with_python=args.with_python)

        pkg_dir = assemble_package(args.version, jar_path, dist_dir, with_python=args.with_python)
        zip_path = create_zip(pkg_dir)

        # 清理 output 目录中的中间产物，仅保留 .zip 文件
        info("清理构建中间产物...")
        for item in OUTPUT_DIR.iterdir():
            try:
                if item.is_dir():
                    shutil.rmtree(item, ignore_errors=True)
                elif item.is_file() and not item.suffix.lower() == '.zip':
                    item.unlink(missing_ok=True)
            except Exception as e:
                warn(f"清理失败: {item.name} - {e}")
        success("中间产物清理完成")

        print_summary(pkg_dir, zip_path, with_python=args.with_python)

    except ComponentError as e:
        error(f"组件获取失败: {e}")
        error("打包已终止，未生成不完整的部署包。")
        sys.exit(1)
    except subprocess.CalledProcessError as e:
        error(f"命令执行失败: {e}")
        sys.exit(1)
    except FileNotFoundError as e:
        error(f"文件不存在: {e}")
        sys.exit(1)
    except Exception as e:
        error(f"打包失败: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
