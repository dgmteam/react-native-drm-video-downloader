package com.digimed.drm.video.downloader.extensions

import com.facebook.react.bridge.ReadableMap

fun ReadableMap.getStringWith(key:String):String? {
  return if (hasKey(key)){
    getString(key)
  } else {
    null
  }
}
fun ReadableMap.getMapWith(key:String): ReadableMap? {
  return if (hasKey(key)){
    return getMap(key)
  } else {
    null
  }
}
