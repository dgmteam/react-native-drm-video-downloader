package com.digimed.drm.video.downloader.trackers

import android.content.Context
import com.digimed.drm.video.downloader.models.DRMVideoRequestModel
import com.digimed.drm.video.downloader.utils.Utils
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory

class DownloadDrmVideoManager{
  init {
      //
  }
  private constructor(){

  }

  private object Holder {
    val INSTANCE = DownloadDrmVideoManager()
  }
  private var downloadTracker: DownloadTracker? = null
  companion object{
    @JvmStatic
    fun getInstance(): DownloadDrmVideoManager{
      return Holder.INSTANCE
    }
  }

   fun initial(context: Context?){
     context?.let {
       this.downloadTracker = Utils.getDownloadTracker(context)
     }
  }

  fun download(context: Context, mediaItem: MediaItem?,listener: DownloadTracker.Listener?, keyRequestProperty: Map<String, String>?){
    mediaItem?.let {
      val renderersFactory: RenderersFactory = Utils.buildRenderersFactory( /* context= */ context, false)
      downloadTracker?.download(mediaItem = mediaItem, listener = listener, renderersFactory = renderersFactory, keyRequestProperty =  keyRequestProperty )
    }
  }

  fun getDownloadableStatus(mediaItem: MediaItem?){

  }

  fun isDownload(mediaItem: MediaItem?): Boolean{
    return downloadTracker?.isDownloaded(mediaItem) ?: false
  }

  fun stopDownloads(){

  }

  fun stopDownload(downloadId: String?){

  }

  fun resumeDownloads(){

  }

  fun resumeDownload(downloadId:String?){

  }
}
