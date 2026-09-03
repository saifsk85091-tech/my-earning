package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdSimulationDialog
import com.example.ui.components.LuckySpinWheelDialog
import com.example.ui.components.MathQuizDialog
import com.example.ui.components.ScratchCardDialog
import com.example.ui.components.TopRewardHeader
import com.example.ui.screens.BoostGuideScreen
import com.example.ui.screens.EarnScreen
import com.example.ui.screens.PayoutScreen
import com.example.ui.screens.UnityInspectorScreen
import com.example.ui.theme.CoinYellow
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.RewardsViewModel
import com.example.ui.viewmodel.UiDialog

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        val viewModel: RewardsViewModel = viewModel()
        RewardsApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun RewardsApp(viewModel: RewardsViewModel) {
  val userState by viewModel.userState.collectAsState()
  val sdkStatus by viewModel.sdkStatus.collectAsState()
  val adStats by viewModel.adStats.collectAsState()
  val rewardedState by viewModel.rewardedAdState.collectAsState()
  val interstitialState by viewModel.interstitialAdState.collectAsState()
  val activeDialog by viewModel.activeDialog.collectAsState()
  val currentQuiz by viewModel.currentQuiz.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(ObsidianBg),
    containerColor = ObsidianBg,
    bottomBar = {
      NavigationBar(
        containerColor = ObsidianCard,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
          .border(1.dp, ObsidianBorder)
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("main_bottom_nav")
      ) {
        val navItems = listOf(
          Triple("Earn", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn),
          Triple("Redeem", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
          Triple("Boost", Icons.Filled.RocketLaunch, Icons.Outlined.RocketLaunch),
          Triple("Inspector", Icons.Filled.Settings, Icons.Outlined.Settings)
        )

        navItems.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
          val isSelected = selectedTab == index
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTab = index },
            icon = {
              Icon(
                imageVector = if (isSelected) filledIcon else outlinedIcon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
              )
            },
            label = {
              Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = EmeraldLight,
              selectedTextColor = EmeraldLight,
              indicatorColor = EmeraldDark.copy(alpha = 0.4f),
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_item_$index")
          )
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.statusBars)
    ) {
      // Top sticky Balance and Unity Status Header
      TopRewardHeader(
        userState = userState,
        sdkStatus = sdkStatus,
        onInspectClick = { selectedTab = 3 }
      )

      // Active screen content
      Box(modifier = Modifier.weight(1f)) {
        when (selectedTab) {
          0 -> EarnScreen(
            viewModel = viewModel,
            userState = userState
          )
          1 -> PayoutScreen(
            viewModel = viewModel,
            userState = userState
          )
          2 -> BoostGuideScreen(
            unityGameId = userState.unityGameId
          )
          3 -> UnityInspectorScreen(
            viewModel = viewModel,
            userState = userState,
            sdkStatus = sdkStatus,
            adStats = adStats,
            rewardedState = rewardedState,
            interstitialState = interstitialState
          )
        }
      }
    }
  }

  // Active Dialog Overlays
  when (val dialog = activeDialog) {
    is UiDialog.SpinWheel -> {
      LuckySpinWheelDialog(
        spinsRemaining = userState.spinsRemaining,
        onSpinWin = { coins -> viewModel.onSpinResult(coins) },
        onRechargeSpins = {
          // Trigger unity ad or recharge
          viewModel.dismissDialog()
          viewModel.completeAdSimulation(100L)
          viewModel.onSpinResult(0L) // updates
        },
        onDismiss = { viewModel.dismissDialog() }
      )
    }

    is UiDialog.ScratchCard -> {
      ScratchCardDialog(
        scratchesRemaining = userState.scratchesRemaining,
        onScratchWin = { coins -> viewModel.onScratchResult(coins) },
        onRechargeCards = {
          viewModel.dismissDialog()
          viewModel.completeAdSimulation(100L)
        },
        onDismiss = { viewModel.dismissDialog() }
      )
    }

    is UiDialog.MathQuiz -> {
      MathQuizDialog(
        quiz = currentQuiz,
        onSelectOption = { index -> viewModel.answerQuiz(index) },
        onDismiss = { viewModel.dismissDialog() }
      )
    }

    is UiDialog.AdSimulation -> {
      AdSimulationDialog(
        title = dialog.title,
        rewardCoins = dialog.rewardCoins,
        durationSec = dialog.durationSec,
        isRewarded = dialog.isRewarded,
        onAdFinished = {
          viewModel.dismissDialog()
          if (dialog.rewardCoins > 0) {
            viewModel.completeAdSimulation(dialog.rewardCoins)
          } else {
            // Booster simulation completed
            viewModel.completeBoosterSimulation()
          }
        },
        onDismiss = { viewModel.dismissDialog() }
      )
    }

    is UiDialog.RewardEarned -> {
      RewardEarnedAlert(
        amount = dialog.amount,
        source = dialog.source,
        currencySymbol = userState.currencySymbol,
        onDismiss = { viewModel.dismissDialog() }
      )
    }

    is UiDialog.Message -> {
      AlertDialog(
        onDismissRequest = { viewModel.dismissDialog() },
        containerColor = ObsidianCard,
        title = {
          Text(
            text = dialog.title,
            fontWeight = FontWeight.Bold,
            color = if (dialog.isError) Color(0xFFEF4444) else Color.White
          )
        },
        text = {
          Text(
            text = dialog.text,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
          )
        },
        confirmButton = {
          Button(
            onClick = { viewModel.dismissDialog() },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
          ) {
            Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      )
    }

    null -> {}
  }
}

@Composable
fun RewardEarnedAlert(
  amount: Long,
  source: String,
  currencySymbol: String,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = ObsidianCard,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Stars,
          contentDescription = null,
          tint = GoldAccent,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Reward Earned!",
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        )
      }
    },
    text = {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(GoldAccent.copy(alpha = 0.3f), Color.Transparent)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = null,
            tint = CoinYellow,
            modifier = Modifier.size(50.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "+$amount COINS",
          fontSize = 28.sp,
          fontWeight = FontWeight.Black,
          color = GoldLight
        )
        Text(
          text = source,
          style = MaterialTheme.typography.bodyMedium,
          color = EmeraldLight
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "+${currencySymbol}${String.format(java.util.Locale.US, "%.3f", amount.toDouble() / 1000.0)} Cash Added to Balance",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("confirm_reward_button")
      ) {
        Text("GREAT!", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    }
  )
}
