import { NativeModules } from 'react-native';

export interface DRMVideoRequestModel {
  id?: string;
  url?: string;
  licenseUrl?: string;
  scheme?: string;
  title?: string;
  drmLicenseRequestHeaders?: object;
  isProtected?: boolean;
  contentKeyIds?: string[];
}

export const DRMVideoEventName = 'DownloadDrmVideo';
export const DRMVideoActionName = {
  ChangeState: 'DOWNLOAD_CHANGE_STATE',
  DownloadFail: 'DOWNLOAD_FAIL',
  UpdateProgress: 'DOWNLOAD_CHANGE_PROGRESS',
};

export const DRMVideoState = {
  NOT_STARTED: -1,
  STATE_QUEUED: 0,
  /** The download is stopped for a specified. */
  STATE_STOPPED: 1,
  /** The download is currently started. */
  STATE_DOWNLOADING: 2,
  /** The download completed. */
  STATE_COMPLETED: 3,
  /** The download failed. */
  STATE_FAILED: 4,
  /** The download is being removed. */
  STATE_REMOVING: 5,
  /** The download will restart after all downloaded data is removed. */
  STATE_RESTARTING: 7,
};

export interface DRMVideoInfo {
  id?: string;
  url?: string;
  state?: number;
  progress?: number;
  action: string;
  /**
   * The key set id of an video.
   * Only available for Android platform
   */
  keySetId?: string;
}

type DrmVideoDownloaderType = {
  download(videoRequestModel?: DRMVideoRequestModel): Promise<any>;
  isDownloaded(videoRequestModel?: DRMVideoRequestModel): Promise<boolean>;
  pauseAllDownload(): Promise<any>;
  removeAllDownload(): Promise<any>;
  resumeAllDownload(): Promise<any>;
  removeDownload(videoRequestModel?: DRMVideoRequestModel): Promise<boolean>;
  getDownloadableStatus(
    videoRequestModel?: DRMVideoRequestModel
  ): Promise<number>;
  getDownloadableInfo(
    videoRequestModel?: DRMVideoRequestModel
  ): Promise<DRMVideoInfo>;
  registerTrackingEvent(): void;
  unregisterTrackingEvent(): void;
  clearAllListener(): void;
  releaseResource(): void;
};

const { DrmVideoDownloader } = NativeModules;

export default DrmVideoDownloader as DrmVideoDownloaderType;
