# Generación de APK Firmado

## Información del APK

- Archivo: app-release.apk
- Tamaño: 25.1 MB
- Ubicación: `app/build/outputs/apk/release/app-release.apk`
- Versión: 1.0
- Application ID: com.vivitasol.carcasamvvm

## Keystore

**Archivo:** app/vivitasol-release-key.jks

- Alias: vivitasol
- Contraseñas: vivitasol2024
- Algoritmo: RSA 2048 bits
- Válido hasta: 2053

### Comando para generar keystore

```bash
keytool -genkey -v -keystore vivitasol-release-key.jks -alias vivitasol -keyalg RSA -keysize 2048 -validity 10000 -storepass vivitasol2024 -keypass vivitasol2024 -dname "CN=VivitaSol, OU=Development, O=VivitaSol, L=Santiago, ST=RM, C=CL"
```

## Configuración build.gradle.kts

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("vivitasol-release-key.jks")
            storePassword = "vivitasol2024"
            keyAlias = "vivitasol"
            keyPassword = "vivitasol2024"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## Generar APK

### Requisitos
- Java 17 instalado
- Keystore en la carpeta app/

### Comando

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
.\gradlew assembleRelease
```

O usar el script:
```powershell
.\generar-apk.ps1
```

## Instalación

### En dispositivo
1. Transferir APK al dispositivo
2. Habilitar instalación de fuentes desconocidas
3. Abrir APK y confirmar

### Con ADB
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## Actualizar versión

1. Editar build.gradle.kts:
```kotlin
versionCode = 2
versionName = "1.1"
```

2. Regenerar APK con el mismo comando

## Notas

- Mantener backup del archivo .jks
- No compartir contraseñas
- No subir keystore a repositorios públicos
- Usar el mismo keystore para todas las versiones
