package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

// Response models
@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

// LocalPulse Actionable Output models
@JsonClass(generateAdapter = true)
data class LocalPulseOutput(
    val situational_analysis: String,
    val staffing_action_plan: StaffingActionPlan,
    val inventory_action_plan: InventoryActionPlan,
    val promotional_marketing_campaign: PromotionalMarketingCampaign,
    val google_ad_campaign: GoogleAdCampaign
)

@JsonClass(generateAdapter = true)
data class StaffingActionPlan(
    val recommended_level: String,
    val actionable_instructions: List<String>
)

@JsonClass(generateAdapter = true)
data class InventoryActionPlan(
    val recommended_action: String,
    val target_items: List<String>
)

@JsonClass(generateAdapter = true)
data class PromotionalMarketingCampaign(
    val campaign_focus: String,
    val hook_line: String,
    val promotional_offer: String,
    val hours_modification: String
)

@JsonClass(generateAdapter = true)
data class GoogleAdCampaign(
    val headline: String,
    val description: String,
    val target_keywords: List<String>,
    val imagen_prompt: String
)
