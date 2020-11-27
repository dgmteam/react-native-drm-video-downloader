//
//  Constants.swift
//  DrmVideoDownloader
//
//  Created by cuong.pham on 11/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class Constants {
    let DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";
    let DOWNLOAD_ACTION_FILE = "actions"
    let DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    let DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    let VIDEO_ID = "id"
    let VIDEO_URL = "url"
    let VIDEO_LICENSE_URL = "licenseUrl"
    let VIDEO_SCHEME = "scheme"
    let VIDEO_TITLE = "title"
    let VIDEO_LICENSE_REQUEST_HEADER = "drmLicenseRequestHeaders"

    let RESULT_PROGRESS = "progress"
    let RESULT_STATE = "state"

    let ERROR_CODE = "error_code"
    let ERROR_MESSAGE = "error_message"
    let EVENT_NAME_DOWNLOAD_CHANGE_STATE = "DOWNLOAD_CHANGE_STATE"
    let EVENT_NAME_DOWNLOAD_FAIL = "DOWNLOAD_FAIL"
    let EVENT_NAME_DOWNLOAD_CHANGE_PROGRESS = "DOWNLOAD_CHANGE_PROGRESS"
    let EVENT_DOWNLOAD_DRM_VIDEO_NAME = "DownloadDrmVideo"
    let EVENT_DOWNLOAD_DRM_VIDEO_ACTION = "action"
}
