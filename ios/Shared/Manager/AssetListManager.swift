/*
 Copyright (C) 2017 Apple Inc. All Rights Reserved.
 See LICENSE.txt for this sample’s licensing information
 
 Abstract:
 The `AssetListManager` class is an `NSObject` subclass that is responsible for providing a list of assets to present
 in the `AssetListTableViewController`.
 */

import Foundation
import AVFoundation

class AssetListManager: NSObject {
    
    // MARK: Properties
    
    /// A singleton instance of `AssetListManager`.
    static let sharedManager = AssetListManager()
    
    /// The internal array of Asset structs.
    private var assets = [Asset]()
    
    // MARK: Initialization
    
    override private init() {
        super.init()
        
        /*
         Do not setup the AssetListManager.assets until AssetPersistenceManager has
         finished restoring.  This prevents race conditions where the `AssetListManager`
         creates a list of `Asset`s that doesn't reuse already existing `AVURLAssets`
         from existng `AVAssetDownloadTasks.
         */
        #if os(iOS)
            let notificationCenter = NotificationCenter.default
            notificationCenter.addObserver(self,
                                           selector: #selector(handleAssetPersistenceManagerDidRestoreState(_:)),
                                           name: .AssetPersistenceManagerDidRestoreState,
                                           object: nil)
        #elseif os(tvOS)
            
            // Iterate over each dictionary in the array.
            for stream in StreamListManager.shared.streams {
                
                let urlAsset = AVURLAsset(url: URL(string: stream.playlistURL)!)
                
                let asset = Asset(stream: stream, urlAsset: urlAsset)
                
                self.assets.append(asset)
            }
            
            NotificationCenter.default.post(name: .AssetListManagerDidLoad,
                                            object: self)
        #endif
    }
    
    deinit {
        #if os(iOS)
            NotificationCenter.default.removeObserver(self,
                                                      name: .AssetPersistenceManagerDidRestoreState,
                                                      object: nil)
        #endif
    }
    
    // MARK: Asset access
    
    /// Returns the number of Assets.
    func numberOfAssets() -> Int {
        return assets.count
    }
    
    /// Returns an Asset for a given IndexPath.
    func asset(at index: Int) -> Asset {
        return assets[index]
    }
    
    func add(asset: Asset){
        assets.append(asset)
//        StreamListManager.shared.add(newStream: asset.stream)
    }
        
    func with(streamName:String?) -> Asset? {
        var ret: Asset? = nil
        assets.forEach { (asset) in
            if (asset.stream.name == streamName){
                ret = asset
            }
        }
        return ret
    }
    
    func with(identifier:String?) -> Asset? {
        var ret: Asset? = nil
        if let _identifier = identifier {
            assets.forEach { (asset) in
                asset.stream.contentKeyIDList?.forEach { (item) in
                    if (item == _identifier){
                        ret = asset
                    }
                }
            }
        }
        return ret
    }
    
#if os(iOS)
    @objc
    func handleAssetPersistenceManagerDidRestoreState(_ notification: Notification) {
//        DispatchQueue.main.async {
//            // Iterate over each dictionary in the array.
//            for stream in StreamListManager.shared.streams {
//                
//                // To ensure that we are reusing AVURLAssets we first find out if there is one available for an already active download.
//                if let asset = AssetPersistenceManager.sharedManager.assetForStream(withName: stream.name) {
//                    self.assets.append(asset)
//                } else {
//                    /*
//                     If an existing `AVURLAsset` is not available for an active
//                     download we then see if there is a file URL available to
//                     create an asset from.
//                     */
//                    if let asset = AssetPersistenceManager.sharedManager.localAssetForStream(withName: stream.name) {
//                        self.assets.append(asset)
//                    } else {
//                        let urlAsset = AVURLAsset(url: URL(string: stream.playlistURL)!)
//                        
//                        let asset = Asset(stream: stream, urlAsset: urlAsset)
//                        
//                        self.assets.append(asset)
//                    }
//                }
//            }
//            
//            NotificationCenter.default.post(name: .AssetListManagerDidLoad,
//                                            object: self)
//        }
    }
#endif
}

extension Notification.Name {
    
    /// Notification for when download progress has changed.
    static let AssetListManagerDidLoad = Notification.Name(rawValue: "AssetListManagerDidLoadNotification")
}
