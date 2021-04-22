/* eslint-disable react-hooks/exhaustive-deps */
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
  const [videoRequestModel, setVideoRequestModel] = useState<
    DRMVideoRequestModel | undefined
  >();
  const [videoInfo, setVideoInfo] = useState<DRMVideoInfo | undefined>();

  useEffect(() => {
    if (Platform.OS === 'android') {
      createModelForAndroid();
    } else {
      createModelForiOS();
    }
  }, []);

  const createModelForAndroid = () => {
    setVideoRequestModel({
      id: 'b4d51cd8-adb3-4eed-bd18-e417944a5d3c',
      licenseUrl:
        'https://mvvuni.keydelivery.southeastasia.media.azure.net/FairPlay/?kid=7360f352-d459-475e-9351-970970b378e4',
      url:
        'https://mvvuni-aase.streaming.media.azure.net/f12053ab-1009-43dd-8e6e-44b1ba5000ed/Big_Buck_Bunny_30s.ism/manifest(format=mpd-time-csf,encryption=cenc)',
      scheme: 'widevine',
      drmLicenseRequestHeaders: {
        Authorization:
          'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiJiNGQ1MWNkOC1hZGIzLTRlZWQtYmQxOC1lNDE3OTQ0YTVkM2MiLCJuYmYiOjE2MDgyMDE3MzgsImV4cCI6MTYwODIwNTYzOCwiaXNzIjoiaHR0cHM6Ly90b3BjbGFzLmNvbS52biIsImF1ZCI6InRvcGNsYXNzIn0.FcPVT1eVw9-Cr0w459SFfplSSv9xGud4brfqO1rTrso',
      },
      title: 'Demo video',
      isProtected: true,
    });
  };

  const createModelForiOS = () => {
    setVideoRequestModel({
      id: 'e851bf95-3011-42c2-9556-33856c6ad0a6',
      licenseUrl: 'https://mvvuni.keydelivery.southeastasia.media.azure.net/FairPlay/?kid=e851bf95-3011-42c2-9556-33856c6ad0a6',
      url: 'https://mvvuni-aase.streaming.media.azure.net/72948a45-7fd9-4c6a-b527-8574d67ffb45/MVV_Uni_Trailer_2_d4f65e15_7f40_.ism/manifest(format=m3u8-aapl,encryption=cbcs-aapl)',
      scheme: 'fairplay',
      drmLicenseRequestHeaders: {
        Authorization:
          'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1cm46bWljcm9zb2Z0OmF6dXJlOm1lZGlhc2VydmljZXM6Y29udGVudGtleWlkZW50aWZpZXIiOiJlODUxYmY5NS0zMDExLTQyYzItOTU1Ni0zMzg1NmM2YWQwYTYiLCJuYmYiOjE2MTkwNjQ5MzcsImV4cCI6MTYxOTA2ODgzNywiaXNzIjoiaHR0cHM6Ly90b3BjbGFzLmNvbS52biIsImF1ZCI6InRvcGNsYXNzIn0.I59jbz_nLxZ3S2_K4ievh7yGTf27TUOg8N-OMTeNFy0',
      },
      contentKeyIds: [
        'skd://mvvuni-aase.streaming.media.azure.net/FairPlay/?kid=e851bf95-3011-42c2-9556-33856c6ad0a6'
      ],
      title: 'Demo video',
      isProtected: true,
    });
  };

  useEffect(() => {
    if (videoRequestModel) {
      getVideoStatus();
    }
  }, [videoRequestModel]);

  useEffect(() => {
    var eventEmitter = new NativeEventEmitter(NativeModules.DrmVideoDownloader);
    var eventListenerSub = eventEmitter.addListener(
      DRMVideoEventName,
      (info?: DRMVideoInfo) => {
        console.log('info', info);
        setVideoInfo(info);
      }
    );
    return () => {
      return eventListenerSub.remove();
    };
  }, []);

  const getVideoStatus = () => {
    DrmVideoDownloader.getDownloadableInfo(videoRequestModel).then(
      (videoInfo) => {
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
          removeVideo();
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
