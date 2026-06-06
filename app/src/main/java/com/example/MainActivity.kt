@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.*
import com.example.ui.BUSINESS_PRESETS
import com.example.ui.BusinessPreset
import com.example.ui.ApiState
import com.example.ui.LocalPulseViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: LocalPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFFF2F2F7), // Classic iOS light system background gray
                    topBar = {
                        Column {
                            CenterAlignedTopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = Color(0xFF007AFF), // Apple Blue
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LocalPulse",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            letterSpacing = (-0.3).sp,
                                            color = Color(0xFF1C1C1E)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = Color.White
                                )
                            )
                            // Elegant thin iOS bottom divider hairline
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(Color(0xFFE5E5EA))
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LocalPulseAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LocalPulseAppContent(viewModel: LocalPulseViewModel) {
    val scrollState = rememberScrollState()
    val apiState by viewModel.apiState.collectAsState()

    // Form states
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val customName by viewModel.customName.collectAsState()
    val customSuburb by viewModel.customSuburb.collectAsState()
    val customCapacity by viewModel.customCapacity.collectAsState()
    val customInFreeTramZone by viewModel.customInFreeTramZone.collectAsState()

    // Weather Simulation
    val temperature by viewModel.temperature.collectAsState()
    val weatherCondition by viewModel.weatherCondition.collectAsState()
    val hasExtremeWeatherAlert by viewModel.hasExtremeWeatherAlert.collectAsState()

    // Event Simulation
    val eventMcg by viewModel.eventMcg.collectAsState()
    val eventRodLaver by viewModel.eventRodLaver.collectAsState()
    val eventComedyFest by viewModel.eventComedyFest.collectAsState()
    val eventMoomba by viewModel.eventMoomba.collectAsState()
    val eventWhatsOnMarket by viewModel.eventWhatsOnMarket.collectAsState()

    // Public Transport Simulation
    val disruptBusesReplacingTrains by viewModel.disruptBusesReplacingTrains.collectAsState()
    val disruptTramWorks by viewModel.disruptTramWorks.collectAsState()
    val disruptRoadClosure by viewModel.disruptRoadClosure.collectAsState()

    // Victorian Calendar States
    val isPublicHoliday by viewModel.isPublicHoliday.collectAsState()
    val holidayName by viewModel.holidayName.collectAsState()
    val academicStage by viewModel.academicStage.collectAsState()

    // API Key control
    val apiKeyOverride by viewModel.apiKeyOverride.collectAsState()
    var showConfigDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Security Notice / API Key setup banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API key Status",
                        tint = if (apiKeyOverride.isNotEmpty() && apiKeyOverride != "MY_GEMINI_API_KEY") Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (apiKeyOverride.isNotEmpty() && apiKeyOverride != "MY_GEMINI_API_KEY") "Gemini API Integrated" else "Gemini API Key Required",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (apiKeyOverride.isNotEmpty() && apiKeyOverride != "MY_GEMINI_API_KEY") "Active. Tap to view security info." else "Provide API key in settings or Secrets panel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(
                    onClick = { showConfigDialog = true },
                    modifier = Modifier.testTag("api_config_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Section header styled like an elegant uppercase iOS list segment description
        Text(
            text = "🏪 1. MERCHANT PROFILE PRESET",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93),
            letterSpacing = 1.1.sp,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
        )

        // Dropdown alternatives: Zero-click horizontal tab selector in gorgeous Apple glass style!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                 .horizontalScroll(rememberScrollState())
                 .padding(bottom = 2.dp),
             horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
             BUSINESS_PRESETS.forEach { preset ->
                 val isSelected = selectedPreset.type == preset.type
                 Box(
                     modifier = Modifier
                         .clip(RoundedCornerShape(20.dp))
                         .background(if (isSelected) Color(0xFF007AFF) else Color(0xFFE5E5EA).copy(alpha = 0.5f))
                          .clickable { viewModel.applyPreset(preset) }
                          .padding(horizontal = 14.dp, vertical = 8.dp)
                 ) {
                     Text(
                          text = preset.type,
                          color = if (isSelected) Color.White else Color(0xFF38383A),
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp
                     )
                 }
             }
        }

        // Consolidated Merchant Profile White Card Group (Apple inspired)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active configuration summary
                Column {
                    Text(
                        text = "ACTIVE BASE PRESET:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E8E93)
                    )
                    Text(
                        text = "${selectedPreset.name} (📍 ${selectedPreset.suburb}, Cap: ${selectedPreset.capacity})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1C1C1E)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { viewModel.customName.value = it },
                        label = { Text("Business Name", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.2f).testTag("business_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customSuburb,
                        onValueChange = { viewModel.customSuburb.value = it },
                        label = { Text("Suburb", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.8f).testTag("suburb_input"),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = customCapacity,
                        onValueChange = { viewModel.customCapacity.value = it },
                        label = { Text("Max capacity", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.width(120.dp).testTag("capacity_input"),
                        singleLine = true
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Switch(
                            checked = customInFreeTramZone,
                            onCheckedChange = { viewModel.customInFreeTramZone.value = it },
                            modifier = Modifier.scale(0.85f).testTag("free_tram_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Free Tram Zone?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1C1C1E)
                        )
                    }
                }
            }
        }

        // Section header for environmental simulation parameters
        Text(
            text = "📡 2. LIVE TELEMETRY SIMULATOR",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93),
            letterSpacing = 1.1.sp,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
        )

        // Weather Simulation Group (Rounded iOS card)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weather Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color(0xFF0A84FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Australian BOM Weather",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Temperature Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Temperature Setting", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                        val isHot = temperature > 35f
                        Text(
                            text = "${String.format("%.1f", temperature)}°C",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isHot) Color(0xFFFF3B30) else Color(0xFF007AFF)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = temperature,
                        onValueChange = { viewModel.temperature.value = it },
                        valueRange = 0f..45f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF007AFF),
                            inactiveTrackColor = Color(0xFFE5E5EA),
                            thumbColor = Color.White
                        ),
                        modifier = Modifier.testTag("temp_slider")
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Weather Condition Picker Button
                var expandedWeather by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sky Conditions", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF2F2F7))
                                .clickable { expandedWeather = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = weatherCondition,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF007AFF)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expandedWeather,
                            onDismissRequest = { expandedWeather = false }
                        ) {
                            listOf("Extreme Heatwave", "Severe Torrential Rain", "Freezing Winter Wind", "Mild & Clear", "Pleasant Spring Day").forEach { cond ->
                                DropdownMenuItem(
                                    text = { Text(cond, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.weatherCondition.value = cond
                                        viewModel.hasExtremeWeatherAlert.value = (temperature > 37 || cond.contains("Extreme") || cond.contains("Severe"))
                                        expandedWeather = false
                                    }
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Switch warning alert
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Active Extreme Warning Alarm?",
                        fontSize = 12.sp,
                        color = Color(0xFF3A3A3C)
                    )
                    Switch(
                        checked = hasExtremeWeatherAlert,
                        onCheckedChange = { viewModel.hasExtremeWeatherAlert.value = it },
                        modifier = Modifier.scale(0.85f).testTag("weather_alert_switch")
                    )
                }
            }
        }

        // Live Events group
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Landscape,
                        contentDescription = null,
                        tint = Color(0xFFFF9500),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Melbourne Sports & Festivities",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    val eventsList = listOf(
                        Triple("MCG Game (85k+)", eventMcg) { b: Boolean -> viewModel.eventMcg.value = b },
                        Triple("Rod Laver Arena", eventRodLaver) { b: Boolean -> viewModel.eventRodLaver.value = b },
                        Triple("Comedy Festival", eventComedyFest) { b: Boolean -> viewModel.eventComedyFest.value = b },
                        Triple("Moomba Carnival", eventMoomba) { b: Boolean -> viewModel.eventMoomba.value = b },
                        Triple("Street Food", eventWhatsOnMarket) { b: Boolean -> viewModel.eventWhatsOnMarket.value = b }
                    )

                    eventsList.forEach { (title, liveState, action) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (liveState) Color(0xFF5856D6).copy(alpha = 0.15f) else Color(0xFFF2F2F7))
                                .border(
                                    width = 1.dp,
                                    color = if (liveState) Color(0xFF5856D6) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { action(!liveState) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (liveState) Color(0xFF5856D6) else Color(0xFF3A3A3C)
                            )
                        }
                    }
                }
            }
        }

        // Transportation and Infrastructure block
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transit & Infrastructure Delays",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Item 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Buses replacing trains", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Switch(
                        checked = disruptBusesReplacingTrains,
                        onCheckedChange = { viewModel.disruptBusesReplacingTrains.value = it },
                        modifier = Modifier.scale(0.85f)
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Item 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tram overhead track repairs", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Switch(
                        checked = disruptTramWorks,
                        onCheckedChange = { viewModel.disruptTramWorks.value = it },
                        modifier = Modifier.scale(0.85f)
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                // Item 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dining corridor road closures", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Switch(
                        checked = disruptRoadClosure,
                        onCheckedChange = { viewModel.disruptRoadClosure.value = it },
                        modifier = Modifier.scale(0.85f)
                    )
                }
            }
        }

        // Calendar Group
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Victorian Calendar Phase",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Is Public Holiday?", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Switch(
                        checked = isPublicHoliday,
                        onCheckedChange = { viewModel.isPublicHoliday.value = it },
                        modifier = Modifier.scale(0.85f)
                    )
                }

                if (isPublicHoliday) {
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))
                    OutlinedTextField(
                        value = holidayName,
                        onValueChange = { viewModel.holidayName.value = it },
                        label = { Text("Holiday Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                var expandedUni by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("University Phase", fontSize = 12.sp, color = Color(0xFF3A3A3C))
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF2F2F7))
                                .clickable { expandedUni = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = academicStage,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF007AFF)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF007AFF),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expandedUni,
                            onDismissRequest = { expandedUni = false }
                        ) {
                            listOf("Standard Semester Weeks", "RMIT / UniMelb O-Week", "Exam Preparation Period", "Semester Break").forEach { stage ->
                                DropdownMenuItem(
                                    text = { Text(stage, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.academicStage.value = stage
                                        expandedUni = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Large solid iOS style task runner button
        Button(
            onClick = { viewModel.generateOperationalGuidelines() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF) // Classic Apple Blue action accent
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate Local Guidelines",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = (-0.1).sp
                )
            }
        }

        // Live engine response state machine with smooth transition
        Crossfade(targetState = apiState) { state ->
            when (state) {
                is ApiState.Idling -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFC7C7CC) // Silver
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Standing Ready to Analyze",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E8E93)
                            )
                            Text(
                                "Configure context dials above and hit Generate.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC7C7CC)
                            )
                        }
                    }
                }
                is ApiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF007AFF),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Synthesizing micro-intelligence plan...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1C1E)
                              )
                            Text(
                                "Querying weather indexes, stadiums, & transit disruptions...",
                                fontSize = 11.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }
                }
                is ApiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEB)),
                        border = BorderStroke(0.5.dp, Color(0xFFFF3B30))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFFF3B30),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Guideline Synthesis Failed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFFF3B30)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    state.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF3A3A3C)
                                )
                            }
                        }
                    }
                }
                is ApiState.Success -> {
                    LocalPulseDashboardResults(
                        output = state.output,
                        suburbName = customSuburb.ifEmpty { selectedPreset.suburb }
                    )
                }
            }
        }
    }

    // Config Setup Dialog
    if (showConfigDialog) {
        AlertDialog(
            onDismissRequest = { showConfigDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFF007AFF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini System Config", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "To run real predictive live simulations, you must configure a Gemini API key. " +
                                "Configure this through the Secrets panel in AI Studio or override it below:",
                        fontSize = 13.sp,
                        color = Color(0xFF3A3A3C)
                    )

                    OutlinedTextField(
                        value = apiKeyOverride,
                        onValueChange = { viewModel.apiKeyOverride.value = it },
                        label = { Text("Gemini API Key") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE5E5EA)))

                    Text(
                        text = "Security Warning: Avoid hardcoding API keys in final production builds as APKs can be decompiled to extract elements. Use the secure AI Studio Secrets panel whenever possible.",
                        fontSize = 11.sp,
                        color = Color(0xFFFF3B30),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showConfigDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                ) {
                    Text("Apply Changes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun LocalPulseDashboardResults(output: LocalPulseOutput, suburbName: String) {
    // Elegant localized checked state tracking for interactive checklists
    // Using output as a key ensures that when a new merchant plan is generated, the checklists reset automatically!
    val completedStaffing = remember(output) { mutableStateMapOf<String, Boolean>() }
    val completedInventory = remember(output) { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Section Header styled like an elegant uppercase iOS Settings section label
        Text(
            text = "📈 DECISION INSIGHTS & ACTIONS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8E8E93), // iOS caption gray
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        // 1. Detailed Situational Analysis Box - iOS Toast Warning style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("situational_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF007AFF), // Apple Blue
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Situational Diagnosis",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = output.situational_analysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A3A3C),
                    lineHeight = 20.sp
                )
            }
        }

        // Row for staffing & inventory operational guidelines with interactive checklists
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            
            // Staffing plan Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("staffing_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = Color(0xFF5856D6), // iOS Purple
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Staffing Action Worksheet",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF1C1C1E)
                            )
                        }

                        // Badge showing resource guidance
                        val staffLevel = output.staffing_action_plan.recommended_level
                        val badgeBg = when (staffLevel) {
                            "Scale Up" -> Color(0xFFFF9500).copy(alpha = 0.12f)
                            "Scale Down" -> Color(0xFFFF3B30).copy(alpha = 0.12f)
                            "Emergency Standby" -> Color(0xFFFF3B30).copy(alpha = 0.15f)
                            else -> Color(0xFF34C759).copy(alpha = 0.12f)
                        }
                        val badgeText = when (staffLevel) {
                            "Scale Up" -> Color(0xFFFF9500)
                            "Scale Down" -> Color(0xFFFF3B30)
                            "Emergency Standby" -> Color(0xFFFF3B30)
                            else -> Color(0xFF34C759)
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = staffLevel,
                                color = badgeText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "TAP STEPS BELOW TO CHECK OFF DECISIONS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E8E93),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // List instructions as beautifully interactive checked list items
                    output.staffing_action_plan.actionable_instructions.forEachIndexed { index, instruction ->
                        val isChecked = completedStaffing[instruction] ?: false
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { completedStaffing[instruction] = !isChecked }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Stunning iOS style rounded checking ring
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isChecked) Color(0xFF34C759) else Color.Transparent
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isChecked) Color(0xFF34C759) else Color(0xFFC7C7CC),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = instruction,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = if (isChecked) Color(0xFF8E8E93) else Color(0xFF3A3A3C),
                                style = androidx.compose.ui.text.TextStyle(
                                    textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (index < output.staffing_action_plan.actionable_instructions.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(Color(0xFFE5E5EA))
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            // Inventory logistics Card with interactive check items
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inventory_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = Color(0xFF34C759), // iOS Green
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Inventory Logistics",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF1C1C1E)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE5F7ED), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = output.inventory_action_plan.recommended_action,
                                color = Color(0xFF34C759),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "STOCK UPDATES & DEPLOYMENT CODES:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E8E93),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    output.inventory_action_plan.target_items.forEachIndexed { index, item ->
                        val isChecked = completedInventory[item] ?: false
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { completedInventory[item] = !isChecked }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isChecked) Color(0xFF34C759) else Color.Transparent
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isChecked) Color(0xFF34C759) else Color(0xFFC7C7CC),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = item,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = if (isChecked) Color(0xFF8E8E93) else Color(0xFF3A3A3C),
                                style = androidx.compose.ui.text.TextStyle(
                                    textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (index < output.inventory_action_plan.target_items.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.5.dp)
                                    .background(Color(0xFFE5E5EA))
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Promotional marketing campaign card in classic iOS clean style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("promotional_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFFFF9500), // iOS Orange
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Promotional Dispatch",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF1C1C1E)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E))) {
                            append("Slogan focus: ")
                        }
                        append(output.promotional_marketing_campaign.campaign_focus)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A3A3C)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Quote bubbles mimicking clean Apple-like messenger nodes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 4.dp))
                        .background(Color(0xFFE5E5EA).copy(alpha = 0.4f))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            "CUSTOMER DISPATCH HOOK CONCEPT:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${output.promotional_marketing_campaign.hook_line}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF1C1C1E),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("PROMO RATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8E8E93))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(output.promotional_marketing_campaign.promotional_offer, fontSize = 12.sp, color = Color(0xFF1C1C1E), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF2F2F7), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("STORE HOURS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8E8E93))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(output.promotional_marketing_campaign.hours_modification, fontSize = 12.sp, color = Color(0xFF1C1C1E), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Simulated Google Ads Mock with stunning Safari mobile-like header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("google_ads_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column {
                // Header bar mimicking iOS/Safari Google search header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F7))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "google.com.au",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E8E93)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Ad Sponsored",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1C1E)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.AdsClick,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "https://${suburbName.split(" ")[0].lowercase()}.com.au/local-pulse",
                        fontSize = 11.sp,
                        color = Color(0xFF5F6368)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = output.google_ad_campaign.headline,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A0DAB)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = output.google_ad_campaign.description,
                        fontSize = 12.sp,
                        color = Color(0xFF3C4043),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Keywords
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisSpacing = 6.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        output.google_ad_campaign.target_keywords.forEach { keyword ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF1F3F4), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "#$keyword",
                                    fontSize = 10.sp,
                                    color = Color(0xFF5F6368),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Imagen Studio styled with beautiful iOS preview panel layout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("imagen_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(0.5.dp, Color(0xFFE5E5EA))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = Color(0xFFFF2D55), // iOS Pink Accent
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Imagen Creative Canvas",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF1C1C1E)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "AI IMAGEN PROMPT GENERATED CONCEPT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8E8E93),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = output.google_ad_campaign.imagen_prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 18.sp,
                    color = Color(0xFF3A3A3C)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Exquisite visual simulation generator falling back to an elegant vector illustration or coil fetch
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF007AFF), // Apple Blue
                                    Color(0xFF5856D6)  // Apple Purple
                                )
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = output.google_ad_campaign.headline,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp,
                            lineHeight = 21.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Melbourne Local Campaign Creative",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .border(1.0.dp, Color.White, RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TAP TO PREVIEW",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Basic flow layout supporting filter chips or presets.
 */
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth

        val lines = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentLine = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentLineWidth = 0

        placeables.forEach { placeable ->
            val spacing = if (currentLine.isNotEmpty()) mainAxisSpacing.roundToPx() else 0
            if (currentLineWidth + spacing + placeable.width <= layoutWidth) {
                currentLine.add(placeable)
                currentLineWidth += spacing + placeable.width
            } else {
                lines.add(currentLine)
                currentLine = mutableListOf(placeable)
                currentLineWidth = placeable.width
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        val mainSpacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()

        val heights = lines.map { line -> line.maxOfOrNull { it.height } ?: 0 }
        val totalHeight = heights.sum() + (lines.size - 1).coerceAtLeast(0) * crossSpacingPx

        layout(layoutWidth, totalHeight) {
            var currentY = 0
            lines.forEachIndexed { lineIndex, line ->
                var currentX = 0
                line.forEach { placeable ->
                    placeable.placeRelative(currentX, currentY)
                    currentX += placeable.width + mainSpacingPx
                }
                currentY += heights[lineIndex] + crossSpacingPx
            }
        }
    }
}
