#import "XtremePushPlugin.h"

@interface XtremePushPlugin()
@property NSString *pushOpenCallback;
@property NSDictionary *launchOptions;
@end

@implementation XtremePushPlugin

- (void)pluginInitialize {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didFinishLaunchingListener:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (void)didFinishLaunchingListener:(NSNotification *)notification {
    self.launchOptions = notification.userInfo;
}

- (void) register:(CDVInvokedUrlCommand *)command {    
    BOOL registerForPush = YES;
    NSDictionary *options = [command.arguments objectAtIndex:0];
    
    id appKey = [options objectForKey:@"appKey"];
    if (appKey != nil) [XPush setAppKey:appKey];
    
    id serverUrl = [options objectForKey:@"serverUrl"];
    if (serverUrl != nil) [XPush setServerURL:serverUrl];
    
    id attributionsEnabled = [options objectForKey:@"attributionsEnabled"];
    if (attributionsEnabled != nil) [XPush setAttributionsEnabled:[attributionsEnabled boolValue]];
    
    id inappMessagingEnabled = [options objectForKey:@"inappMessagingEnabled"];
    if (inappMessagingEnabled != nil) [XPush setInAppMessageEnabled:[inappMessagingEnabled boolValue]];
    
    id debugLogsEnabled = [options objectForKey:@"debugLogsEnabled"];
    if (debugLogsEnabled != nil) [XPush setShouldShowDebugLogs:[debugLogsEnabled boolValue]];
    
    id tagsBatchingEnabled = [options objectForKey:@"tagsBatchingEnabled"];
    if (tagsBatchingEnabled != nil) [XPush setTagsBatchingEnabled:[tagsBatchingEnabled boolValue]];
    
    id impressionsBatchingEnabled = [options objectForKey:@"impressionsBatchingEnabled"];
    if (impressionsBatchingEnabled != nil) [XPush setImpressionsBatchingEnabled:[impressionsBatchingEnabled boolValue]];
    
    id pushOpenCallback = [options objectForKey:@"pushOpenCallback"];
    if (pushOpenCallback != nil) self.pushOpenCallback = pushOpenCallback;
    
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
        
        id pushPermissionsRequest = [iosOptions objectForKey:@"pushPermissionsRequest"];
        if (pushPermissionsRequest != nil) registerForPush = [pushPermissionsRequest boolValue];
    }
    
    [XPush setShouldShowDebugLogs:YES];
    
    if (registerForPush) [self requestPushPermissions:nil];
    
    [XPush applicationDidFinishLaunchingWithOptions:self.launchOptions];
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

- (void) hitImpression:(CDVInvokedUrlCommand *)command {
    NSString *impression = [command.arguments objectAtIndex:0];
    [XPush hitImpression:impression];
}

- (void) hitEvent:(CDVInvokedUrlCommand *)command {
    NSString *event = [command.arguments objectAtIndex:0];
    [XPush hitEvent:event];
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

- (void) deviceInfo:(CDVInvokedUrlCommand *)command {
    NSDictionary *deviceInfo = [XPush deviceInfo];
    [self successWithDictionary:deviceInfo withCallbackId:command.callbackId];
}



- (void) callPushOpenCallback:(NSDictionary *)userInfo {
    if (self.pushOpenCallback) {
        NSMutableString *jsonStr = [NSMutableString stringWithString:@"{"];
        [self parseDictionary:userInfo intoJSON:jsonStr];
        [jsonStr appendString:@"}"];
        
        NSString * jsCallBack = [NSString stringWithFormat:@"%@(%@);", self.pushOpenCallback, jsonStr];
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

@end
