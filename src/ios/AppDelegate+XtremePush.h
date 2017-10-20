#import "AppDelegate.h"

@interface AppDelegate (notifications)
- (void)xtremepushReplaced:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)xtremepushReplaced:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
    fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler;
- (void)xtremepushReplaced:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)userInfo;

- (void) application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forRemoteNotification:(NSDictionary *)userInfo
   completionHandler:(void (^)())completionHandler;

- (void) application:(UIApplication *)application handleActionWithIdentifier:(NSString *)identifier forLocalNotification:(UILocalNotification *)notification completionHandler:(void (^)())completionHandler;
@end
