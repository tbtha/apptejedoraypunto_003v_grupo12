# APK Firmado - VivitaSol

## Información del APK

- **Archivo:** app-release.apk
- **Ubicación:** `app/build/outputs/apk/release/app-release.apk`
- **Tamaño:** 25.1 MB
- **Fecha:** 28/11/2025
- **Application ID:** com.vivitasol.carcasamvvm
- **Versión:** 1.0 (Code: 1)
- **SDK mínimo:** Android 13 (API 33)

## Configuración de Firma

**Keystore:** app/vivitasol-release-key.jks

- Alias: vivitasol
- Contraseña: vivitasol2024
- Algoritmo: RSA 2048 bits
- Válido hasta: abril 2053

## Archivos Entregados

- APK: `app/build/outputs/apk/release/app-release.apk`
- Keystore: `app/vivitasol-release-key.jks`
- Documentación: GENERACION_APK_FIRMADO.md, KEYSTORE_INFO.md, GUIA_EJECUTAR_TESTS.md
- Scripts: generar-apk.ps1, install-java17.ps1

## Configuración build.gradle.kts

```kotlin
android {
    defaultConfig {
        applicationId = "com.vivitasol.carcasamvvm"
        versionCode = 1
        versionName = "1.0"
    }

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

## Instalación

**Dispositivo Android:**
1. Transferir APK al dispositivo
2. Habilitar "Fuentes desconocidas" en Ajustes → Seguridad
3. Abrir y confirmar instalación

**Con ADB:**
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## Generar nueva versión

1. Actualizar en build.gradle.kts:
```kotlin
versionCode = 2
versionName = "1.1"
```

2. Ejecutar:
```powershell
.\generar-apk.ps1
```

## Notas

- No compartir las contraseñas del keystore
- Mantener backup del archivo .jks
- No subir a repositorios públicos
- Usar el mismo keystore para actualizaciones

---

**Proyecto:** VivitaSol  
**Fecha:** 28/11/2025  
**Grupo:** 12
