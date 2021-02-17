package com.digimed.drm.video.downloader

import android.net.Uri
import com.digimed.drm.video.downloader.extensions.toResult
import com.digimed.drm.video.downloader.models.DRMVideoDownloadErrorModel
import com.digimed.drm.video.downloader.trackers.DownloadDrmVideoManager
import com.digimed.drm.video.downloader.trackers.DownloadTracker
import com.digimed.drm.video.downloader.utils.Constants
import com.digimed.drm.video.downloader.utils.Utils
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.util.Log
import java.lang.Exception
import javax.annotation.Nullable


class DrmVideoDownloaderModule : ReactContextBaseJavaModule, DownloadTracker.Listener{
  private val TAG = "DrmVideoDownloaderModule"
  constructor(reactContext: ReactApplicationContext) : super(reactContext) {
  }

  override fun getName(): String {
    return "DrmVideoDownloader"
  }

  @ReactMethod
  fun isDownloaded(params: ReadableMap, promise: Promise){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    var mediaItem = videoRequestModel?.toMediaItem()
    val isDownloaded = DownloadDrmVideoManager.getInstance().isDownload(mediaItem)
    promise.resolve(isDownloaded)
  }

  @ReactMethod
  fun pauseAllDownload(promise: Promise){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    DownloadDrmVideoManager.getInstance().pauseAllDownload()
    promise.resolve(true)
  }

  @ReactMethod
  fun removeAllDownload(promise: Promise){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    DownloadDrmVideoManager.getInstance().removeAllDownload()
    promise.resolve(true)
  }

  @ReactMethod
  fun resumeAllDownload(promise: Promise){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    DownloadDrmVideoManager.getInstance().resumeAllDownload()
    promise.resolve(true)
  }

  @ReactMethod
  fun removeDownload(params: ReadableMap, promise: Promise){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    var mediaItem = videoRequestModel?.toMediaItem()
    DownloadDrmVideoManager.getInstance().removeDownload(mediaItem)
    promise.resolve(true)
  }

  @ReactMethod
  fun download(params: ReadableMap, promise: Promise) {
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    if (Utils.isValidRequest(videoRequestModel)) {
      var mediaItem = videoRequestModel?.toMediaItem()
      DownloadDrmVideoManager.getInstance().download(reactApplicationContext, mediaItem, keyRequestProperty = videoRequestModel?.getRequestPropertyHeaders())
      promise.resolve(null)
    } else {
      promise?.reject("The request is invalid",DRMVideoDownloadErrorModel(1000, "The request is invalid", mediaId = null).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
    }
  }

  @ReactMethod
  fun getDownloadableStatus(params: ReadableMap, promise: Promise) {
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    if (Utils.isValidRequest(videoRequestModel)) {
      var mediaItem = videoRequestModel?.toMediaItem()
      val state = DownloadDrmVideoManager.getInstance().getDownloadableStatus(mediaItem)
      promise.resolve(state)
    } else {
      promise.reject("The request is invalid",DRMVideoDownloadErrorModel(1000, "The request is invalid", mediaId = null).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
    }
  }

  @ReactMethod
  fun getDownloadableInfo(params: ReadableMap, promise: Promise) {
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    val videoRequestModel = Utils.getVideoRequestModelFrom(params)
    if (Utils.isValidRequest(videoRequestModel)) {
      var mediaItem = videoRequestModel?.toMediaItem()
      val downloadInfo = DownloadDrmVideoManager.getInstance().getDownloadableInfo(mediaItem)
      promise.resolve(downloadInfo?.toResult(Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE))
    } else {
      promise.reject("The request is invalid",DRMVideoDownloadErrorModel(1000, "The request is invalid", mediaId = null).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
    }
  }

  @ReactMethod
  fun registerTrackingEvent(){
    DownloadDrmVideoManager.getInstance().initial(reactApplicationContext)
    DownloadDrmVideoManager.getInstance().addListener(this)
  }

  @ReactMethod
  fun unregisterTrackingEvent(){
    DownloadDrmVideoManager.getInstance().removeListener(this)
  }

  @ReactMethod
  fun clearAllListener(){
    DownloadDrmVideoManager.getInstance().clearListener()
  }

  @ReactMethod
  fun releaseResource(){
    DownloadDrmVideoManager.getInstance().release()
  }

  override fun onDownloadChanged(downloadManager: DownloadManager?, download: Download?, keySetId: String?) {
    if (download?.state == Download.STATE_COMPLETED){
      var downloadTracker = Utils.getDownloadTracker(this.reactApplicationContext)
      var downloadRequest = downloadTracker?.getDownloadRequest(download.request.uri)
      Log.d(TAG,  "onDownload completed");
//      downloadTracker.getDownloadRequest(checkNotNull(item.playbackProperties).uri);
    }
    sendEvent(Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME, download?.toResult(Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE, keySetId = keySetId))
  }

  override fun onDownloadFailed(mediaItem: MediaItem?, exception: Exception?) {
    sendEvent(Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME,DRMVideoDownloadErrorModel(1000, "The request download failed", mediaId = mediaItem?.mediaId).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
  }

  override fun onDownloadChangeProgress( download: Download?, keySetId: String?) {
    if (download?.state == Download.STATE_COMPLETED){
      var downloadTracker = Utils.getDownloadTracker(this.reactApplicationContext)
      var downloadRequest = downloadTracker?.getDownloadRequest(download.request.uri)
      Log.d(TAG,  "onDownload completed");
//      downloadTracker.getDownloadRequest(checkNotNull(item.playbackProperties).uri);
    }
    sendEvent(Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME, download?.toResult(Constants.EVENT_NAME_DOWNLOAD_CHANGE_PROGRESS, keySetId = keySetId))
  }

  private fun sendEvent(eventName: String,@Nullable params: WritableMap?){
    reactApplicationContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }
}


