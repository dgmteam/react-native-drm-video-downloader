@available(iOS 11.2, *)
@objc(DrmVideoDownloader)
class DrmVideoDownloader: NSObject {

//    @objc(multiply:withB:withResolver:withRejecter:)
//    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//        resolve(a*b)
//    }
    
    @objc(isDownloaded:withResolver:withRejecter:)
    func isDownloaded(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            resolve(state == .downloaded ? 1: 0)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
    @objc(download:withResolver:withRejecter:)
    func download(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            AssetListManager.sharedManager.add(asset: asset)
            if videoRequestModel?.isProtected ?? false{
                ContentKeyManager.shared.contentKeyDelegate.requestPersistableContentKeys(forAsset: asset)
            } else {
                AssetPersistenceManager.sharedManager.downloadStream(for: asset)
            }
            resolve(nil)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
    @objc(getDownloadableInfo:withResolver:withRejecter:)
    func getDownloadableInfo(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            let ret = asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE, progress: 0, state: state)
            resolve(ret)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
    @objc(getDownloadableStatus:withResolver:withRejecter:)
    func getDownloadableStatus(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            resolve(Utils.getState(state: state))
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
    @objc(removeDownload:withResolver:withRejecter:)
    func removeDownload(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            AssetPersistenceManager.sharedManager.cancelDownload(for: asset)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
    
    @objc
    func restorePersistenceManager(){
        AssetPersistenceManager.sharedManager.restorePersistenceManager()
    }
    
    @objc
    func registerTrackingEvent(){
        AssetPersistenceManager.sharedManager.enableTrackingEvent()
        self.registerEventWithNotificationCenter()
    }
    
    @objc
    func unregisterTrackingEvent(){
        AssetPersistenceManager.sharedManager.disableTrackingEvent()
        self.unregisterEventWithNotificationCenter()
    }
    
    @objc
    func clearAllListener(){
        self.unregisterTrackingEvent()
    }
}

@available(iOS 11.2, *)
extension DrmVideoDownloader {
    func registerEventWithNotificationCenter(){
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self,
                                       selector: #selector(handleAssetDownloadStateChanged(_:)),
                                       name: .AssetDownloadStateChanged, object: nil)
        notificationCenter.addObserver(self, selector: #selector(handleAssetDownloadProgress(_:)),
                                       name: .AssetDownloadProgress, object: nil)
    }
    
    func unregisterEventWithNotificationCenter(){
        NotificationCenter.default.removeObserver(self,
                                                  name: .AssetDownloadStateChanged,
                                                  object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: .AssetDownloadProgress,
                                                  object: nil)
    }
}

@available(iOS 11.2, *)
extension DrmVideoDownloader {
    @objc
    func handleAssetDownloadStateChanged(_ notification: Notification){
        print("handleAssetDownloadStateChanged")
    }
    
    @objc
    func handleAssetDownloadProgress(_ notification: Notification){
        print("handleAssetDownloadProgress")
        guard let assetStreamName = notification.userInfo![Asset.Keys.name] as? String, let asset = AssetListManager.sharedManager.with(streamName: assetStreamName) else { return }
        guard let progress = notification.userInfo![Asset.Keys.percentDownloaded] as? Double else { return }
        
       // self.downloadProgressView.setProgress(Float(progress), animated: true)    }
    }
}

//@available(iOS 11.2, *)
//extension DrmVideoDownloader {
//    func notifyDataChanged() {
//
//    }
//}
