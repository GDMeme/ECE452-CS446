const express = require("express");
const app = express();
app.use(express.json());

app.post("/api/xp", (req, res) => {
  const { user_id, xp, activity } = req.body;
  res.send({ status: "success" });
});

app.get("/api/leaderboard", async (req, res) => {
  res.json([
    { user_id: "abc", xp: 120 },
    { user_id: "xyz", xp: 95 }
  ]);
});

app.listen(3000, () => console.log("Server running on port 3000"));
