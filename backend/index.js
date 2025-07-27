const express = require("express");
const cors = require("cors");

const app = express();
app.use(cors());              // Allow requests from your Android app
app.use(express.json());      // Parse JSON request bodies

// In-memory leaderboard with mock data
let leaderboard = {
  "alice": 1200,
  "bob": 950,
  "carol": 1500,
  "dave": 700,
  "eve": 1100,
  "frank": 600,
  "grace": 1400,
  "heidi": 500,
  "ivan": 1300,
  "judy": 900,
  "mallory": 400
};

// POST /api/xp — Add XP for a user
app.post("/api/xp", (req, res) => {
  const { user_id, xp, activity } = req.body;

  if (!user_id || typeof xp !== 'number') {
    return res.status(400).json({ error: "Invalid input" });
  }

  // Add XP to user
  if (!leaderboard[user_id]) {
    leaderboard[user_id] = 0;
  }

  leaderboard[user_id] += xp;

  console.log(`${user_id} earned ${xp} XP from ${activity}`);
  res.json({ status: "XP added", total_xp: leaderboard[user_id] });
});

// GET /api/leaderboard — Return top 10 leaderboard
app.get("/api/leaderboard", (req, res) => {
  const sorted = Object.entries(leaderboard)
    .map(([user_id, xp]) => ({ user_id, xp }))
    .sort((a, b) => b.xp - a.xp)
    .slice(0, 10);  // Limit to top 10

  res.json(sorted);
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`✅ Backend running on port ${PORT}`);
});
