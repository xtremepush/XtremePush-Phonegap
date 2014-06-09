//
//  AppDelefate+notification.m
//  push
//
//  Created by Dima Maleev on 6/7/14.
//
//

#import "AppDelegate.h"

@interface AppDelegate (notification)

- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions;

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (id) getCommandInstance:(NSString*)className;

@property (nonatomic, retain) NSDictionary	*launchNotification;

@end