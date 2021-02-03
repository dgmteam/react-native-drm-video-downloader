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
    const val VIDEO_KEY_SET_ID = "keySetId"

    const val RESULT_PROGRESS = "progress"
    const val RESULT_STATE = "state"

    const val ERROR_CODE = "error_code"
    const val ERROR_MESSAGE = "error_message"
    const val EVENT_NAME_DOWNLOAD_CHANGE_STATE = "DOWNLOAD_CHANGE_STATE"
    const val EVENT_NAME_DOWNLOAD_FAIL = "DOWNLOAD_FAIL"
    const val EVENT_NAME_DOWNLOAD_CHANGE_PROGRESS = "DOWNLOAD_CHANGE_PROGRESS"
    const val EVENT_DOWNLOAD_DRM_VIDEO_NAME = "DownloadDrmVideo"
    const val EVENT_DOWNLOAD_DRM_VIDEO_ACTION = "action"
  }
}
