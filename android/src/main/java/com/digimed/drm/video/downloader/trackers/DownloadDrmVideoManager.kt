package com.digimed.drm.video.downloader.trackers

import android.content.Context
import com.digimed.drm.video.downloader.models.DRMVideoRequestModel
import com.digimed.drm.video.downloader.utils.Utils
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.offline.Download

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
    if (downloadTracker == null){
      context?.let {
        this.downloadTracker = Utils.getDownloadTracker(context.applicationContext)
      }
    }
  }

  fun download(context: Context, mediaItem: MediaItem?, keyRequestProperty: Map<String, String>?){
    mediaItem?.let {
      val renderersFactory: RenderersFactory = Utils.buildRenderersFactory( /* context= */ context, false)
      downloadTracker?.download(mediaItem = mediaItem, renderersFactory = renderersFactory, keyRequestProperty =  keyRequestProperty )
    }
  }

  fun getDownloadableStatus(mediaItem: MediaItem?): Int{
    return downloadTracker?.getDownloadState(mediaItem) ?: -1
  }

  fun getDownloadableInfo(mediaItem: MediaItem?): Download?{
    return downloadTracker?.getDownloadInfo(mediaItem)
  }

  fun isDownload(mediaItem: MediaItem?): Boolean{
    return downloadTracker?.isDownloaded(mediaItem) ?: false
  }

  fun removeAllDownload(){
    downloadTracker?.removeAllDownload()
  }

  fun removeDownload(mediaItem: MediaItem?){
    downloadTracker?.removeDownload(mediaItem)
  }

  fun resumeAllDownload(){
    downloadTracker?.resumeAllDownload()
  }

  fun pauseAllDownload(){
    downloadTracker?.pauseAllDownload()
  }

  fun addListener(listener: DownloadTracker.Listener){
    downloadTracker?.addListener(listener)
  }

  fun removeListener(listener: DownloadTracker.Listener){
    downloadTracker?.removeListener(listener)
  }

  fun clearListener(){
    downloadTracker?.clearListener()
  }

  fun release(){
    this.pauseAllDownload()
    downloadTracker?.release()
    downloadTracker = null
    Utils.release()
  }
}
