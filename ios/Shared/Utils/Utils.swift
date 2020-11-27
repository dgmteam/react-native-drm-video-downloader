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
            let id = _params.value(forKey: Constants.VIDEO_ID)
            let videoUrl = _params.value(forKey: Constants.VIDEO_URL)
            let videoScheme = _params.value(forKey: Constants.VIDEO_SCHEME)
            let licenseUrl = _params.value(forKey: Constants.VIDEO_LICENSE_URL)
            let videoTitle = _params.value(forKey: Constants.VIDEO_TITLE)
            let videoLicenseRequestHeader = _params.value(forKey: Constants.VIDEO_LICENSE_REQUEST_HEADER) as? NSDictionary
            ret = DRMVideoRequestModel(id: id, url: videoUrl, licenseUrl: licenseUrl, scheme: videoScheme, title: videoTitle, drmLicenseRequestHeaders: videoLicenseRequestHeader);
        }
        return ret
    }
    
    static func isValidRequest(videoRequestModel: DRMVideoRequestModel?) -> Boolean{
      var ret = true
        if (videoRequestModel == null || videoRequestModel?.url == null || videoRequestModel?.url?.isEmpty() || videoRequestModel?.licenseUrl == null || videoRequestModel?.licenseUrl.isEmpty()){
        ret = false
      }
      return ret
    }
}
