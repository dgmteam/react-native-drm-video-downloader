package com.digimed.drm.video.downloader.extensions

import com.digimed.drm.video.downloader.utils.Constants
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.android.exoplayer2.offline.Download


fun Download.toResult(mediaId: String?): WritableMap {
  val ret: WritableMap = Arguments.createMap()
  ret.putString(Constants.VIDEO_ID, mediaId)
  ret.putDouble(Constants.RESULT_PROGRESS, this.percentDownloaded.toDouble())
  ret.putInt(Constants.RESULT_STATE, this.state)
  return ret
}
