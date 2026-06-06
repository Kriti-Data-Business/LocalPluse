package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BusinessPreset(
    val type: String,
    val name: String,
    val suburb: String,
    val capacity: Int,
    val inFreeTramZone: Boolean
)

val BUSINESS_PRESETS = listOf(
    BusinessPreset(
        type = "Specialty Coffee House",
        name = "Lygon Street Roasters",
        suburb = "Carlton VIC",
        capacity = 80,
        inFreeTramZone = true
    ),
    BusinessPreset(
        type = "Traditional Pub / Sports Bar",
        name = "The Corner Post Hotel",
        suburb = "Richmond VIC (Near MCG)",
        capacity = 350,
        inFreeTramZone = true
    ),
    BusinessPreset(
        type = "Nightclub / Music Venue",
        name = "Basement Beat Club",
        suburb = "Fitzroy VIC (Brunswick St)",
        capacity = 500,
        inFreeTramZone = false
    ),
    BusinessPreset(
        type = "Airbnb / Short-Term Rental",
        name = "Southbank Skyline Loft",
        suburb = "Southbank VIC (Near Crown)",
        capacity = 6,
        inFreeTramZone = true
    ),
    BusinessPreset(
        type = "Fine Dining Restaurant",
        name = "Flinders Lane Bistro",
        suburb = "Melbourne CBD",
        capacity = 120,
        inFreeTramZone = true
    )
)

sealed interface ApiState {
    object Idling : ApiState
    object Loading : ApiState
    data class Success(val output: LocalPulseOutput, val rawJson: String) : ApiState
    data class Error(val message: String) : ApiState
}

class LocalPulseViewModel : ViewModel() {

    // Simulation states
    var selectedPreset = MutableStateFlow(BUSINESS_PRESETS[0])
    var customName = MutableStateFlow("")
    var customSuburb = MutableStateFlow("")
    var customCapacity = MutableStateFlow("")
    var customInFreeTramZone = MutableStateFlow(false)

    // Weather Simulation
    var temperature = MutableStateFlow(38.5f)
    var weatherCondition = MutableStateFlow("Extreme Heatwave")
    var hasExtremeWeatherAlert = MutableStateFlow(true)

    // Event Simulation
    var eventMcg = MutableStateFlow(false)
    var eventRodLaver = MutableStateFlow(true)
    var eventComedyFest = MutableStateFlow(false)
    var eventMoomba = MutableStateFlow(false)
    var eventWhatsOnMarket = MutableStateFlow(false)

    // Public Transport Simulation
    var disruptBusesReplacingTrains = MutableStateFlow(true)
    var disruptTramWorks = MutableStateFlow(false)
    var disruptRoadClosure = MutableStateFlow(false)

    // Victorian Calendar States
    var isPublicHoliday = MutableStateFlow(false)
    var holidayName = MutableStateFlow("AFL Grand Final Friday")
    var academicStage = MutableStateFlow("Standard Semester Weeks")

    // API Key State
    var apiKeyOverride = MutableStateFlow("")

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Idling)
    val apiState: StateFlow<ApiState> = _apiState.asStateFlow()

    init {
        // Load API Key from BuildConfig initially
        apiKeyOverride.value = BuildConfig.GEMINI_API_KEY
    }

    fun applyPreset(preset: BusinessPreset) {
        selectedPreset.value = preset
        customName.value = preset.name
        customSuburb.value = preset.suburb
        customCapacity.value = preset.capacity.toString()
        customInFreeTramZone.value = preset.inFreeTramZone
        
        // Dynamic simulation defaults based on category choice to make demo exciting
        when (preset.type) {
            "Traditional Pub / Sports Bar" -> {
                eventMcg.value = true
                eventRodLaver.value = false
                temperature.value = 24.0f
                weatherCondition.value = "Mild & Clear"
                hasExtremeWeatherAlert.value = false
            }
            "Specialty Coffee House" -> {
                eventMcg.value = false
                eventRodLaver.value = false
                temperature.value = 14.5f
                weatherCondition.value = "Freezing Winter Wind"
                hasExtremeWeatherAlert.value = false
                academicStage.value = "RMIT / UniMelb O-Week"
            }
            "Nightclub / Music Venue" -> {
                eventRodLaver.value = true
                disruptBusesReplacingTrains.value = true
                temperature.value = 18.0f
                weatherCondition.value = "Pleasant Spring Day"
                hasExtremeWeatherAlert.value = false
            }
            "Airbnb / Short-Term Rental" -> {
                isPublicHoliday.value = true
                holidayName.value = "Melbourne Cup Day"
                temperature.value = 32.0f
                weatherCondition.value = "Pleasant Spring Day"
                hasExtremeWeatherAlert.value = false
            }
            else -> {
                temperature.value = 38.5f
                weatherCondition.value = "Extreme Heatwave"
                hasExtremeWeatherAlert.value = true
            }
        }
    }

    fun generateOperationalGuidelines() {
        val key = apiKeyOverride.value.trim()
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            _apiState.value = ApiState.Error("Please enter a valid Gemini API Key to run the simulation.")
            return
        }

        _apiState.value = ApiState.Loading

        viewModelScope.launch {
            try {
                val activeEvents = mutableListOf<String>()
                if (eventMcg.value) activeEvents.add("AFL Blockbuster Match at MCG")
                if (eventRodLaver.value) activeEvents.add("Major Arena Concert at Rod Laver Arena")
                if (eventComedyFest.value) activeEvents.add("Melbourne International Comedy Festival Session")
                if (eventMoomba.value) activeEvents.add("Moomba Carnival Street Parade")
                if (eventWhatsOnMarket.value) activeEvents.add("What's On Melbourne Street Food Pop-up")

                val activeDisruptions = mutableListOf<String>()
                if (disruptBusesReplacingTrains.value) activeDisruptions.add("Buses replacing trains on primary Eastern Lines")
                if (disruptTramWorks.value) activeDisruptions.add("Tram route detours due to major CBD overhead track works")
                if (disruptRoadClosure.value) activeDisruptions.add("Vehicle closures along major dining strip corridors")

                val businessName = customName.value.ifEmpty { selectedPreset.value.name }
                val businessType = selectedPreset.value.type
                val locationSuburb = customSuburb.value.ifEmpty { selectedPreset.value.suburb }
                val spaceLimit = customCapacity.value.toIntOrNull() ?: selectedPreset.value.capacity
                val freeTramZone = customInFreeTramZone.value

                val prompt = """
                    Analyze the following operating parameters for a local Melbourne business and synthesize optimized operational plans.
                    
                    --- BUSINESS PROFILE ---
                    Name: $businessName
                    Type: $businessType
                    Location Suburb: $locationSuburb
                    Inside Free Tram Zone: $freeTramZone
                    Limit Capacity: $spaceLimit

                    --- BOM METEOROLOGICAL TELEMETRY ---
                    Temperature: ${temperature.value}°C
                    Condition Summary: ${weatherCondition.value}
                    Extreme Warning Active: ${hasExtremeWeatherAlert.value}

                    --- MASS URBAN POPULATION DRAWS ---
                    Active Events in Suburb: ${activeEvents.joinToString(", ")}

                    --- METROPOLITAN PUBLIC TRANSIT ISSUES ---
                    Active Disruptions: ${activeDisruptions.joinToString(", ")}

                    --- CALENDAR METRICS ---
                    Victorian Public Holiday: ${if (isPublicHoliday.value) holidayName.value else "No"}
                    Academic Stage: ${academicStage.value}

                    --- REQUIRED ACTIONABLE SCHEMA ---
                    You must return a valid JSON object matching this structure:
                    {
                      "situational_analysis": "A detailed 3-sentence evaluation of how the overlapping factors (extreme weather, transit issues, stadium crowds, and holidays) uniquely influence this specific category of local merchant.",
                      "staffing_action_plan": {
                        "recommended_level": "Scale Up" | "Status Quo" | "Scale Down" | "Emergency Standby",
                        "actionable_instructions": [
                          "Specific staffing changes (kitchen/FOH count, security/RSA marshal needs, guest host/check-in, or cleaning staff coordination).",
                          "How to handle staff logistics considering train replacement buses/delays affecting staff commutes.",
                          "Specific health, safety, or work environment tips under the current weather/crowd environment."
                        ]
                      },
                      "inventory_action_plan": {
                        "recommended_action": "Aggressive Stocking" | "Slight Increase" | "Normal Operations" | "Reduce & Liquidate" | "Amenities Adjustment",
                        "target_items": [
                          "Perishable stock updates (milk, fresh produce, cold/hot drink preps) reflecting current weather.",
                          "Supplies tailored to local crowd profiles.",
                          "Emergency item prep (backup items, water, fans, ponchos etc.)."
                        ]
                      },
                      "promotional_marketing_campaign": {
                        "campaign_focus": "Theme of marketing campaign based on current conditions.",
                        "hook_line": "A punchy, geo-targeted Melbourne-centric marketing hook line.",
                        "promotional_offer": "Highly actionable instore promotion or dynamic pricing adjustment.",
                        "hours_modification": "Adjusted opening or booking hours to maximize revenue or reduce unused labor."
                      },
                      "google_ad_campaign": {
                        "headline": "Punchy Google Search Ad headline (max 30 characters).",
                        "description": "Engaging ad description (max 90 characters).",
                        "target_keywords": ["keyword1", "keyword2", "keyword3"],
                        "imagen_prompt": "A detailed photographic or flat illustration design prompt (1:1 ratio) specifically optimized for Imagen 3 model to generate advertisement matching the local Melbourne vibe."
                      }
                    }

                    Ensure your response is valid JSON only. Do not wrap in markdown tags like ```json or ```.
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(
                        text = "You are 'LocalPulse', an expert retail, hospitality, and real-estate operations analyst. " +
                               "Your response must be a single correctly formatted JSON object. Return only valid raw JSON."
                    ))),
                    generationConfig = GeminiGenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.2f
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    LocalPulseApiClient.apiService.generateContent(key, request)
                }

                val candidates = response.candidates
                if (candidates.isNullOrEmpty()) {
                    _apiState.value = ApiState.Error("No candidates returned from Gemini. Please double check your API key.")
                    return@launch
                }

                val rawText = candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (rawText.isNullOrEmpty()) {
                    _apiState.value = ApiState.Error("The API returned an empty response. Let's try again.")
                    return@launch
                }

                // Clean backticks
                var cleanedText = rawText.trim()
                if (cleanedText.startsWith("```json")) {
                    cleanedText = cleanedText.removePrefix("```json")
                } else if (cleanedText.startsWith("```")) {
                    cleanedText = cleanedText.removePrefix("```")
                }
                if (cleanedText.endsWith("```")) {
                    cleanedText = cleanedText.removeSuffix("```")
                }
                cleanedText = cleanedText.trim()

                val parsedOutput = LocalPulseApiClient.parseLocalPulseOutput(cleanedText)
                if (parsedOutput != null) {
                    _apiState.value = ApiState.Success(parsedOutput, cleanedText)
                } else {
                    _apiState.value = ApiState.Error("Failed to parse Gemini JSON output structure. Raw text was: \n$rawText")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _apiState.value = ApiState.Error("API call failed: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
