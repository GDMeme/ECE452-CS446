package com.example.schedula.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object XpService {
    private const val BASE_URL = "https://schedula-api.onrender.com"

    fun sendXpToBackend(userId: String, xp: Int, activity: String, context: Context) {
        val url = "$BASE_URL/api/xp"
        val json = JSONObject().apply {
            put("user_id", userId)
            put("xp", xp)
            put("activity", activity)
        }

        val request = JsonObjectRequest(Request.Method.POST, url, json,
            { response ->
                val totalXp = response.optInt("total_xp", -1)
                Toast.makeText(context, "XP updated: $totalXp", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.e("XpService", "Failed to send XP", error)
                Toast.makeText(context, "Failed to send XP", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    fun fetchLeaderboard(context: Context, onResult: (List<Pair<String, Int>>) -> Unit) {
        val url = "$BASE_URL/api/leaderboard"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                val leaderboard = mutableListOf<Pair<String, Int>>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val userId = obj.optString("user_id", "unknown")
                    val xp = obj.optInt("xp", 0)
                    leaderboard.add(Pair(userId, xp))
                }
                onResult(leaderboard)
            },
            { error ->
                Log.e("XpService", "Failed to load leaderboard", error)
                Toast.makeText(context, "Failed to load leaderboard", Toast.LENGTH_SHORT).show()
                onResult(emptyList())
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}
