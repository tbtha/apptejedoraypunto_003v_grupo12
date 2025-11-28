# Información del Keystore

## Datos del keystore

- **Archivo:** app/vivitasol-release-key.jks
- **Alias:** vivitasol
- **Contraseña del store:** vivitasol2024
- **Contraseña del key:** vivitasol2024

## Certificado

- Organización: VivitaSol
- Ubicación: Santiago, RM, CL
- Algoritmo: RSA 2048 bits
- Válido hasta: abril 2053

## Huellas SHA

```
SHA1: 7F:C3:5D:ED:BF:7D:F9:09:E2:88:43:17:61:51:BB:18:57:DC:73:0D
SHA256: 24:AF:FF:17:C6:84:21:38:08:5E:19:CE:BF:11:1E:CC:C4:A5:71:74:CB:DA:63:BC:02:0C:DA:46:C5:39:00:00
```

## Comandos útiles

Ver información del keystore:
```bash
keytool -list -v -keystore app/vivitasol-release-key.jks -storepass vivitasol2024
```

## Importante

- NO compartir este archivo públicamente
- Mantener backup en lugar seguro
- NO subir a GitHub o repositorios públicos
- Agregar `*.jks` al .gitignore
- Usar el mismo keystore para TODAS las actualizaciones

Si se pierde el keystore, no se podrán publicar actualizaciones de la app.
