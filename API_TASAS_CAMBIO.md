# 💱 API de Tasas de Cambio - Documentación

## ✅ ¿Qué hace?

Muestra las **tasas de cambio del día** para el Peso Chileno (CLP) a:
- 💵 **USD** (Dólar estadounidense)
- 💶 **EUR** (Euro)

## 📍 ¿Dónde se ve?

### Ubicación en la App:
1. Abre la aplicación
2. Ve a **Inventario**
3. Busca la **tarjeta azul** debajo del Dashboard

### Ejemplo Visual:
```
┌─────────────────────────────────────────────────────────┐
│ 💱 Tasas del Día:                                      │
│                                                         │
│ 💵 1 CLP = $0.001054 USD   💶 1 CLP = €0.000968 EUR   │
└─────────────────────────────────────────────────────────┘
```

## 🔢 ¿Cómo usar las tasas?

Si la tasa muestra: **1 CLP = $0.001054 USD**

Para convertir **CLP $55,000** a dólares:
```
55,000 × 0.001054 = $57.97 USD
```

## 🛠️ Implementación Técnica

### Archivos Modificados:

#### 1. **CurrencyConverter.kt**
```kotlin
suspend fun getTasaUSD(): Double?  // Obtiene tasa CLP → USD
suspend fun getTasaEUR(): Double?  // Obtiene tasa CLP → EUR
```

#### 2. **InventarioViewModel.kt**
```kotlin
val tasaUSD: StateFlow<Double?>    // Estado reactivo de tasa USD
val tasaEUR: StateFlow<Double?>    // Estado reactivo de tasa EUR
```

#### 3. **InventarioView.kt**
```kotlin
TasasCambioCard(tasaUSD, tasaEUR)  // Componente visual
```

## 🌐 API Externa

**Proveedor**: ExchangeRate-API  
**URL**: https://v6.exchangerate-api.com/v6/{API_KEY}/pair/CLP/{CURRENCY}/1  
**Plan**: Gratuito (1,500 requests/mes)  
**Respuesta**:
```json
{
  "result": "success",
  "conversion_rate": 0.001054,
  "conversion_result": 0.001054
}
```

## 📊 Verificación

### En Logcat verás:
```
💱 Obteniendo tasas de cambio del día...
🌐 Obteniendo tasa USD: https://v6.exchangerate-api.com/v6/.../pair/CLP/USD/1
🌐 Tasa USD obtenida: 0.001054
✅ Tasa USD: 1 CLP = $0.001054 USD
🌐 Obteniendo tasa EUR: https://v6.exchangerate-api.com/v6/.../pair/CLP/EUR/1
🌐 Tasa EUR obtenida: 0.000968
✅ Tasa EUR: 1 CLP = €0.000968 EUR
💱 Tasas de cambio obtenidas
```

## ⚡ Ventajas de este Enfoque

✅ **Simple**: Solo 2 llamadas a la API  
✅ **Rápido**: No convierte cada producto individualmente  
✅ **Eficiente**: No consume el límite de la API rápidamente  
✅ **Claro**: El usuario ve las tasas y puede calcular mentalmente  
✅ **Siempre actualizado**: Las tasas se refrescan al abrir Inventario  

## 🔄 Actualización

Las tasas se obtienen automáticamente cuando:
- Se carga la pantalla de Inventario
- Se presiona el botón de recarga (🔄)

## ❓ Troubleshooting

### No aparece la tarjeta azul
- Verifica que el dispositivo/emulador tenga internet
- Revisa Logcat en busca de errores con emoji ❌

### Tasas desactualizadas
- Cierra y vuelve a abrir la pantalla de Inventario
- O presiona el botón de recarga

### Error "API no responde"
- Verifica que no hayas excedido el límite de 1,500 requests/mes
- Comprueba en: https://www.exchangerate-api.com/

## 📅 Estado

✅ **IMPLEMENTADO Y FUNCIONANDO**  
Última actualización: Noviembre 2025
