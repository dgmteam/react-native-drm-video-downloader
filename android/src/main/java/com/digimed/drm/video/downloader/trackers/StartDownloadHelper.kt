package com.digimed.drm.video.downloader.trackers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.digimed.drm.video.downloader.services.VideoDownloaderService
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.DrmInitData
import com.google.android.exoplayer2.drm.DrmSession.DrmSessionException
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.drm.OfflineLicenseHelper
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadHelper.LiveContentUnsupportedException
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.io.IOException
import java.lang.Exception
import javax.annotation.Nullable

class StartDownloadHelper: DownloadHelper.Callback {
  interface Listener {
    fun onOfflineLicenseFetchFailed(mediaItem: MediaItem?,exception: Exception?)
  }
  companion object {
    const val TAG = "StartDownloadHelper"
  }
  private var mediaItem: MediaItem? = null
  private var drmSessionManager: DefaultDrmSessionManager? = null
  private var downloadHelper: DownloadHelper? = null
  private var context: Context? = null
  private var mappedTrackInfo: MappedTrackInfo? = null
  private var listener: Listener? = null
  @Nullable private var keySetId: String? = null
  constructor(context: Context?, mediaItem: MediaItem?, drmSessionManager: DefaultDrmSessionManager?, downloadHelper: DownloadHelper, listener: Listener){
    this.downloadHelper = downloadHelper
    this.mediaItem = mediaItem
    this.drmSessionManager = drmSessionManager
    this.context = context
    this.listener = listener
    this.downloadHelper?.prepare(this)
  }

  override fun onPrepared(helper: DownloadHelper) {
    val format: Format? = this.getFirstFormatWithDrmInitData(helper)
    if (format == null) {
      onDownloadPrepared(helper)
      return
    }
    // The content is DRM protected. We need to acquire an offline license.
    if (Util.SDK_INT < 18) {
      Log.e(StartDownloadHelper.TAG, "Downloading DRM protected content is not supported on API versions below 18")
      return
    }
    // TODO(internal b/163107948): Support cases where DrmInitData are not in the manifest.
    if (!hasSchemaData(format?.drmInitData)) {
      Log.e(
        StartDownloadHelper.TAG, "Downloading content where DRM scheme data is not located in the manifest is not"
        + " supported")
      return
    }
    this.fetchLicense(format)
  }

  private fun fetchLicense(format: Format?){
    format?.let {
      var exception: Exception? = null
      var thread = Thread {
        val offlineLicenseHelper = OfflineLicenseHelper(this.drmSessionManager!!, DrmSessionEventListener.EventDispatcher())
        try {
          val keySetIdBytes = offlineLicenseHelper.downloadLicense(it)
          this.keySetId = String(keySetIdBytes!!)
        } catch (e: Exception) {
          exception = e
        } finally {
          offlineLicenseHelper.release()
        }
        Handler(Looper.getMainLooper()).post {
          onOfflineLicenseFetched(exception)
        }
      }
      thread.run()
    }
  }

  private fun onOfflineLicenseFetched(exception: Exception?) {
    if (exception == null){
      downloadHelper?.let {
        onDownloadPrepared(it)
      }
    } else {
      listener?.onOfflineLicenseFetchFailed(this.mediaItem,exception)
      this.release()
    }
  }


  override fun onPrepareError(helper: DownloadHelper, e: IOException) {
    val isLiveContent = e is LiveContentUnsupportedException
    val logMessage = if (isLiveContent) "Downloading live content unsupported" else "Failed to start download"
    Log.e(StartDownloadHelper.TAG, logMessage, e)
    listener?.onOfflineLicenseFetchFailed(this.mediaItem,e)
  }

  private fun onDownloadPrepared(helper: DownloadHelper) {
    startDownload()
    helper?.release()
  }

  private fun startDownload() {
    var downloadRequest = this.buildDownloadRequest()
    downloadRequest?.let {
      startDownload(it)
    }
  }

  private fun startDownload(downloadRequest: DownloadRequest) {
    context?.let {
      DownloadService.sendAddDownload(it, VideoDownloaderService::class.java, downloadRequest,  /* foreground= */false)
    }
  }

  private fun buildDownloadRequest(): DownloadRequest? {
    val keySetIdBytes = this.keySetId?.toByteArray();
    return downloadHelper?.getDownloadRequest(Util.getUtf8Bytes(Assertions.checkNotNull(mediaItem!!.mediaMetadata.title)))?.copyWithKeySetId(keySetIdBytes)
  }

  // Internal methods.
  /**
   * Returns the first [Format] with a non-null [Format.drmInitData] found in the
   * content's tracks, or null if none is found.
   */
  private fun getFirstFormatWithDrmInitData(helper: DownloadHelper): Format? {
    for (periodIndex in 0 until helper.periodCount) {
      val mappedTrackInfo = helper.getMappedTrackInfo(periodIndex)
      for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
        val trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
        for (trackGroupIndex in 0 until trackGroups.length) {
          val trackGroup = trackGroups[trackGroupIndex]
          for (formatIndex in 0 until trackGroup.length) {
            val format = trackGroup.getFormat(formatIndex)
            if (format.drmInitData != null) {
              return format
            }
          }
        }
      }
    }
    return null
  }

  /**
   * Returns whether any the [DrmInitData.SchemeData] contained in `drmInitData` has
   * non-null [DrmInitData.SchemeData.data].
   */
  private fun hasSchemaData(drmInitData: DrmInitData?): Boolean {
    drmInitData?.let {
      for (i in 0 until it.schemeDataCount) {
        if (drmInitData[i].hasData()) {
          return true
        }
      }
    }
    return false
  }

  fun getKeySetID(): String?{
    return this.keySetId;
  }

  fun  release(){
    this.downloadHelper?.release()
  }

}
