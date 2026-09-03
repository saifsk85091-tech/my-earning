package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PayoutRecord(
  val id: String,
  val method: String,
  val account: String,
  val coinsCost: Long,
  val cashAmount: Double,
  val currencySymbol: String,
  val timestamp: Long,
  val status: String // "Processing", "Approved", "Completed"
)

data class UserRewardState(
  val coins: Long = 100L, // Welcome bonus
  val lifetimeCoinsEarned: Long = 100L,
  val totalAdsWatched: Int = 0,
  val dailyAdsWatched: Int = 0,
  val dailyStreak: Int = 1,
  val isDailyClaimedToday: Boolean = false,
  val boostEndTime: Long = 0L,
  val isBoostActive: Boolean = false,
  val spinsRemaining: Int = 5,
  val scratchesRemaining: Int = 5,
  val unityGameId: String = "6171812",
  val isTestMode: Boolean = true,
  val rewardedPlacement: String = "Rewarded_Android",
  val interstitialPlacement: String = "Interstitial_Android",
  val bannerPlacement: String = "Banner_Android",
  val currencySymbol: String = "$",
  val payoutHistory: List<PayoutRecord> = emptyList()
)

class RewardsPreferences(context: Context) {

  companion object {
    private const val PREFS_NAME = "unity_rewards_prefs"
    private const val KEY_COINS = "coins"
    private const val KEY_LIFETIME_COINS = "lifetime_coins"
    private const val KEY_TOTAL_ADS = "total_ads"
    private const val KEY_DAILY_ADS = "daily_ads"
    private const val KEY_LAST_AD_DAY = "last_ad_day"
    private const val KEY_DAILY_STREAK = "daily_streak"
    private const val KEY_LAST_CHECKIN_DAY = "last_checkin_day"
    private const val KEY_BOOST_END_TIME = "boost_end_time"
    private const val KEY_SPINS_REMAINING = "spins_remaining"
    private const val KEY_LAST_SPIN_DAY = "last_spin_day"
    private const val KEY_SCRATCHES_REMAINING = "scratches_remaining"
    private const val KEY_LAST_SCRATCH_DAY = "last_scratch_day"
    private const val KEY_GAME_ID = "unity_game_id"
    private const val KEY_TEST_MODE = "unity_test_mode"
    private const val KEY_REWARDED_PLACEMENT = "rewarded_placement"
    private const val KEY_INTERSTITIAL_PLACEMENT = "interstitial_placement"
    private const val KEY_BANNER_PLACEMENT = "banner_placement"
    private const val KEY_CURRENCY = "currency_symbol"
    private const val KEY_PAYOUTS_JSON = "payouts_json"
    const val COINS_PER_USD = 1000L // 1,000 Coins = $1.00 USD
  }

  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _state = MutableStateFlow(loadState())
  val state: StateFlow<UserRewardState> = _state.asStateFlow()

  private fun getTodayDateString(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
  }

  private fun loadState(): UserRewardState {
    val today = getTodayDateString()
    val lastAdDay = prefs.getString(KEY_LAST_AD_DAY, "") ?: ""
    val dailyAds = if (lastAdDay == today) prefs.getInt(KEY_DAILY_ADS, 0) else 0

    val lastCheckin = prefs.getString(KEY_LAST_CHECKIN_DAY, "") ?: ""
    val isDailyClaimed = lastCheckin == today

    val lastSpinDay = prefs.getString(KEY_LAST_SPIN_DAY, "") ?: ""
    val spins = if (lastSpinDay == today) prefs.getInt(KEY_SPINS_REMAINING, 5) else 5

    val lastScratchDay = prefs.getString(KEY_LAST_SCRATCH_DAY, "") ?: ""
    val scratches = if (lastScratchDay == today) prefs.getInt(KEY_SCRATCHES_REMAINING, 5) else 5

    val boostEnd = prefs.getLong(KEY_BOOST_END_TIME, 0L)
    val isBoost = boostEnd > System.currentTimeMillis()

    val payouts = loadPayoutsFromJson(prefs.getString(KEY_PAYOUTS_JSON, "[]") ?: "[]")

    return UserRewardState(
      coins = prefs.getLong(KEY_COINS, 100L),
      lifetimeCoinsEarned = prefs.getLong(KEY_LIFETIME_COINS, 100L),
      totalAdsWatched = prefs.getInt(KEY_TOTAL_ADS, 0),
      dailyAdsWatched = dailyAds,
      dailyStreak = prefs.getInt(KEY_DAILY_STREAK, 1),
      isDailyClaimedToday = isDailyClaimed,
      boostEndTime = boostEnd,
      isBoostActive = isBoost,
      spinsRemaining = spins,
      scratchesRemaining = scratches,
      unityGameId = prefs.getString(KEY_GAME_ID, "6171812") ?: "6171812",
      isTestMode = prefs.getBoolean(KEY_TEST_MODE, true),
      rewardedPlacement = prefs.getString(KEY_REWARDED_PLACEMENT, "Rewarded_Android") ?: "Rewarded_Android",
      interstitialPlacement = prefs.getString(KEY_INTERSTITIAL_PLACEMENT, "Interstitial_Android") ?: "Interstitial_Android",
      bannerPlacement = prefs.getString(KEY_BANNER_PLACEMENT, "Banner_Android") ?: "Banner_Android",
      currencySymbol = prefs.getString(KEY_CURRENCY, "$") ?: "$",
      payoutHistory = payouts
    )
  }

  fun addCoins(amount: Long) {
    val current = _state.value
    val multiplier = if (current.isBoostActive && current.boostEndTime > System.currentTimeMillis()) 2 else 1
    val actualEarned = amount * multiplier

    val newCoins = current.coins + actualEarned
    val newLifetime = current.lifetimeCoinsEarned + actualEarned

    prefs.edit()
      .putLong(KEY_COINS, newCoins)
      .putLong(KEY_LIFETIME_COINS, newLifetime)
      .apply()

    _state.value = _state.value.copy(
      coins = newCoins,
      lifetimeCoinsEarned = newLifetime
    )
  }

  fun recordAdWatched(rewardCoins: Long) {
    val today = getTodayDateString()
    val current = _state.value

    val newTotalAds = current.totalAdsWatched + 1
    val newDailyAds = current.dailyAdsWatched + 1

    val multiplier = if (current.isBoostActive && current.boostEndTime > System.currentTimeMillis()) 2 else 1
    val actualEarned = rewardCoins * multiplier

    val newCoins = current.coins + actualEarned
    val newLifetime = current.lifetimeCoinsEarned + actualEarned

    prefs.edit()
      .putLong(KEY_COINS, newCoins)
      .putLong(KEY_LIFETIME_COINS, newLifetime)
      .putInt(KEY_TOTAL_ADS, newTotalAds)
      .putInt(KEY_DAILY_ADS, newDailyAds)
      .putString(KEY_LAST_AD_DAY, today)
      .apply()

    _state.value = _state.value.copy(
      coins = newCoins,
      lifetimeCoinsEarned = newLifetime,
      totalAdsWatched = newTotalAds,
      dailyAdsWatched = newDailyAds
    )
  }

  fun claimDailyStreak(): Long {
    val today = getTodayDateString()
    val current = _state.value
    if (current.isDailyClaimedToday) return 0L

    val streak = current.dailyStreak
    // Day 1: 50, Day 2: 75, Day 3: 100, Day 4: 150, Day 5: 200, Day 6: 300, Day 7: 500
    val bonus = when (streak) {
      1 -> 50L
      2 -> 75L
      3 -> 100L
      4 -> 150L
      5 -> 200L
      6 -> 300L
      else -> 500L
    }

    val nextStreak = if (streak >= 7) 1 else streak + 1
    val newCoins = current.coins + bonus
    val newLifetime = current.lifetimeCoinsEarned + bonus

    prefs.edit()
      .putLong(KEY_COINS, newCoins)
      .putLong(KEY_LIFETIME_COINS, newLifetime)
      .putInt(KEY_DAILY_STREAK, nextStreak)
      .putString(KEY_LAST_CHECKIN_DAY, today)
      .apply()

    _state.value = _state.value.copy(
      coins = newCoins,
      lifetimeCoinsEarned = newLifetime,
      dailyStreak = nextStreak,
      isDailyClaimedToday = true
    )
    return bonus
  }

  fun activate2xBoost(durationMinutes: Int = 15) {
    val now = System.currentTimeMillis()
    val endTime = now + (durationMinutes * 60 * 1000L)

    prefs.edit()
      .putLong(KEY_BOOST_END_TIME, endTime)
      .apply()

    _state.value = _state.value.copy(
      boostEndTime = endTime,
      isBoostActive = true
    )
  }

  fun useSpin(): Boolean {
    val current = _state.value
    if (current.spinsRemaining <= 0) return false
    val updated = current.spinsRemaining - 1
    val today = getTodayDateString()

    prefs.edit()
      .putInt(KEY_SPINS_REMAINING, updated)
      .putString(KEY_LAST_SPIN_DAY, today)
      .apply()

    _state.value = _state.value.copy(spinsRemaining = updated)
    return true
  }

  fun addFreeSpins(amount: Int = 3) {
    val updated = _state.value.spinsRemaining + amount
    val today = getTodayDateString()

    prefs.edit()
      .putInt(KEY_SPINS_REMAINING, updated)
      .putString(KEY_LAST_SPIN_DAY, today)
      .apply()

    _state.value = _state.value.copy(spinsRemaining = updated)
  }

  fun useScratchCard(): Boolean {
    val current = _state.value
    if (current.scratchesRemaining <= 0) return false
    val updated = current.scratchesRemaining - 1
    val today = getTodayDateString()

    prefs.edit()
      .putInt(KEY_SCRATCHES_REMAINING, updated)
      .putString(KEY_LAST_SCRATCH_DAY, today)
      .apply()

    _state.value = _state.value.copy(scratchesRemaining = updated)
    return true
  }

  fun addFreeScratchCards(amount: Int = 3) {
    val updated = _state.value.scratchesRemaining + amount
    val today = getTodayDateString()

    prefs.edit()
      .putInt(KEY_SCRATCHES_REMAINING, updated)
      .putString(KEY_LAST_SCRATCH_DAY, today)
      .apply()

    _state.value = _state.value.copy(scratchesRemaining = updated)
  }

  fun requestWithdrawal(
    method: String,
    account: String,
    coinCost: Long,
    cashAmount: Double
  ): Boolean {
    val current = _state.value
    if (current.coins < coinCost) return false

    val newCoins = current.coins - coinCost
    val newRecord = PayoutRecord(
      id = "TX-${UUID.randomUUID().toString().substring(0, 8).uppercase()}",
      method = method,
      account = account,
      coinsCost = coinCost,
      cashAmount = cashAmount,
      currencySymbol = current.currencySymbol,
      timestamp = System.currentTimeMillis(),
      status = "Processing"
    )

    val updatedHistory = listOf(newRecord) + current.payoutHistory
    val jsonString = savePayoutsToJson(updatedHistory)

    prefs.edit()
      .putLong(KEY_COINS, newCoins)
      .putString(KEY_PAYOUTS_JSON, jsonString)
      .apply()

    _state.value = _state.value.copy(
      coins = newCoins,
      payoutHistory = updatedHistory
    )
    return true
  }

  fun updateUnitySettings(
    gameId: String,
    testMode: Boolean,
    rewardedPlacement: String = _state.value.rewardedPlacement,
    interstitialPlacement: String = _state.value.interstitialPlacement,
    bannerPlacement: String = _state.value.bannerPlacement
  ) {
    prefs.edit()
      .putString(KEY_GAME_ID, gameId)
      .putBoolean(KEY_TEST_MODE, testMode)
      .putString(KEY_REWARDED_PLACEMENT, rewardedPlacement)
      .putString(KEY_INTERSTITIAL_PLACEMENT, interstitialPlacement)
      .putString(KEY_BANNER_PLACEMENT, bannerPlacement)
      .apply()

    _state.value = _state.value.copy(
      unityGameId = gameId,
      isTestMode = testMode,
      rewardedPlacement = rewardedPlacement,
      interstitialPlacement = interstitialPlacement,
      bannerPlacement = bannerPlacement
    )
  }

  fun updatePlacements(
    rewarded: String,
    interstitial: String,
    banner: String
  ) {
    prefs.edit()
      .putString(KEY_REWARDED_PLACEMENT, rewarded)
      .putString(KEY_INTERSTITIAL_PLACEMENT, interstitial)
      .putString(KEY_BANNER_PLACEMENT, banner)
      .apply()

    _state.value = _state.value.copy(
      rewardedPlacement = rewarded,
      interstitialPlacement = interstitial,
      bannerPlacement = banner
    )
  }

  fun checkBoostExpired() {
    val current = _state.value
    if (current.isBoostActive && current.boostEndTime <= System.currentTimeMillis()) {
      _state.value = _state.value.copy(isBoostActive = false)
    }
  }

  private fun savePayoutsToJson(list: List<PayoutRecord>): String {
    val arr = JSONArray()
    for (item in list) {
      val obj = JSONObject()
      obj.put("id", item.id)
      obj.put("method", item.method)
      obj.put("account", item.account)
      obj.put("coinsCost", item.coinsCost)
      obj.put("cashAmount", item.cashAmount)
      obj.put("currencySymbol", item.currencySymbol)
      obj.put("timestamp", item.timestamp)
      obj.put("status", item.status)
      arr.put(obj)
    }
    return arr.toString()
  }

  private fun loadPayoutsFromJson(jsonStr: String): List<PayoutRecord> {
    val list = mutableListOf<PayoutRecord>()
    try {
      val arr = JSONArray(jsonStr)
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          PayoutRecord(
            id = obj.optString("id", "TX-0000"),
            method = obj.optString("method", "PayPal"),
            account = obj.optString("account", ""),
            coinsCost = obj.optLong("coinsCost", 1000L),
            cashAmount = obj.optDouble("cashAmount", 1.0),
            currencySymbol = obj.optString("currencySymbol", "$"),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
            status = obj.optString("status", "Processing")
          )
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return list
  }
}
