package com.digimed.drm.video.downloader.trackers

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.digimed.drm.video.downloader.services.VideoDownloaderService
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Log
import java.io.File
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet

/** Tracks media that has been downloaded. */
class DownloadTracker : DownloadManager.Listener, StartDownloadHelper.Listener {
  /** Listens for changes in the tracked downloads.  */
  interface Listener {
    /** Called when the tracked downloads changed.  */
    fun onDownloadsChanged(downloadManager: DownloadManager?, download: Download?)
    fun onDownloadFailed(mediaItem: MediaItem?, exception: java.lang.Exception?)
  }
  private val TAG = "DownloadTracker"
  private var context: Context? = null
  private var httpDataSourceFactory: HttpDataSource.Factory? = null
  private var listeners: CopyOnWriteArraySet<Listener>? = null
  private var downloads: HashMap<Uri, Download>? = null
  private var downloadIndex: DownloadIndex? = null
  private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null

  constructor(
    context: Context,
    httpDataSourceFactory: HttpDataSource.Factory?,
    downloadManager: DownloadManager?) {
    this.context = context.applicationContext
    this.httpDataSourceFactory = httpDataSourceFactory
    listeners = CopyOnWriteArraySet()
    downloads = HashMap()
    downloadIndex = downloadManager?.downloadIndex
    trackSelectorParameters = DownloadHelper.getDefaultTrackSelectorParameters(context)
    downloadManager?.addListener(this)
    loadDownloads()
  }

  private fun addListener(listener: Listener) {
    Assertions.checkNotNull(listener)
    listeners?.add(listener)
  }

  fun removeListener(listener: Listener?) {
    listeners?.remove(listener)
  }

  fun isDownloaded(mediaItem: MediaItem?): Boolean {
    val download = downloads!![Assertions.checkNotNull(mediaItem?.playbackProperties)?.uri]
    return download != null && download.state != Download.STATE_FAILED
  }

  fun getDownloadRequest(uri: Uri?): DownloadRequest? {
    val download = downloads!![uri]
    return if (download != null && download.state != Download.STATE_FAILED) download.request else null
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  fun download(mediaItem: MediaItem, renderersFactory: RenderersFactory, listener: Listener?, keyRequestProperty: Map<String, String>?) {
    val download = downloads!![Assertions.checkNotNull(mediaItem?.playbackProperties).uri]
    if (download != null) {
      DownloadService.sendRemoveDownload(
        context!!, VideoDownloaderService::class.java, download.request.id,  /* foreground= */false)
    } else {
      listener?.let {
        this.addListener(it)
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
      val startDownloadHelper = StartDownloadHelper(this.context?.applicationContext, mediaItem, drmSessionManager, DownloadHelper.forMediaItem(
        mediaItem, DownloadHelper.getDefaultTrackSelectorParameters(context!!), renderersFactory, httpDataSourceFactory), this)
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

  override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
    downloads?.put(download.request.uri, download)
    listeners?.let {
      for (listener in it) {
        listener.onDownloadsChanged(downloadManager, download)
      }
    }
  }

  override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
    downloads?.remove(download.request.uri)
    listeners?.let {
      for (listener in it) {
        listener.onDownloadsChanged(downloadManager, download)
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

}
