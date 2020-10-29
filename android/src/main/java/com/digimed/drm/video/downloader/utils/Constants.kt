package com.digimed.drm.video.downloader.utils

class Constants {
  companion object{
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";
    const val DOWNLOAD_ACTION_FILE = "actions"
    const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    const val VIDEO_ID = "id"
    const val VIDEO_URL = "url"
    const val VIDEO_LICENSE_URL = "licenseUrl"
    const val VIDEO_SCHEME = "scheme"
    const val VIDEO_TITLE = "title"
    const val VIDEO_LICENSE_REQUEST_HEADER = "drmLicenseRequestHeaders"

    const val RESULT_PROGRESS = "progress"
    const val RESULT_STATE = "state"
//    const val RESULT_PROGRESS = "progress"
  }
}
