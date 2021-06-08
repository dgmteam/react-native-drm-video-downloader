//
//  Utils.swift
//  DrmVideoDownloader
//
//  Created by cuong.pham on 11/27/20.
//  Copyright © 2020 DigiMed. All rights reserved.
//

import Foundation

class Utils {
    static func getVideoRequestModelFrom(params: NSDictionary?) -> DRMVideoRequestModel?{
        var ret:DRMVideoRequestModel?
        if let _params = params {
            let id = _params.value(forKey: Constants.VIDEO_ID) as? String
            let videoUrl = _params.value(forKey: Constants.VIDEO_URL) as? String
            let isProtected = _params.value(forKey: Constants.VIDEO_IS_PROTECTED) as? Bool
            let videoScheme = _params.value(forKey: Constants.VIDEO_SCHEME) as? String
            let licenseUrl = _params.value(forKey: Constants.VIDEO_LICENSE_URL) as? String
            let contentKeyIds = _params.value(forKey: Constants.VIDEO_CONTENT_KEY_IDS) as? [String]
            let videoTitle = _params.value(forKey: Constants.VIDEO_TITLE) as? String
            let videoLicenseRequestHeader = _params.value(forKey: Constants.VIDEO_LICENSE_REQUEST_HEADER) as? NSDictionary
            ret = DRMVideoRequestModel.init(id: id ?? "" , url: videoUrl, contentKeyIds: contentKeyIds ?? [], scheme: videoScheme, title: videoTitle,licenseUrl: licenseUrl ?? "" , drmLicenseRequestHeaders: videoLicenseRequestHeader, isProtected: isProtected)
        }
        return ret
    }
    
    static func isValidRequest(videoRequestModel: DRMVideoRequestModel?) -> Bool{
      var ret = true
        if (videoRequestModel == nil || videoRequestModel?.url == nil || videoRequestModel?.url?.isEmpty ?? false || videoRequestModel?.contentKeyIds == nil || videoRequestModel?.contentKeyIds.isEmpty ?? false){
        ret = false
      }
      return ret
    }
    
    @available(iOS 11.2, *)
    static func getState(state: Asset.DownloadState) -> Int {
        var ret = -1
        switch state {
        case .downloaded:
            ret = 3
            break
        case .downloading:
            ret = 2
            break
        case .notDownloaded:
            ret = -1
            break
        case .failed:
            ret = 4
            break
        default:
            ret = -1
            break
        }
        return ret
    }
}
