package xiamo.tk.wifiadb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import xiamo.tk.wifiadb.R;
import xiamo.tk.wifiadb.service.ToggleAdbService;

/**
 * Created by wangh on 2015-9-28-0028.
 */
public class NetworkStatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.preference_config_name),Context.MODE_PRIVATE);
//        if(sharedPreferences.getBoolean(context.getResources().getString(R.string.preference_disable_when_wifi_disconnect),false)){
            Intent closeAab=new Intent(context, ToggleAdbService.class);
            closeAab.setAction(intent.getAction());
            context.startService(closeAab);
            Log.d("NetworkStatReceiver", "接收到的消息为：" + intent.getAction()+"服务已经启动");
//        }
    }

}
