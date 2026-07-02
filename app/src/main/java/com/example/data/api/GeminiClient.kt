package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(prompt: String, conversationHistory: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default placeholder!")
            return@withContext "Désolé, ma clé API Black-Carl GB n'est pas configurée dans AI Studio. Activez la clé dans les secrets ! 🖤"
        }

        try {
            val contentsArray = JSONArray()

            // Add previous history if any
            for (turn in conversationHistory) {
                val turnObj = JSONObject()
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", turn.first)
                partsArray.put(partObj)
                turnObj.put("parts", partsArray)
                // If true, user. If false, model.
                turnObj.put("role", if (turn.second) "user" else "model")
                contentsArray.put(turnObj)
            }

            // Add the current prompt
            val currentTurnObj = JSONObject()
            val currentPartsArray = JSONArray()
            val currentPartObj = JSONObject()
            currentPartObj.put("text", prompt)
            currentPartsArray.put(currentPartObj)
            currentTurnObj.put("parts", currentPartsArray)
            currentTurnObj.put("role", "user")
            contentsArray.put(currentTurnObj)

            // Setup the main request object
            val requestBodyJson = JSONObject()
            requestBodyJson.put("contents", contentsArray)

            // Add system instructions
            val systemInstructionObj = JSONObject()
            val sysPartsArray = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", "Tu es Black-Carl GB, l'assistant IA officiel du clone WhatsApp 'Black GB'. Ton style est élégant, mystérieux, amical et axé sur le thème sombre de l'application. Réponds de manière concise (max 3 phrases), utilise des émojis sombres ou WhatsApp (🖤, 🟢, 📱, 💬, 🚀) et assiste l'utilisateur avec passion.")
            sysPartsArray.put(sysPartObj)
            systemInstructionObj.put("parts", sysPartsArray)
            requestBodyJson.put("systemInstruction", systemInstructionObj)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response: ${response.code}, body: $errBody")
                    return@withContext "Erreur de connexion avec Black-Carl GB (${response.code}). Veuillez réessayer. 🖤"
                }

                val responseBody = response.body?.string() ?: return@withContext "Réponse vide de Black-Carl GB. 🖤"
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "Pas de réponse.")
                        }
                    }
                }
                "Black-Carl GB n'a pas pu formuler de réponse. 🖤"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            "Une erreur est survenue en contactant l'assistant Black-Carl GB : ${e.localizedMessage}. 🖤"
        }
    }
}
