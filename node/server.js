import express from 'express';
import http from 'http';
import { WebSocketServer } from 'ws';

import { GoogleGenAI } from '@google/genai';
import dotenv from 'dotenv';
dotenv.config();

const app = express();
const PORT = process.env.PORT || 10000;

app.get('/', (req, res) => {
  res.send('Express + WebSocket + Gemini is live');
});

const server = http.createServer(app);
const wss = new WebSocketServer({ server });

// Initialize the new Gemini client
const ai = new GoogleGenAI({});

async function callGemini(fixedEvents = [], flexibleTasks = []) {
  const promptText = `Given the following fixed events: ${JSON.stringify(
    fixedEvents
  )}, and the following flexible tasks: ${JSON.stringify(
    flexibleTasks
  )}, generate a complete weekly schedule in JSON with fields: day, start, end, and title.`;

  const response = await ai.models.generateContent({
    model: 'gemini-2.5-flash',
    contents: promptText,
  });

  return response.text || 'No response from Gemini.';
}

wss.on('connection', (ws) => {
  console.log('Client connected');

  ws.on('message', async (message) => {
    let data;
    try {
      data = JSON.parse(message);
    } catch {
      ws.send(JSON.stringify({ error: 'Invalid JSON' }));
      return;
    }

    if (data.type === 'generate-schedule') {
      const { fixedEvents = [], flexibleTasks = [] } = data.payload;

      try {
        const geminiResponse = await callGemini(fixedEvents, flexibleTasks);
        ws.send(
          JSON.stringify({
            type: 'schedule-response',
            payload: geminiResponse,
          })
        );
      } catch (err) {
        console.error('Gemini call failed:', err);
        ws.send(
          JSON.stringify({ error: 'Failed to generate schedule from Gemini' })
        );
      }
    } else {
      ws.send(JSON.stringify({ error: 'Unknown message type' }));
    }
  });

  ws.on('close', () => {
    console.log('Client disconnected');
  });
});

server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
