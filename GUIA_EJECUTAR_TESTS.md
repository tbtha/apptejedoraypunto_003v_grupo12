# Guía para Ejecutar Tests

## Requisitos

- Java 17 instalado
- Gradle configurado

## Ejecutar tests

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
.\gradlew :app:testDebugUnitTest --tests "com.vivitasol.carcasamvvm.*"
```

## Ver resultados

```powershell
Invoke-Item app\build\reports\tests\testDebugUnitTest\index.html
```

## Tests disponibles

### ProductoTest (5 tests)
- Crear producto con valores válidos
- Producto inactivo
- Producto con stock cero
- Producto sin categoría
- Precio positivo

### CategoriaTest (5 tests)
- Crear categoría
- Categoría con descripción null
- Comparar por ID
- Mismo nombre diferente ID
- ToString

### InventarioViewModelTest (4 tests)
- Filtro de categoría
- Búsqueda por nombre
- Limpiar filtros
- Valores iniciales

### ApiClientTest (2 tests)
- Base URL configurada
- Timeout configurado

### TasaCambioServiceTest (4 tests)
- Tasa CLP a USD
- Tasa USD a EUR
- Convertir montos
- Manejo de errores

## Comandos útiles

Ejecutar test específico:
```powershell
.\gradlew :app:testDebugUnitTest --tests "com.vivitasol.carcasamvvm.models.ProductoTest"
```

Limpiar y ejecutar:
```powershell
.\gradlew clean :app:testDebugUnitTest
```

Con más información:
```powershell
.\gradlew :app:testDebugUnitTest --info
```

## Problemas comunes

**Error de JAVA_HOME:**
Configurar Java 17:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
```

**Error de build bloqueado:**
```powershell
.\gradlew --stop
```

**Configurar Java permanentemente:**
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
```
