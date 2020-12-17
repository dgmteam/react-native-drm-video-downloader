import { useEffect, useState } from 'react';
import {
  DRMVideoRequestModel,
  DRMVideoState,
  DRMVideoInfo,
  DRMVideoEventName,
} from 'react-native-drm-video-downloader';
import DrmVideoDownloader from 'react-native-drm-video-downloader';
import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

export const useApp = () => {
  // const [videoUrl, setVideoUrl] = useState<string | undefined>(undefined);
  // const [videoTitle, setVideoTitle] = useState<string | undefined>(undefined);
  // const [keysetId, setKeysetId] = useState<string | undefined>(undefined);
  const [videoRequestModel, setVideoRequestModel] = useState<
    DRMVideoRequestModel | undefined
  >();
  const [videoInfo, setVideoInfo] = useState<DRMVideoInfo | undefined>();

  useEffect(() => {
    // for android
    // setVideoRequestModel({
    //   id: '495ac53d-831f-412b-98fe-5445bbecf5b1',
    //   licenseUrl: 'https://proxy.uat.widevine.com/proxy?provider=widevine_test',
    //   url: 'https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd',
    //   scheme: 'widevine',
    //   drmLicenseRequestHeaders: {
    //     Authorization:
    //       'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiI0OTVhYzUzZC04MzFmLTQxMmItOThmZS01NDQ1YmJlY2Y1YjEiLCJuYmYiOjE2MDYyNzU0NzMsImV4cCI6MTYwNjI3OTM3MywiaXNzIjoiaHR0cHM6Ly90b3BjbGFzLmNvbS52biIsImF1ZCI6InRvcGNsYXNzIn0.B9dpaMMGK1Y-YR39KuOzCuZXpcFBDngTSxDEaYHB99Y',
    //   },
    //   title: 'Demo video',
    // });

    // for ios
    setVideoRequestModel({
            id: '7360f352-d459-475e-9351-970970b378e4',
            licenseUrl: 'https://mvvuni.keydelivery.southeastasia.media.azure.net/FairPlay/?kid=7360f352-d459-475e-9351-970970b378e4',
            url: 'https://mvvuni-aase.streaming.media.azure.net/daa6aef5-c6c9-42ae-967b-ab190ad18a85/Big_Buck_Bunny_30s.ism/manifest(format=m3u8-aapl,encryption=cbcs-aapl)',
            scheme: Platform.OS === 'ios' ? 'fairplay' : 'widevine',
            drmLicenseRequestHeaders: {
              Authorization:
                'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiI3MzYwZjM1Mi1kNDU5LTQ3NWUtOTM1MS05NzA5NzBiMzc4ZTQiLCJuYmYiOjE2MDgxOTc5NDcsImV4cCI6MTYwODIwMTg0NywiaXNzIjoiaHR0cHM6Ly90b3BjbGFzLmNvbS52biIsImF1ZCI6InRvcGNsYXNzIn0.xecu5D_kZ8UShT9pqb_AlSYgy3G3rTrQY-zSN1prwpw',
            },
            contentKeyIds: [
              'skd://mvvuni-aase.streaming.media.azure.net/FairPlay/?kid=7360f352-d459-475e-9351-970970b378e4'
            ],
            title: 'Demo video',
            isProtected: true
    })
  }, []);

  useEffect(() => {
    DRMVideoState;
    if (videoRequestModel) {
      getVideoStatus();
    }
  }, [videoRequestModel]);

  useEffect(() => {
    var eventEmitter = new NativeEventEmitter(
      NativeModules.DrmVideoDownloader
    );
    var eventListenerSub = eventEmitter.addListener(
      DRMVideoEventName,
      (info?: DRMVideoInfo) => {
        console.log('info',info)
        setVideoInfo(info);
      }
    );
    return () => {
      return eventListenerSub.remove();
    };
  }, []);

  const getVideoStatus = () => {
    // DrmVideoDownloader.getDownloadableStatus(videoRequestModel).then(
    //   (state?: number) => {
    //     setVideoState(state);
    //   }
    // );

    DrmVideoDownloader.getDownloadableInfo(videoRequestModel).then(
      (videoInfo?: DRMVideoInfo) => {
        setVideoInfo(videoInfo);
      }
    );
  };

  const download = () => {
    console.log('start download video');
    DrmVideoDownloader.download(videoRequestModel).finally(() => {
      getVideoStatus();
    });
  };

  const removeVideo = () => {
    console.log('remove download video');
    DrmVideoDownloader.removeDownload(videoRequestModel).finally(() => {
      getVideoStatus();
    });
  };

  const resume = () => {
    console.log('resume download video');
    DrmVideoDownloader.resumeAllDownload().finally(() => {
      getVideoStatus();
    });
  };

  const pause = () => {
    console.log('resume download video');
    DrmVideoDownloader.pauseAllDownload().finally(() => {
      getVideoStatus();
    });
  };

  const controlDownloadVideo = () => {
    if (videoInfo) {
      switch (videoInfo.state) {
        case DRMVideoState.NOT_STARTED:
            download();
          break;
        case DRMVideoState.STATE_COMPLETED:
          {
            removeVideo();
          }
          break;
        case DRMVideoState.STATE_DOWNLOADING:
          pause();
          break;
        case DRMVideoState.STATE_QUEUED:
          resume();
          break;
        default:
          console.log('not active action for request downlaod video');
          break;
      }
    } else {
      download();
    }
  };

  const getButtonText = () => {
    switch (videoInfo?.state) {
      case DRMVideoState.NOT_STARTED:
        return 'Click to start download video';
      case DRMVideoState.STATE_COMPLETED:
        return 'Video has been downloaded complete.  Click here to delete download';
      case DRMVideoState.STATE_DOWNLOADING:
        return 'Video is downloading. Click here to pause download';
      case DRMVideoState.STATE_FAILED:
        return 'Download video is failed.Click to start download video';
      case DRMVideoState.STATE_RESTARTING:
        return 'Download video is failed.Click to start download video';
      case DRMVideoState.STATE_QUEUED:
        return 'Download video was pause. Click here to continue';
      default:
        return 'Click to start download video';
    }
  };

  const getProgressText = () => {
    if (videoInfo) {
      switch (videoInfo.state) {
        case DRMVideoState.STATE_DOWNLOADING:
          return `Download progress : ${videoInfo.progress ?? 0}`;
      }
    }
    return undefined;
  };

  return {
    videoRequestModel,
    setVideoRequestModel,
    getVideoStatus,
    videoInfo,
    controlDownloadVideo,
    getButtonText,
    getProgressText,
  };
};