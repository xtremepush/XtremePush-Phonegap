//
//  XTremePushPlugin.h
//  push
//
//  Created by Dima Maleev on 6/7/14.
//
//
#import <Cordova/CDVPlugin.h>
#import "XPush.h"


@interface XTremePushPlugin : CDVPlugin
{
    NSDictionary *notificationMessage;
    NSString *callback;
    NSString *callbackId;
    BOOL showAlerts;
    BOOL isInline;
}

@property (nonatomic, strong) NSDictionary *notificationMessage;
@property (nonatomic, copy) NSString *callback;
@property (nonatomic, copy) NSString *callbackId;
@property BOOL showAlerts;
@property BOOL isInline;

/**
 *	Register current application and this lib to receive notifications. You should call it instead of [UIApplication registerForRemoteNotificationTypes:].
 * @see  -registerForRemoteNotificationTypes:
 * @see UIApplication
 *
 * @see UIRemoteNotificationType
 */
- (void) register:(CDVInvokedUrlCommand*)command;

/**
 * @see -unregisterForRemoteNotifications in UIApplication class.
 */
- (void) unregister:(CDVInvokedUrlCommand *)command;

/**
 *	Check sandbox mode or not. Call singletone and check sandbox mode in it.
 * Sandbox mode can switched in plist. Key for it: "XtremePushSandoxMode"
 *
 *	@return	YES, if this device in sandbox, otherwise NO.
 */
- (void) isSandboxModeOn:(CDVInvokedUrlCommand *)command;

/**
 * Returns version of the lib.
 * @see XPushLibraryVersion const.
 */
- (void) version:(CDVInvokedUrlCommand *)command;

/**
 * Returns dictionary with device token and device id.
 * deviceToken - key for token
 * XPushDeviceID - key for device id.
 */
- (void) deviceInfo:(CDVInvokedUrlCommand *)command;

/**
 *	Should or not application reset a badge icon.
 *
 *	@param	shouldWipeBadgeNumber if YES, application will reset badge icon, otherwise NO.
 */
- (void) setShouldWipeBadgeNumber:(CDVInvokedUrlCommand*)command;
- (void) shouldWipeBadgeNumber:(CDVInvokedUrlCommand *)command;

/**
 *  Switches on/ooff use location manager in the app. Should calls before + (void)applicationDidFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
 *
 */
- (void) setLocationEnabled:(CDVInvokedUrlCommand*)command;

//if location disables then does nothing
- (void) setAsksForLocationPermissions:(CDVInvokedUrlCommand *)command;

/**
 *	Calls "tagHit" api method.
 */
- (void) hitTag:(CDVInvokedUrlCommand *)command;

/**
 *	Calls "impressionHit" api method.
 */
- (void) hitImpression:(CDVInvokedUrlCommand *)command;

/**
 *	Shows XPPushListViewController like modal view controller. Shows Inbox screen.
 */
- (void) showPushListController:(CDVInvokedUrlCommand *)command;

/**
 *	Calls "pushList" api method.
 */
- (void) getPushNotificationsOffset:(CDVInvokedUrlCommand *)command;


- (void)didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;

/**
 *	You should call it when application calls [UIApplication application: didRegisterForRemoteNotificationsWithDeviceToken:] method. If device was created in the server then lib updates location and send tags which was cached, otherwise lib calls "deviceCreate" api method and saves token.
 *
 *	@param	deviceToken	device token from -application: didRegisterForRemoteNotificationsWithDeviceToken:.
 */
- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;


/**
 *	You should call it when your app receives notification and calls -applicationDidReceiveRemoteNotification: method. Lib shows alert with notification text and calls "hitAction" api.
 *
 *	@param	userInfo	user info from -applicationDidReceiveRemoteNotification: method.
 *	@param	showAlert Show alert with push text if needed, otherwise NO.
 */
- (void)notificationReceived;

- (NSDictionary *) convertModelToDicitionary:(XPPushModel *)model;

@end
