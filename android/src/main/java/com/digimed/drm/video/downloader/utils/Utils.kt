package com.digimed.drm.video.downloader.utils

import android.content.Context
import com.digimed.drm.video.downloader.extensions.getMapWith
import com.digimed.drm.video.downloader.extensions.getStringWith
import com.digimed.drm.video.downloader.models.DRMVideoRequestModel
import com.digimed.drm.video.downloader.trackers.DownloadTracker
import com.facebook.react.bridge.ReadableMap
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.cronet.CronetDataSourceFactory
import com.google.android.exoplayer2.ext.cronet.CronetEngineWrapper
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

class Utils private constructor(){
  companion object {
    private const val TAG = "Utils"

    private var dataSourceFactory: DataSource.Factory? = null
    private var httpDataSourceFactory: HttpDataSource.Factory? = null
    private var databaseProvider: DatabaseProvider? = null
    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
    private var downloadManager: DownloadManager? = null
    private var downloadTracker: DownloadTracker? = null
    private var downloadNotificationHelper: DownloadNotificationHelper? = null


    fun setup(dataSourceFactory:DataSource.Factory?,downloadCache: Cache?, downloadDirectory:  File? = null ,databaseProvider: DatabaseProvider?,
              httpDataSourceFactory:  HttpDataSource.Factory?){
      this.dataSourceFactory= dataSourceFactory
      this.downloadCache = downloadCache
      this.downloadDirectory = downloadDirectory
      this.databaseProvider = databaseProvider
      this.httpDataSourceFactory = httpDataSourceFactory
    }

    /** Returns whether extension renderers should be used.  */
    fun useExtensionRenderers(): Boolean {
      return false
    }

    fun buildRenderersFactory(
      context: Context, preferExtensionRenderer: Boolean): RenderersFactory {
      @ExtensionRendererMode val extensionRendererMode = if (useExtensionRenderers()) if (preferExtensionRenderer) DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
      return DefaultRenderersFactory(context.applicationContext)
        .setExtensionRendererMode(extensionRendererMode)
    }

    @Synchronized
    fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory? {
      var context = context
      if (httpDataSourceFactory == null) {
        context = context.applicationContext
        val cronetEngineWrapper = CronetEngineWrapper(context)
        httpDataSourceFactory = CronetDataSourceFactory(cronetEngineWrapper, Executors.newSingleThreadExecutor())
      }
      return httpDataSourceFactory
    }

    /** Returns a [DataSource.Factory].  */
    @Synchronized
    fun getDataSourceFactory(context: Context): DataSource.Factory? {
      var context = context
      if (dataSourceFactory == null) {
        context = context.applicationContext
        val upstreamFactory = DefaultDataSourceFactory(context, getHttpDataSourceFactory(context)!!)
        dataSourceFactory = buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context)!!)
      }
      return dataSourceFactory
    }

    @Synchronized
    fun getDownloadNotificationHelper(
      context: Context?): DownloadNotificationHelper? {
      if (downloadNotificationHelper == null) {
        downloadNotificationHelper = DownloadNotificationHelper(context!!, Constants.DOWNLOAD_NOTIFICATION_CHANNEL_ID)
      }
      return downloadNotificationHelper
    }

    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager? {
      ensureDownloadManagerInitialized(context)
      return downloadManager
    }

    @Synchronized
    fun getDownloadTracker(context: Context): DownloadTracker? {
      ensureDownloadManagerInitialized(context)
      return downloadTracker
    }

    @Synchronized
    private fun getDownloadCache(context: Context): Cache? {
      if (downloadCache == null) {
        Log.d(TAG, "initial getDownloadCache ")
        val downloadContentDirectory = File(getDownloadDirectory(context), Constants.DOWNLOAD_CONTENT_DIRECTORY)
        downloadCache = SimpleCache(
          downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)!!)
      }
      return downloadCache
    }

    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
      if (downloadManager == null) {
        val downloadIndex = DefaultDownloadIndex(getDatabaseProvider(context)!!)
        upgradeActionFile(
          context, Constants.DOWNLOAD_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */false)
        upgradeActionFile(
          context,
          Constants.DOWNLOAD_TRACKER_ACTION_FILE,
          downloadIndex,  /* addNewDownloadsAsCompleted= */
          true)
        downloadManager = DownloadManager(
          context,
          getDatabaseProvider(context)!!,
          getDownloadCache(context)!!,
          getHttpDataSourceFactory(context)!!,
          Executors.newFixedThreadPool( /* nThreads= */1))
        downloadTracker = DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager)
      }
    }

    @Synchronized
    private fun upgradeActionFile(
      context: Context,
      fileName: String,
      downloadIndex: DefaultDownloadIndex,
      addNewDownloadsAsCompleted: Boolean) {
      try {
        ActionFileUpgradeUtil.upgradeAndDelete(
          File(getDownloadDirectory(context), fileName),  /* downloadIdProvider= */
          null,
          downloadIndex,  /* deleteOnFailure= */
          true,
          addNewDownloadsAsCompleted)
      } catch (e: IOException) {
        Log.e(TAG, "Failed to upgrade action file: $fileName", e)
      }
    }

    @Synchronized
    private fun getDatabaseProvider(context: Context): DatabaseProvider? {
      if (databaseProvider == null) {
        databaseProvider = ExoDatabaseProvider(context)
      }
      return databaseProvider
    }

    @Synchronized
    private fun getDownloadDirectory(context: Context): File? {
      if (downloadDirectory == null) {
        downloadDirectory = context.getExternalFilesDir(/* type= */ null);
        if (downloadDirectory == null) {
          downloadDirectory = context.filesDir
        }
      }
      return downloadDirectory
    }

    private fun buildReadOnlyCacheDataSource(
      upstreamFactory: DataSource.Factory, cache: Cache): CacheDataSource.Factory? {
      return CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(upstreamFactory)
        .setCacheWriteDataSinkFactory(null)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun isValidRequest(videoRequestModel: DRMVideoRequestModel?): Boolean{
      var ret = true
      if (videoRequestModel == null || videoRequestModel?.url.isNullOrBlank() || videoRequestModel?.licenseUrl.isNullOrBlank()){
        ret = false
      }
      return ret
    }

    fun getVideoRequestModelFrom(params: ReadableMap?): DRMVideoRequestModel? {
      var ret: DRMVideoRequestModel? = null
      params?.let {
        val id = it.getStringWith(Constants.VIDEO_ID)
        val videoUrl = it.getStringWith(Constants.VIDEO_URL)
        val videoScheme = it.getStringWith(Constants.VIDEO_SCHEME)
        val licenseUrl = it.getStringWith(Constants.VIDEO_LICENSE_URL)
        val videoTitle = it.getStringWith(Constants.VIDEO_TITLE)
        val videoLicenseRequestHeader = it.getMapWith(Constants.VIDEO_LICENSE_REQUEST_HEADER)?.toHashMap()
        ret = DRMVideoRequestModel(id = id, licenseUrl = licenseUrl, scheme = videoScheme, url = videoUrl, title = videoTitle, drmLicenseRequestHeaders = videoLicenseRequestHeader);
      }
      return ret
    }
    fun release(){
      Log.d(TAG, "release cache of downloader")
      this.downloadCache?.release()
      this.downloadCache = null
      this.dataSourceFactory = null;
      this.databaseProvider = null;
      this.downloadDirectory = null;
//      this.downloadManager?.release()
      this.downloadManager = null;
      this.downloadNotificationHelper = null;
      this.downloadTracker = null;
      this.httpDataSourceFactory = null;
    }
  }
}




