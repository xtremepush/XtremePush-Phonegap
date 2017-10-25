//
//  Storage.h
//  HelloCordova
//
//  Created by MIchael Breen on 25/10/2017.
//

#import <Foundation/Foundation.h>

@interface Storage : NSObject
+ (instancetype)store;

@property(nonatomic) NSDictionary* tempUserStuff;
@property(nonatomic) BOOL isRegistered;
@property(nonatomic) NSString* identifier;

@end
