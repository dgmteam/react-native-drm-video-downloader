@objc(DrmVideoDownloader)
class DrmVideoDownloader: NSObject {
    @objc
    static func restorePersistenceManager() {
        print("restorePersistenceManager")
        AssetPersistenceManager.sharedManager.restorePersistenceManager()
    }
    
    @objc
    static func setDelegate(delegate: String?) {
        print("setDelegate")
//        AssetPersistenceManager.sharedManager.delegate = delegate
    }

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    @objc(isDownloaded:withResolver:withRejecter:)
    func isDownloaded(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Bool {
        var ret = false
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            ret = state == .downloaded
            resolve(state == .downloaded ? 1: 0)
        } else {
            reject("1000", "The request is invalid", nil)
        }
        return ret
    }
    
    @objc(download:withResolver:withRejecter:)
    func download(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            AssetListManager.sharedManager.add(asset: asset)
            if videoRequestModel?.isProtected ?? false{
                self.registerTrackingEvent()
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
//    func getDownloadableStatus(params: NSDictionary) -> Asset.DownloadState {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
//            return state
            resolve(Utils.getState(state: state))
        } else {
            reject("1000", "The request is invalid", nil)
        }
//        return .notDownloaded
    }
    
    @objc(removeDownload:withResolver:withRejecter:)
    func removeDownload(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//    func removeDownload(params: NSDictionary){
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            AssetPersistenceManager.sharedManager.cancelDownload(for: asset)
            resolve(true)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }
    
//    func delete(params: NSDictionary) {
//        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
//        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
//            AssetPersistenceManager.sharedManager.deleteAsset(asset)
//            ContentKeyManager.shared.contentKeyDelegate.deleteAllPeristableContentKeys(forAsset: asset)
//        } else {
//            reject("1000", "The request is invalid", nil)
//        }
//    }
//    @objc
//    func restorePersistenceManager(){
//        print("restorePersistenceManager")
//        AssetPersistenceManager.sharedManager.restorePersistenceManager()
//    }
    
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

extension DrmVideoDownloader {
    @objc
    func handleAssetDownloadStateChanged(_ notification: Notification){
        guard let assetStreamName = notification.userInfo![Asset.Keys.name] as? String,
            let downloadStateRawValue = notification.userInfo![Asset.Keys.downloadState] as? String,
            let downloadState = Asset.DownloadState(rawValue: downloadStateRawValue),
            let asset = AssetListManager.sharedManager.with(streamName: assetStreamName) else { return }
        print("handleAssetDownloadStateChanged")
    }
    
    @objc
    func handleAssetDownloadProgress(_ notification: Notification){
        print("handleAssetDownloadProgress")
        guard let assetStreamName = notification.userInfo![Asset.Keys.name] as? String, let asset = AssetListManager.sharedManager.with(streamName: assetStreamName) else { return }
        guard let progress = notification.userInfo![Asset.Keys.percentDownloaded] as? Double else { return }
        print("progress percent \(progress)")
       // self.downloadProgressView.setProgress(Float(progress), animated: true)    }
    }
}
