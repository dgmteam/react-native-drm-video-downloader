package com.digimed.drm.video.downloader

import android.telecom.Call
import android.util.Log
import com.digimed.drm.video.downloader.extensions.toResult
import com.digimed.drm.video.downloader.models.DRMVideoDownloadErrorModel
import com.digimed.drm.video.downloader.models.DRMVideoDownloadableStatus
import com.digimed.drm.video.downloader.models.DRMVideoRequestModel
import com.digimed.drm.video.downloader.models.ErrorCode
import com.digimed.drm.video.downloader.trackers.DownloadDrmVideoManager
import com.digimed.drm.video.downloader.trackers.DownloadTracker
import com.digimed.drm.video.downloader.utils.Utils
import com.facebook.react.bridge.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import java.lang.Exception
import com.facebook.react.bridge.Callback;


class DrmVideoDownloaderModule : ReactContextBaseJavaModule {
  constructor(reactContext: ReactApplicationContext) : super(reactContext) {
    DownloadDrmVideoManager.getInstance().initial(reactContext)
  }

  override fun getName(): String {
    return "DrmVideoDownloader"
  }

  // Example method
  // See https://facebook.github.io/react-native/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Int, b: Int, promise: Promise) {
    promise.resolve(a * b)
  }

  @ReactMethod
    fun download(params: ReadableMap, onSuccess: Callback?, onFailed: Callback?) {
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    if (Utils.isValidRequest(videoRequestModel)) {
      var mediaItem = videoRequestModel?.toMediaItem()
      mediaItem?.let {
        DownloadDrmVideoManager.getInstance().download(reactApplicationContext, mediaItem, object : DownloadTracker.Listener {
          override fun onDownloadsChanged(downloadManager: DownloadManager?, download: Download?) {
            if (videoRequestModel?.id?.equals(mediaItem?.mediaId) == true){
              if (download?.state != Download.STATE_FAILED){
                Log.e("Download", "ondownload result ")
//                promise.resolve(download?.toResult(mediaItem.mediaId))
                onSuccess?.invoke(download?.toResult(mediaItem.mediaId))
              } else {
//                promise.reject(ErrorCode.DOWNLOAD_FAILED, "Download media item was failed")
                onFailed?.invoke(DRMVideoDownloadErrorModel(1000, "Download media item was failed"))
              }
            }
          }

          override fun onDownloadFailed(mediaItem: MediaItem?, exception: Exception?) {
            if (videoRequestModel?.id?.equals(mediaItem?.mediaId) == true) {
//              promise.reject(ErrorCode.DOWNLOAD_FAILED, exception?.message)
              onFailed?.invoke(DRMVideoDownloadErrorModel(1000, exception?.message))
            }
          }
        }, keyRequestProperty = videoRequestModel?.getRequestPropertyHeaders())
      }
    } else {
//      promise.reject(ErrorCode.INVALID_PARAMS, "The request in invalid")
      onFailed?.invoke(DRMVideoDownloadErrorModel(1000, "The request in invalid"))
    }
  }

  @ReactMethod
  fun getDownloadableStatus(videoRequestModel: DRMVideoRequestModel, promise: Promise) {

  }

}
