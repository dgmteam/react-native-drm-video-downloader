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
            let videoScheme = _params.value(forKey: Constants.VIDEO_SCHEME) as? String
            let licenseUrl = _params.value(forKey: Constants.VIDEO_LICENSE_URL) as? String
            let videoTitle = _params.value(forKey: Constants.VIDEO_TITLE) as? String
            let videoLicenseRequestHeader = _params.value(forKey: Constants.VIDEO_LICENSE_REQUEST_HEADER) as? NSDictionary
            ret = DRMVideoRequestModel(id: id ?? "" , url: videoUrl, licenseUrl: licenseUrl, scheme: videoScheme, title: videoTitle, drmLicenseRequestHeaders: videoLicenseRequestHeader);
        }
        return ret
    }
    
    static func isValidRequest(videoRequestModel: DRMVideoRequestModel?) -> Bool{
      var ret = true
        if (videoRequestModel == nil || videoRequestModel?.url == nil || videoRequestModel?.url?.isEmpty ?? false || videoRequestModel?.licenseUrl == nil || videoRequestModel?.licenseUrl?.isEmpty ?? false){
        ret = false
      }
      return ret
    }
}
