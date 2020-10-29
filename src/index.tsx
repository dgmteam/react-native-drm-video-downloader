import { NativeModules } from 'react-native';

export interface DRMVideoRequestModel{
  id?:string,
  url?: string
  licenseUrl?:string
  scheme?:string
  title?:string
  drmLicenseRequestHeaders?:object
}

type DrmVideoDownloaderType = {
  download(videoRequestModel?: DRMVideoRequestModel, onSuccess?: Function, onFailed?: Function): void
}

const { DrmVideoDownloader } = NativeModules;

export default DrmVideoDownloader as DrmVideoDownloaderType;
