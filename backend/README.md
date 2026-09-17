# Nexus Web3 Local Task Runner (Backend)

Este es el backend local para "Nexus Web3 Portfolio", encargado de gestionar scripts de automatización mediante PM2 (ej. minado en testnets, monitoreo de redes, cronjobs locales).

## Requisitos
- Node.js (v16 o superior)
- PM2 (`npm install -g pm2`)

## Instalación y Ejecución en Termux / Linux

1. Instalar dependencias:
   ```bash
   npm install
   ```

2. Ejecutar el servidor:
   ```bash
   npm start
   ```
   Para desarrollo continuo, usa:
   ```bash
   npm run dev
   ```

3. El servidor se ejecutará en `http://localhost:3000` (y la aplicación Android se conectará internamente mediante la IP local o `10.0.2.2` en el emulador).

## Endpoints

- `GET /api/process-status`: Lista todos los procesos de PM2 actuales con su uso de CPU y memoria.
- `POST /api/process-control`:
  - `action`: "start", "stop", "restart"
  - `processName`: nombre del proceso en PM2.
  - `scriptPath`: ruta absoluta o relativa al script (solo requerido para "start").
