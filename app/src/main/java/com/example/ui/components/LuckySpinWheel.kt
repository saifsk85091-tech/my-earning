package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
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
import com.example.ui.theme.PurpleBoost
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class WheelSegment(val coins: Long, val color: Color)

@Composable
fun LuckySpinWheelDialog(
  spinsRemaining: Int,
  onSpinWin: (Long) -> Unit,
  onRechargeSpins: () -> Unit,
  onDismiss: () -> Unit
) {
  val segments = remember {
    listOf(
      WheelSegment(50, Color(0xFF10B981)),
      WheelSegment(100, Color(0xFFF59E0B)),
      WheelSegment(25, Color(0xFF3B82F6)),
      WheelSegment(150, Color(0xFF8B5CF6)),
      WheelSegment(35, Color(0xFFEC4899)),
      WheelSegment(200, Color(0xFFEAB308)),
      WheelSegment(40, Color(0xFF14B8A6)),
      WheelSegment(300, Color(0xFFF97316))
    )
  }

  val rotation = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()
  var isSpinning by remember { mutableStateOf(false) }
  var winMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
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
              text = "Lucky Spin Wheel",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Text(
              text = "Win up to 300 Coins!",
              style = MaterialTheme.typography.bodySmall,
              color = GoldAccent
            )
          }
          IconButton(
            onClick = { if (!isSpinning) onDismiss() },
            enabled = !isSpinning,
            modifier = Modifier.testTag("close_spin_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Canvas Spin Wheel with Center Needle
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(260.dp)
        ) {
          // Wheel Drawing
          Canvas(modifier = Modifier.size(250.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            val sweepAngle = 360f / segments.size

            // Outer gold border ring
            drawCircle(
              brush = Brush.sweepGradient(
                listOf(GoldAccent, GoldLight, GoldAccent, Color(0xFFD97706), GoldAccent)
              ),
              radius = radius,
              center = center,
              style = Stroke(width = 8.dp.toPx())
            )

            // Segments
            segments.forEachIndexed { i, seg ->
              val startAngle = rotation.value + (i * sweepAngle) - (sweepAngle / 2)
              drawArc(
                color = seg.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(radius * 2 - 16.dp.toPx(), radius * 2 - 16.dp.toPx()),
                topLeft = Offset(8.dp.toPx(), 8.dp.toPx())
              )

              // Draw segment text (Coins)
              val midAngleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
              val textRadius = radius * 0.65f
              val textX = center.x + (textRadius * cos(midAngleRad)).toFloat()
              val textY = center.y + (textRadius * sin(midAngleRad)).toFloat()

              drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                  color = android.graphics.Color.WHITE
                  textSize = 34f
                  isFakeBoldText = true
                  textAlign = android.graphics.Paint.Align.CENTER
                  setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
                }
                save()
                rotate((startAngle + sweepAngle / 2 + 90), textX, textY)
                drawText("+${seg.coins}", textX, textY, paint)
                restore()
              }
            }

            // Outer Pegs
            for (i in 0 until segments.size * 2) {
              val pegAngleRad = Math.toRadians((rotation.value + (i * (360f / (segments.size * 2)))).toDouble())
              val pegRadius = radius - 4.dp.toPx()
              val pegX = center.x + (pegRadius * cos(pegAngleRad)).toFloat()
              val pegY = center.y + (pegRadius * sin(pegAngleRad)).toFloat()
              drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(pegX, pegY))
            }
          }

          // Fixed Top Needle Pointer pointing Down into wheel
          Canvas(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .size(24.dp, 32.dp)
          ) {
            val path = Path().apply {
              moveTo(0f, 0f)
              lineTo(size.width, 0f)
              lineTo(size.width / 2, size.height)
              close()
            }
            drawPath(path, color = Color(0xFFEF4444), style = Fill)
            drawPath(path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
          }

          // Center Hub
          Surface(
            shape = CircleShape,
            color = Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(3.dp, GoldAccent),
            modifier = Modifier.size(54.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = GoldLight,
                modifier = Modifier.size(28.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status & Spins Remaining
        Text(
          text = "Spins Remaining Today: $spinsRemaining",
          style = MaterialTheme.typography.bodyMedium,
          color = if (spinsRemaining > 0) EmeraldLight else TextMuted,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Spin Action Button
        if (spinsRemaining > 0) {
          Button(
            onClick = {
              if (isSpinning) return@Button
              isSpinning = true
              winMessage = null

              // Choose random winning segment index
              val targetIndex = (0 until segments.size).random()
              val winningSegment = segments[targetIndex]

              // Calculate angle: pointer is at 270 degrees (Top center).
              // We need the center of winning segment to end at 270 deg.
              val sweep = 360f / segments.size
              val segmentCenterOffset = (targetIndex * sweep)
              val fullSpins = 5 * 360f
              val finalRotation = rotation.value + fullSpins + (270f - (rotation.value % 360f) - segmentCenterOffset)

              scope.launch {
                rotation.animateTo(
                  targetValue = finalRotation,
                  animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
                )
                isSpinning = false
                onSpinWin(winningSegment.coins)
              }
            },
            enabled = !isSpinning,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("spin_wheel_button")
          ) {
            Text(
              text = if (isSpinning) "Spinning..." else "SPIN NOW",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color.Black
            )
          }
        } else {
          // Recharge spins by watching Unity Rewarded Ad
          Button(
            onClick = {
              onRechargeSpins()
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("recharge_spins_ad_button")
          ) {
            Icon(
              imageVector = Icons.Default.VideoLibrary,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Watch Unity Ad for +3 Spins",
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        }
      }
    }
  }
}
