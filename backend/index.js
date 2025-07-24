const express = require("express");
const cors = require("cors");

const app = express();
app.use(cors());              // Allow requests from your Android app
app.use(express.json());      // Parse JSON request bodies

// In-memory leaderboard
let leaderboard = {};

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

// GET /api/leaderboard — Return sorted leaderboard
app.get("/api/leaderboard", (req, res) => {
  const sorted = Object.entries(leaderboard)
    .map(([user_id, xp]) => ({ user_id, xp }))
    .sort((a, b) => b.xp - a.xp);  // Sort by XP descending

  res.json(sorted);
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`✅ Backend running on port ${PORT}`);
});
