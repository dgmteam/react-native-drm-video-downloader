import { useEffect, useState } from 'react';
import {
  DRMVideoRequestModel,
  DRMVideoState,
  DRMVideoInfo,
  DRMVideoEventName,
} from 'react-native-drm-video-downloader';
import DrmVideoDownloader from 'react-native-drm-video-downloader';
import { NativeEventEmitter, NativeModules } from 'react-native';

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
            id: 'c80dd297-68de-46dd-b58c-9056f29ea16e',
            licenseUrl: 'https://mvvuni.keydelivery.southeastasia.media.azure.net/FairPlay/?kid=c80dd297-68de-46dd-b58c-9056f29ea16e',
            url: 'https://mvvuni-aase.streaming.media.azure.net/c6253401-0928-43ab-adc7-ea4aad27bb34/Big_Buck_Bunny_1080_10s_1MB.ism/manifest(format=m3u8-aapl,encryption=cbcs-aapl)',
           // url: 'https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd',
            scheme: 'widevine',
            drmLicenseRequestHeaders: {
              Authorization:
                'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiJjODBkZDI5Ny02OGRlLTQ2ZGQtYjU4Yy05MDU2ZjI5ZWExNmUiLCJuYmYiOjE2MDc0OTc4ODUsImV4cCI6MTYwNzUwMTc4NSwiaXNzIjoiaHR0cHM6Ly90b3BjbGFzLmNvbS52biIsImF1ZCI6InRvcGNsYXNzIn0.5GGsj8pEgATuuVKmdiMU7BCS-865a8WOluPsiD30wuw',
            },
            contentKeyIds: [
              'skd://mvvuni-aase.streaming.media.azure.net/FairPlay/?kid=c80dd297-68de-46dd-b58c-9056f29ea16e'
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

  // useEffect(() => {
  //   var eventEmitter = new NativeEventEmitter(
  //     NativeModules.DrmVideoDownloaderExample
  //   );
  //   var eventListenerSub = eventEmitter.addListener(
  //     DRMVideoEventName,
  //     (info?: DRMVideoInfo) => {
  //       setVideoInfo(info);
  //     }
  //   );
  //   return () => {
  //     return eventListenerSub.remove();
  //   };
  // }, []);

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
