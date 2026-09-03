package com.example.ui.components

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.UnityAdsManager
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.unity3d.ads.UnityAds
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

@Composable
fun UnityBannerContainer(
  gameId: String = UnityAdsManager.DEFAULT_GAME_ID,
  placementId: String = UnityAdsManager.DEFAULT_BANNER_PLACEMENT,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  var bannerView by remember { mutableStateOf<BannerView?>(null) }
  var isBannerLoaded by remember { mutableStateOf(false) }
  var bannerError by remember { mutableStateOf<String?>(null) }

  DisposableEffect(placementId) {
    if (activity != null && UnityAds.isInitialized) {
      try {
        val banner = BannerView(activity, placementId, UnityBannerSize(320, 50))
        banner.listener = object : BannerView.IListener {
          override fun onBannerLoaded(bannerAdView: BannerView?) {
            Log.d("UnityBanner", "Banner loaded successfully")
            isBannerLoaded = true
            bannerError = null
          }

          override fun onBannerShown(bannerAdView: BannerView?) {
            Log.d("UnityBanner", "Banner shown")
          }

          override fun onBannerFailedToLoad(
            bannerAdView: BannerView?,
            errorInfo: BannerErrorInfo?
          ) {
            val msg = errorInfo?.errorMessage ?: "Banner load failed"
            Log.w("UnityBanner", "Banner failed: $msg")
            bannerError = msg
          }

          override fun onBannerClick(bannerAdView: BannerView?) {
            Log.d("UnityBanner", "Banner clicked")
          }

          override fun onBannerLeftApplication(bannerAdView: BannerView?) {
            Log.d("UnityBanner", "Banner left application")
          }
        }
        banner.load()
        bannerView = banner
      } catch (e: Exception) {
        Log.e("UnityBanner", "Error initializing banner: ${e.message}")
        bannerError = e.message
      }
    }

    onDispose {
      try {
        bannerView?.destroy()
        bannerView = null
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(ObsidianCard)
      .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    if (activity != null && isBannerLoaded && bannerView != null) {
      AndroidView(
        factory = {
          FrameLayout(it).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT
            )
            bannerView?.parent?.let { p -> (p as? ViewGroup)?.removeView(bannerView) }
            addView(bannerView)
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
      )
    } else {
      // Clean fallback banner card
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AdsClick,
            contentDescription = null,
            tint = GoldAccent,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Unity Ads Banner",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E3A8A))
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Placement: $placementId",
                fontSize = 9.sp,
                color = Color(0xFF93C5FD)
              )
            }
          }
          Text(
            text = if (bannerError != null) "Banner ready on live device ($bannerError)" else "Serving responsive ads via Game ID: $gameId",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1
          )
        }
      }
    }
  }
}
