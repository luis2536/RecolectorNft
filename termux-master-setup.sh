#!/data/data/com.termux/files/usr/bin/bash
# =====================================================================
# Nexus PlayNixies Bot - Master Automation Script para Termux
# Este script instala Node.js, PM2, configura el servidor local y 
# arranca los procesos de recolección automática 24/7.
# =====================================================================

echo "========================================================="
echo " [Nexus] Iniciando instalación automática en Termux..."
echo "========================================================="

# 1. Actualizar paquetes y repositorios de Termux
pkg update -y && pkg upgrade -y

# 2. Instalar dependencias esenciales (Node.js, Git, Curl, Python)
pkg install nodejs git curl python build-essential -y

# 3. Verificar instalación de Node.js y NPM
node -v
npm -v

# 4. Crear directorio de trabajo para el backend del bot
mkdir -p ~/nexus-nixies-backend
cd ~/nexus-nixies-backend

# 5. Inicializar proyecto Node.js e instalar PM2 y express
npm init -y
npm install express body-parser cors axios pm2

# 6. Crear el servidor local del backend (server.js)
cat << 'EOF' > server.js
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { exec } = require('child_process');

const app = express();
app.use(bodyParser.json());
app.use(cors());

const PORT = 3000;

// Almacén en memoria de sesiones y cuentas bot
let activeSessions = [];

// Endpoint de estado para la App Android
app.get('/api/status', (req, res) => {
    exec('pm2 jlist', (error, stdout, stderr) => {
        if (error) {
            return res.json({ success: false, error: error.message, processes: [] });
        }
        try {
            const pm2List = JSON.parse(stdout);
            const processes = pm2List.map(p => ({
                name: p.name,
                status: p.pm2_env.status,
                memory: p.monit.memory,
                cpu: p.monit.cpu
            }));
            res.json({ success: true, processes });
        } catch (e) {
            res.json({ success: false, error: "Error parsing PM2 output", processes: [] });
        }
    });
});

// Endpoint para recibir sesiones desde el Navegador In-App
app.post('/api/sync-session', (req, res) => {
    const { sessionData } = req.body;
    console.log('[Nexus Backend] Sesión recibida:', sessionData);
    activeSessions.push({ timestamp: new Date(), data: sessionData });
    res.json({ success: true, message: "Sesión sincronizada correctamente." });
});

// Endpoint de control de procesos PM2 (start, stop, restart)
app.post('/api/control', (req, res) => {
    const { action, processName, scriptPath } = req.body;
    let cmd = '';
    if (action === 'start') {
        cmd = `pm2 start ${scriptPath || 'worker.js'} --name "${processName || 'nexus-bot'}"`;
    } else if (action === 'stop') {
        cmd = `pm2 stop "${processName}"`;
    } else if (action === 'restart') {
        cmd = `pm2 restart "${processName}"`;
    }

    exec(cmd, (error, stdout, stderr) => {
        if (error) {
            return res.json({ success: false, error: error.message });
        }
        res.json({ success: true, output: stdout });
    });
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`[Nexus] Servidor backend PlayNixies corriendo en http://127.0.0.1:${PORT}`);
});
EOF

# 7. Crear un script worker de ejemplo para automatizar recolección en PlayNixies
cat << 'EOF' > worker.js
console.log('[Nexus Worker] Bot de recolección PlayNixies iniciado...');
setInterval(() => {
    console.log('[Nexus Worker] Ejecutando barrido automático de energía, Arena y Aventura en PlayNixies... [' + new Date().toISOString() + ']');
}, 15000);
EOF

# 8. Iniciar el servidor con PM2 para ejecución persistente 24/7
pm2 start server.js --name "nexus-backend-server"
pm2 start worker.js --name "nexus-playnixies-bot"
pm2 save
pm2 startup

echo "========================================================="
echo " ¡INSTALACIÓN COMPLETADA CON ÉXITO!"
echo " El servidor backend y el bot de PlayNixies están corriendo."
echo " Accede desde tu App Android en: http://127.0.0.1:3000"
echo "========================================================="
