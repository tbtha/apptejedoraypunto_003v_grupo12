# Script para generar APK firmado de VivitaSol
# Ejecutar desde la raíz del proyecto

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Generador de APK Firmado - VivitaSol  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar Java 17
Write-Host "1. Configurando Java 17..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

# Verificar Java
$javaVersion = & java -version 2>&1 | Select-Object -First 1
Write-Host "   $javaVersion" -ForegroundColor Green
Write-Host ""

# Verificar keystore
Write-Host "2. Verificando keystore..." -ForegroundColor Yellow
if (Test-Path "app\vivitasol-release-key.jks") {
    Write-Host "   ✓ Keystore encontrado" -ForegroundColor Green
} else {
    Write-Host "   ✗ Keystore NO encontrado" -ForegroundColor Red
    Write-Host "   Por favor, genere el keystore primero" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Limpiar build anterior (opcional)
$limpiar = Read-Host "¿Desea limpiar el build anterior? (S/N)"
if ($limpiar -eq "S" -or $limpiar -eq "s") {
    Write-Host "3. Limpiando build anterior..." -ForegroundColor Yellow
    .\gradlew clean
    Write-Host ""
}

# Generar APK
Write-Host "4. Generando APK firmado..." -ForegroundColor Yellow
Write-Host "   Esto puede tomar varios minutos..." -ForegroundColor Gray
Write-Host ""

$startTime = Get-Date
.\gradlew assembleRelease --no-daemon

if ($LASTEXITCODE -eq 0) {
    $endTime = Get-Date
    $duration = $endTime - $startTime
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  ✓ APK GENERADO EXITOSAMENTE" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    
    # Información del APK
    $apkPath = "app\build\outputs\apk\release\app-release.apk"
    if (Test-Path $apkPath) {
        $apkInfo = Get-Item $apkPath
        $apkSizeMB = [math]::Round($apkInfo.Length / 1MB, 2)
        
        Write-Host "Ubicación: $apkPath" -ForegroundColor Cyan
        Write-Host "Tamaño: $apkSizeMB MB" -ForegroundColor Cyan
        Write-Host "Fecha: $($apkInfo.LastWriteTime)" -ForegroundColor Cyan
        Write-Host "Tiempo de compilación: $($duration.Minutes)m $($duration.Seconds)s" -ForegroundColor Cyan
        Write-Host ""
        
        # Preguntar si desea abrir la carpeta
        $abrir = Read-Host "¿Desea abrir la carpeta del APK? (S/N)"
        if ($abrir -eq "S" -or $abrir -eq "s") {
            Invoke-Item "app\build\outputs\apk\release"
        }
        
        # Preguntar si desea copiar al escritorio
        $copiar = Read-Host "¿Desea copiar el APK al escritorio? (S/N)"
        if ($copiar -eq "S" -or $copiar -eq "s") {
            $destino = "$env:USERPROFILE\Desktop\VivitaSol-v1.0.apk"
            Copy-Item $apkPath $destino
            Write-Host ""
            Write-Host "✓ APK copiado a: $destino" -ForegroundColor Green
        }
    }
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  ✗ ERROR AL GENERAR APK" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Revise los errores anteriores" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Presione cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
