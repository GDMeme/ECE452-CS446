    import express from "express";
    import http from "http";
    import { WebSocketServer } from "ws";
    import { GoogleGenAI } from "@google/genai";

    const app = express();
    const PORT = process.env.PORT || 10000;

    app.get("/", (req, res) => {
        res.send("Express + WebSocket + Gemini is live");
    });

    const server = http.createServer(app);
    const wss = new WebSocketServer({ server });

    // Initialize the new Gemini client
    const ai = new GoogleGenAI({
        apiKey: process.env.GOOGLE_API_KEY,
    });

    async function callGemini(fixedEvents = [], flexibleTasks = []) {
        const promptText = `
        Given the following fixed events: ${JSON.stringify(fixedEvents)},
        and the following flexible tasks: ${JSON.stringify(flexibleTasks)},
        generate a weekly schedule in the form of a pure JSON array (no markdown, no code block, no explanation).
        
        Each event must be an object with **exactly** the following keys:
        - "title": string
        - "startTime": string (24-hour format, e.g., "09:00")
        - "endTime": string (24-hour format, e.g., "10:30")
        - "date": string (format: "YYYY-MM-DD")
        
        Only return events for a **single representative week** (e.g., Monday to Sunday).
        
        Return only the JSON array and nothing else.
        `;

        const response = await ai.models.generateContent({
            model: "gemini-2.5-flash",
            contents: promptText,
        });

        return response.text || "No response from Gemini.";
    }

    wss.on("connection", (ws) => {
        console.log("Client connected");

        ws.on("message", async (message) => {
            let data;
            try {
                data = JSON.parse(message);
            } catch {
                ws.send(JSON.stringify({ error: "Invalid JSON" }));
                return;
            }

            if (data.type === "generate-schedule") {
                const { fixedEvents = [], flexibleTasks = [] } = data.payload;
                
                console.log("Fixed events: ", fixedEvents);
                console.log("Flexible tasks: ", flexibleTasks);
                
                if (fixedEvents.length === 0 && flexibleTasks.length === 0) {
                    ws.send(
                        JSON.stringify({ error: "0 fixed events and 0 flexible tasks found" })
                    );
                }
                try {
                    const geminiResponse = await callGemini(fixedEvents, flexibleTasks);
                    ws.send(
                        JSON.stringify({
                            type: "schedule-response",
                            payload: geminiResponse,
                        })
                    );
                } catch (err) {
                    console.error("Gemini call failed:", err);
                    ws.send(
                        JSON.stringify({ error: "Failed to generate schedule from Gemini" })
                    );
                }
            } else {
                ws.send(JSON.stringify({ error: `Unknown message type: ${data.type}` }));
            }
        });

        ws.on("close", () => {
            console.log("Client disconnected");
        });
    });

    server.listen(PORT, () => {
        console.log(`Server listening on port ${PORT}`);
    });
