@objc(DrmVideoDownloader)
class DrmVideoDownloader: RCTEventEmitter {
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
                ContentKeyManager.shared.contentKeySession.addContentKeyRecipient(asset.urlAsset)
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
            asset.state = state
            let ret = asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE)
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
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            if state == .downloaded {
                self.deleteDownload(asset: asset)
            } else {
                self.cancelDownload(asset: asset)
            }
            resolve(true)
        } else {
            reject("1000", "The request is invalid", nil)
        }
    }

    fileprivate func cancelDownload(asset:Asset) -> Void {
        AssetPersistenceManager.sharedManager.cancelDownload(for: asset)
    }

    func deleteDownload(asset:Asset) -> Void {
        AssetPersistenceManager.sharedManager.deleteAsset(asset)
        ContentKeyManager.shared.contentKeyDelegate.deleteAllPeristableContentKeys(forAsset: asset)
    }

    @objc
    func registerTrackingEvent(){
        print("register tracking event")
        AssetPersistenceManager.sharedManager.enableTrackingEvent()
        self.registerEventWithNotificationCenter()
    }

    @objc
    func unregisterTrackingEvent(){
        print("unregister tracking event")
        AssetPersistenceManager.sharedManager.disableTrackingEvent()
        self.unregisterEventWithNotificationCenter()
    }

    @objc
    func clearAllListener(){
        self.unregisterTrackingEvent()
    }

    override func supportedEvents() -> [String]! {
        return [Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME]
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
        notificationCenter.addObserver(self,
                                       selector: #selector(handleAssetDownloadFailed(_:)),
                                       name: .AssetDownloadFail, object: nil)
    }

    func unregisterEventWithNotificationCenter(){
        NotificationCenter.default.removeObserver(self,
                                                  name: .AssetDownloadStateChanged,
                                                  object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: .AssetDownloadProgress,
                                                  object: nil)
        NotificationCenter.default.removeObserver(self,
                                                  name: .AssetDownloadFail,
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
        asset.state = downloadState
        if downloadState == .downloaded {
            ContentKeyManager.shared.contentKeySession.removeContentKeyRecipient(asset.urlAsset)
        }
        self.sendEvents(eventName: Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME, params: asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE))
    }

    @objc
    func handleAssetDownloadProgress(_ notification: Notification){
        print("handleAssetDownloadProgress")
        guard let assetStreamName = notification.userInfo![Asset.Keys.name] as? String, let asset = AssetListManager.sharedManager.with(streamName: assetStreamName) else { return }
        guard let progress = notification.userInfo![Asset.Keys.percentDownloaded] as? Double else { return }
        asset.progress = progress * 100
        asset.state = .downloading
        self.sendEvents(eventName: Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME, params: asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_CHANGE_PROGRESS))
    }
    @objc
    func handleAssetDownloadFailed(_ notification: Notification){
        print("handleAssetDownloadFailed")
        if let identifier = notification.userInfo![Asset.Keys.identifier] as? String, let asset = AssetListManager.sharedManager.with(identifier: identifier) {
            self.sendEvents(eventName: Constants.EVENT_DOWNLOAD_DRM_VIDEO_NAME, params: asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_FAIL))
        }
    }
}

extension DrmVideoDownloader {
    fileprivate func sendEvents(eventName: String, params: NSDictionary?) {
        if (AssetPersistenceManager.sharedManager.isEnableTrackingEvent()){
            sendEvent(withName: eventName, body: params ?? [:])
        }
    }
}
