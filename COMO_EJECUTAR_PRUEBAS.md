# 🧪 Cómo Ejecutar las Pruebas Unitarias

## ✅ Opción 1: Android Studio (RECOMENDADO)

### Pasos:
1. Abre el proyecto en **Android Studio**
2. En el panel izquierdo, navega a:
   ```
   app/src/test/java/com/vivitasol/carcasamvvm/
   ```
3. Haz **clic derecho** en la carpeta `test/java`
4. Selecciona **"Run 'Tests in 'java''"**
5. Verás los resultados en el panel **Run** (abajo)

### Ejecutar un archivo específico:
- Clic derecho en `ProductoTest.kt`
- Selecciona **"Run 'ProductoTest'"**

### Ver cobertura:
- Clic derecho en `test/java`
- Selecciona **"Run 'Tests in 'java'' with Coverage"**

---

## ⚠️ Opción 2: Visual Studio Code (NO RECOMENDADO)

VS Code no está diseñado para proyectos Android/Kotlin. Tendrías que:

1. **Instalar extensiones**:
   - Kotlin Language
   - Gradle for Java
   - Test Runner for Java

2. **Configurar JAVA_HOME**:
   ```powershell
   # En PowerShell (como Administrador)
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   [System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17', 'Machine')
   ```

3. **Ejecutar desde terminal**:
   ```bash
   ./gradlew test
   ```

**Problema actual**: Tu JAVA_HOME apunta a un `.exe` en vez de una carpeta:
```
C:\Users\tbtha\Downloads\jdk-24_windows-x64_bin.exe  ❌ INCORRECTO
```

Debería ser algo como:
```
C:\Program Files\Java\jdk-17  ✅ CORRECTO
```

---

## 🚀 Opción 3: Terminal (Con Gradle)

Si tienes Gradle configurado correctamente:

```bash
# Ejecutar todas las pruebas
./gradlew test

# Ejecutar pruebas específicas
./gradlew test --tests "ProductoTest"

# Ver reporte HTML
./gradlew test
# El reporte estará en: app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 💡 RECOMENDACIÓN

**Usa Android Studio** porque:
- ✅ Configuración automática de Gradle
- ✅ JDK incluido
- ✅ Ejecución visual de pruebas
- ✅ Cobertura de código integrada
- ✅ Debug de pruebas
- ✅ Ver qué prueba falló y por qué

---

## 📊 Verificar que las pruebas existen

En tu proyecto, las pruebas están en:
```
app/src/test/java/com/vivitasol/carcasamvvm/
├── models/
│   ├── ProductoTest.kt (5 pruebas)
│   ├── CategoriaTest.kt (3 pruebas)
│   ├── InventarioTest.kt (4 pruebas)
│   └── FrankfurterResponseTest.kt (3 pruebas)
└── viewmodels/
    ├── InventarioViewModelTest.kt (4 pruebas)
    └── ProductoFormViewModelTest.kt (9 pruebas)

Total: 28 pruebas unitarias
```

---

## ✅ Resultado Esperado

Al ejecutar en Android Studio verás algo como:

```
✓ ProductoTest (5/5 passed)
✓ CategoriaTest (3/3 passed)
✓ InventarioTest (4/4 passed)
✓ FrankfurterResponseTest (3/3 passed)
✓ InventarioViewModelTest (4/4 passed)
✓ ProductoFormViewModelTest (9/9 passed)

28 tests completed in 2.3s
All tests passed ✓
```
