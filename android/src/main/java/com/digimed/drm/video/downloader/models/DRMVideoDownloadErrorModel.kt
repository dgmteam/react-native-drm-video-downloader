package com.digimed.drm.video.downloader.models

import com.digimed.drm.video.downloader.utils.Constants
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

data class DRMVideoDownloadErrorModel(val errorCode: Int,val  message: String?,val mediaId:String?) {
  fun toWritableMap(action:String):WritableMap {
    val ret: WritableMap = Arguments.createMap()
    ret.putString(Constants.EVENT_DOWNLOAD_DRM_VIDEO_ACTION, action)
    ret.putString(Constants.VIDEO_ID,mediaId)
    ret.putInt(Constants.ERROR_CODE,errorCode)
    ret.putString(Constants.ERROR_MESSAGE, message)
    return ret
  }
}
