#import "AppDelegate.h"

@interface AppDelegate (notifications)
- (void)xtremepushReplaced:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
- (void)xtremepushReplaced:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
- (void)xtremepushReplaced:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;
- (void)xtremepushReplaced:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)userInfo;
@end
