#import "XtremePushPlugin.h"
#import "Storage.h"

@interface XtremePushPlugin()
@property (nonatomic, strong) NSString *_receiveCallback;//callbackId
@property (nonatomic, strong) NSString *_deeplinkCallback;
@property NSString *inboxBadgeCallback;
@property NSDictionary *launchOptions;
@end

@implementation XtremePushPlugin

static NSNotification *savedNotification;
bool foregroundNotificationsEnabledValue = true;
bool requestLocationPermissions = true;
bool requestNotificationPermissions = true;
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
    if (inboxEnabled != nil) [XPush setInboxEnabled:[inboxEnabled boolValue]];
    
    id receiveCallback = [options objectForKey:@"messageResponseCallback"];
    if (receiveCallback != nil) self._receiveCallback = receiveCallback;
    
    id deeplinkCallback = [options objectForKey:@"deeplinkCallback"];
    if (deeplinkCallback != nil) self._deeplinkCallback = deeplinkCallback;
    
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

        id beaconsEnabled = [iosOptions objectForKey:@"beaconsEnabled"];
        if (beaconsEnabled != nil) [XPush setBeaconsEnabled:[beaconsEnabled boolValue]];

        id locationsPermissionsRequest = [iosOptions objectForKey:@"locationsPermissionsRequest"];
        if (locationsPermissionsRequest != nil) requestLocationPermissions = [locationsPermissionsRequest boolValue];
        
        id badgeWipingEnabled = [iosOptions objectForKey:@"badgeWipingEnabled"];
        if (badgeWipingEnabled != nil) [XPush setShouldWipeBadgeNumber:[badgeWipingEnabled boolValue]];
        
        id pushPermissionsRequest = [iosOptions objectForKey:@"pushPermissionsRequest"];
        if (pushPermissionsRequest != nil) requestNotificationPermissions = [pushPermissionsRequest boolValue];

        id sandboxModeEnabled = [iosOptions objectForKey:@"sandboxMode"];
        if (sandboxModeEnabled != nil) [XPush setSandboxModeEnabled:[sandboxModeEnabled boolValue]];

    }
    [XPush setAsksForLocationPermissions:requestLocationPermissions];
    if (requestNotificationPermissions)
        [XPush registerForRemoteNotificationTypes:XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge];
    pushNotificationBackupList = [[NSMutableDictionary alloc] init];
    [self registerXpushConfiguration];
    [XPush setShouldProcessNotificationsFromLaunchOptions:YES];
    
    [XPush applicationDidFinishLaunchingWithOptions:self.launchOptions];
    
    Storage.store.isRegistered = true;
    //NSLog(@"tempUserInfo = %@", Storage.store.tempUserInfo);
    //NSLog(@"identifier = %@", Storage.store.identifier);
    if (Storage.store.tempUserInfo != nil) {
        if (Storage.store.identifier != nil) {
            [XPush application:[UIApplication sharedApplication] handleActionWithIdentifier:Storage.store.identifier forRemoteNotification:Storage.store.tempUserInfo  completionHandler:nil];
        }
    }
    Storage.store.tempUserInfo = nil;
    Storage.store.identifier = nil;
}

- (void)registerXpushConfiguration {
    
    [XPush registerForegroundNotificationOptions:^XPNotificationType(XPMessage *message) {
        // Show notification if the specific notification has foreground = true
        if (message.payload[@"foreground"] != nil) {
            id showForegroundNotification = message.payload[@"foreground"];
            if ([showForegroundNotification boolValue]) {
                return XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge;
            }
            return XPNotificationType_None;
        }
        if (foregroundNotificationsEnabledValue) {
            return XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge;
        }
        return XPNotificationType_None;
    }];
    
    [XPush registerDeeplinkHandler:^(NSString *x) {
        [self callDeeplinkCallback: x];
    }];
    
    [XPush registerMessageResponseHandler: ^(XPMessageResponse * _Nonnull response) {
        // Remove the surplus of old notifications in the backup
        if (response.message.identifier != nil){
            if ([pushNotificationBackupList count] > 30) {
                NSArray *keys = [pushNotificationBackupList allKeys];
                NSInteger xmin = MAXFLOAT;
                for (NSNumber *num in keys) {
                    NSInteger x = num.integerValue;
                    if (x < xmin) xmin = x;
                }
                [pushNotificationBackupList removeObjectForKey:[NSString stringWithFormat: @"%ld", xmin]];
            }
            //Insert in the list last notification arrived that is not in the list yet
            if ([pushNotificationBackupList objectForKey:response.message.identifier]==nil)
            {
                [pushNotificationBackupList setObject:response.message forKey:response.message.identifier];
            }
        }
        //Create NSMutableDictionary with message and response
        NSMutableDictionary *mapToReturn = [NSMutableDictionary new];
        //            [mapToReturn setObject:response.message.payload forKey:@"message"];
        NSMutableDictionary *messageMap = [NSMutableDictionary new];
        if(response.message!=nil){
            if(response.message.type == XPMessageType_Push)
            [messageMap setObject:@"push" forKey:@"type"];
            if(response.message.type == XPMessageType_Inapp)
            [messageMap setObject:@"inapp" forKey:@"type"];
            if(response.message.type == XPMessageType_Inbox)
            [messageMap setObject:@"inbox" forKey:@"type"];
            
            if(response.message.text!=nil)
            [messageMap setObject:response.message.text forKey:@"text"];
            if(response.message.title!=nil)
            [messageMap setObject:response.message.text forKey:@"title"];
            if(response.message.campaignIdentifier!=nil)
            [messageMap setObject:response.message.campaignIdentifier forKey:@"campaignId"];
            if(response.message.identifier!=nil)
            [messageMap setObject:response.message.identifier forKey:@"id"];
            if(response.message.data!=nil)
            [messageMap setObject:response.message.data forKey:@"data"];
            
            if([response.message.payload objectForKey:@"um"]){
                NSString *um = [response.message.payload valueForKeyPath:@"um"];
                if(([um isEqualToString:@"outside"]) && ([response.message.payload objectForKey:@"u"])){
                    [messageMap setObject:[response.message.payload objectForKey:@"u"] forKey:@"url"];
                } else if (([um isEqualToString:@"deeplink"]) && ([response.message.payload valueForKeyPath:@"u"])){
                    [messageMap setObject:[response.message.payload objectForKey:@"u"] forKey:@"deeplink"];
                }
            }
            
            if([response.message.payload objectForKey:@"url"])
            [messageMap setObject:[response.message.payload objectForKey:@"url"] forKey:@"url"];
            
            if([response.message.payload objectForKey:@"deeplink"])
            [messageMap setObject:[response.message.payload objectForKey:@"deeplink"] forKey:@"deeplink"];
            
            [mapToReturn setObject:messageMap forKey:@"message"];
        }
        
        NSMutableDictionary *responseMap = [NSMutableDictionary new];
        if(response.action!=nil){
            if(response.action.identifier != nil)
            [responseMap setObject:response.action.identifier forKey:@"action"];
            
            if(response.action.type == XPActionType_Click)
            [responseMap setObject:@"click" forKey:@"type"];
            if(response.action.type == XPActionType_Present)
            [responseMap setObject:@"present" forKey:@"type"];
            if(response.action.type == XPActionType_Dismiss)
            [responseMap setObject:@"dismiss" forKey:@"type"];
        }
        
        [mapToReturn setObject:responseMap forKey:@"response"];
        
        if(self._receiveCallback != nil){
            [self callPushOpenCallback: mapToReturn];
        }
        //NSLog(@"!!!Notification: %@", response.message.payload);
    }];
}


- (void)requestPushPermissions:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [XPush registerForRemoteNotificationTypes:XPNotificationType_Alert | XPNotificationType_Sound | XPNotificationType_Badge];
    }];
}

- (void)requestLocationsPermissions:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [XPush askForLocationPermissions];
    }];
}

- (void)unregisterForRemoteNotifications:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [XPush unregisterForRemoteNotifications];
    }];
}

- (void) hitTag:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        if ([command.arguments count] == 2) {
            NSString *tag = [command.arguments objectAtIndex:0];
            NSString *value = [command.arguments objectAtIndex:1];
            [XPush hitTag:tag withValue: value];
        } else {
            NSString *tag = [command.arguments objectAtIndex:0];
            [XPush hitTag:tag];
        }
    }];
}

- (void) registerWithToken :(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString* tokenString = [command.arguments objectAtIndex:0][@"value"];
        NSString* tokenToPass = tokenString.lowercaseString;
        [XPush applicationDidRegisterForRemoteNotificationsWithDeviceTokenString:tokenToPass];
    }];
}

- (void)hitEvent:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *title = [command.arguments objectAtIndex:0];
        if (title != nil) {
            if ([command.arguments count] == 2) {
                NSObject *value = [command.arguments objectAtIndex:1];
                if (value != nil) {
                    if ([value isKindOfClass:[NSString class]]) {
                        NSString *value = [command.arguments objectAtIndex:1];
                        [XPush hitEvent:title withValue: value];
                    }
                    if ([value isKindOfClass:[NSDictionary class]]) {
                        //ToDo Fix native support for event with values then uncomment
                        //NSDictionary *value = [command.arguments objectAtIndex:1];
                        //[XPush hitEvent:title withValues: value];
                        [XPush hitEvent:title];
                    }
                } else {
                    [XPush hitEvent:title];
                }
            } else {
                [XPush hitEvent:title];
            }
        }
    }];
}

- (void) hitImpression:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *impression = [command.arguments objectAtIndex:0];
        [XPush hitImpression:impression];
    }];
}

- (void) sendTags:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [XPush sendTags];
    }];
}

- (void) sendImpressions:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [XPush sendImpressions];
    }];
}

- (void) setExternalId:(CDVInvokedUrlCommand *)command {
    NSString *externalId = [command.arguments objectAtIndex:0];
    [XPush setExternalId:externalId];
}

- (void) setUser:(CDVInvokedUrlCommand *)command {
    NSString *userId = [command.arguments objectAtIndex:0];
    [XPush setUser:userId];
}

- (void) setTempUser:(CDVInvokedUrlCommand *)command {
    NSString *userId = [command.arguments objectAtIndex:0];
    [XPush setTempUser:userId];
}

- (void) setSubscription:(CDVInvokedUrlCommand *)command {
    BOOL subscription = [[command.arguments objectAtIndex:0] boolValue];
    [XPush setSubscription:subscription];
}


- (void) openInbox:(CDVInvokedUrlCommand *)command {
    [XPush openInbox];
}

- (void) getInboxBadge:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        [self callInboxBadgeCallback];
    }];
}

- (void) deviceInfo:(CDVInvokedUrlCommand *)command {
    NSDictionary *deviceInfo = [XPush deviceInfo];
    [self successWithMessage:deviceInfo withCallbackId:command.callbackId];
}

- (void)clickMessage:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        NSString *idNotification = [command.arguments objectAtIndex:0];
        NSString *actionNotification = [command.arguments objectAtIndex:1];
        XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
        if (x != nil) {
            if (![actionNotification isEqual:[NSNull null]]) {
                [XPush clickMessage:x actionIdentifier:actionNotification];
            } else {
                [XPush clickMessage:x];
            }
        } else {
            NSLog(@"clickMessage - Invalid push notification with id = %@", idNotification);
            return;
        }
    }];
}

- (void)reportMessageClicked:(CDVInvokedUrlCommand *)command {
    NSString *idNotification = [command.arguments objectAtIndex:0];
    NSString *actionNotification = [command.arguments objectAtIndex:1];
    XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
    if (x != nil) {
        if (![actionNotification isEqual:[NSNull null]]) {
            [XPush reportMessageClicked:x actionIdentifier:actionNotification];
        } else {
            [XPush reportMessageClicked:x];
        }
    } else {
        NSLog(@"clickMessage - Invalid push notification with id = %@", idNotification);
        return;
    }
}

- (void)reportMessageDismissed:(CDVInvokedUrlCommand *)command {
    NSString *idNotification = [command.arguments objectAtIndex:0];
    XPMessage *x = [pushNotificationBackupList objectForKey:idNotification];
    if (x != nil) {
        [XPush reportMessageDismissed:x];
    }else {
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

- (void) callDeeplinkCallback:(NSString *)x {
    if (self._deeplinkCallback) {
        NSString * jsCallBack = [NSString stringWithFormat:@"%@('%@');", self._deeplinkCallback, x];
        [self.commandDelegate evalJs:jsCallBack];
    }
}

- (void) callInboxBadgeCallback {
    if (self.inboxBadgeCallback) {
        NSString * jsCallBack = [NSString stringWithFormat:@"%@(%d);", self.inboxBadgeCallback, [XPush getInboxBadge]];
        [self.commandDelegate evalJs:jsCallBack];
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
