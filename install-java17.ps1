# Script para instalar Java 17 para desarrollo Android
Write-Host "=== Instalador de Java 17 ===" -ForegroundColor Green

# URL de descarga de Java 17 (Oracle JDK)
$javaUrl = "https://download.oracle.com/java/17/archive/jdk-17.0.11_windows-x64_bin.exe"
$downloadPath = "$env:TEMP\jdk-17-installer.exe"

Write-Host "`nDescargando Java 17..." -ForegroundColor Yellow
Write-Host "Esto puede tomar unos minutos dependiendo de tu conexion."

try {
    # Descargar el instalador
    Invoke-WebRequest -Uri $javaUrl -OutFile $downloadPath -UseBasicParsing
    
    Write-Host "`nDescarga completada. Ejecutando instalador..." -ForegroundColor Green
    Write-Host "IMPORTANTE: Acepta la instalacion en la ubicacion predeterminada." -ForegroundColor Cyan
    
    # Ejecutar instalador
    Start-Process -FilePath $downloadPath -Wait
    
    Write-Host "`nInstalacion completada!" -ForegroundColor Green
    
    # Buscar la instalación
    $javaPath = Get-ChildItem "C:\Program Files\Java" -Filter "jdk-17*" -Directory | Select-Object -First 1
    
    if ($javaPath) {
        Write-Host "`nJava 17 instalado en: $($javaPath.FullName)" -ForegroundColor Green
        
        # Configurar JAVA_HOME
        Write-Host "`nConfigurando JAVA_HOME..." -ForegroundColor Yellow
        [Environment]::SetEnvironmentVariable("JAVA_HOME", $javaPath.FullName, "User")
        $env:JAVA_HOME = $javaPath.FullName
        
        Write-Host "`nOK JAVA_HOME configurado: $env:JAVA_HOME" -ForegroundColor Green
        Write-Host "`nListo! Cierra y vuelve a abrir VS Code para que los cambios tomen efecto." -ForegroundColor Cyan
    } else {
        Write-Host "`nNo se pudo encontrar la instalacion de Java. Verifica manualmente en C:\Program Files\Java" -ForegroundColor Red
    }
    
    # Limpiar archivo temporal
    Remove-Item -Path $downloadPath -Force -ErrorAction SilentlyContinue
    
} catch {
    Write-Host "`nError durante la descarga o instalacion: $_" -ForegroundColor Red
    Write-Host "`nPor favor, descarga Java 17 manualmente desde:" -ForegroundColor Yellow
    Write-Host "https://www.oracle.com/java/technologies/downloads/#java17" -ForegroundColor Cyan
}

Write-Host "`nPresiona cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
