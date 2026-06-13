param(
    [Parameter(Position = 0)]
    [string]$Command = "help",
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$RemainingArgs
)

$ErrorActionPreference = "Stop"

$AppName = "jndc-client"
$AppMain = "jndc_client.start.ClientStart"
$JavaRequiredMajor = 21

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$AppHome = Split-Path -Parent $ScriptDir
$LogDir = Join-Path $AppHome "logs"
$ConfDir = Join-Path $AppHome "conf"
$LibDir = Join-Path $AppHome "lib"
$PidFile = Join-Path $ScriptDir ".app.pid"
$BootstrapOutFile = Join-Path $LogDir "bootstrap.out"
$BootstrapErrFile = Join-Path $LogDir "bootstrap.err"
$AppLogFile = Join-Path $AppHome "output.log"
$EnvFile = Join-Path $ScriptDir "jndc.env"
$RuntimeConfigFile = Join-Path $HOME ".jndc\client\conf\config.yml"
$TemplateConfigFile = Join-Path $ConfDir "config.template.yml"

function Write-Info([string]$Message) {
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $Message"
}

function Fail([string]$Message) {
    throw "[ERROR] $Message"
}

function Load-EnvFile {
    if (-not (Test-Path $EnvFile)) {
        return
    }

    foreach ($line in Get-Content $EnvFile) {
        $trimmed = $line.Trim()
        if ([string]::IsNullOrWhiteSpace($trimmed) -or $trimmed.StartsWith("#")) {
            continue
        }

        $separator = $trimmed.IndexOf("=")
        if ($separator -le 0) {
            continue
        }

        $key = $trimmed.Substring(0, $separator).Trim()
        $value = $trimmed.Substring($separator + 1).Trim()
        if (-not [string]::IsNullOrWhiteSpace($key)) {
            Set-Item -Path "Env:$key" -Value $value
        }
    }
}

function Get-RunMode {
    if ($env:JNDC_MODE) {
        return $env:JNDC_MODE
    }

    $normalized = $AppHome.Replace("\", "/")
    if ($normalized -match "/target/|/build/") {
        return "dev"
    }

    return "prod"
}

function Initialize-Defaults {
    param([string]$RunMode)

    if ($RunMode -eq "prod") {
        if (-not $env:SHUTDOWN_TIMEOUT) { $env:SHUTDOWN_TIMEOUT = "30" }
        if (-not $env:JVM_XMS) { $env:JVM_XMS = "-Xms256m" }
        if (-not $env:JVM_XMX) { $env:JVM_XMX = "-Xmx512m" }
        if (-not $env:JVM_METASPACE) { $env:JVM_METASPACE = "-XX:MaxMetaspaceSize=192m" }
        if (-not $env:GC_LOG) { $env:GC_LOG = "false" }
        return
    }

    if (-not $env:SHUTDOWN_TIMEOUT) { $env:SHUTDOWN_TIMEOUT = "10" }
    if (-not $env:JVM_XMS) { $env:JVM_XMS = "-Xms128m" }
    if (-not $env:JVM_XMX) { $env:JVM_XMX = "-Xmx256m" }
    if (-not $env:JVM_METASPACE) { $env:JVM_METASPACE = "-XX:MaxMetaspaceSize=128m" }
    if (-not $env:GC_LOG) { $env:GC_LOG = "false" }
}

function Get-Pid {
    if (-not (Test-Path $PidFile)) {
        return $null
    }

    $value = (Get-Content $PidFile -ErrorAction SilentlyContinue | Select-Object -First 1).Trim()
    if ([string]::IsNullOrWhiteSpace($value)) {
        return $null
    }

    return [int]$value
}

function Test-Running {
    $pid = Get-Pid
    if (-not $pid) {
        return $false
    }

    try {
        Get-Process -Id $pid -ErrorAction Stop | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Find-Java {
    if ($env:JAVA_HOME) {
        $javaFromHome = Join-Path $env:JAVA_HOME "bin\java.exe"
        if (Test-Path $javaFromHome) {
            return $javaFromHome
        }
    }

    $wildcardPatterns = @(
        "C:\Program Files\Java\jdk-21*\bin\java.exe",
        "C:\Program Files\Eclipse Adoptium\jdk-21*\bin\java.exe",
        "C:\Program Files\Microsoft\jdk-21*\bin\java.exe"
    )
    foreach ($pattern in $wildcardPatterns) {
        $candidate = Get-ChildItem -Path $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($candidate) {
            return $candidate.FullName
        }
    }

    $command = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    Fail "未找到 JDK $JavaRequiredMajor+，请设置 JAVA_HOME 或将 java.exe 加入 PATH"
}

function Get-JavaMajorVersion {
    param([string]$JavaCommand)

    $versionLine = (& $JavaCommand -version 2>&1 | Select-Object -First 1)
    if (-not $versionLine) {
        return $null
    }

    if ($versionLine -match '"([^"]+)"') {
        $version = $matches[1]
        $parts = $version.Split(".")
        if ($parts[0] -eq "1" -and $parts.Length -gt 1) {
            return [int]$parts[1]
        }
        return [int]$parts[0]
    }

    return $null
}

function Require-RuntimeConfig {
    if (Test-Path $RuntimeConfigFile) {
        return
    }

    $runtimeConfDir = Split-Path -Parent $RuntimeConfigFile
    if (-not (Test-Path $runtimeConfDir)) {
        New-Item -ItemType Directory -Force -Path $runtimeConfDir | Out-Null
    }

    Fail "缺少运行配置: $RuntimeConfigFile，请先参考 $TemplateConfigFile 创建 config.yml"
}

function Preflight {
    param([string]$JavaCommand)

    if (-not (Test-Path $JavaCommand)) {
        Fail "Java 不存在: $JavaCommand"
    }
    if (-not (Test-Path $LibDir)) {
        Fail "lib 目录不存在: $LibDir"
    }
    if (-not (Test-Path $ConfDir)) {
        Fail "conf 目录不存在: $ConfDir"
    }

    Require-RuntimeConfig

    $majorVersion = Get-JavaMajorVersion -JavaCommand $JavaCommand
    if (-not $majorVersion) {
        Fail "无法解析 Java 版本，请检查 $JavaCommand"
    }
    if ($majorVersion -lt $JavaRequiredMajor) {
        Fail "检测到 Java $majorVersion，JNDC 需要 JDK $JavaRequiredMajor+。请调整 JAVA_HOME 或 PATH。"
    }

    if (-not (Get-ChildItem -Path (Join-Path $LibDir "jndc_client*.jar") -ErrorAction SilentlyContinue)) {
        Fail "未找到 jndc_client.jar，请先执行 mvn package"
    }
}

function Rotate-BootstrapLog {
    $maxSize = 5MB
    $keepFiles = 5
    if (-not (Test-Path $BootstrapOutFile)) {
        return
    }

    $fileInfo = Get-Item $BootstrapOutFile
    if ($fileInfo.Length -lt $maxSize) {
        return
    }

    $rotatedFile = Join-Path $LogDir ("bootstrap.{0}.out" -f (Get-Date -Format "yyyyMMddHHmmss"))
    Move-Item $BootstrapOutFile $rotatedFile -Force

    $history = Get-ChildItem -Path $LogDir -Filter "bootstrap.*.out" | Sort-Object Name -Descending
    if ($history.Count -le $keepFiles) {
        return
    }

    $history | Select-Object -Skip $keepFiles | Remove-Item -Force
}

function Build-JavaArgs {
    param([string]$RunMode)

    $classpath = "$ConfDir;$LibDir\*"
    $args = @(
        "-Dapp.home=$AppHome",
        "-Dapp.mode=$RunMode",
        $env:JVM_XMS,
        $env:JVM_XMX,
        $env:JVM_METASPACE
    )

    if ($env:GC_LOG -eq "true") {
        $gcLogFile = Join-Path $LogDir "gc.log"
        $args += "-Xlog:gc*:file=$($gcLogFile):time,uptime,level,tags:filecount=5,filesize=10M"
    }

    if ($env:JVM_OPTS) {
        $args += ($env:JVM_OPTS -split "\s+" | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    }

    $args += @("-classpath", $classpath, $AppMain)
    return $args
}

function Show-Banner {
    Write-Host "       __  _   _ ____   ____ ____"
    Write-Host "      |  \| | | |  _ \ / ___/ ___|"
    Write-Host "      | | | | | | | | | |  | |"
    Write-Host "      | |\  | |_| | |_| |__| |___"
    Write-Host "      |_| \_|\___/|____/\____\____|  Client"
    Write-Host "  模式: $RunMode  |  版本: $(Get-Date -Format 'yyyyMMdd')"
    Write-Host ""
}

function Do-Start {
    if (Test-Running) {
        Write-Info "客户端已在运行 (PID: $(Get-Pid))"
        return
    }

    $javaCommand = Find-Java
    Preflight -JavaCommand $javaCommand

    New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
    Rotate-BootstrapLog
    Show-Banner

    Write-Info "启动中..."
    Write-Info "  主类:      $AppMain"
    Write-Info "  运行配置:  $RuntimeConfigFile"
    Write-Info "  业务日志:  $AppLogFile"
    Write-Info "  引导日志:  $BootstrapOutFile"

    $javaArgs = Build-JavaArgs -RunMode $RunMode
    $process = Start-Process -FilePath $javaCommand `
        -ArgumentList $javaArgs `
        -WorkingDirectory $AppHome `
        -RedirectStandardOutput $BootstrapOutFile `
        -RedirectStandardError $BootstrapErrFile `
        -WindowStyle Hidden `
        -PassThru

    Set-Content -Path $PidFile -Value $process.Id -NoNewline
    Start-Sleep -Seconds 2

    try {
        Get-Process -Id $process.Id -ErrorAction Stop | Out-Null
        Write-Info "启动成功 (PID: $($process.Id))"
    } catch {
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        Fail "启动失败，进程已退出。查看日志: $BootstrapOutFile / $BootstrapErrFile"
    }
}

function Do-Stop {
    param([bool]$ForceStop)

    if (-not (Test-Running)) {
        Write-Info "客户端未运行"
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        return
    }

    $pid = Get-Pid
    $timeout = [int]$env:SHUTDOWN_TIMEOUT

    if ($ForceStop) {
        Write-Info "强制停止 (PID: $pid)"
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        Write-Info "已停止"
        return
    }

    Write-Info "停止中 (PID: $pid, 超时: ${timeout}s)..."
    Stop-Process -Id $pid -ErrorAction SilentlyContinue

    for ($elapsed = 0; $elapsed -lt $timeout; $elapsed++) {
        if (-not (Test-Running)) {
            Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
            Write-Info "已停止 (${elapsed}s)"
            return
        }
        Start-Sleep -Seconds 1
        if ((($elapsed + 1) % 5) -eq 0) {
            Write-Info ("  等待中... ({0}/{1}s)" -f ($elapsed + 1), $timeout)
        }
    }

    Write-Info "停止超时，执行强制终止"
    Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
    Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
    Write-Info "已强制停止"
}

function Do-Status {
    if (-not (Test-Running)) {
        Write-Host "$AppName 未运行"
        if (Test-Path $PidFile) {
            Write-Host "  提示: 发现残留 PID 文件: $PidFile"
        }
        exit 1
    }

    $pid = Get-Pid
    $process = Get-Process -Id $pid -ErrorAction Stop
    Write-Host "$AppName 正在运行 (PID: $pid)"
    Write-Host ""
    $process | Select-Object Id, ProcessName, StartTime, CPU, WorkingSet | Format-List
}

function Do-Logs {
    param([bool]$FollowLog)

    $target = $AppLogFile
    if (-not (Test-Path $target)) {
        $target = $BootstrapOutFile
    }
    if (-not (Test-Path $target)) {
        Fail "日志文件不存在: $AppLogFile 或 $BootstrapOutFile"
    }

    if ($FollowLog) {
        Get-Content -Path $target -Tail 50 -Wait
        return
    }

    Get-Content -Path $target -Tail 50
}

function Show-Help {
    @"
JNDC Client 管理脚本

用法:
  .\jndc.ps1 <command> [options]

命令:
  start   [--dev]     启动客户端
  stop    [--force]   停止客户端
  restart [--dev]     重启客户端
  status              查看运行状态
  logs    [-f]        查看日志
  help                显示帮助

运行前置:
  运行配置读取自: $RuntimeConfigFile
  若文件不存在，请参考: $TemplateConfigFile
"@ | Write-Host
}

Load-EnvFile

if ($RemainingArgs -contains "--dev") {
    $env:JNDC_MODE = "dev"
}

$RunMode = Get-RunMode
Initialize-Defaults -RunMode $RunMode

switch ($Command) {
    "start" {
        Do-Start
    }
    "stop" {
        Do-Stop -ForceStop ($RemainingArgs -contains "--force")
    }
    "restart" {
        Do-Stop -ForceStop ($RemainingArgs -contains "--force")
        Start-Sleep -Seconds 1
        Do-Start
    }
    "status" {
        Do-Status
    }
    "logs" {
        Do-Logs -FollowLog ($RemainingArgs -contains "-f")
    }
    "help" {
        Show-Help
    }
    "-h" {
        Show-Help
    }
    "--help" {
        Show-Help
    }
    default {
        Write-Host "未知命令: $Command"
        Show-Help
        exit 1
    }
}
