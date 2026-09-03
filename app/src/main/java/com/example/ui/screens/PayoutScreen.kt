package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.PayoutRecord
import com.example.data.UserRewardState
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RewardsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PayoutOption(val amountUSD: Double, val coinCost: Long)

@Composable
fun PayoutScreen(
  viewModel: RewardsViewModel,
  userState: UserRewardState,
  modifier: Modifier = Modifier
) {
  var selectedMethod by remember { mutableStateOf("PayPal") }
  var selectedAmountUSD by remember { mutableDoubleStateOf(1.0) }
  var selectedCoinCost by remember { mutableLongStateOf(1000L) }
  var accountInput by remember { mutableStateOf("") }

  val payoutMethods = listOf(
    Pair("PayPal", Icons.Default.Payment),
    Pair("UPI / Paytm", Icons.Default.CreditCard),
    Pair("Google Play", Icons.Default.AccountBalanceWallet),
    Pair("Amazon Gift", Icons.Default.ShoppingBag),
    Pair("Crypto USDT", Icons.Default.CurrencyBitcoin)
  )

  val payoutTiers = listOf(
    PayoutOption(1.0, 1000L),
    PayoutOption(5.0, 5000L),
    PayoutOption(10.0, 10000L),
    PayoutOption(25.0, 25000L)
  )

  val minPayout = 1000L
  val progressToMin = (userState.coins.toFloat() / minPayout.toFloat()).coerceIn(0f, 1f)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Balance & Minimum Payout Progress Card
    item {
      Surface(
        shape = RoundedCornerShape(20.dp),
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
            Column {
              Text(
                text = "AVAILABLE FOR CASHOUT",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${userState.currencySymbol}${String.format(Locale.US, "%.2f", userState.coins.toDouble() / 1000.0)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "${String.format(Locale.US, "%,d", userState.coins)} Coins",
                style = MaterialTheme.typography.titleSmall,
                color = GoldLight,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Progress to $1.00
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Min Payout: 1,000 Coins ($1.00)",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
            Text(
              text = "${(progressToMin * 100).toInt()}%",
              style = MaterialTheme.typography.bodySmall,
              color = if (progressToMin >= 1f) EmeraldLight else GoldAccent,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { progressToMin },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (progressToMin >= 1f) EmeraldPrimary else GoldAccent,
            trackColor = Color(0xFF0F172A)
          )
        }
      }
    }

    // 2. Select Payout Method
    item {
      Text(
        text = "1. CHOOSE PAYMENT METHOD",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = GoldAccent,
        letterSpacing = 1.1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        payoutMethods.take(3).forEach { (name, icon) ->
          val isSelected = selectedMethod == name
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) EmeraldDark else ObsidianCard,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) EmeraldLight else ObsidianBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedMethod = name }
              .testTag("method_$name")
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        payoutMethods.drop(3).forEach { (name, icon) ->
          val isSelected = selectedMethod == name
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) EmeraldDark else ObsidianCard,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) EmeraldLight else ObsidianBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedMethod = name }
              .testTag("method_$name")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    // 3. Select Amount
    item {
      Text(
        text = "2. SELECT WITHDRAWAL AMOUNT",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = EmeraldLight,
        letterSpacing = 1.1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        payoutTiers.forEach { tier ->
          val isSelected = selectedCoinCost == tier.coinCost
          val canAfford = userState.coins >= tier.coinCost

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) GoldAccent else ObsidianCard,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) GoldLight else ObsidianBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable {
                selectedAmountUSD = tier.amountUSD
                selectedCoinCost = tier.coinCost
              }
              .testTag("amount_${tier.amountUSD.toInt()}")
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "$${tier.amountUSD.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) Color.Black else Color.White
              )
              Text(
                text = "${tier.coinCost / 1000}k coins",
                fontSize = 10.sp,
                color = if (isSelected) Color.Black else if (canAfford) EmeraldLight else TextMuted
              )
            }
          }
        }
      }
    }

    // 4. Account Input & Submit Button
    item {
      Text(
        text = "3. RECIPIENT DETAILS",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 1.1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = accountInput,
        onValueChange = { accountInput = it },
        placeholder = {
          Text(
            text = when (selectedMethod) {
              "PayPal" -> "Enter your PayPal Email"
              "UPI / Paytm" -> "Enter UPI ID (e.g. user@okhdfcbank)"
              "Crypto USDT" -> "Enter USDT (TRC20 / BEP20) Address"
              else -> "Enter Email for Gift Card Code"
            },
            color = TextMuted,
            fontSize = 13.sp
          )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = ObsidianCard,
          unfocusedContainerColor = ObsidianCard,
          focusedBorderColor = EmeraldPrimary,
          unfocusedBorderColor = ObsidianBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("payout_account_input")
      )

      Spacer(modifier = Modifier.height(14.dp))

      val canSubmit = userState.coins >= selectedCoinCost && accountInput.isNotBlank()

      Button(
        onClick = {
          viewModel.requestWithdrawal(
            method = selectedMethod,
            account = accountInput,
            coinCost = selectedCoinCost,
            cashAmount = selectedAmountUSD
          )
          accountInput = ""
        },
        enabled = canSubmit,
        colors = ButtonDefaults.buttonColors(
          containerColor = EmeraldPrimary,
          disabledContainerColor = Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_withdraw_button")
      ) {
        Text(
          text = if (userState.coins < selectedCoinCost) "Need ${selectedCoinCost - userState.coins} more coins" else "WITHDRAW $${selectedAmountUSD.toInt()} NOW",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = if (canSubmit) Color.Black else TextMuted
        )
      }
    }

    // 5. Payout History Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CASHOUT RECORDS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.1.sp
          )
        }
        Text(
          text = "${userState.payoutHistory.size} total",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }
    }

    if (userState.payoutHistory.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No withdrawal requests yet. Watch Unity Ads and collect coins to make your first cashout!",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      items(userState.payoutHistory) { record ->
        PayoutRecordItem(record = record)
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun PayoutRecordItem(record: PayoutRecord) {
  val dateFormatted = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.US).format(Date(record.timestamp))

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${record.currencySymbol}${"%.2f".format(record.cashAmount)} via ${record.method}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = record.account,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          fontSize = 12.sp
        )
        Text(
          text = "${record.id} • $dateFormatted",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted,
          fontSize = 10.sp
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
              when (record.status) {
                "Approved", "Completed" -> EmeraldLight.copy(alpha = 0.2f)
                else -> GoldAccent.copy(alpha = 0.2f)
              }
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = record.status,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = when (record.status) {
              "Approved", "Completed" -> EmeraldLight
              else -> GoldAccent
            }
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "-${record.coinsCost} coins",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }
    }
  }
}
