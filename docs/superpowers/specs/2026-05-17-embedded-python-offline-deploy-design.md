# PM6-ZM 离线部署包内嵌 Python 方案设计文档

## 1. 背景与问题

PM6-ZM 直流照明监控系统的 Windows 离线部署包 (`PM6-ZM-Deploy-v1.0.0.zip`) 设计目标是在**完全不联网、刚重装系统的 Windows 机器**上实现"解压即用"。

当前部署入口 `deploy.bat` 按以下顺序查找 Python 解释器：
1. `packages\python\python.exe`（内嵌 Python）
2. 系统 PATH 中的 `python.exe` / `python3.exe`
3. 常见安装路径

**问题**：当前生成的部署包中 `packages/python/` 目录不存在，导致重装系统后的目标机器（无 Python）无法运行部署脚本。

## 2. 目标

- 部署包包含完整的内嵌 Python 运行时
- 目标机器无需预装任何软件，解压后双击 `deploy.bat` 即可部署
- 不增加不必要的包体积
- 保持现有部署流程不变

## 3. 约束条件

- 目标操作系统：Windows 10/11 64位
- 目标机器状态：刚重装系统，无网络，无预装软件
- `deploy.py` 必须仅使用 Python 标准库（已验证）
- 打包脚本 `build-deploy-package.py` 的 `--with-python` 参数已有实现，需验证并修复

## 4. 架构设计

```
打包机（联网）
  └── build-deploy-package.py --with-python
        ├── 编译后端 (Maven) → ruoyi-admin.jar
        ├── 构建前端 (npm) → dist/
        ├── 准备组件：JDK / MySQL / Redis / Nginx
        ├── 准备内嵌 Python → packages/python/
        ├── 验证所有组件完整性
        └── 组装 → PM6-ZM-Deploy-v1.0.0.zip

目标机（离线，重装系统）
  └── 解压 PM6-ZM-Deploy-v1.0.0.zip
        └── 双击 deploy.bat
              ├── 找到 packages\python\python.exe ✓
              └── 执行 deploy.py
                    ├── 启动 MySQL
                    ├── 启动 Redis
                    ├── 启动 Java 后端
                    ├── 启动 Nginx
                    ├── 初始化数据库
                    └── 健康检查
```

## 5. 组件分析

### 5.1 deploy.py 依赖审计

`deploy.py` 使用的所有导入模块：

| 模块 | 类型 | 说明 |
|------|------|------|
| argparse, configparser, ctypes, os, re, shutil, socket, subprocess, sys, time | 标准库 | 基础功能 |
| dataclasses, datetime, pathlib, typing | 标准库 | 类型与数据结构 |
| urllib.request | 标准库 | 仅 health_check 中使用 |

**结论**：100% 纯标准库，Windows embeddable Python 完全支持，无需 pip。

### 5.2 内嵌 Python 技术细节

使用 `python-3.11.9-embed-amd64.zip`（约 8MB）：
- 解压即用，无需安装程序
- 包含完整标准库
- 通过 `python311._pth` 控制模块搜索路径
- 不包含 pip，但本项目不需要

**兼容性说明**：
- `deploy.bat` 通过绝对路径调用 `python.exe deploy.py`，`__file__` 解析正常
- embeddable Python 的 `sys.path` 限制不影响本场景

## 6. 现有代码验证

### 6.1 build-deploy-package.py

| 功能 | 代码位置 | 状态 |
|------|----------|------|
| PYTHON_CONFIG 定义 | 第 74-81 行 | 已定义 embeddable Python URL |
| prepare_python() | 第 269-307 行 | 已实现下载/解压逻辑 |
| verify_components() | 第 354-387 行 | 已检查 python.exe |
| assemble_package() | 第 427-431 行 | 已复制 python 到输出包 |
| CLI 参数 | 第 499 行 | `--with-python` 已暴露 |

### 6.2 deploy.bat

| 功能 | 代码位置 | 状态 |
|------|----------|------|
| 内嵌 Python 优先查找 | 第 19-23 行 | 已正确实现 |
| 版本检查 | 第 71-77 行 | 要求 Python 3.7+ |

**结论**：核心逻辑已存在，问题仅在于打包时未使用 `--with-python` 参数，或 embeddable Python 下载失败。

## 7. 修复点

### 7.1 修复 embeddable Python 解压逻辑

当前 `prepare_python()` 中的 `shutil.move(str(extract_dir), str(target_dir))` 在 embeddable zip 场景下工作正确（zip 内无子目录，解压后文件直接位于 `_extract_py/` 下，move 后变为 `python/`）。

**但存在边界问题**：如果 `_extract_py` 目录已存在且非空，`shutil.move` 在 Windows 上可能将源目录**移入**目标目录而非替换。需确保清理逻辑正确。

### 7.2 修复 assemble_package 中的 config 复制

当前 `assemble_package()` 第 405-406 行：
```python
config_dest = pkg_dir / "config"
shutil.copytree(DEPLOY_DIR / "config", config_dest)
```

如果 `pkg_dir/config` 已存在（重复打包），`shutil.copytree` 会抛出 `FileExistsError`。应添加 `dirs_exist_ok=True`。

### 7.3 修复 assemble_package 中的 app/dist 复制

第 411 行：
```python
shutil.copytree(dist_dir, app_dest / "dist")
```

同样存在 `FileExistsError` 风险，需添加 `dirs_exist_ok=True`。

## 8. 测试策略

### 8.1 打包测试

```bash
cd deploy/windows
python build-deploy-package.py --with-python --skip-build --skip-download
```

验证：
- `packages/python/python.exe` 存在且可执行
- `python.exe -c "import sys; print(sys.version)"` 输出 3.11.x
- `python.exe deploy.py --help` 正常输出

### 8.2 组装测试

验证输出目录 `output/PM6-ZM-Deploy-v1.0.0/packages/python/` 包含：
- `python.exe`
- `python311.dll`
- `python311._pth`
- `Lib/` 目录（标准库）

### 8.3 模拟离线部署测试

1. 将输出 zip 复制到临时目录
2. 解压
3. 运行 `deploy.bat start`
4. 确认日志中显示 "使用 Python: <内嵌路径>"
5. 确认所有服务启动成功

## 9. 风险评估

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| embeddable Python 下载失败 | 中 | 高 | 提供手动下载指引；支持本地缓存 |
| embeddable Python 与 deploy.py 不兼容 | 低 | 高 | 已审计为标准库-only；测试验证 |
| 包体积过大 | 低 | 低 | embeddable 仅 8MB，总包约 360MB |
| Windows 权限问题 | 中 | 中 | 以管理员运行 deploy.bat |

## 10. 成功标准

- [ ] `build-deploy-package.py --with-python` 成功生成完整部署包
- [ ] 部署包 `packages/python/python.exe` 存在
- [ ] 在临时目录模拟离线环境，`deploy.bat` 成功使用内嵌 Python
- [ ] 所有服务（MySQL/Redis/Java/Nginx）正常启动
- [ ] 健康检查全部通过
