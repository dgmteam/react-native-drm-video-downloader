package com.digimed.drm.video.downloader.models

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.util.Util
import java.util.HashMap

data class DRMVideoRequestModel(val id: String?, val url: String?, val licenseUrl: String?, val scheme: String?, val title: String?, val drmLicenseRequestHeaders: HashMap<String, Any>?){
  fun toMediaItem(): MediaItem?{
    var mediaBuilder = MediaItem.Builder()
    var uri = Uri.parse(this.url)
    val adaptiveMimeType: String? = Util.getAdaptiveMimeTypeForContentType(Util.inferContentType(uri, null))
    mediaBuilder.setUri(this.url)
      .setMediaMetadata(MediaMetadata.Builder().setTitle(this.title ?: this.url).build())
      .setMimeType(adaptiveMimeType)
      .setMediaId(id)
      .setDrmLicenseUri(this.licenseUrl)
    this.scheme?.let {
      mediaBuilder.setDrmUuid(Util.getDrmUuid(it))
    }
    return mediaBuilder.build()
  }

  fun getRequestPropertyHeaders(): Map<String,String>? {
    val requestHeaders: HashMap<String, String> = HashMap()
    this.drmLicenseRequestHeaders?.forEach {
      requestHeaders.put(it.key, it.value as String)
    }
    return  requestHeaders
  }
}
