/********* XGPush.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "XGPush.h"

@interface CDVXGPush : CDVPlugin <XGPushDelegate>{
    // Member variables go here.
}
@property(nonatomic,copy) NSString* callbackId;
- (void)bindAccount:(CDVInvokedUrlCommand*)command;
- (void)unBindAccount:(CDVInvokedUrlCommand*)command;
- (void)listenNotifyClick:(CDVInvokedUrlCommand*)command;
@end

@implementation CDVXGPush

-(void)pluginInitialize{
    UInt32 id = [[[self.commandDelegate settings] objectForKey:@"ios_id"] unsignedIntValue];
    NSString* key=[[self.commandDelegate settings] objectForKey:@"ios_key"];
    if(id && key){
        [[XGPush defaultManager] startXGWithAppID:id appKey:key delegate:self];
        [[XGPush defaultManager] setEnableDebug:YES];
    }
}

- (void)bindAccount:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* account = [command.arguments objectAtIndex:0];

    if (account != nil && [account length] > 0) {
        [[XGPushTokenManager defaultTokenManager] bindWithIdentifier:account type:XGPushTokenBindTypeAccount];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"绑定推送账号不能为空"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)unBindAccount:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* account = [command.arguments objectAtIndex:0];

    if (account != nil && [account length] > 0) {
        [[XGPushTokenManager defaultTokenManager] unbindWithIdentifer: account type:XGPushTokenBindTypeAccount];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"解除绑定推送账号不能为空"];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
/*
 @param center [UNUserNotificationCenter currentNotificationCenter]
 @param response 用户对通知消息的响应对象
 @param completionHandler 回调对象，必须调用
 */
- (void)xgPushUserNotificationCenter:(nonnull UNUserNotificationCenter *)center didReceiveNotificationResponse:(nullable UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler __IOS_AVAILABLE(10.0){
    [[XGPush defaultManager] reportXGNotificationResponse:response];
    NSDictionary *userInfo=response.notification.request.content.userInfo;
    NSMutableDictionary *customContent=[NSMutableDictionary dictionaryWithCapacity:userInfo.count-2];
    for(NSString *key in userInfo){
        if(![key isEqualToString:@"aps"] &![key isEqualToString:@"xg"]){
            [customContent setObject:[userInfo valueForKey:key] forKey:key];
        }
    }
    completionHandler();
    if(self.callbackId!=nil){
        CDVPluginResult *result=[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:customContent];
        [result setKeepCallback:[NSNumber numberWithBool:YES]];
        [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    }
}
#endif

/**
 @brief 监控信鸽推送服务地启动情况

 @param isSuccess 信鸽推送是否启动成功
 @param error 信鸽推送启动错误的信息
 */
- (void)xgPushDidFinishStart:(BOOL)isSuccess error:(nullable NSError *)error{
    NSLog(@"%@", error.description);
}

/**
 收到通知消息的回调，通常此消息意味着有新数据可以读取（iOS 7.0+）

 @param application  UIApplication 实例
 @param userInfo 推送时指定的参数
 @param completionHandler 完成回调
 */
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    [[XGPush defaultManager] reportXGNotificationInfo:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
    if(self.callbackId!=nil){
        CDVPluginResult *result=[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:userInfo];
        [result setKeepCallback:[NSNumber numberWithBool:YES]];
        [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    }
}

- (void)listenNotifyClick:(CDVInvokedUrlCommand*)command{
    self.callbackId=command.callbackId;
}
@end
