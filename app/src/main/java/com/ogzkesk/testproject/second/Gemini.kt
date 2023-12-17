package com.ogzkesk.testproject.second

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow

class Gemini {

    object Model {
        const val PRO = "gemini-pro"
        const val PRO_VISION = "gemini-pro-vision"
        const val EMBEDDING = "embedding-001"
        const val AQA = "aqa"
    }


    val model = GenerativeModel(
        "gemini-pro",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.9f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        ),
    )


    private val state = MutableStateFlow<List<Content>>(emptyList())
    private val chat = model.startChat(state.value)


    suspend fun sendMessage(text: String): String? {

        val response = chat.sendMessage(text)
        state.value = state.value + response.candidates.first().content

        println(response.text)
        println(response.promptFeedback)
        response.candidates.forEach {
            println(it.content.role)
            println(it.content.parts)
            println(it.finishReason)
            println(it.citationMetadata)
            println(it.safetyRatings)
        }

        return response.text
    }

    companion object {
        private const val API_KEY = "AIzaSyAM2idrUpK0BoGZHNtTyqmSH7nVGZTCzCY"
    }
}
