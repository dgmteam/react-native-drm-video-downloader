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
        }
//        if (Utils.isValidRequest(videoRequestModel: videoRequestModel)) {
//            let asset = videoRequestModel?.toAsset()
//            if let _asset = asset {
//                AssetPersistenceManager.sharedManager.downloadStream(for: _asset)
//            } else {
//
//            }
////            resolve()
//        } else {
////            reject("The request is invalid")
////          promise?.reject("The request is invalid",DRMVideoDownloadErrorModel(1000, "The request is invalid", mediaId = null).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
//        }
        
    }
    @objc
    func restorePersistenceManager(){
        AssetPersistenceManager.sharedManager.restorePersistenceManager()
    }
}
