package com.digimed.drm.video.downloader.services

import android.net.Uri
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.upstream.HttpDataSource

class WidevineOfflineLicenseFetchTask {
  private var format: Format? = null
  private var licenseUri: Uri? = null
  private var httpDataSourceFactory: HttpDataSource.Factory? = null
  private var downloadHelper: DownloadHelper? = null
  private var drmSessionManager: DefaultDrmSessionManager? = null

  constructor(
    format: Format,
    licenseUri: Uri,
    httpDataSourceFactory: HttpDataSource.Factory,
    downloadHelper: DownloadHelper,
    drmSessionManager: DefaultDrmSessionManager) {
    this.format = format
    this.licenseUri = licenseUri
    this.httpDataSourceFactory = httpDataSourceFactory
    this.downloadHelper = downloadHelper
    this.drmSessionManager = drmSessionManager
  }
  fun run(){
//    withContext(Dispatchers.IO)
  }
}
