package com.digimed.drm.video.downloader.services

import android.app.Notification
import android.content.Context
import com.digimed.drm.video.downloader.R
import com.digimed.drm.video.downloader.utils.Constants
import com.digimed.drm.video.downloader.utils.Utils
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util

class VideoDownloaderService : DownloadService {

  companion object {
    private const val JOB_ID = 1
    private const val FOREGROUND_NOTIFICATION_ID = 1602
  }

  // TODO
  constructor() : super(FOREGROUND_NOTIFICATION_ID, DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL, Constants.DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    R.string.str_notification_channel_name, 0)

  override fun getDownloadManager(): DownloadManager {
    // This will only happen once, because getDownloadManager is guaranteed to be called only once
    // in the life cycle of the process.
    val downloadManager: DownloadManager? = Utils.getDownloadManager( /* context= */this)
    val downloadNotificationHelper: DownloadNotificationHelper? = Utils.getDownloadNotificationHelper( /* context= */this)
    downloadManager?.addListener(
      TerminalStateNotificationHelper(
        this, downloadNotificationHelper!!, FOREGROUND_NOTIFICATION_ID))
    return downloadManager!!
  }

  override fun getScheduler(): PlatformScheduler? {
    var ret: PlatformScheduler? = null
    if (Util.SDK_INT >= 21) {
      ret = PlatformScheduler(this, JOB_ID)
    }
    return ret
  }

  override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
    return Utils.getDownloadNotificationHelper( /* context= */this)?.buildProgressNotification( /* context= */
      this,
      R.drawable.ic_download,  /* contentIntent= */
      null,  /* message= */
      null,
      downloads)!!
  }

  /**
   * Creates and displays notifications for downloads when they complete or fail.
   *
   *
   * This helper will outlive the lifespan of a single instance of [VideoDownloaderService].
   * It is static to avoid leaking the first [VideoDownloaderService] instance.
   */
  private class TerminalStateNotificationHelper(
    context: Context, notificationHelper: DownloadNotificationHelper, firstNotificationId: Int) : DownloadManager.Listener {
    private val context: Context = context.applicationContext
    private val notificationHelper: DownloadNotificationHelper = notificationHelper
    private var nextNotificationId: Int = firstNotificationId
    override fun onDownloadChanged(
      downloadManager: DownloadManager, download: Download, finalException: Exception?) {
      val notification: Notification = if (download.state == Download.STATE_COMPLETED) {
        notificationHelper.buildDownloadCompletedNotification(
          context,
          R.drawable.ic_download_done,  /* contentIntent= */
          null,
          Util.fromUtf8Bytes(download.request.data))
      } else if (download.state == Download.STATE_FAILED) {
        notificationHelper.buildDownloadFailedNotification(
          context,
          R.drawable.ic_download_failed,  /* contentIntent= */
          null,
          Util.fromUtf8Bytes(download.request.data))
      } else {
        return
      }
      NotificationUtil.setNotification(context, nextNotificationId++, notification)
    }

  }
}
