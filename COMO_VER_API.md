# 🔍 Cómo Verificar que la API Externa Funciona

## ✅ Pasos para Ver la Conversión de Monedas

### 1. **Compilar la Aplicación**
```bash
# En Android Studio, haz clic en el botón Run (▶️)
# O ejecuta en terminal:
./gradlew installDebug
```

### 2. **Verificar Internet en el Emulador**
- El emulador debe tener conexión a internet activa
- En Settings > Network, verifica que WiFi esté ON

### 3. **Ir a la Pantalla de Inventario**
- Abre la app
- Navega al menú de Inventario/Productos
- La lista de productos debe cargarse

### 4. **Verificar los Logs**
Abre **Logcat** en Android Studio y busca:
```
💱 Iniciando conversión de precios para X productos
💱 Convirtiendo precio de producto 1: CLP XXXX
✅ USD: $XX.XX
✅ EUR: €XX.XX
💱 Precios convertidos: X productos en USD/EUR
```

### 5. **Qué Deberías Ver en la Pantalla**

#### Banner Verde (si funciona):
```
💱 API Externa Activa: 5 precios convertidos
```

#### En Cada Producto:
```
Producto: Ovillos de Lana
CLP $15000      (precio original en negro/bold)
USD $16.85      (en verde, más pequeño)
EUR €15.42      (en azul, más pequeño)
```

## ❌ Si NO Ves las Conversiones

### Problema 1: Sin Internet
**Síntoma**: No aparece el banner verde ni conversiones
**Solución**: 
- Verifica internet en el emulador
- En la app, pulsa el botón de recarga (🔄)

### Problema 2: API No Responde
**Síntoma**: En Logcat ves errores de conexión
**Solución**: 
- La API gratuita tiene límite de 1,500 requests/mes
- Verifica en: https://www.exchangerate-api.com/

### Problema 3: Backend No Funciona
**Síntoma**: No se cargan productos
**Solución**:
- Inicia tu backend local en puerto 8082
- Verifica que `http://10.0.2.2:8082/api/productos` responda

## 📱 Ubicación Visual

La conversión aparece en:
- **Pantalla**: Inventario (lista de productos)
- **Ubicación**: Debajo del precio de cada producto
- **Vista**: Solo en modo Desktop/Tablet (pantallas grandes)

## 🔧 Archivos Clave

1. **CurrencyConverter.kt** → Hace la llamada HTTP a la API
2. **InventarioViewModel.kt** → Gestiona los precios convertidos
3. **InventarioView.kt** → Muestra las conversiones

## 🌐 API Utilizada

- **URL**: https://v6.exchangerate-api.com/v6/
- **Endpoint**: `/pair/CLP/USD/{amount}` y `/pair/CLP/EUR/{amount}`
- **Tipo**: API REST pública y gratuita
- **Límite**: 1,500 requests/mes
