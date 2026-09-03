package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AdSimulationDialog(
  title: String,
  rewardCoins: Long,
  durationSec: Int = 5,
  isRewarded: Boolean = true,
  onAdFinished: () -> Unit,
  onDismiss: () -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(durationSec) }
  var isCompleted by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000)
      secondsLeft -= 1
    }
    isCompleted = true
  }

  Dialog(
    onDismissRequest = {
      if (isCompleted) onDismiss()
    },
    properties = DialogProperties(dismissOnBackPress = isCompleted, dismissOnClickOutside = false)
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = ObsidianCard,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top ad header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2563EB))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "UNITY ADS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isRewarded) "Rewarded Video" else "Interstitial Video",
              style = MaterialTheme.typography.labelMedium,
              color = TextSecondary
            )
          }

          if (isCompleted) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
            }
          } else {
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(1.dp, ObsidianBorder, CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${secondsLeft}s",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ad Creative Video Canvas simulation
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.linearGradient(
                listOf(
                  Color(0xFF1E1B4B),
                  Color(0xFF312E81),
                  Color(0xFF0F172A)
                )
              )
            )
            .border(1.dp, Color(0xFF4338CA), RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
          ) {
            Icon(
              imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
              contentDescription = null,
              tint = if (isCompleted) EmeraldLight else Color.White,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isCompleted) "Video playback completed successfully!" else "High eCPM Ad Impression in progress...",
              style = MaterialTheme.typography.bodySmall,
              color = if (isCompleted) EmeraldLight else TextSecondary
            )
          }

          // Progress indicator at the bottom of the video player
          val progress = if (durationSec > 0) 1f - (secondsLeft.toFloat() / durationSec.toFloat()) else 1f
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .height(4.dp),
            color = if (isCompleted) EmeraldLight else GoldAccent,
            trackColor = Color(0x33FFFFFF)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (rewardCoins > 0) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0F172A))
              .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = CoinYellow,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Reward: +$rewardCoins Coins",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = GoldLight
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom CTA Button
        Button(
          onClick = {
            if (isCompleted) {
              onAdFinished()
            }
          },
          enabled = isCompleted,
          colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldPrimary,
            disabledContainerColor = Color(0xFF334155)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("claim_ad_reward_button")
        ) {
          Text(
            text = if (isCompleted) "CLAIM REWARD NOW" else "Please wait ${secondsLeft}s...",
            fontWeight = FontWeight.Bold,
            color = if (isCompleted) Color.Black else TextMuted
          )
        }
      }
    }
  }
}
