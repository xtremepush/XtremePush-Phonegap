#import <Cordova/CDVPlugin.h>
#import "XPush.h"

@interface XtremePushPlugin : CDVPlugin {}

- (void) register:(CDVInvokedUrlCommand*)command;
- (void) requestLocationsPermissions:(CDVInvokedUrlCommand*)command;
- (void) requestPushPermissions:(CDVInvokedUrlCommand*)command;

- (void) hitEvent:(CDVInvokedUrlCommand*)command;
- (void) hitTag:(CDVInvokedUrlCommand*)command;
- (void) hitImpression:(CDVInvokedUrlCommand*)command;
- (void) sendTags:(CDVInvokedUrlCommand*)command;
- (void) sendImpressions:(CDVInvokedUrlCommand*)command;

- (void) setExternalId:(CDVInvokedUrlCommand*)command;
- (void) setSubscription:(CDVInvokedUrlCommand*)command;
- (void) openInbox:(CDVInvokedUrlCommand*)command;
- (void) getInboxBadge:(CDVInvokedUrlCommand*)command;
- (void) deviceInfo:(CDVInvokedUrlCommand*)command;

- (void) clickMessage:(CDVInvokedUrlCommand*)command;
- (void) reportMessageClick:(CDVInvokedUrlCommand*)command;
- (void) reportMessageDismissed:(CDVInvokedUrlCommand*)command;

@end
