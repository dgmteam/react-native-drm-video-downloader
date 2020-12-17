//
//  DrmVideoDownloader.h
//  DrmVideoDownloader
//
//  Created by pahm cougn on 12/16/20.
//  Copyright © 2020 Facebook. All rights reserved.
//
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#ifndef DrmVideoDownloader_h
#define DrmVideoDownloader_h


#endif /* DrmVideoDownloader_h */

@protocol DrmVideoDownloaderDelegate <NSObject>
-(NSData*)contentCertificate;
@end

@interface DrmVideoDownloader : RCTEventEmitter <RCTBridgeModule>
+ (void)restorePersistenceManager;
+ (void)setDelegate:(NSString*)delegate;
@end
