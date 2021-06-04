package com.digimed.drm.video.downloader.trackers

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.digimed.drm.video.downloader.services.VideoDownloaderService
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Log
import java.io.IOException
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.collections.HashMap

/** Tracks media that has been downloaded. */
class DownloadTracker : DownloadManager.Listener, StartDownloadHelper.Listener {
  /** Listens for changes in the tracked downloads.  */
  interface Listener {
    /** Called when the tracked downloads changed.  */
    fun onDownloadChanged(downloadManager: DownloadManager?, download: Download?, keySetId: String?)
    fun onDownloadFailed(mediaItem: MediaItem?, exception: java.lang.Exception?)
    fun onDownloadChangeProgress(download: Download?,keySetId: String?)
  }
  private val TAG = "DownloadTracker"
  private var context: Context? = null
  private var httpDataSourceFactory: HttpDataSource.Factory? = null
  private var listeners: CopyOnWriteArraySet<Listener>? = null
  private var downloads: HashMap<Uri, Download>? = null
  private var downloadIndex: DownloadIndex? = null
  private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
  private var timer: Timer? = null
  private var downloadManager: DownloadManager? = null
  private var isStart: Boolean = false
  private var startDownloadHelper: StartDownloadHelper? = null

  constructor(
    context: Context,
    httpDataSourceFactory: HttpDataSource.Factory?,
    downloadManager: DownloadManager?) {
    this.context = context.applicationContext
    this.httpDataSourceFactory = httpDataSourceFactory
    this.downloadManager = downloadManager
    this.timer = Timer()
    listeners = CopyOnWriteArraySet()
    downloads = HashMap()
    downloadIndex = downloadManager?.downloadIndex
    trackSelectorParameters = DownloadHelper.getDefaultTrackSelectorParameters(context)
    downloadManager?.addListener(this)
    loadDownloads()
  }

  fun addListener(listener: Listener) {
    Assertions.checkNotNull(listener)
    listeners?.add(listener)
  }

  fun removeListener(listener: Listener?) {
    listeners?.remove(listener)
  }

  fun clearListener() {
    listeners?.clear()
  }

  fun isDownloaded(mediaItem: MediaItem?): Boolean {
    val download = downloads!![Assertions.checkNotNull(mediaItem?.playbackProperties)?.uri]
    return download != null && download.state == Download.STATE_COMPLETED
  }

  fun getDownloadState(mediaItem: MediaItem?): Int {
    return this.getDownloadInfo(mediaItem)?.state ?: -1
  }

  fun getDownloadInfo(mediaItem: MediaItem?): Download? {
    var ret :Download? = null
    downloads?.let {
      ret =  it[Assertions.checkNotNull(mediaItem?.playbackProperties)?.uri]
    }
    return ret
  }

  fun getDownloadRequest(uri: Uri?): DownloadRequest? {
    val download = downloads!![uri]
    return if (download != null && download.state != Download.STATE_FAILED) download.request else null
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  fun download(mediaItem: MediaItem, renderersFactory: RenderersFactory, keyRequestProperty: Map<String, String>?) {
    val download = downloads!![Assertions.checkNotNull(mediaItem?.playbackProperties).uri]
    if (download != null && download.state == Download.STATE_COMPLETED) {
      this.onDownloadChanged(this.downloadManager!!, download, null)
      return
    } else if (download != null && download.state == Download.STATE_STOPPED) {
      DownloadService.sendRemoveDownload(context!!, VideoDownloaderService::class.java, download.request.id,  /* foreground= */false)
    }
    val drmSchemeUuid = C.WIDEVINE_UUID
    val licenseDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSourceFactory()
    val drmCallback = HttpMediaDrmCallback(mediaItem.playbackProperties!!.drmConfiguration!!.licenseUri.toString(), licenseDataSourceFactory)
    keyRequestProperty?.forEach {
      drmCallback.setKeyRequestProperty(it.key, it.value)
    }
    val drmSessionManager: DefaultDrmSessionManager = DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(drmSchemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)
    startDownloadHelper = StartDownloadHelper(this.context?.applicationContext, mediaItem, drmSessionManager, DownloadHelper.forMediaItem(
            mediaItem, DownloadHelper.getDefaultTrackSelectorParameters(context!!), renderersFactory, httpDataSourceFactory), this)
    startTrackingProgressChanged()
  }

  fun removeDownload(mediaItem: MediaItem?){
    if (mediaItem != null && mediaItem?.playbackProperties?.uri != null){
      val download = downloads!![Assertions.checkNotNull(mediaItem?.playbackProperties).uri]
      if (download != null){
//        if (download.state === Download.STATE_DOWNLOADING){
        DownloadService.sendRemoveDownload(context!!, VideoDownloaderService::class.java, download.request.id, false)
//        }
      }
    }
  }

  fun pauseAllDownload(){
    DownloadService.sendPauseDownloads(context!!, VideoDownloaderService::class.java, false)
  }

  fun removeAllDownload(){
    DownloadService.sendRemoveAllDownloads(context!!, VideoDownloaderService::class.java, false)
  }

  fun resumeAllDownload(){
    DownloadService.sendResumeDownloads(context!!, VideoDownloaderService::class.java, false)
  }

  private fun startTrackingProgressChanged(){
    if (!isStart){
      timer = Timer()
      timer?.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          if (downloadManager != null){
            if (downloadManager?.currentDownloads?.isEmpty() ?: true){
              stopTrackingProgressChanged()
            } else {
              for (download in downloadManager!!.currentDownloads) {
                Log.d("DownloadTracker", "On download changed of " + download.request.id + " has progress " + download.percentDownloaded)
                fireOnDownloadChangeProgress(download = download)
              }
            }
          }
        }
      }, 0, 2000)
      isStart = true
    }
  }

  private fun stopTrackingProgressChanged(){
    if (isStart){
      timer?.purge();
      timer?.cancel();
      isStart = false
      timer = null
    }
  }

  private fun loadDownloads() {
    try {
      downloadIndex?.getDownloads().use { loadedDownloads ->
        while (loadedDownloads?.moveToNext() == true) {
          val download = loadedDownloads.download
          downloads!![download.request.uri] = download
        }
      }
    } catch (e: IOException) {
      Log.w(TAG, "Failed to query downloads", e)
    }
  }

  fun fireOnDownloadChangeProgress( download: Download){
    listeners?.let {
      for (listener in it) {
        listener.onDownloadChangeProgress(download, keySetId = this.startDownloadHelper?.getKeySetID())
      }
    }
  }

  override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
    downloads?.put(download.request.uri, download)
    listeners?.let {
      for (listener in it) {
        listener.onDownloadChanged(downloadManager, download, keySetId =  this.startDownloadHelper?.getKeySetID())
      }
    }
    startTrackingProgressChanged()
  }

  override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
    downloads?.remove(download.request.uri)
    listeners?.let {
      for (listener in it) {
        listener.onDownloadChanged(downloadManager, download, keySetId = null)
      }
    }
  }

  override fun onOfflineLicenseFetchFailed(mediaItem: MediaItem?, exception: java.lang.Exception?) {
    listeners?.let {
      for (listener in it) {
        listener.onDownloadFailed(mediaItem, exception)
      }
    }
  }

  fun release(){
    downloadManager?.release()
    downloadManager = null;
    startDownloadHelper?.release()
    startDownloadHelper = null
  }
}
