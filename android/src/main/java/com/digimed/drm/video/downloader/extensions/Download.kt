package com.digimed.drm.video.downloader.extensions

import com.digimed.drm.video.downloader.utils.Constants
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.android.exoplayer2.offline.Download


fun Download.toResult(action:String, keySetId:String? = null): WritableMap {
  val ret: WritableMap = Arguments.createMap()
  var progress: Double = this.percentDownloaded.toDouble();
  if (this.state == Download.STATE_COMPLETED){
    progress = 100.0;
  }
  ret.putString(Constants.EVENT_DOWNLOAD_DRM_VIDEO_ACTION, action)
  ret.putString(Constants.VIDEO_ID, this.request.id)
  ret.putString(Constants.VIDEO_URL, this.request.uri.toString())
  ret.putDouble(Constants.RESULT_PROGRESS,progress )
  ret.putInt(Constants.RESULT_STATE, this.state)
  ret.putString(Constants.VIDEO_KEY_SET_ID, keySetId)
  return ret
}
