//
//  XPush.h
//  XtremePush
//
//  Created by Sergey Shmaliy
//  Copyright (c) 2016 Xtremepush. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 *	Notification name, which will be sent when device registered in the server.
 */
extern NSString *const XPushDeviceRegistrationNotification;

/**
 *	Notification name, which will be sent when inbox badge has changed
 */
extern NSString *const XPushInboxBadgeChangeNotification;


@interface XPush : NSObject

/**
 *	You should call it in [UIApplication applicationDidFinishLaunchingWithOptions:] method after configuring the library.
 */
+ (void)applicationDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions;

/**
 *	You should call it when application calls [UIApplication application: didRegisterForRemoteNotificationsWithDeviceToken:] method.
 */
+ (void)applicationDidRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;

/**
 *	You should call it when application calls [UIApplication application: didFailToRegisterForRemoteNotificationsWithError:] method.
 */
+ (void)applicationDidFailToRegisterForRemoteNotificationsWithError:(NSError *)error;

/**
 *	You should call it when application calls [UIApplication application: didReceiveRemoteNotification:] method.
 */
+ (void)applicationDidReceiveRemoteNotification:(NSDictionary *)userInfo;

/**
 *	You should call it when application calls [UIApplication application: didReceiveLocalNotification:] method.
 */
+ (void)applicationDidReceiveLocalNotification:(UILocalNotification *)notification;




/**
 *	Register current application and this lib to receive notifications. You should call it instead of [UIApplication registerForRemoteNotificationTypes:].
 * Uses UIRemoteNotificationType or UIUserNotificationType for ios8
 */
+ (void)registerForRemoteNotificationTypes:(NSInteger)types;

/**
 * Unregister current application and this lib to receive notifications
 */
+ (void)unregisterForRemoteNotifications;

/**
 *	Should or not application reset a badge icon.
 */
+ (void)setShouldWipeBadgeNumber:(BOOL)shouldWipeBadgeNumber;




/**
 *  Switches on/off using geofence and ibeacon monitoring in the app.
 *  By default is off.
 */
+ (void)setLocationEnabled:(BOOL)locationEnabled;

/**
 *  If set to YES, application will ask about location permissions on first launch.
 *  If set to NO, you have manually ask about location permissions anytime you need.
 *  By default is YES.
 *  Has no affect is locationEnabled is set to NO.
 */
+ (void)setAsksForLocationPermissions:(BOOL)asksForLocationPermissions;

/**
 *  Ask for location permission.
 *  Use only of locationEnabled is set to YES and asksForLocationPermissions is set to NO.
 */
+ (void)askForLocationPermissions;




/**
 *	Switches on/off in-app messages.
 *  By default is off.
 */
+ (void)setInAppMessageEnabled:(BOOL)enabled;

/**
 *	Calls "eventHit" api method.
 */
+ (void)hitEvent:(NSString *)event;

/**
 *	Calls "tagHit" api method.
 */
+ (void)hitTag:(NSString *)tag;

/**
 *  Calls "tagHit" api method with value
 */
+ (void)hitTag:(NSString *)tag withValue:(NSString *)value;

/**
 *	Calls "impressionHit" api method.
 */
+ (void)hitImpression:(NSString *)impression;

/**
 *  If set to YES, application will start to batch tag hits and send it on change of application state or on call sendTags method.
 *  If set to NO, application will send tag immediately after tag hit.
 *  By default is NO.
 */
+ (void)setTagsBatchingEnabled:(BOOL)tagsBatchingEnabled;

/**
 *  Send tags batch. You should use this method only if tags batching is enabled.
 */
+ (void)sendTags;

/**
 *  If set to YES, application will start to batch impression hits and send it on change of application state or on call sendImpressions method.
 *  If set to NO, application will send impression immediately after impression hit.
 *  By default is NO.
 */
+ (void)setImpressionsBatchingEnabled:(BOOL)impressionsBatchingEnabled;

/**
 *  Send impressions batch. You should use this method only if impressions batching is enabled.
 */
+ (void)sendImpressions;

/**
 *  Set a limit for a maximum number of stored tags, impressions and sessions
 */
+ (void)setTagsStoreLimit:(NSUInteger *)limit;
+ (void)setImpressionsStoreLimit:(NSUInteger *)limit;
+ (void)setSessionsStoreLimit:(NSUInteger *)limit;




/**
 *  Switches on/off collecting IDFA.
 *  By default is off.
 */
+ (void)setAttributionsEnabled:(BOOL)attributionsEnabled;

/**
 *  Switches on/off collecting device name.
 *  By default is on.
 */
+ (void)setNameCollectingEnabled:(BOOL)nameCollectingEnabled;

/**
 *  Set external id of device which can be used then on platform to target devices
 */
+ (void)setExternalId:(NSString *)externalId;

/**
 *	Returns version of the lib.
 */
+ (NSString *)version;

/**
 *	Returns dictionary with device token and device id.
 *  XPushDeviceID - key for XtremePush device id.
 *  deviceToken - key for token.
 *  deviceID - key for device identifier (IDFV).
 *  externalID - key for external id.
 */
+ (NSDictionary *)deviceInfo;





/**
 *	Used to get a list of push notifications for current device
 */
+ (void)getPushNotificationsOffset:(NSUInteger)offset limit:(NSUInteger)limit completion:(void(^)(NSArray *pushList, NSError *error))completion;

/**
 *	Used to manually mark a push as read.
 */
+ (void)markPushAsRead:(NSString *)actionId;

/**
 *	Shows Inbox screen.
 */
+ (void)showPushListController;

/**
 *  Switches on/off user subscription
 *  By default is on.
 */
+ (void)setSubscription:(BOOL)subscription;



/**
 *  Switches on/off using inbox in the app.
 *  By default is off.
 */
+ (void)setInboxEnabled:(BOOL)inboxEnabled;

/**
 *	Shows Inbox screen.
 */
+ (void)openInbox;

/**
 *  Get Inbox badge
 */
+ (NSInteger *)getInboxBadge;



/**
 *  Change app key at runtime
 */
+ (void)setSandboxModeEnabled:(BOOL)sandboxModeEnabled;

/**
 *  Turn on/off debug mode
 */
+ (void)setDebugModeEnabled:(BOOL)debugModeEnabled;

/**
 *  Turn on/off showing of debug logs
 */
+ (void)setShouldShowDebugLogs:(BOOL)log;

/**
 *  Change server url at runtime
 */
+ (void)setServerURL:(NSString *)url;

/**
 *  Change app key at runtime
 */
+ (void)setAppKey:(NSString *)appKey;

/**
 *  Certificate pinning
 */
+ (void)setServerExpectedCertificate:(NSString *)certDataString;
+ (void)setServerExpectedCertificateFromFile:(NSString *)filePath;
@end

/**
 *  Push model
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
@property (nonatomic, readonly) BOOL        isRead;
@property (nonatomic, readonly) NSDictionary *customPayload;
@end

@interface XPInboxButton : UIButton
- (UILabel *)badge;
- (void)setBadgeSize:(int)badgeSize;
- (void)setBadgePosition:(CGPoint)badgeSize;
- (void)setBadgeColor:(UIColor *)color;
- (void)setBadgeTextColor:(UIColor *)color;
@end


