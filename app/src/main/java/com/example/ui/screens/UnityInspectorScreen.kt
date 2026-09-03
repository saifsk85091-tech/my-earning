package com.example.ui.screens

import android.app.Activity
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdState
import com.example.ads.SdkInitStatus
import com.example.ads.UnityAdStats
import com.example.ads.UnityAdsManager
import com.example.data.UserRewardState
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PurpleBoost
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RewardsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UnityInspectorScreen(
  viewModel: RewardsViewModel,
  userState: UserRewardState,
  sdkStatus: SdkInitStatus,
  adStats: UnityAdStats,
  rewardedState: AdState,
  interstitialState: AdState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val app = context.applicationContext as Application

  var gameIdInput by remember(userState.unityGameId) { mutableStateOf(userState.unityGameId) }
  var testModeSwitch by remember(userState.isTestMode) { mutableStateOf(userState.isTestMode) }
  var rewardedInput by remember(userState.rewardedPlacement) { mutableStateOf(userState.rewardedPlacement) }
  var interstitialInput by remember(userState.interstitialPlacement) { mutableStateOf(userState.interstitialPlacement) }
  var bannerInput by remember(userState.bannerPlacement) { mutableStateOf(userState.bannerPlacement) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Live Status Overview Card
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = EmeraldLight,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Unity Ads Inspector",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  when (sdkStatus) {
                    is SdkInitStatus.Initialized -> EmeraldDark
                    is SdkInitStatus.Initializing -> GoldAccent.copy(alpha = 0.3f)
                    is SdkInitStatus.Failed -> Color(0xFF7F1D1D)
                    else -> Color(0xFF1E293B)
                  }
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = when (sdkStatus) {
                  is SdkInitStatus.Initialized -> "ACTIVE"
                  is SdkInitStatus.Initializing -> "INITIALIZING"
                  is SdkInitStatus.Failed -> "ERROR"
                  else -> "STANDBY"
                },
                style = MaterialTheme.typography.labelSmall,
                color = when (sdkStatus) {
                  is SdkInitStatus.Initialized -> EmeraldLight
                  is SdkInitStatus.Initializing -> GoldAccent
                  is SdkInitStatus.Failed -> Color(0xFFFCA5A5)
                  else -> TextMuted
                },
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Detail rows
          InspectorInfoRow(label = "Game ID", value = userState.unityGameId)
          InspectorInfoRow(
            label = "Ad Serving Mode",
            value = if (userState.isTestMode) "Test Mode (Safe Sandbox)" else "PRODUCTION LIVE (Real Money)"
          )
          InspectorInfoRow(label = "Rewarded Placement", value = userState.rewardedPlacement)
          InspectorInfoRow(label = "Interstitial Placement", value = userState.interstitialPlacement)
          InspectorInfoRow(label = "Banner Placement", value = userState.bannerPlacement)

          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF1E293B))
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "Dashboard Configuration Tip:",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "If Unity Ads logs show 'Header bidding load invocation failed: adMarkup is missing', set 'Mediation Partner' to 'Unity Ads only' in dashboard.unity.com or switch below to standard waterfall placements.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }

          if (sdkStatus is SdkInitStatus.Failed) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF450A0A))
                .padding(10.dp)
            ) {
              Text(
                text = "Notice: ${sdkStatus.error} - ${sdkStatus.message}\n(Ad simulation preview is automatically available)",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFCA5A5),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // 2. Configuration Form
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "CONFIGURATION",
            style = MaterialTheme.typography.labelMedium,
            color = GoldAccent,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          // Game ID Input
          Text(
            text = "Unity Game ID (Android)",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = gameIdInput,
            onValueChange = { gameIdInput = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF0F172A),
              unfocusedContainerColor = Color(0xFF0F172A),
              focusedBorderColor = EmeraldPrimary,
              unfocusedBorderColor = ObsidianBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("inspector_game_id_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Placement Presets
          Text(
            text = "Placement Quick Presets",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                rewardedInput = "Rewarded_Android"
                interstitialInput = "Interstitial_Android"
                bannerInput = "Banner_Android"
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Modern Placements", fontSize = 11.sp, color = EmeraldLight)
            }
            OutlinedButton(
              onClick = {
                rewardedInput = "rewardedVideo"
                interstitialInput = "video"
                bannerInput = "banner"
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Legacy/Waterfall", fontSize = 11.sp, color = GoldAccent)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Rewarded Placement Input
          Text(
            text = "Rewarded Placement ID",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = rewardedInput,
            onValueChange = { rewardedInput = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF0F172A),
              unfocusedContainerColor = Color(0xFF0F172A),
              focusedBorderColor = EmeraldPrimary,
              unfocusedBorderColor = ObsidianBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("inspector_rewarded_placement_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Interstitial Placement Input
          Text(
            text = "Interstitial Placement ID",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = interstitialInput,
            onValueChange = { interstitialInput = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF0F172A),
              unfocusedContainerColor = Color(0xFF0F172A),
              focusedBorderColor = EmeraldPrimary,
              unfocusedBorderColor = ObsidianBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("inspector_interstitial_placement_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Banner Placement Input
          Text(
            text = "Banner Placement ID",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = bannerInput,
            onValueChange = { bannerInput = it },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF0F172A),
              unfocusedContainerColor = Color(0xFF0F172A),
              focusedBorderColor = EmeraldPrimary,
              unfocusedBorderColor = ObsidianBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("inspector_banner_placement_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Test Mode Toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Test Mode",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = if (testModeSwitch) "Test ads enabled for debugging without policy risk" else "LIVE PRODUCTION ads enabled to earn real revenue",
                style = MaterialTheme.typography.bodySmall,
                color = if (testModeSwitch) GoldAccent else EmeraldLight
              )
            }
            Switch(
              checked = testModeSwitch,
              onCheckedChange = { testModeSwitch = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = GoldAccent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = EmeraldPrimary
              ),
              modifier = Modifier.testTag("test_mode_switch")
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              viewModel.updateSettings(
                app,
                gameIdInput,
                testModeSwitch,
                rewardedInput,
                interstitialInput,
                bannerInput
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("save_unity_settings_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Apply & Re-initialize Unity SDK",
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        }
      }
    }

    // 3. Ad Unit Diagnostic Triggers
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "LIVE AD UNIT CONTROLS",
            style = MaterialTheme.typography.labelMedium,
            color = EmeraldLight,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          // Rewarded Video test
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Rewarded Video",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "State: ${formatAdState(rewardedState)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }

            Button(
              onClick = {
                if (activity != null) viewModel.watchRewardedAd(activity, 100L)
              },
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("test_show_rewarded")
            ) {
              Text(text = "Test Show", fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Interstitial test
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Interstitial Video",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "State: ${formatAdState(interstitialState)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }

            Button(
              onClick = {
                if (activity != null) viewModel.watchInterstitialAd(activity, 40L)
              },
              colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("test_show_interstitial")
            ) {
              Text(text = "Test Show", fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = { viewModel.reloadAds() },
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Reload / Preload All Placements", color = TextSecondary)
          }
        }
      }
    }

    // 4. Lifetime Ad Statistics
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "SESSION AD TELEMETRY",
            style = MaterialTheme.typography.labelMedium,
            color = PurpleBoost,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
          )
          Spacer(modifier = Modifier.height(12.dp))

          InspectorInfoRow(label = "Total Ads Displayed", value = "${adStats.totalAdsShown}")
          InspectorInfoRow(label = "Rewarded Ads Completed", value = "${adStats.rewardedCompleted}")
          InspectorInfoRow(label = "Interstitials Completed", value = "${adStats.interstitialCompleted}")
          InspectorInfoRow(
            label = "Last Ad Served",
            value = if (adStats.lastAdTimestamp > 0) {
              SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(adStats.lastAdTimestamp)) + " (${adStats.lastAdPlacement})"
            } else "None yet"
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun InspectorInfoRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      color = Color.White,
      fontWeight = FontWeight.SemiBold
    )
  }
}

private fun formatAdState(state: AdState): String {
  return when (state) {
    is AdState.Idle -> "Idle / Loaded"
    is AdState.Loading -> "Loading Ad..."
    is AdState.Ready -> "Ready to Show"
    is AdState.Showing -> "Currently Playing"
    is AdState.Error -> "Not Ready (${state.message})"
  }
}
