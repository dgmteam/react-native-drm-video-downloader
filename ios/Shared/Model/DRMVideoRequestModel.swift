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
    var licenseUrl: String
    var contentKeyIds: [String]
    var scheme: String?
    var title: String?
    var drmLicenseRequestHeaders: NSDictionary?
    var isProtected: Bool?
    
    init(id: String?, url:String?, contentKeyIds:[String],scheme:String?,title:String?,licenseUrl:String,drmLicenseRequestHeaders:NSDictionary?, isProtected: Bool?) {
        self.id = id
        self.url = url
        self.contentKeyIds = contentKeyIds
        self.scheme = scheme
        self.drmLicenseRequestHeaders = drmLicenseRequestHeaders
        self.title = title
        self.isProtected = isProtected
        self.licenseUrl = licenseUrl
    }
}

extension DRMVideoRequestModel {
    func toAsset() -> Asset? {
        let stream = Stream.init(name: self.id ?? "", isProtected: true, contentKeyIDList: self.contentKeyIds, playlistURL: self.url ?? "",licenseUrl: self.licenseUrl ,header: self.drmLicenseRequestHeaders)
        let localAssetUrl = self.getLocalAssetUrl(stream: stream)
        let asset = Asset.init(stream: stream, urlAsset: localAssetUrl ?? AVURLAsset(url: URL(string: stream.playlistURL)!) )
        return asset
    }
    
    
    func getLocalAssetUrl(stream: Stream) -> AVURLAsset? {
        let userDefaults = UserDefaults.standard
        guard let localFileLocation = userDefaults.value(forKey: stream.name) as? Data else { return nil }
        var bookmarkDataIsStale = false
        do {
            guard let url = try URL(resolvingBookmarkData: localFileLocation,
                                    bookmarkDataIsStale: &bookmarkDataIsStale) else {
                                        fatalError("Failed to create URL from bookmark!")
            }
            
            if bookmarkDataIsStale {
                fatalError("Bookmark data is stale!")
            }
            
            let urlAsset = AVURLAsset(url: url)
            return urlAsset
        } catch {
            fatalError("Failed to create URL from bookmark with error: \(error)")
        }
        return nil
    }
}

extension DRMVideoRequestModel {
    
}
