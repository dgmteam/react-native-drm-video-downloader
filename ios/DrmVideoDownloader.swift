@available(iOS 11.2, *)
@objc(DrmVideoDownloader)
class DrmVideoDownloader: NSObject {

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    @objc(download:withResolver:withRejecter:)
    func download(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            AssetPersistenceManager.sharedManager.downloadStream(for: asset)
            AssetListManager.sharedManager.add(asset: asset)
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
            resolve(asset.toResult(action: Constants.EVENT_NAME_DOWNLOAD_CHANGE_STATE, progress: 0, state: state))
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
    
    @objc(isDownloaded:withResolver:withRejecter:)
    func isDownloaded(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params: params)
        if Utils.isValidRequest(videoRequestModel: videoRequestModel), let asset = videoRequestModel?.toAsset() {
            let state = AssetPersistenceManager.sharedManager.downloadState(for: asset)
            resolve(state == .downloaded)
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
}
