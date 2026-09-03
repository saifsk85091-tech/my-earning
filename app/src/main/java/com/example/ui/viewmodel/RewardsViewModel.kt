package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdState
import com.example.ads.SdkInitStatus
import com.example.ads.UnityAdStats
import com.example.ads.UnityAdsManager
import com.example.data.RewardsPreferences
import com.example.data.UserRewardState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiDialog {
  data class RewardEarned(val amount: Long, val source: String) : UiDialog
  data class Message(val title: String, val text: String, val isError: Boolean = false) : UiDialog
  object SpinWheel : UiDialog
  object ScratchCard : UiDialog
  object MathQuiz : UiDialog
  data class AdSimulation(
    val title: String,
    val rewardCoins: Long,
    val durationSec: Int = 5,
    val isRewarded: Boolean = true
  ) : UiDialog
}

data class QuizQuestion(
  val question: String,
  val options: List<Int>,
  val correctIndex: Int,
  val reward: Long = 30L
)

class RewardsViewModel(application: Application) : AndroidViewModel(application) {

  private val prefs = RewardsPreferences(application)
  private val adsManager = UnityAdsManager.getInstance()

  val userState: StateFlow<UserRewardState> = prefs.state
  val sdkStatus: StateFlow<SdkInitStatus> = adsManager.sdkStatus
  val adStats: StateFlow<UnityAdStats> = adsManager.adStats
  val rewardedAdState: StateFlow<AdState> = adsManager.rewardedAdState
  val interstitialAdState: StateFlow<AdState> = adsManager.interstitialAdState

  private val _activeDialog = MutableStateFlow<UiDialog?>(null)
  val activeDialog: StateFlow<UiDialog?> = _activeDialog.asStateFlow()

  private val _currentQuiz = MutableStateFlow<QuizQuestion>(generateNewQuiz())
  val currentQuiz: StateFlow<QuizQuestion> = _currentQuiz.asStateFlow()

  private var timerJob: Job? = null

  init {
    // Initialize Unity Ads using stored settings (default 6171812)
    val state = prefs.state.value
    adsManager.initialize(
      context = application,
      gameId = state.unityGameId,
      testMode = state.isTestMode,
      rewardedPlacement = state.rewardedPlacement,
      interstitialPlacement = state.interstitialPlacement,
      bannerPlacement = state.bannerPlacement
    )

    // Periodically verify boost expiration
    timerJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        prefs.checkBoostExpired()
      }
    }
  }

  fun dismissDialog() {
    _activeDialog.value = null
  }

  fun showDialog(dialog: UiDialog) {
    _activeDialog.value = dialog
  }

  fun claimDailyStreak() {
    val earned = prefs.claimDailyStreak()
    if (earned > 0) {
      _activeDialog.value = UiDialog.RewardEarned(earned, "Daily Streak Bonus")
    } else {
      _activeDialog.value = UiDialog.Message(
        title = "Already Claimed",
        text = "You have already collected today's daily streak reward! Come back tomorrow for the next day's bonus."
      )
    }
  }

  fun watchRewardedAd(activity: Activity, rewardCoins: Long = 100L) {
    adsManager.showRewardedAd(
      activity = activity,
      placementId = userState.value.rewardedPlacement,
      onRewardEarned = {
        prefs.recordAdWatched(rewardCoins)
        _activeDialog.value = UiDialog.RewardEarned(rewardCoins, "Unity Rewarded Video")
      },
      onError = { errorMsg ->
        // Offer simulation fallback so the user can test all reward features seamlessly!
        _activeDialog.value = UiDialog.AdSimulation(
          title = "Unity Ads Preview (${userState.value.rewardedPlacement})",
          rewardCoins = rewardCoins,
          durationSec = 5,
          isRewarded = true
        )
      }
    )
  }

  fun watchInterstitialAd(activity: Activity, rewardCoins: Long = 40L) {
    adsManager.showInterstitialAd(
      activity = activity,
      placementId = userState.value.interstitialPlacement,
      onCompleted = {
        prefs.recordAdWatched(rewardCoins)
        _activeDialog.value = UiDialog.RewardEarned(rewardCoins, "Quick Video Ad")
      },
      onError = { errorMsg ->
        _activeDialog.value = UiDialog.AdSimulation(
          title = "Unity Interstitial Preview (${userState.value.interstitialPlacement})",
          rewardCoins = rewardCoins,
          durationSec = 4,
          isRewarded = false
        )
      }
    )
  }

  fun completeAdSimulation(rewardCoins: Long) {
    prefs.recordAdWatched(rewardCoins)
    _activeDialog.value = UiDialog.RewardEarned(rewardCoins, "Unity Ad Completed")
  }

  fun activateBooster(activity: Activity) {
    adsManager.showRewardedAd(
      activity = activity,
      placementId = userState.value.rewardedPlacement,
      onRewardEarned = {
        prefs.activate2xBoost(15)
        _activeDialog.value = UiDialog.Message(
          title = "2X Boost Activated!",
          text = "All coins earned for the next 15 minutes are doubled! Enjoy 2X earnings on ads, spins, and scratch cards."
        )
      },
      onError = {
        // Fallback simulation
        _activeDialog.value = UiDialog.AdSimulation(
          title = "2X Multiplier Boost Video",
          rewardCoins = 0L,
          durationSec = 5,
          isRewarded = true
        )
      }
    )
  }

  fun completeBoosterSimulation() {
    prefs.activate2xBoost(15)
    _activeDialog.value = UiDialog.Message(
      title = "2X Boost Activated!",
      text = "All coins earned for the next 15 minutes are doubled! Enjoy 2X earnings on ads, spins, and scratch cards."
    )
  }

  fun onSpinResult(winCoins: Long) {
    val used = prefs.useSpin()
    if (used) {
      prefs.addCoins(winCoins)
      _activeDialog.value = UiDialog.RewardEarned(winCoins, "Lucky Spin Wheel")
    }
  }

  fun rechargeSpins(activity: Activity) {
    adsManager.showRewardedAd(
      activity = activity,
      onRewardEarned = {
        prefs.addFreeSpins(3)
        _activeDialog.value = UiDialog.Message(
          title = "3 Extra Spins Added!",
          text = "You watched a Unity ad and recharged 3 free spins! Good luck!"
        )
      },
      onError = {
        prefs.addFreeSpins(3)
        _activeDialog.value = UiDialog.Message(
          title = "3 Extra Spins Added!",
          text = "You recharged 3 free spins! Good luck!"
        )
      }
    )
  }

  fun onScratchResult(winCoins: Long) {
    val used = prefs.useScratchCard()
    if (used) {
      prefs.addCoins(winCoins)
      _activeDialog.value = UiDialog.RewardEarned(winCoins, "Scratch & Win Card")
    }
  }

  fun rechargeScratchCards(activity: Activity) {
    adsManager.showRewardedAd(
      activity = activity,
      onRewardEarned = {
        prefs.addFreeScratchCards(3)
        _activeDialog.value = UiDialog.Message(
          title = "3 Extra Scratch Cards Added!",
          text = "You watched a Unity ad and unlocked 3 new scratch cards!"
        )
      },
      onError = {
        prefs.addFreeScratchCards(3)
        _activeDialog.value = UiDialog.Message(
          title = "3 Extra Scratch Cards Added!",
          text = "You unlocked 3 new scratch cards!"
        )
      }
    )
  }

  fun answerQuiz(selectedOptionIndex: Int) {
    val quiz = _currentQuiz.value
    if (selectedOptionIndex == quiz.correctIndex) {
      prefs.addCoins(quiz.reward)
      _activeDialog.value = UiDialog.RewardEarned(quiz.reward, "Quick Math Solved")
      _currentQuiz.value = generateNewQuiz()
    } else {
      _activeDialog.value = UiDialog.Message(
        title = "Incorrect Answer",
        text = "That was not correct. Try another question!",
        isError = true
      )
      _currentQuiz.value = generateNewQuiz()
    }
  }

  fun requestWithdrawal(method: String, account: String, coinCost: Long, cashAmount: Double) {
    if (account.isBlank()) {
      _activeDialog.value = UiDialog.Message(
        title = "Missing Account Info",
        text = "Please enter your valid recipient email, UPI ID, or wallet address.",
        isError = true
      )
      return
    }

    val success = prefs.requestWithdrawal(method, account, coinCost, cashAmount)
    if (success) {
      _activeDialog.value = UiDialog.Message(
        title = "Withdrawal Submitted!",
        text = "Your cashout request of ${userState.value.currencySymbol}${"%.2f".format(cashAmount)} ($coinCost coins) via $method has been submitted successfully.\n\nStatus: Processing. Check payout history below."
      )
    } else {
      _activeDialog.value = UiDialog.Message(
        title = "Insufficient Coins",
        text = "You need at least $coinCost coins for this withdrawal. Watch more Unity Ads to earn coins fast!",
        isError = true
      )
    }
  }

  fun updateSettings(
    context: Application,
    newGameId: String,
    testMode: Boolean,
    rewarded: String = userState.value.rewardedPlacement,
    interstitial: String = userState.value.interstitialPlacement,
    banner: String = userState.value.bannerPlacement
  ) {
    val trimmedId = newGameId.trim()
    if (trimmedId.isEmpty()) {
      _activeDialog.value = UiDialog.Message(
        title = "Invalid Game ID",
        text = "Unity Game ID cannot be empty.",
        isError = true
      )
      return
    }

    val trimmedRewarded = rewarded.trim().ifEmpty { "Rewarded_Android" }
    val trimmedInterstitial = interstitial.trim().ifEmpty { "Interstitial_Android" }
    val trimmedBanner = banner.trim().ifEmpty { "Banner_Android" }

    prefs.updateUnitySettings(
      gameId = trimmedId,
      testMode = testMode,
      rewardedPlacement = trimmedRewarded,
      interstitialPlacement = trimmedInterstitial,
      bannerPlacement = trimmedBanner
    )
    adsManager.initialize(
      context = context,
      gameId = trimmedId,
      testMode = testMode,
      rewardedPlacement = trimmedRewarded,
      interstitialPlacement = trimmedInterstitial,
      bannerPlacement = trimmedBanner,
      onSuccess = {
        _activeDialog.value = UiDialog.Message(
          title = "Settings Saved",
          text = "Unity Ads initialized with Game ID $trimmedId (Test Mode: $testMode).\nPlacements: $trimmedRewarded, $trimmedInterstitial, $trimmedBanner."
        )
      },
      onFailure = { err ->
        _activeDialog.value = UiDialog.Message(
          title = "Initialization Alert",
          text = "Unity Ads received: $err\nPlease ensure network access and valid Unity Dashboard placement config.",
          isError = true
        )
      }
    )
  }

  fun reloadAds() {
    adsManager.preloadAds()
  }

  private fun generateNewQuiz(): QuizQuestion {
    val a = (5..45).random()
    val b = (3..35).random()
    val isAddition = listOf(true, false).random()
    val correct = if (isAddition) a + b else a - b
    val operation = if (isAddition) "+" else "-"

    val wrong1 = correct + (-5..5).filter { it != 0 }.random()
    val wrong2 = correct + (-8..8).filter { it != 0 && it != wrong1 - correct }.random()
    val wrong3 = correct + (-10..10).filter { it != 0 && it != wrong1 - correct && it != wrong2 - correct }.random()

    val options = listOf(correct, wrong1, wrong2, wrong3).shuffled()
    val correctIndex = options.indexOf(correct)

    return QuizQuestion(
      question = "$a $operation $b = ?",
      options = options,
      correctIndex = correctIndex,
      reward = 25L
    )
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
