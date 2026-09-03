package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRewardState
import com.example.ui.components.UnityBannerContainer
import com.example.ui.theme.CoinYellow
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
import com.example.ui.viewmodel.RewardsViewModel
import com.example.ui.viewmodel.UiDialog

@Composable
fun EarnScreen(
  viewModel: RewardsViewModel,
  userState: UserRewardState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Daily Check-in Streak Banner
    item {
      DailyStreakCard(
        userState = userState,
        onClaimClick = { viewModel.claimDailyStreak() }
      )
    }

    // 2. 2X Earning Booster Banner
    item {
      BoosterCard(
        isBoostActive = userState.isBoostActive,
        boostEndTime = userState.boostEndTime,
        onActivateClick = {
          if (activity != null) viewModel.activateBooster(activity)
        }
      )
    }

    // 3. Unity Ads Watch & Earn Section
    item {
      Text(
        text = "WATCH UNITY ADS & EARN",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = GoldAccent,
        letterSpacing = 1.1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AdRewardCard(
          title = "High CPM Rewarded Video",
          reward = "+100 Coins",
          badge = "HIGHEST PAYOUT",
          badgeColor = EmeraldLight,
          icon = Icons.Default.VideoLibrary,
          accentColor = EmeraldPrimary,
          onClick = {
            if (activity != null) viewModel.watchRewardedAd(activity, 100L)
          },
          testTag = "watch_rewarded_100"
        )

        AdRewardCard(
          title = "Quick Interstitial Video",
          reward = "+40 Coins",
          badge = "FAST REWARD",
          badgeColor = ElectricBlue,
          icon = Icons.Default.PlayArrow,
          accentColor = ElectricBlue,
          onClick = {
            if (activity != null) viewModel.watchInterstitialAd(activity, 40L)
          },
          testTag = "watch_interstitial_40"
        )

        AdRewardCard(
          title = "Mega Bonus Video Ad",
          reward = "+150 Coins",
          badge = "EXCLUSIVE",
          badgeColor = PurpleBoost,
          icon = Icons.Default.Stars,
          accentColor = PurpleBoost,
          onClick = {
            if (activity != null) viewModel.watchRewardedAd(activity, 150L)
          },
          testTag = "watch_mega_150"
        )
      }
    }

    // 4. Daily Milestone Progress
    item {
      DailyMilestoneCard(
        dailyAdsWatched = userState.dailyAdsWatched,
        goal = 5,
        onClaimBonus = {
          viewModel.completeAdSimulation(300L)
        }
      )
    }

    // 5. Interactive Mini Earning Games
    item {
      Text(
        text = "LUCKY EARNING GAMES",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = EmeraldLight,
        letterSpacing = 1.1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        MiniGameCard(
          title = "Lucky Wheel",
          subtitle = "${userState.spinsRemaining} spins left",
          tag = "Win 300",
          icon = Icons.Default.Casino,
          color = GoldAccent,
          onClick = { viewModel.showDialog(UiDialog.SpinWheel) },
          modifier = Modifier.weight(1f),
          testTag = "open_spin_wheel"
        )
        MiniGameCard(
          title = "Scratch & Win",
          subtitle = "${userState.scratchesRemaining} cards left",
          tag = "Win 200",
          icon = Icons.Default.CardGiftcard,
          color = EmeraldLight,
          onClick = { viewModel.showDialog(UiDialog.ScratchCard) },
          modifier = Modifier.weight(1f),
          testTag = "open_scratch_card"
        )
      }
    }

    // 6. Quick Math Challenge
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = ObsidianCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.showDialog(UiDialog.MathQuiz) }
          .testTag("open_math_quiz")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Functions,
              contentDescription = null,
              tint = EmeraldLight,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Speed Math Quiz",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Solve quick math puzzles for +25 Coins each",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(EmeraldDark.copy(alpha = 0.5f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "PLAY",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = EmeraldLight
            )
          }
        }
      }
    }

    // 7. Embedded Unity Banner container
    item {
      UnityBannerContainer(
        gameId = userState.unityGameId,
        placementId = userState.bannerPlacement,
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
      )
    }
  }
}

@Composable
private fun DailyStreakCard(
  userState: UserRewardState,
  onClaimClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Daily Check-in Streak",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = if (userState.isDailyClaimedToday) "Claimed for today! Next reward tomorrow." else "Claim today's coin streak reward!",
            style = MaterialTheme.typography.bodySmall,
            color = if (userState.isDailyClaimedToday) EmeraldLight else TextSecondary
          )
        }

        Button(
          onClick = onClaimClick,
          enabled = !userState.isDailyClaimedToday,
          colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldPrimary,
            disabledContainerColor = Color(0xFF1E293B)
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("claim_daily_streak_button")
        ) {
          Text(
            text = if (userState.isDailyClaimedToday) "Claimed" else "Claim",
            fontWeight = FontWeight.Bold,
            color = if (userState.isDailyClaimedToday) TextMuted else Color.Black
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 7-day streak bubbles
      val streakDays = listOf(50L, 75L, 100L, 150L, 200L, 300L, 500L)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        streakDays.forEachIndexed { index, coins ->
          val dayNum = index + 1
          val isPassed = dayNum < userState.dailyStreak || (dayNum == userState.dailyStreak && userState.isDailyClaimedToday)
          val isCurrent = dayNum == userState.dailyStreak && !userState.isDailyClaimedToday

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isPassed -> EmeraldPrimary
                    isCurrent -> GoldAccent
                    else -> Color(0xFF0F172A)
                  }
                )
                .border(
                  1.dp,
                  if (isCurrent) GoldLight else ObsidianBorder,
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isPassed) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(18.dp)
                )
              } else {
                Text(
                  text = "+$coins",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) Color.Black else TextSecondary
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "D$dayNum",
              fontSize = 10.sp,
              color = if (isCurrent) GoldLight else TextMuted
            )
          }
        }
      }
    }
  }
}

@Composable
private fun BoosterCard(
  isBoostActive: Boolean,
  boostEndTime: Long,
  onActivateClick: () -> Unit
) {
  val remainingMins = ((boostEndTime - System.currentTimeMillis()) / (60 * 1000)).coerceAtLeast(0)

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFF2E1065),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C3AED)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(Color(0xFF5B21B6)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Bolt,
          contentDescription = null,
          tint = GoldLight,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "2X EARNINGS BOOST",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
          if (isBoostActive && boostEndTime > System.currentTimeMillis()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ACTIVE (${remainingMins}m left)",
              style = MaterialTheme.typography.labelSmall,
              color = EmeraldLight,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Text(
          text = if (isBoostActive) "Double coins on all ads, spins & quizzes!" else "Watch a video ad to double all earnings for 15 mins",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFFDDD6FE)
        )
      }

      if (!isBoostActive || boostEndTime <= System.currentTimeMillis()) {
        Button(
          onClick = onActivateClick,
          colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("activate_booster_button")
        ) {
          Text(
            text = "Boost 2X",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@Composable
private fun AdRewardCard(
  title: String,
  reward: String,
  badge: String,
  badgeColor: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(accentColor.copy(alpha = 0.15f))
          .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(badgeColor.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = reward,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = GoldLight
        )
        Text(
          text = "Tap to Watch",
          style = MaterialTheme.typography.labelSmall,
          color = EmeraldLight
        )
      }
    }
  }
}

@Composable
private fun DailyMilestoneCard(
  dailyAdsWatched: Int,
  goal: Int = 5,
  onClaimBonus: () -> Unit
) {
  val progress = (dailyAdsWatched.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
  val isGoalReached = dailyAdsWatched >= goal

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CardGiftcard,
            contentDescription = null,
            tint = GoldAccent,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Daily Ad Milestone",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
        Text(
          text = "$dailyAdsWatched / $goal Ads",
          style = MaterialTheme.typography.labelMedium,
          color = if (isGoalReached) EmeraldLight else TextSecondary,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = if (isGoalReached) EmeraldLight else GoldAccent,
        trackColor = Color(0xFF0F172A)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Watch 5 Unity Ads today for +300 Coins bonus!",
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted,
          modifier = Modifier.weight(1f)
        )
        if (isGoalReached) {
          Button(
            onClick = onClaimBonus,
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("claim_ad_milestone_button")
          ) {
            Text(
              text = "Claim +300",
              fontWeight = FontWeight.Bold,
              color = Color.Black,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MiniGameCard(
  title: String,
  subtitle: String,
  tag: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = ObsidianCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
    modifier = modifier
      .clickable { onClick() }
      .testTag(testTag)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondary
      )
    }
  }
}
