#import "XtremePushPlugin.h"

@interface XtremePushPlugin()
@property (nonatomic, strong) NSString *_receiveCallback;//callbackId
@property NSString *inboxBadgeCallback;
@property NSDictionary *launchOptions;
@end

@implementation XtremePushPlugin

static NSNotification *savedNotification;
bool foregroundNotificationsEnabledValue = true;
static NSMutableDictionary *pushNotificationBackupList;

- (void)pluginInitialize {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didFinishLaunchingListener:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (void)didFinishLaunchingListener:(NSNotification *)notification {
    self.launchOptions = notification.userInfo;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(callInboxBadgeCallback) name:XPushInboxBadgeChangeNotification object:nil];
}

#pragma Public APIs

- (void) register:(CDVInvokedUrlCommand *)command {    
    //BOOL registerForPush = YES;
    NSDictionary *options = [command.arguments objectAtIndex:0];
    
    id appKey = [options objectForKey:@"appKey"];
    if (appKey != nil) [XPush setAppKey:appKey];

    id debugLogsEnabled = [options objectForKey:@"debugLogsEnabled"];
    if (debugLogsEnabled != nil) [XPush setShouldShowDebugLogs:[debugLogsEnabled boolValue]];

    id impressionsBatchingEnabled = [options objectForKey:@"impressionsBatchingEnabled"];
    if (impressionsBatchingEnabled != nil) [XPush setImpressionsBatchingEnabled:[impressionsBatchingEnabled boolValue]];

    id inappMessagingEnabled = [options objectForKey:@"inappMessagingEnabled"];
    if (inappMessagingEnabled != nil) [XPush setInAppMessageEnabled:[inappMessagingEnabled boolValue]];

    id inboxBadgeCallback = [options objectForKey:@"inboxBadgeCallback"];
    if (inboxBadgeCallback != nil) self.inboxBadgeCallback = inboxBadgeCallback;

    id inboxEnabled = [options objectForKey:@"inboxEnabled"];
    if (inboxEnabled != nil) [XPush setInboxEnabled:[inappMessagingEnabled boolValue]];

    id receiveCallback = [options objectForKey:@"messageResponseCallback"];
    if (receiveCallback != nil) self._receiveCallback = receiveCallback;
    
    id serverUrl = [options objectForKey:@"serverUrl"];
    if (serverUrl != nil) [XPush setServerURL:serverUrl];
    
    id tagsBatchingEnabled = [options objectForKey:@"tagsBatchingEnabled"];
    if (tagsBatchingEnabled != nil) [XPush setTagsBatchingEnabled:[tagsBatchingEnabled boolValue]];

    id attributionsEnabled = [options objectForKey:@"attributionsEnabled"];
    if (attributionsEnabled != nil) [XPush setAttributionsEnabled:[attributionsEnabled boolValue]];

    id foregroundNotificationsEnabled = [options objectForKey:@"foregroundNotificationsEnabled"];
    if (foregroundNotificationsEnabled != nil) foregroundNotificationsEnabledValue = [foregroundNotificationsEnabled boolValue];

    
    NSDictionary *iosOptions = [options objectForKey:@"ios"];
    
    if (iosOptions != nil)
    {
        id nameCollectingEnabled = [iosOptions objectForKey:@"nameCollectingEnabled"];
        if (nameCollectingEnabled != nil) [XPush setNameCollectingEnabled:[nameCollectingEnabled boolValue]];
        
        id locationsEnabled = [iosOptions objectForKey:@"locationsEnabled"];
        if (locationsEnabled != nil) [XPush setLocationEnabled:[locationsEnabled boolValue]];
        
        id locationsPermissionsRequest = [iosOptions objectForKey:@"locationsPermissionsRequest"];
        if (locationsPermissionsRequest != nil) [XPush setAsksForLocationPermissions:[locationsPermissionsRequest boolValue]];
        
        id badgeWipingEnabled = [iosOptions objectForKey:@"badgeWipingEnabled"];
        if (badgeWipingEnabled != nil) [XPush setShouldWipeBadgeNumber:[badgeWipingEnabled boolValue]];
        
        // id pushPermissionsRequest = [iosOptions objectForKey:@"pushPermissionsRequest"];
        // if (pushPermissionsRequest != nil) registerForPush = [pushPermissionsRequest boolValue];
    }

    [XPush registerForRemoteNotificationTypes:XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge];
    pushNotificationBackupList = [[NSMutableDictionary alloc] init];
    [self registerXpushConfiguration];
    
    [XPush applicationDidFinishLaunchingWithOptions:self.launchOptions];
}

- (void)registerXpushConfiguration {
    
    [XPush registerForegroundNotificationOptions:^XPNotificationType(XPMessage *message) {        
            //Show notification if the specific notification has showForegroundNotifications = true
            if (foregroundNotificationsEnabledValue) {
                return XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge;
            }
            else {
                if (message.payload[@"showForegroundNotifications"] != nil) {
                    id showForegroundNotifications = message.payload[@"showForegroundNotifications"];
                    if ([showForegroundNotifications boolValue]) {
                        return XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge;
                    }
                    return XPNotificationType_None;
                }else{
                    return XPNotificationType_None;
                }
            }
        }];

    [XPush registerMessageResponseHandler: ^(XPMessageResponse * _Nonnull response) { 
        
        // Remove the entry of old notifications in the backup
        if ([pushNotificationBackupList count] > 30) {
            NSArray *keys=[pushNotificationBackupList allKeys];
            NSInteger xmin = MAXFLOAT;
            for (NSNumber *num in keys) {
                NSInteger x = num.integerValue;
                if (x < xmin) xmin = x;
            }
            [pushNotificationBackupList removeObjectForKey:[NSString stringWithFormat: @"%ld", xmin]];
        }
        //Insert in the list last notification arrived that don't have in the list yet
        if ([pushNotificationBackupList objectForKey:response.message.identifier]==nil)
        {
            [pushNotificationBackupList setObject:response.message forKey:response.message.identifier];
        }
        //Create NSMutableDictionary with message and response
        NSMutableDictionary *mapToReturn = [NSMutableDictionary new];
        [mapToReturn setObject:response.message.payload forKey:@"message"];
        
        NSMutableDictionary *responseMap = [NSMutableDictionary new];
        if(response.action!=nil){
            if(response.action.identifier != nil)
                [responseMap setObject:response.action.identifier forKey:@"identifier"];
            if(response.action.url != nil)
                [responseMap setObject:response.action.url.absoluteString forKey:@"url"];
            if(response.action.deeplink != nil)
                [responseMap setObject:response.action.deeplink forKey:@"deeplink"];
            if(response.action.type != nil){
                if(response.action.type == XPActionType_Click)
                    [responseMap setObject:@"0" forKey:@"type"];
                if(response.action.type == XPActionType_Present)
                    [responseMap setObject:@"1" forKey:@"type"];
                if(response.action.type == XPActionType_Dismiss)
                    [responseMap setObject:@"2" forKey:@"type"];
            }
        }
        
        [mapToReturn setObject:responseMap forKey:@"response"];
        
        if(self._receiveCallback != nil){
            [self callPushOpenCallback: mapToReturn];
            //NSLog(@"mapToReturn = %@", mapToReturn);
        }
        NSLog(@"!!!Notification: %@", response.message.payload);
        
    }];
    [XPush askForLocationPermissions];
}


- (void)requestPushPermissions:(CDVInvokedUrlCommand *)command {
    NSInteger types;
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.f) {
        types = UIUserNotificationTypeBadge | UIUserNotificationTypeAlert | UIUserNotificationTypeSound;
    } else {
        types = UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeBadge;
    }
    
    [XPush registerForRemoteNotificationTypes:types];
}

- (void)requestLocationsPermissions:(CDVInvokedUrlCommand *)command {
    [XPush askForLocationPermissions];
}

- (void)unregisterForRemoteNotifications:(CDVInvokedUrlCommand *)command {
    [XPush unregisterForRemoteNotifications];
}

- (void) hitTag:(CDVInvokedUrlCommand *)command {
    if ([command.arguments count] == 2) {
        NSString *tag = [command.arguments objectAtIndex:0];
        NSString *value = [command.arguments objectAtIndex:1];
        [XPush hitTag:tag withValue: value];
    } else {
        NSString *tag = [command.arguments objectAtIndex:0];
        [XPush hitTag:tag];
    }
}

- (void)hitTagWithValue:(CDVInvokedUrlCommand *)command {

    NSDictionary *options = [command.arguments objectAtIndex:0];
    NSString *tag = [options objectForKey:@"tag"];
    NSString *value = [options objectForKey:@"value"];
    
    [XPush hitTag:tag withValue: value];
}

- (void) hitEvent:(CDVInvokedUrlCommand *)command {
    NSString *event = [command.arguments objectAtIndex:0];
    [XPush hitEvent:event];
}

- (void)hitEventWithValue:(CDVInvokedUrlCommand *)command {
    NSDictionary *options = [command.arguments objectAtIndex:0];
    NSString *title = [options objectForKey:@"title"];
    if (title != nil){
        NSObject *value = [options objectForKey:@"value"];

        if([value isKindOfClass:[NSString class]])
        {
            NSString *value = [options objectForKey:@"value"];
            [XPush hitEvent:title withValue: value];
        }
        if([value isKindOfClass:[NSDictionary class]])
        {
            NSDictionary* value = [options objectForKey:@"value"];
            [XPush hitEvent:title withValues: value];
        }
    }
}

- (void) hitImpression:(CDVInvokedUrlCommand *)command {
    NSString *impression = [command.arguments objectAtIndex:0];
    [XPush hitImpression:impression];
}

- (void) sendTags:(CDVInvokedUrlCommand *)command {
    [XPush sendTags];
}

- (void) sendImpressions:(CDVInvokedUrlCommand *)command {
    [XPush sendImpressions];
}

- (void) setExternalId:(CDVInvokedUrlCommand *)command {
    NSString *externalId = [command.arguments objectAtIndex:0];
    [XPush setExternalId:externalId];
}

- (void) setSubscription:(CDVInvokedUrlCommand *)command {
    BOOL subscription = [[command.arguments objectAtIndex:0] boolValue];
    [XPush setSubscription:subscription];
}

- (void) openInbox:(CDVInvokedUrlCommand *)command {
    [XPush openInbox];
}

- (void) getInboxBadge:(CDVInvokedUrlCommand *)command {
    [self callInboxBadgeCallback];
}

- (void) deviceInfo:(CDVInvokedUrlCommand *)command {
    NSDictionary *deviceInfo = [XPush deviceInfo];
    [self successWithMessage:deviceInfo withCallbackId:command.callbackId];
}

- (void)clickMessage:(CDVInvokedUrlCommand *)command {
    NSString *idNotification = [command.arguments objectAtIndex:0];
    XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
    if (x!=nil){
        [XPush clickMessage:x];
    }else
    {
        NSLog(@"clickMessage - Invalid push notification with id = %@", idNotification);
        return;
    }
}

- (void)reportMessageClicked:(CDVInvokedUrlCommand *)command {
    NSString *idNotification = [command.arguments objectAtIndex:0];
    XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
    if (x!=nil){
        [XPush reportMessageClicked:x];
    }else
    {
        NSLog(@"Invalid push notification with id = %@", idNotification);
        return;
    }
}

- (void)reportMessageDismissed:(CDVInvokedUrlCommand *)command {
    NSString *idNotification = [command.arguments objectAtIndex:0];
    XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
    if (x!=nil){
        [XPush reportMessageDismissed:x];
    }else
    {
        NSLog(@"Invalid push notification with id = %@", idNotification);
        return;
    }
}

- (void) callPushOpenCallback:(NSDictionary *)userInfo {
    if (self._receiveCallback) {
        NSError *error; 
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:userInfo 
                                                           options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
                                                             error:&error];
        if (! jsonData) {
            NSLog(@"Got an error: %@", error);
        } else {
            NSString *jsonStr = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            NSString * jsCallBack = [NSString stringWithFormat:@"%@(%@);", self._receiveCallback, jsonStr];
            //NSLog(@"!!!jsCallBack: %@", jsCallBack);
            [self.commandDelegate evalJs:jsCallBack];
        }
    }
}

- (void) callInboxBadgeCallback {
    if (self.inboxBadgeCallback) {        
        NSString * jsCallBack = [NSString stringWithFormat:@"%@(%d);", self.inboxBadgeCallback, [XPush getInboxBadge]];
        if ([self.webView respondsToSelector:@selector(stringByEvaluatingJavaScriptFromString:)]) {
            // Cordova-iOS pre-4
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsCallBack waitUntilDone:NO];
        } else {
            // Cordova-iOS 4+
            [self.webView performSelectorOnMainThread:@selector(evaluateJavaScript:completionHandler:) withObject:jsCallBack waitUntilDone:NO];
        }
    }
}

- (void) parseDictionary:(NSDictionary *)inDictionary intoJSON:(NSMutableString *)jsonString {
    NSArray *keys = [inDictionary allKeys];
    NSString *key;
    
    for (key in keys)
    {
        id thisObject = [inDictionary objectForKey:key];
        if ([thisObject isKindOfClass:[NSDictionary class]]) {
            [self parseDictionary:thisObject intoJSON:jsonString];
        } else if ([thisObject isKindOfClass:[NSString class]]) {
            [jsonString appendFormat:@"\"%@\":\"%@\",", key,
             [[[[inDictionary objectForKey:key]
                stringByReplacingOccurrencesOfString:@"\\" withString:@"\\\\"]
               stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""]
              stringByReplacingOccurrencesOfString:@"\n" withString:@"\\n"]
             ];
        } else {
            [jsonString appendFormat:@"\"%@\":\"%@\",", key, [inDictionary objectForKey:key]];
        }
    }
}

- (void) successWithMessage:(NSString *)message withCallbackId:(NSString *)callback {
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callback];
}

- (void) successWithDictionary:(NSDictionary *)dictionary withCallbackId:(NSString *)callback {
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callback];
}

- (void) failWithMessage:(NSString *)message withError:(NSError *)error withCallbackId:(NSString *)callback {
    NSString *errorMessage = (error) ? [NSString stringWithFormat:@"%@ - %@", message, [error localizedDescription]] : message;
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callback];
}


- (void) application:(UIApplication *)application 
    handleActionWithIdentifier:(NSString *)identifier 
    forRemoteNotification:(NSDictionary *)userInfo
    completionHandler:(void (^)())completionHandler {

    [XPush application:application
           handleActionWithIdentifier:identifier
           forRemoteNotification:userInfo
           completionHandler:completionHandler];
}

- (void) application:(UIApplication *)application 
    handleActionWithIdentifier:(NSString *)identifier 
    forLocalNotification:(UILocalNotification *)notification
    completionHandler:(void (^)())completionHandler {

    [XPush application:application
           handleActionWithIdentifier:identifier
           forLocalNotification:notification
           completionHandler:completionHandler];
}

@end
