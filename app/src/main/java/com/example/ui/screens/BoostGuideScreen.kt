package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
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
import java.util.Locale

@Composable
fun BoostGuideScreen(
  unityGameId: String = "6171812",
  modifier: Modifier = Modifier
) {
  var dailyUsers by remember { mutableFloatStateOf(500f) }
  var adsPerUser by remember { mutableFloatStateOf(6f) }
  var avgEcpm by remember { mutableFloatStateOf(18f) }

  // Revenue math: (Daily impressions / 1000) * eCPM
  val dailyImpressions = dailyUsers * adsPerUser
  val dailyEarnings = (dailyImpressions / 1000f) * avgEcpm
  val monthlyEarnings = dailyEarnings * 30f

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header Banner
    item {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E1B4B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4F46E5)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF4338CA)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.RocketLaunch,
                contentDescription = null,
                tint = GoldLight,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "MAXIMIZE UNITY ADS INCOME",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Text(
                text = "Tested strategies for Game ID: $unityGameId",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFC7D2FE)
              )
            }
          }
        }
      }
    }

    // 2. Interactive Revenue & eCPM Calculator
    item {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = null,
              tint = GoldAccent,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Interactive Revenue Estimator",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Projected Earnings Box
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF0F172A))
              .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "DAILY REVENUE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
              Text(
                text = "$${String.format(Locale.US, "%.2f", dailyEarnings)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldLight
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(ObsidianBorder)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "MONTHLY REVENUE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
              Text(
                text = "$${String.format(Locale.US, "%,.0f", monthlyEarnings)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GoldLight
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Slider 1: Active Users
          Text(
            text = "Active Users: ${dailyUsers.toInt()}",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
          )
          Slider(
            value = dailyUsers,
            onValueChange = { dailyUsers = it },
            valueRange = 50f..5000f,
            colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary),
            modifier = Modifier.testTag("slider_users")
          )

          // Slider 2: Ads Watched Per User
          Text(
            text = "Ads Watched Per User: ${adsPerUser.toInt()}",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
          )
          Slider(
            value = adsPerUser,
            onValueChange = { adsPerUser = it },
            valueRange = 1f..20f,
            colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent),
            modifier = Modifier.testTag("slider_ads")
          )

          // Slider 3: eCPM
          Text(
            text = "Average eCPM: $${avgEcpm.toInt()}",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
          )
          Slider(
            value = avgEcpm,
            onValueChange = { avgEcpm = it },
            valueRange = 2f..50f,
            colors = SliderDefaults.colors(thumbColor = PurpleBoost, activeTrackColor = PurpleBoost),
            modifier = Modifier.testTag("slider_ecpm")
          )
        }
      }
    }

    // 3. Pro Secret 1: Tier-1 Geo Focus
    item {
      StrategyCard(
        title = "1. Target Tier-1 Countries",
        subtitle = "USA, UK, Canada & Australia pay 10x-20x more!",
        description = "A user in the United States watching 5 rewarded ads can produce an eCPM of $25 - $45 (earning you ~$0.20 per user daily). Focus your user acquisition or Google Play store listing on English-speaking Tier-1 regions to achieve top payouts.",
        icon = Icons.Default.Public,
        accentColor = ElectricBlue
      )
    }

    // 4. Pro Secret 2: Rewarded Video Domination
    item {
      StrategyCard(
        title = "2. Prioritize Rewarded Video Placement",
        subtitle = "Rewarded Ads beat Banners by 500%",
        description = "Advertisers bid exponentially more when a user voluntarily clicks 'Watch Video' to earn coins, spins, or scratch cards. The Unity placement 'Rewarded_Android' has over 95% video completion rates, which gives your Game ID the highest bidding priority in Unity's ad exchange.",
        icon = Icons.Default.VideoLibrary,
        accentColor = EmeraldPrimary
      )
    }

    // 5. Pro Secret 3: High-Value Advertiser Categories
    item {
      StrategyCard(
        title = "3. Enable High-Paying Ad Categories",
        subtitle = "Fintech, Mobile RPGs, and E-commerce",
        description = "Log in to dashboard.unity.com, go to Monetization -> Ad Filters, and ensure categories like Finance, Crypto, and Midcore Mobile Games are ALLOWED. These categories consistently pay the highest bids across Unity's network.",
        icon = Icons.Default.Diamond,
        accentColor = GoldAccent
      )
    }

    // 6. Pro Secret 4: 2X Boost Retention Engine
    item {
      StrategyCard(
        title = "4. The 2X Multiplier Retention Loop",
        subtitle = "Triple session length without ad burnout",
        description = "By offering 2X multiplier boosts, lucky wheel spins, and scratch cards, users naturally watch 6-10 video ads per session while remaining highly satisfied. This organic loop maximizes your ad impression volume effortlessly without a backend database!",
        icon = Icons.Default.TrendingUp,
        accentColor = PurpleBoost
      )
    }

    // 7. Unity Ads Placement & Mediation Setup Guide
    item {
      StrategyCard(
        title = "5. Mediation Partner: Select 'Unity Ads only'",
        subtitle = "Fix: 'Header bidding load invocation failed: adMarkup is missing'",
        description = "If your Unity console logs show 'adMarkup is missing', open dashboard.unity.com -> Monetization -> Settings. Under 'Mediation Partner', ensure it is set to 'Unity Ads only' (or create standard Waterfall placements: Rewarded_Android, Interstitial_Android, and Banner_Android) rather than third-party Header Bidding adapters without auction markup.",
        icon = Icons.Default.Info,
        accentColor = ElectricBlue
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun StrategyCard(
  title: String,
  subtitle: String,
  description: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = accentColor,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary,
        lineHeight = 20.sp
      )
    }
  }
}
