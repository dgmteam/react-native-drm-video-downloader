//
//  DRMVideoRequestModel.swift
//  DrmVideoDownloader
//
//  Created by cuong.pham on 11/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

class DRMVideoRequestModel {
    var id: String?
    var url: String?
    var licenseUrl: String?
    var scheme: String?
    var title: String?
    var drmLicenseRequestHeaders: NSDictionary?
    
    init(id: String?, url:String?, licenseUrl:String?,scheme:String?,title:String?,drmLicenseRequestHeaders:NSDictionary?) {
        self.id = id
        self.url = url
        self.licenseUrl = licenseUrl
        self.scheme = scheme
        self.drmLicenseRequestHeaders = drmLicenseRequestHeaders
    }
}

@available(iOS 11.2, *)
extension DRMVideoRequestModel {
    func toAsset() -> Asset? {
        let stream = Stream.init(name: self.id?, isProtected: true, contentKeyIDList: [
            self.licenseUrl], playlistURL: self.licenseUrl, accessToken: drmLicenseRequestHeaders?.value(forKey: Constants.DOWNLOAD_ACTION_FILE))
        let urlAsset = AVURLAsset(url: URL(string: stream.playlistURL)!)
        var asset = Asset.init(stream: stream, urlAsset: urlAsset)
        return asset
    }
}
