import express from 'express';
import http from 'http';
import { WebSocketServer } from 'ws';

import { GoogleAuth } from 'google-auth-library';
import { TextServiceClient } from '@google-ai/generativelanguage';

import dotenv from 'dotenv';
dotenv.config();

const app = express();
const PORT = process.env.PORT || 10000;

app.get('/', (req, res) => {
  res.send('Express + WebSocket + Gemini is live');
});

const server = http.createServer(app);
const wss = new WebSocketServer({ server });

async function start() {
  const auth = new GoogleAuth({
    keyFile: process.env.GOOGLE_APPLICATION_CREDENTIALS,
    scopes: 'https://www.googleapis.com/auth/generative-language'
  });

  const geminiClient = new TextServiceClient({
    authClient: await auth.getClient(),
  });

  async function callGemini(fixedEvents = [], flexibleTasks = []) {
    const promptText = `Given the following fixed events: ${JSON.stringify(
      fixedEvents
    )}, and the following flexible tasks: ${JSON.stringify(
      flexibleTasks
    )}, generate a complete weekly schedule in JSON with fields: day, start, end, and title.`;

    const request = {
      model: 'chat-bison-001',
      prompt: { text: promptText },
      temperature: 0.7,
      maxTokens: 512,
    };

    const [response] = await geminiClient.generateText(request);
    return response.candidates?.[0]?.output || 'No response from Gemini.';
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
}

start().catch((err) => {
  console.error('Failed to start server:', err);
});
