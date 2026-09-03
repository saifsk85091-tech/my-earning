package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.SdkInitStatus
import com.example.data.UserRewardState
import com.example.ui.theme.CoinYellow
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

@Composable
fun TopRewardHeader(
  userState: UserRewardState,
  sdkStatus: SdkInitStatus,
  onInspectClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "coinPulse"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    shape = RoundedCornerShape(20.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    shadowElevation = 8.dp
  ) {
    Box(
      modifier = Modifier
        .background(
          Brush.linearGradient(
            colors = listOf(
              Color(0xFF1E293B),
              Color(0xFF0F172A),
              Color(0xFF064E3B).copy(alpha = 0.4f)
            )
          )
        )
        .padding(16.dp)
    ) {
      Column {
        // Top status row: Game ID badge + boost indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Unity Ads Connection Badge
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.8f))
              .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
              .clickable { onInspectClick() }
              .padding(horizontal = 10.dp, vertical = 5.dp)
              .testTag("unity_status_badge"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            val (statusColor, statusText) = when (sdkStatus) {
              is SdkInitStatus.Initialized -> Pair(EmeraldLight, "Unity Ads Active")
              is SdkInitStatus.Initializing -> Pair(GoldAccent, "Connecting...")
              is SdkInitStatus.Failed -> Pair(Color(0xFFEF4444), "Init Alert")
              else -> Pair(TextMuted, "Unity SDK")
            }

            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(statusColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "$statusText • ID: ${userState.unityGameId}",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary,
              fontWeight = FontWeight.Medium
            )
          }

          // Active 2X boost badge if active
          if (userState.isBoostActive && userState.boostEndTime > System.currentTimeMillis()) {
            val remainingSec = ((userState.boostEndTime - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            val mins = remainingSec / 60
            val secs = remainingSec % 60

            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.horizontalGradient(listOf(PurpleBoost, Color(0xFF6D28D9)))
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "2X Boost Active",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "2X (${mins}m ${secs}s)",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Balance presentation
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "YOUR BALANCE",
              style = MaterialTheme.typography.labelMedium,
              color = TextMuted,
              letterSpacing = 1.2.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Coins",
                tint = CoinYellow,
                modifier = Modifier
                  .size(34.dp)
                  .scale(pulseScale)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = String.format(java.util.Locale.US, "%,d", userState.coins),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Coins",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = GoldLight
              )
            }
          }

          // Cash equivalent card
          val cashEquivalent = userState.coins.toDouble() / 1000.0
          Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF0F172A).copy(alpha = 0.9f))
              .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Text(
              text = "CASH VALUE",
              style = MaterialTheme.typography.labelSmall,
              color = EmeraldLight,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${userState.currencySymbol}${String.format(java.util.Locale.US, "%.2f", cashEquivalent)}",
              style = MaterialTheme.typography.titleLarge,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "1k = $1.00",
              style = MaterialTheme.typography.labelSmall,
              color = TextMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom stats strip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          StatPill(
            title = "Streak",
            value = "Day ${userState.dailyStreak}",
            icon = Icons.Default.Star,
            color = GoldAccent
          )
          StatPill(
            title = "Ads Today",
            value = "${userState.dailyAdsWatched}",
            icon = Icons.Default.CheckCircle,
            color = EmeraldLight
          )
          StatPill(
            title = "Spins Left",
            value = "${userState.spinsRemaining}",
            icon = Icons.Default.MonetizationOn,
            color = PurpleBoost
          )
        }
      }
    }
  }
}

@Composable
private fun StatPill(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = color,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        fontSize = 10.sp
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
