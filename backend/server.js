const express = require('express');
const cors = require('cors');
const { exec } = require('child_process');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3000;

app.post('/api/process-control', (req, res) => {
    const { action, processName, scriptPath } = req.body;

    if (!action || !processName) {
        return res.status(400).json({ error: 'Action and processName are required' });
    }

    let command = '';
    switch (action) {
        case 'start':
            if (!scriptPath) {
                return res.status(400).json({ error: 'scriptPath is required for start action' });
            }
            command = `pm2 start ${scriptPath} --name ${processName}`;
            break;
        case 'stop':
            command = `pm2 stop ${processName}`;
            break;
        case 'restart':
            command = `pm2 restart ${processName}`;
            break;
        default:
            return res.status(400).json({ error: 'Invalid action' });
    }

    exec(command, (error, stdout, stderr) => {
        if (error) {
            console.error(`exec error: ${error}`);
            return res.status(500).json({ error: stderr || error.message });
        }
        res.json({ success: true, output: stdout });
    });
});

app.get('/api/process-status', (req, res) => {
    exec('pm2 jlist', (error, stdout, stderr) => {
        if (error) {
             return res.status(500).json({ error: stderr || error.message });
        }
        try {
            const list = JSON.parse(stdout);
            const statusList = list.map(p => ({
                name: p.name,
                status: p.pm2_env.status,
                memory: p.monit.memory,
                cpu: p.monit.cpu
            }));
            res.json({ processes: statusList });
        } catch (e) {
            res.status(500).json({ error: 'Failed to parse pm2 output' });
        }
    });
});

app.listen(PORT, () => {
    console.log(`Nexus Web3 Local Task Runner listening on port ${PORT}`);
});
