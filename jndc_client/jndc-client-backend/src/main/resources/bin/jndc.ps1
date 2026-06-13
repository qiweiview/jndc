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
    $appPid = Get-Pid
    if (-not $appPid) {
        return $false
    }

    try {
        Get-Process -Id $appPid -ErrorAction Stop | Out-Null
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

    Fail "JDK $JavaRequiredMajor+ not found. Set JAVA_HOME or add java.exe to PATH."
}

function Get-JavaMajorVersion {
    param([string]$JavaCommand)

    $versionOutput = & cmd.exe /d /c ('"{0}" -version 2>&1' -f $JavaCommand)
    $versionLine = $versionOutput | Select-Object -First 1
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

    Fail "Missing runtime config: $RuntimeConfigFile. Create config.yml from $TemplateConfigFile first."
}

function Preflight {
    param([string]$JavaCommand)

    if (-not (Test-Path $JavaCommand)) {
        Fail "Java not found: $JavaCommand"
    }
    if (-not (Test-Path $LibDir)) {
        Fail "lib directory not found: $LibDir"
    }
    if (-not (Test-Path $ConfDir)) {
        Fail "conf directory not found: $ConfDir"
    }

    Require-RuntimeConfig

    $majorVersion = Get-JavaMajorVersion -JavaCommand $JavaCommand
    if (-not $majorVersion) {
        Fail "Cannot parse Java version: $JavaCommand"
    }
    if ($majorVersion -lt $JavaRequiredMajor) {
        Fail "Detected Java $majorVersion, but JNDC requires JDK $JavaRequiredMajor+."
    }

    if (-not (Get-ChildItem -Path (Join-Path $LibDir "jndc_client*.jar") -ErrorAction SilentlyContinue)) {
        Fail "jndc_client.jar not found. Run mvn package first."
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

    $classpath = "{0};{1}" -f $ConfDir, (Join-Path $LibDir "*")
    $args = @(
        ("-Dapp.home={0}" -f $AppHome),
        ("-Dapp.mode={0}" -f $RunMode),
        $env:JVM_XMS,
        $env:JVM_XMX,
        $env:JVM_METASPACE
    )

    if ($env:GC_LOG -eq "true") {
        $gcLogFile = Join-Path $LogDir "gc.log"
        $args += ("-Xlog:gc*:file={0}:time,uptime,level,tags:filecount=5,filesize=10M" -f $gcLogFile)
    }

    if ($env:JVM_OPTS) {
        $args += ($env:JVM_OPTS -split "\s+" | Where-Object { -not [string]::IsNullOrWhiteSpace($_) })
    }

    $args += @("-classpath", $classpath, $AppMain)
    return $args
}

function Show-Banner {
    Write-Host 'JNDC Client'
    Write-Host ("Mode: {0} | Version: {1}" -f $RunMode, (Get-Date -Format 'yyyyMMdd'))
    Write-Host ""
}

function Do-Start {
    if (Test-Running) {
        Write-Info ("Client already running (PID: {0})" -f (Get-Pid))
        return
    }

    $javaCommand = Find-Java
    Preflight -JavaCommand $javaCommand

    New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
    Rotate-BootstrapLog
    Show-Banner

    Write-Info "Starting..."
    Write-Info ("  Main class:    {0}" -f $AppMain)
    Write-Info ("  Runtime config:{0}{1}" -f ' ', $RuntimeConfigFile)
    Write-Info ("  App log:       {0}" -f $AppLogFile)
    Write-Info ("  Bootstrap log: {0}" -f $BootstrapOutFile)

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
        Write-Info ("Started successfully (PID: {0})" -f $process.Id)
    } catch {
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        Fail "Startup failed. Check logs: $BootstrapOutFile / $BootstrapErrFile"
    }
}

function Do-Stop {
    param([bool]$ForceStop)

    if (-not (Test-Running)) {
        Write-Info "Client is not running"
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        return
    }

    $appPid = Get-Pid
    $timeout = [int]$env:SHUTDOWN_TIMEOUT

    if ($ForceStop) {
        Write-Info ("Force stopping (PID: {0})" -f $appPid)
        Stop-Process -Id $appPid -Force -ErrorAction SilentlyContinue
        Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
        Write-Info "Stopped"
        return
    }

    Write-Info ("Stopping (PID: {0}, timeout: {1}s)..." -f $appPid, $timeout)
    Stop-Process -Id $appPid -ErrorAction SilentlyContinue

    for ($elapsed = 0; $elapsed -lt $timeout; $elapsed++) {
        if (-not (Test-Running)) {
            Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
            Write-Info ("Stopped ({0}s)" -f $elapsed)
            return
        }
        Start-Sleep -Seconds 1
        if ((($elapsed + 1) % 5) -eq 0) {
            Write-Info ("  Waiting... ({0}/{1}s)" -f ($elapsed + 1), $timeout)
        }
    }

    Write-Info "Stop timed out. Forcing process termination."
    Stop-Process -Id $appPid -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 1
    Remove-Item -Path $PidFile -Force -ErrorAction SilentlyContinue
    Write-Info "Stopped"
}

function Do-Status {
    if (-not (Test-Running)) {
        Write-Host "$AppName is not running"
        if (Test-Path $PidFile) {
            Write-Host "  Stale PID file found: $PidFile"
        }
        exit 1
    }

    $appPid = Get-Pid
    $process = Get-Process -Id $appPid -ErrorAction Stop
    Write-Host "$AppName is running (PID: $appPid)"
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
        Fail "Log file not found: $AppLogFile or $BootstrapOutFile"
    }

    if ($FollowLog) {
        Get-Content -Path $target -Tail 50 -Wait
        return
    }

    Get-Content -Path $target -Tail 50
}

function Show-Help {
    @"
JNDC Client management script

Usage:
  .\jndc.ps1 <command> [options]

Commands:
  start   [--dev]     Start client
  stop    [--force]   Stop client
  restart [--dev]     Restart client
  status              Show status
  logs    [-f]        Show logs
  help                Show help

Runtime config:
  Read from: $RuntimeConfigFile
  If missing, create it from: $TemplateConfigFile
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
        Write-Host "Unknown command: $Command"
        Show-Help
        exit 1
    }
}
