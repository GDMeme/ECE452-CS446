import { WebSocketServer } from 'ws';
import { createServer } from 'http';

// Render will inject the PORT via env
const PORT = 10000;
const server = createServer();
server.listen(PORT, () => {
  console.log(`WebSocket server listening on port ${PORT}`);
});

const wss = new WebSocketServer({ server });

wss.on('connection', (ws) => {
  console.log('Client connected');

  ws.on('message', (message) => {
    let data;

    try {
      data = JSON.parse(message);
    } catch (err) {
      ws.send(JSON.stringify({ error: 'Invalid JSON' }));
      return;
    }

    if (data.type === 'generate-schedule') {
      const { fixedEvents = [], flexibleTasks = [] } = data.payload;

      const defaultTime = ['09:00', '10:00', '15:00', '16:00'];
      const schedule = [...fixedEvents];

      for (let task of flexibleTasks) {
        schedule.push({
          day: 'Tuesday',
          start: defaultTime[Math.floor(Math.random() * defaultTime.length)],
          end: defaultTime[Math.floor(Math.random() * defaultTime.length)],
          title: task,
        });
      }

      ws.send(
        JSON.stringify({
          type: 'schedule-response',
          payload: schedule,
        })
      );
    } else {
      ws.send(JSON.stringify({ error: 'Unknown message type' }));
    }
  });

  ws.on('close', () => {
    console.log('Client disconnected');
  });
});
