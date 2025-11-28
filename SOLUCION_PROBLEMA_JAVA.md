# Solución al Problema de Ejecución de Tests

## Problema
El proyecto tiene problemas al ejecutar tests debido a:
1. **Java 24 no es compatible** con Android Gradle Plugin
2. Archivos del directorio `build` bloqueados por procesos

## Solución Aplicada

### 1. Instalación de Java 17 (EN PROCESO)
Se está descargando e instalando Java 17, que es la versión recomendada para Android.

Una vez completada la instalación:
1. Cierra VS Code completamente
2. Vuelve a abrirlo
3. El script ya habrá configurado `JAVA_HOME` automáticamente

### 2. Después de instalar Java 17

Ejecuta estos comandos en la terminal de VS Code:

```powershell
# Verificar que Java 17 está configurado
java -version
# Debe mostrar: java version "17.x.x"

# Limpiar el proyecto
.\gradlew clean

# Ejecutar los tests
.\gradlew :app:testDebugUnitTest --tests "com.vivitasol.carcasamvvm.*"
```

## Alternativa Manual

Si la instalación automática falla:

1. **Descargar Java 17 manualmente:**
   - Visita: https://www.oracle.com/java/technologies/downloads/#java17
   - Descarga: "Windows x64 Installer"
   - Ejecuta el instalador

2. **Configurar JAVA_HOME:**
   ```powershell
   [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
   ```

3. **Reiniciar VS Code** y ejecutar los tests

## Comandos Útiles

```powershell
# Detener procesos de Gradle
.\gradlew --stop

# Ver estado de los daemons de Gradle
.\gradlew --status

# Ejecutar tests con más información
.\gradlew :app:testDebugUnitTest --tests "com.vivitasol.carcasamvvm.*" --info

# Ejecutar todos los tests
.\gradlew :app:testDebugUnitTest
```

## Nota Importante
- Java 17 es la versión LTS recomendada para desarrollo Android
- Java 24 es muy nueva y tiene problemas de compatibilidad
- Una vez instalado Java 17, todos los problemas deberían resolverse
