package cordova.plugin.xiaolong.xgpush;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class XGPushPlugin extends CordovaPlugin {
	public static JSONObject customContent = null;
	public static CallbackContext callbackContext;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Context context=this.cordova.getActivity();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String xiaomiAppId = preferences.getString("xiaomi_appId", "");
				String xiaomiAppKey = preferences.getString("xiaomi_appKey", "");
				XGPushConfig.setMiPushAppId(context, xiaomiAppId);
				XGPushConfig.setMiPushAppKey(context, xiaomiAppKey);
				XGPushConfig.enableOtherPush(context, true);
				XGPushManager.registerPush(context, new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						//token在设备卸载重装的时候有可能会变
						Log.d("TPush", "注册成功，设备token为：" + data);
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("bindAccount")) {
			String account = args.getString(0);
			this.bindAccount(account, callbackContext);
			return true;
		}
		if (action.equals("unBindAccount")) {
			String account = args.getString(0);
			this.unBindAccount(account, callbackContext);
			return true;
		}
		if (action.equals("listenNotifyClick")) {
			this.listenNotifyClick(callbackContext);
			return true;
		}
		return false;
	}

	private void bindAccount(String account, CallbackContext callbackContext) {
		if (account != null && account.length() > 0) {
			XGPushManager.bindAccount(this.cordova.getActivity().getApplicationContext(), account);
			callbackContext.success();
		} else {
			callbackContext.error("绑定接收推送的账号不能为空");
		}
	}

	private void unBindAccount(String account, CallbackContext callbackContext) {
		if (account != null && account.length() > 0) {
			XGPushManager.delAccount(this.cordova.getActivity().getApplicationContext(), account);
			callbackContext.success();
		} else {
			callbackContext.error("解除绑定推送的账号不能为空");
		}
	}

	private void listenNotifyClick(CallbackContext callbackContext) {
		XGPushPlugin.callbackContext = callbackContext;
		if (XGPushPlugin.customContent != null) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, customContent);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
			XGPushPlugin.customContent = null;
		}
	}
}
