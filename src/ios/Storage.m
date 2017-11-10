//
//  Storage.m
//
//  Created by MIchael Breen on 25/10/2017.
//

#import "Storage.h"

@implementation Storage

+ (instancetype)store {
    static Storage *settingsStore = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        settingsStore =
        [[Storage alloc] init];
        
    });
    
    return settingsStore;
}

@end
