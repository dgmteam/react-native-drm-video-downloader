//
//  DRMVideoRequestModel.swift
//  DrmVideoDownloader
//
//  Created by cuong.pham on 11/27/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import AVFoundation

class DRMVideoRequestModel {
    var id: String?
    var url: String?
    var contentKeyIds: [String]
    var scheme: String?
    var title: String?
    var drmLicenseRequestHeaders: NSDictionary?
    
    init(id: String?, url:String?, contentKeyIds:[String],scheme:String?,title:String?,drmLicenseRequestHeaders:NSDictionary?) {
        self.id = id
        self.url = url
        self.contentKeyIds = contentKeyIds
        self.scheme = scheme
        self.drmLicenseRequestHeaders = drmLicenseRequestHeaders
    }
}

@available(iOS 11.2, *)
extension DRMVideoRequestModel {
    func toAsset() -> Asset? {
        let stream = Stream.init(name: self.title ?? "", isProtected: true, contentKeyIDList: self.contentKeyIds, playlistURL: self.url ?? "", header: self.drmLicenseRequestHeaders)
        let urlAsset = AVURLAsset.init(url: URL(string: stream.playlistURL)!)
        let asset = Asset.init(stream: stream, urlAsset: urlAsset)
        return asset
    }
}
