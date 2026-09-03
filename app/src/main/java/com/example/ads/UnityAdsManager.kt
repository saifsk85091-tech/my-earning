package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SdkInitStatus {
  object NotInitialized : SdkInitStatus
  object Initializing : SdkInitStatus
  data class Initialized(val gameId: String, val testMode: Boolean) : SdkInitStatus
  data class Failed(val error: String, val message: String) : SdkInitStatus
}

sealed interface AdState {
  object Idle : AdState
  object Loading : AdState
  object Ready : AdState
  object Showing : AdState
  data class Error(val message: String) : AdState
}

data class UnityAdStats(
  val totalAdsShown: Int = 0,
  val rewardedCompleted: Int = 0,
  val interstitialCompleted: Int = 0,
  val lastAdPlacement: String = "",
  val lastAdTimestamp: Long = 0L
)

class UnityAdsManager private constructor() {

  companion object {
    const val DEFAULT_GAME_ID = "6171812"
    const val DEFAULT_REWARDED_PLACEMENT = "Rewarded_Android"
    const val DEFAULT_INTERSTITIAL_PLACEMENT = "Interstitial_Android"
    const val DEFAULT_BANNER_PLACEMENT = "Banner_Android"

    @Volatile
    private var INSTANCE: UnityAdsManager? = null

    fun getInstance(): UnityAdsManager {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: UnityAdsManager().also { INSTANCE = it }
      }
    }
  }

  private val _sdkStatus = MutableStateFlow<SdkInitStatus>(SdkInitStatus.NotInitialized)
  val sdkStatus: StateFlow<SdkInitStatus> = _sdkStatus.asStateFlow()

  private val _rewardedAdState = MutableStateFlow<AdState>(AdState.Idle)
  val rewardedAdState: StateFlow<AdState> = _rewardedAdState.asStateFlow()

  private val _interstitialAdState = MutableStateFlow<AdState>(AdState.Idle)
  val interstitialAdState: StateFlow<AdState> = _interstitialAdState.asStateFlow()

  private val _adStats = MutableStateFlow(UnityAdStats())
  val adStats: StateFlow<UnityAdStats> = _adStats.asStateFlow()

  private var currentGameId: String = DEFAULT_GAME_ID
  private var isTestMode: Boolean = true
  var currentRewardedPlacement: String = DEFAULT_REWARDED_PLACEMENT
  var currentInterstitialPlacement: String = DEFAULT_INTERSTITIAL_PLACEMENT
  var currentBannerPlacement: String = DEFAULT_BANNER_PLACEMENT

  fun updatePlacementConfig(rewarded: String, interstitial: String, banner: String) {
    currentRewardedPlacement = rewarded
    currentInterstitialPlacement = interstitial
    currentBannerPlacement = banner
    if (UnityAds.isInitialized) {
      preloadAds()
    }
  }

  fun initialize(
    context: Context,
    gameId: String = DEFAULT_GAME_ID,
    testMode: Boolean = true,
    rewardedPlacement: String = DEFAULT_REWARDED_PLACEMENT,
    interstitialPlacement: String = DEFAULT_INTERSTITIAL_PLACEMENT,
    bannerPlacement: String = DEFAULT_BANNER_PLACEMENT,
    onSuccess: (() -> Unit)? = null,
    onFailure: ((String) -> Unit)? = null
  ) {
    this.currentGameId = gameId
    this.isTestMode = testMode
    this.currentRewardedPlacement = rewardedPlacement
    this.currentInterstitialPlacement = interstitialPlacement
    this.currentBannerPlacement = bannerPlacement

    if (UnityAds.isInitialized) {
      _sdkStatus.value = SdkInitStatus.Initialized(gameId, testMode)
      onSuccess?.invoke()
      preloadAds()
      return
    }

    _sdkStatus.value = SdkInitStatus.Initializing
    Log.d("UnityAdsManager", "Initializing Unity Ads with Game ID: $gameId (testMode=$testMode)")

    UnityAds.initialize(
      context.applicationContext,
      gameId,
      testMode,
      object : IUnityAdsInitializationListener {
        override fun onInitializationComplete() {
          Log.i("UnityAdsManager", "Unity Ads initialized successfully!")
          _sdkStatus.value = SdkInitStatus.Initialized(gameId, testMode)
          preloadAds()
          onSuccess?.invoke()
        }

        override fun onInitializationFailed(
          error: UnityAds.UnityAdsInitializationError?,
          message: String?
        ) {
          val errorDesc = error?.name ?: "UNKNOWN_ERROR"
          val msg = message ?: "Failed to initialize Unity Ads SDK"
          Log.e("UnityAdsManager", "Unity Ads initialization failed: $errorDesc - $msg")
          _sdkStatus.value = SdkInitStatus.Failed(errorDesc, msg)
          onFailure?.invoke("$errorDesc: $msg")
        }
      }
    )
  }

  fun preloadAds() {
    loadRewardedAd(currentRewardedPlacement)
    loadInterstitialAd(currentInterstitialPlacement)
  }

  fun loadRewardedAd(placementId: String? = null, tryFallback: Boolean = true) {
    if (!UnityAds.isInitialized) return
    val targetPlacement = placementId ?: currentRewardedPlacement
    _rewardedAdState.value = AdState.Loading
    Log.d("UnityAdsManager", "Loading Rewarded ad on placement: $targetPlacement")

    UnityAds.load(
      targetPlacement,
      object : IUnityAdsLoadListener {
        override fun onUnityAdsAdLoaded(loadedPlacementId: String?) {
          Log.d("UnityAdsManager", "Rewarded Ad Loaded: $loadedPlacementId")
          _rewardedAdState.value = AdState.Ready
        }

        override fun onUnityAdsFailedToLoad(
          failedPlacementId: String?,
          error: UnityAds.UnityAdsLoadError?,
          message: String?
        ) {
          val msg = message ?: "Failed to load ad"
          Log.w("UnityAdsManager", "Rewarded Ad Load failed ($failedPlacementId, error=$error): $msg")

          val friendlyMsg = if (msg.contains("adMarkup", ignoreCase = true) || error == UnityAds.UnityAdsLoadError.INVALID_ARGUMENT) {
            "Header bidding placement detected. In Unity Dashboard (dashboard.unity.com -> Monetization -> Settings), set Mediation Partner to 'Unity Ads only' or ensure standard waterfall placements are active."
          } else {
            msg
          }
          _rewardedAdState.value = AdState.Error(friendlyMsg)
        }
      }
    )
  }

  fun loadInterstitialAd(placementId: String? = null, tryFallback: Boolean = true) {
    if (!UnityAds.isInitialized) return
    val targetPlacement = placementId ?: currentInterstitialPlacement
    _interstitialAdState.value = AdState.Loading
    Log.d("UnityAdsManager", "Loading Interstitial ad on placement: $targetPlacement")

    UnityAds.load(
      targetPlacement,
      object : IUnityAdsLoadListener {
        override fun onUnityAdsAdLoaded(loadedPlacementId: String?) {
          Log.d("UnityAdsManager", "Interstitial Ad Loaded: $loadedPlacementId")
          _interstitialAdState.value = AdState.Ready
        }

        override fun onUnityAdsFailedToLoad(
          failedPlacementId: String?,
          error: UnityAds.UnityAdsLoadError?,
          message: String?
        ) {
          val msg = message ?: "Failed to load interstitial"
          Log.w("UnityAdsManager", "Interstitial Ad Load failed ($failedPlacementId, error=$error): $msg")

          val friendlyMsg = if (msg.contains("adMarkup", ignoreCase = true) || error == UnityAds.UnityAdsLoadError.INVALID_ARGUMENT) {
            "Header bidding placement detected. In Unity Dashboard (dashboard.unity.com -> Monetization -> Settings), set Mediation Partner to 'Unity Ads only' or ensure standard waterfall placements are active."
          } else {
            msg
          }
          _interstitialAdState.value = AdState.Error(friendlyMsg)
        }
      }
    )
  }

  fun showRewardedAd(
    activity: Activity,
    placementId: String? = null,
    onRewardEarned: () -> Unit,
    onAdClosed: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val targetPlacement = placementId ?: currentRewardedPlacement
    if (!UnityAds.isInitialized) {
      onError("Unity Ads is not initialized yet. Check Game ID: $currentGameId")
      return
    }

    val currentAdState = _rewardedAdState.value
    if (currentAdState is AdState.Error) {
      onError(currentAdState.message)
      loadRewardedAd(targetPlacement)
      return
    }

    _rewardedAdState.value = AdState.Showing
    val showOptions = UnityAdsShowOptions()

    UnityAds.show(
      activity,
      targetPlacement,
      showOptions,
      object : IUnityAdsShowListener {
        override fun onUnityAdsShowFailure(
          placementId: String?,
          error: UnityAds.UnityAdsShowError?,
          message: String?
        ) {
          val err = "${error?.name}: ${message ?: "Show failed"}"
          Log.e("UnityAdsManager", "Rewarded Ad Show Failed: $err")
          _rewardedAdState.value = AdState.Error(err)
          onError(err)
          loadRewardedAd(targetPlacement)
        }

        override fun onUnityAdsShowStart(placementId: String?) {
          Log.d("UnityAdsManager", "Rewarded Ad Show Started: $placementId")
        }

        override fun onUnityAdsShowClick(placementId: String?) {
          Log.d("UnityAdsManager", "Rewarded Ad Clicked: $placementId")
        }

        override fun onUnityAdsShowComplete(
          placementId: String?,
          state: UnityAds.UnityAdsShowCompletionState?
        ) {
          Log.d("UnityAdsManager", "Rewarded Ad Show Complete: state=$state")
          _rewardedAdState.value = AdState.Idle
          if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
            _adStats.value = _adStats.value.copy(
              totalAdsShown = _adStats.value.totalAdsShown + 1,
              rewardedCompleted = _adStats.value.rewardedCompleted + 1,
              lastAdPlacement = targetPlacement,
              lastAdTimestamp = System.currentTimeMillis()
            )
            onRewardEarned()
          }
          onAdClosed()
          loadRewardedAd(targetPlacement)
        }
      }
    )
  }

  fun showInterstitialAd(
    activity: Activity,
    placementId: String? = null,
    onCompleted: () -> Unit,
    onAdClosed: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val targetPlacement = placementId ?: currentInterstitialPlacement
    if (!UnityAds.isInitialized) {
      onError("Unity Ads is not initialized. Please wait or check Game ID.")
      return
    }

    val currentAdState = _interstitialAdState.value
    if (currentAdState is AdState.Error) {
      onError(currentAdState.message)
      loadInterstitialAd(targetPlacement)
      return
    }

    _interstitialAdState.value = AdState.Showing
    val showOptions = UnityAdsShowOptions()

    UnityAds.show(
      activity,
      targetPlacement,
      showOptions,
      object : IUnityAdsShowListener {
        override fun onUnityAdsShowFailure(
          placementId: String?,
          error: UnityAds.UnityAdsShowError?,
          message: String?
        ) {
          val err = "${error?.name}: ${message ?: "Interstitial failed"}"
          Log.e("UnityAdsManager", "Interstitial Show Failed: $err")
          _interstitialAdState.value = AdState.Error(err)
          onError(err)
          loadInterstitialAd(targetPlacement)
        }

        override fun onUnityAdsShowStart(placementId: String?) {
          Log.d("UnityAdsManager", "Interstitial Show Started")
        }

        override fun onUnityAdsShowClick(placementId: String?) {
          Log.d("UnityAdsManager", "Interstitial Clicked")
        }

        override fun onUnityAdsShowComplete(
          placementId: String?,
          state: UnityAds.UnityAdsShowCompletionState?
        ) {
          Log.d("UnityAdsManager", "Interstitial Show Complete: $state")
          _interstitialAdState.value = AdState.Idle
          _adStats.value = _adStats.value.copy(
            totalAdsShown = _adStats.value.totalAdsShown + 1,
            interstitialCompleted = _adStats.value.interstitialCompleted + 1,
            lastAdPlacement = targetPlacement,
            lastAdTimestamp = System.currentTimeMillis()
          )
          onCompleted()
          onAdClosed()
          loadInterstitialAd(targetPlacement)
        }
      }
    )
  }
}
