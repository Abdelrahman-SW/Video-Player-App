//
//  ISTencentAdapter.h
//  ISTencentAdapter
//
//  Created by maoz.elbaz on 10/05/2021.
//

#import <Foundation/Foundation.h>
#import "IronSource/ISBaseAdapter+Internal.h"


static NSString * const TencentAdapterVersion = @"4.3.2";
static NSString * GitHash = @"698d287e9";


//System Frameworks For Tencent Adapter
@import AdSupport;
@import AVFoundation;
@import CoreLocation;
@import CoreTelephony;
@import libxml2;
@import QuartzCore;
@import Security;
@import StoreKit;
@import SystemConfiguration;
@import WebKit;


@interface ISTencentAdapter : ISBaseAdapter

@end
