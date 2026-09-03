package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.QuizQuestion

@Composable
fun MathQuizDialog(
  quiz: QuizQuestion,
  onSelectOption: (Int) -> Unit,
  onDismiss: () -> Unit
) {
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Functions,
              contentDescription = null,
              tint = EmeraldLight,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Quick Math Challenge",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_math_quiz")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Question card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(vertical = 24.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = quiz.question,
              fontSize = 32.sp,
              fontWeight = FontWeight.ExtraBold,
              color = GoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = CoinYellow,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "+${quiz.reward} Coins for correct answer",
                style = MaterialTheme.typography.labelMedium,
                color = EmeraldLight
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Options Grid (2x2)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          for (row in 0 until 2) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              for (col in 0 until 2) {
                val index = row * 2 + col
                if (index < quiz.options.size) {
                  val optionVal = quiz.options[index]
                  Button(
                    onClick = { onSelectOption(index) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier
                      .weight(1f)
                      .height(52.dp)
                      .testTag("quiz_option_$index")
                  ) {
                    Text(
                      text = "$optionVal",
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
