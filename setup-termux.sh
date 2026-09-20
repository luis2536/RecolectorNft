#!/bin/bash
# ==============================================================================
# NEXUS WEB3 PORTFOLIO - SCRIPT MASTER DE INSTALACIÓN AUTOMÁTICA PARA TERMUX
# ==============================================================================

clear
echo "=================================================="
echo "  🚀 INICIANDO INSTALACIÓN DE NEXUS WEB3 BACKEND  "
echo "=================================================="

# 1. Actualizar repositorios y paquetes base
echo "[1/4] Actualizando entorno Termux..."
pkg update && pkg upgrade -y

# 2. Instalar Node.js, Git y dependencias esenciales
echo "[2/4] Instalando Node.js y herramientas del sistema..."
pkg install nodejs git curl -y

# 3. Instalar PM2 de forma global para gestión de procesos 24/7
echo "[3/4] Instalando gestor de procesos PM2..."
npm install -g pm2

# 4. Configurar e iniciar el servidor local en la carpeta backend
echo "[4/4] Configurando el servidor Node.js y dependencias..."
if [ -d "backend" ]; then
    cd backend
else
    mkdir -p backend
    cd backend
fi

# Generar package.json robusto con soporte HTTP/Proxies y Express
cat << 'EOF' > package.json
{
  "name": "nexus-web3-backend",
  "version": "1.0.0",
  "main": "server.js",
  "scripts": {
    "start": "node server.js"
  },
  "dependencies": {
    "express": "^4.18.2",
    "cors": "^2.8.5",
    "axios": "^1.6.0",
    "socks-proxy-agent": "^8.0.2",
    "user-agents": "^1.1.286"
  }
}
EOF

# Instalar dependencias locales
npm install

# Iniciar servidor mediante PM2
pm2 delete nexus-backend 2>/dev/null || true
pm2 start server.js --name "nexus-backend"
pm2 save

echo "=================================================="
echo "  ✅ ¡INSTALACIÓN COMPLETADA EXITOSAMENTE!       "
echo "=================================================="
echo "🌐 Servidor local activo en: http://127.0.0.1:3000"
echo "📊 Para monitorear procesos: pm2 monit"
echo "📜 Para ver logs del servidor: pm2 logs nexus-backend"
echo "🔄 Para reiniciar el servidor: pm2 restart nexus-backend"
echo "=================================================="
