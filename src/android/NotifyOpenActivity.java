package cordova.plugin.xiaolong.xgpush;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;


public class NotifyOpenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		if (uri != null) {
			Set<String> set = uri.getQueryParameterNames();
			try {
				JSONObject data = new JSONObject();
				for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
					String name = (String) iterator.next();
					String value = uri.getQueryParameter(name);
					data.put(name, value);
				}
				if (XGPushPlugin.callbackContext == null) {
					XGPushPlugin.customContent = data;
				} else {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
					pluginResult.setKeepCallback(true);
					XGPushPlugin.callbackContext.sendPluginResult(pluginResult);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction("xgpush.xiaolong.action.main");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		startActivity(intent);
	}
}
