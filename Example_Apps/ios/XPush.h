//
//  XPush.h
//  XtremePush
//
//  Created by Xtremepush on 3/10/13.
//  Copyright (c) 2013 Xtremepush. All rights reserved.
//

#import <UIKit/UIKit.h>
/**
 *	Notification name, which will be sent when device registered in the server. If this device was registered already, notification will be sent too. In a word, thic notification will be sent when api request "deviceCreate" finished and success.
 */
extern NSString *const XPushDeviceRegistrationNotification;

@interface XPush : NSObject
/**
 *	Check sandbox mode or not. Call singletone and check sandbox mode in it.
 * Sandbox mode can switched in plist. Key for it: "XtremePushSandoxMode"
 *
 *	@return	YES, if this device in sandbox, otherwise NO.
 */
+ (BOOL)isSandboxModeOn;
/**
 *	Should or not application reset a badge icon.
 *
 *	@param	shouldWipeBadgeNumber if YES, application will reset badge icon, otherwise NO.
 */
+ (void)setShouldWipeBadgeNumber:(BOOL)shouldWipeBadgeNumber;
+ (BOOL)shouldWipeBadgeNumber;
/**
 *	Register current application and this lib to receive notifications. You should call it instead of [UIApplication registerForRemoteNotificationTypes:].
 * Uses UIRemoteNotificationType or UIUserNotificationType for ios8
 * @see  -registerForRemoteNotificationTypes:
 * @see UIApplication
 *
 * @see UIRemoteNotificationType
 */
+ (void)registerForRemoteNotificationTypes:(NSInteger)types;
/**
 * @see -unregisterForRemoteNotifications in UIApplication class.
 */
+ (void)unregisterForRemoteNotifications;
/**
 *	Creates and runs lib. You should call it in [UIApplication applicationDidFinishLaunchingWithOptions:] method.
 *
 *	@param	launchOptions	launch options from [UIApplication applicationDidFinishLaunchingWithOptions:].
 */
+ (void)applicationDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
/**
 *	You should call it when application calls [UIApplication application: didRegisterForRemoteNotificationsWithDeviceToken:] method. If device was created in the server then lib updates location and send tags which was cached, otherwise lib calls "deviceCreate" api method and saves token.
 *
 *	@param	deviceToken	device token from -application: didRegisterForRemoteNotificationsWithDeviceToken:.
 */
+ (void)applicationDidRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
/**
 *	You should call it when your app receives notification and calls -applicationDidReceiveRemoteNotification: method. Lib shows alert with notification text and calls "hitAction" api.
 *
 *	@param	userInfo	user info from -applicationDidReceiveRemoteNotification: method.
 *	@param	showAlert Show alert with push text if needed, otherwise NO.
 */
+ (void)applicationDidReceiveRemoteNotification:(NSDictionary *)userInfo showAlert:(BOOL)showAlert;
/**
 *	You should call it when your app receives notification and calls -applicationDidReceiveRemoteNotification: method. Lib shows alert with notification text and calls "hitAction" api.
 *
 *	@param	userInfo	user info from -applicationDidReceiveRemoteNotification: method.
 */
+ (void)applicationDidReceiveRemoteNotification:(NSDictionary *)userInfo;

/**
 *	Does nothing.
 */
+ (void)applicationDidFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
/**
 *	Returns version of the lib. 
 * @see XPushLibraryVersion const.
 */
+ (NSString *)version;
/**
 *	Returns dictionary with device token and device id. 
 * deviceToken - key for token
 * XPushDeviceID - key for device id.
 */
+ (NSDictionary *)deviceInfo;
/**
 *	Calls "eventHit" api method.
 */
+ (void)hitEvent:(NSString *)event;
/**
 *	Calls "tagHit" api method.
 */
+ (void)hitTag:(NSString *)tag;
/**
 *	Calls "impressionHit" api method.
 */
+ (void)hitImpression:(NSString *)impression;
/**
 *	Calls "pushList" api method.
 */
+ (void)getPushNotificationsOffset:(NSUInteger)offset limit:(NSUInteger)limit completion:(void(^)(NSArray *pushList, NSError *error))completion;
/**
 *	Shows XPPushListViewController like modal view controller. Shows Inbox screen.
 */
+ (void)showPushListController;
/**
 *  Switches on/ooff use location manager in the app. Should calls before + (void)applicationDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
 *
 */
+ (void)setLocationEnabled:(BOOL)locationEnabled;
//if location disables then does nothing
+ (void)setAsksForLocationPermissions:(BOOL)asksForLocationPermissions;
@end

/**
 * Model for XPPushListViewController. When server returns pushList then list is parsed to array of models.
 */
@interface XPPushModel : NSObject

@property (nonatomic, readonly) NSDate      *createDate;
@property (nonatomic, readonly) NSString    *pushId;
@property (nonatomic, readonly) NSString    *locationId;
@property (nonatomic, readonly) NSString    *alert;
@property (nonatomic, readonly) NSInteger   badge;
@property (nonatomic, readonly) NSString    *messageId;
@property (nonatomic, readonly) NSString    *url;
@property (nonatomic, readonly) BOOL        shouldOpenInApp;
@property (nonatomic, assign)   BOOL        isRead;

- (instancetype)initWithDictionary:(NSDictionary *)dictionary;

@end


