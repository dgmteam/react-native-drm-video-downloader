@objc(DrmVideoDownloader)
class DrmVideoDownloader: NSObject {

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    @objc(download:withB:withResolver:withRejecter:)
    func download(params: NSDictionary, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        let videoRequestModel = Utils.getVideoRequestModelFrom(params)
        if (Utils.isValidRequest(videoRequestModel)) {
            let asset = videoRequestModel?.toAsset()
            AssetPersistenceManager.sharedManager.downloadStream(for: asset)
            resolve()
        } else {
            reject("The request is invalid")
//          promise?.reject("The request is invalid",DRMVideoDownloadErrorModel(1000, "The request is invalid", mediaId = null).toWritableMap(Constants.EVENT_NAME_DOWNLOAD_FAIL))
        }
        
    }
}
