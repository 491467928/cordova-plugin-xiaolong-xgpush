package cordova.plugin.xiaolong.xgpush;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;

public class HWReceiver extends PushReceiver {
	@Override
	public void onEvent(Context context, Event arg1, Bundle arg2) {
		super.onEvent(context, arg1, arg2);
	}

	@Override
	public boolean onPushMsg(Context context, byte[] arg1, Bundle arg2) {
		//return super.onPushMsg(context, arg1, arg2);
//		try {
//			String content = new String(arg1, "UTF-8");
//			Log.i("HWPush", "收到华为透传信息:" + content);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return false;
	}

	@Override
	public void onPushMsg(Context context, byte[] arg1, String arg2) {
		super.onPushMsg(context, arg1, arg2);
	}

	@Override
	public void onPushState(Context context, boolean arg1) {
		super.onPushState(context, arg1);
	}

	@Override
	public void onToken(Context context, String arg1, Bundle arg2) {
		super.onToken(context, arg1, arg2);
	}

	@Override
	public void onToken(Context context, String arg1) {
		super.onToken(context, arg1);
	}
}
