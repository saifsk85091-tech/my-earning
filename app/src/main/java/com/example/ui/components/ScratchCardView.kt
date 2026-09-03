package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

@Composable
fun ScratchCardDialog(
  scratchesRemaining: Int,
  onScratchWin: (Long) -> Unit,
  onRechargeCards: () -> Unit,
  onDismiss: () -> Unit
) {
  val hiddenPrize = remember {
    listOf(40L, 60L, 80L, 100L, 120L, 150L, 200L).random()
  }

  val scratchPoints = remember { mutableStateListOf<Offset>() }
  var isClaimed by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = ObsidianCard,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, ObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Scratch & Win",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Text(
              text = "Scratch to reveal coin prize!",
              style = MaterialTheme.typography.bodySmall,
              color = GoldAccent
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_scratch_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (scratchesRemaining > 0) {
          // The Interactive Scratch Canvas Container
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .clip(RoundedCornerShape(16.dp))
              .border(2.dp, GoldAccent, RoundedCornerShape(16.dp))
              .background(Color(0xFF0F172A))
          ) {
            // Revealed Underneath Layer (The prize)
            Column(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFF064E3B), Color(0xFF022C22))
                  )
                ),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = CoinYellow,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "+$hiddenPrize COINS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = GoldLight
              )
              Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.bodyMedium,
                color = EmeraldLight
              )
            }

            // Top Scratchable Foil Layer
            Canvas(
              modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.99f) // Required for blend mode clear
                .pointerInput(Unit) {
                  detectDragGestures { change, _ ->
                    change.consume()
                    scratchPoints.add(change.position)
                    if (scratchPoints.size > 40 && !isClaimed) {
                      isClaimed = true
                      onScratchWin(hiddenPrize)
                    }
                  }
                }
            ) {
              // Draw Silver-Gold metallic foil background
              drawRect(
                brush = Brush.linearGradient(
                  listOf(
                    Color(0xFF94A3B8),
                    Color(0xFFCBD5E1),
                    Color(0xFF64748B),
                    Color(0xFFE2E8F0)
                  )
                )
              )

              // Draw instructions text on top of foil
              if (scratchPoints.isEmpty()) {
                // Pattern / emblem
                drawCircle(
                  color = Color(0x33000000),
                  radius = 50.dp.toPx(),
                  center = center
                )
              }

              // Clear pixels where user dragged
              for (point in scratchPoints) {
                drawCircle(
                  color = Color.Transparent,
                  radius = 34.dp.toPx(),
                  center = point,
                  blendMode = BlendMode.Clear
                )
              }
            }

            // Instruction prompt if untouched
            if (scratchPoints.isEmpty()) {
              Box(
                modifier = Modifier
                  .align(Alignment.Center)
                  .clip(RoundedCornerShape(20.dp))
                  .background(Color(0xCC000000))
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = "Rub here with finger to scratch",
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Scratches Remaining
          Text(
            text = "Cards Left Today: $scratchesRemaining",
            style = MaterialTheme.typography.bodyMedium,
            color = EmeraldLight,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Reveal / Claim Button if user prefers one-tap
          Button(
            onClick = {
              if (!isClaimed) {
                isClaimed = true
                onScratchWin(hiddenPrize)
              }
            },
            enabled = !isClaimed,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("claim_scratch_button")
          ) {
            Text(
              text = if (isClaimed) "Claimed!" else "Scratch All / Claim Prize",
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        } else {
          // Out of scratch cards -> Watch Unity ad to recharge!
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
          ) {
            Text(
              text = "No Scratch Cards Remaining Today",
              style = MaterialTheme.typography.titleMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Watch a fast Unity video ad to recharge 3 fresh scratch cards instantly!",
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
              onClick = onRechargeCards,
              colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("recharge_scratch_ad_button")
            ) {
              Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                tint = Color.Black
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Watch Unity Ad for +3 Cards",
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
          }
        }
      }
    }
  }
}
