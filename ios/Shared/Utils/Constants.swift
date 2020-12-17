//
//  Constants.swift
//  DrmVideoDownloader
//
//  Created by cuong.pham on 11/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class Constants {
    static let DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";
    static let DOWNLOAD_ACTION_FILE = "actions"
    static let DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    static let DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    static let VIDEO_ID = "id"
    static let VIDEO_URL = "url"
    static let VIDEO_LICENSE_URL = "licenseUrl"
    static let VIDEO_CONTENT_KEY_IDS = "contentKeyIds"
    static let VIDEO_SCHEME = "scheme"
    static let VIDEO_TITLE = "title"
    static let VIDEO_LICENSE_REQUEST_HEADER = "drmLicenseRequestHeaders"
    static let VIDEO_HEADER_AUTHORIZATION = "Authorization"
    static let VIDEO_IS_PROTECTED = "isProtected"

    static let RESULT_PROGRESS = "progress"
    static let RESULT_STATE = "state"

    static let ERROR_CODE = "error_code"
    static let ERROR_MESSAGE = "error_message"
    static let EVENT_NAME_DOWNLOAD_CHANGE_STATE = "DOWNLOAD_CHANGE_STATE"
    static let EVENT_NAME_DOWNLOAD_FAIL = "DOWNLOAD_FAIL"
    static let EVENT_NAME_DOWNLOAD_CHANGE_PROGRESS = "DOWNLOAD_CHANGE_PROGRESS"
    static let EVENT_DOWNLOAD_DRM_VIDEO_NAME = "DownloadDrmVideo"
    static let EVENT_DOWNLOAD_DRM_VIDEO_ACTION = "action"
}
