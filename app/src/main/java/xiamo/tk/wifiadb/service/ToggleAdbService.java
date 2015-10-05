package xiamo.tk.wifiadb.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import xiamo.tk.wifiadb.R;
import xiamo.tk.wifiadb.ui.RootDeviceFragment;
import xiamo.tk.wifiadb.utils.ShellUtils;
import xiamo.tk.wifiadb.utils.WifiUtils;

/**
 * Created by wangh on 2015-9-28-0028.
 */
public class ToggleAdbService extends IntentService {


    public ToggleAdbService() {
        super("ToggleAdbService");

    }
    Runnable updateUI=new Runnable() {
        Message message=null;
        @Override
        public void run() {
            message=new Message();
            message.obj=getString(R.string.service_close_adb_success);
            RootDeviceFragment.mHandler.sendMessage(message);

        }
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service",intent.getAction());

        switch (intent.getAction()) {
            case "onDestory_exit_app_disable_wifiadb":
            case "android.net.conn.CONNECTIVITY_CHANGE"://网络状态改变
                if (!WifiUtils.isWifiConnected()) {//关闭wifi adb
                    if(getSharedPreferences(getString(R.string.preference_config_name),MODE_PRIVATE).getBoolean(getString(R.string.preference_disable_when_wifi_disconnect), false)){
                        if(ShellUtils.closeWifiDebug()){
                            new Thread(updateUI).start();
                        }else {
                            Log.d("service", "关闭adb失败！");
                        }
                    }else {
                        new Thread(updateUI).start();
                    }
                }else {
                    new Thread(updateUI).start();
                }
                break;
            case "notification_close_adb_service":
                if(ShellUtils.closeWifiDebug()){
                    new Thread(updateUI).start();
                }else {
                    Log.d("service", "关闭adb失败！");
                }
                break;
            case "notification_start_adb_service":
                Log.d("notification_service","intent.getFlags()="+intent.getIntExtra("adb_port", -100));
                if (WifiUtils.isWifiConnected()){
                    if(ShellUtils.activeWifiDebug(getSharedPreferences(getString(R.string.preference_config_name), MODE_PRIVATE).getInt(getString(R.string.preference_adb_server_port), -1))){
                        new Thread(updateUI).start();
                    }
                }
                break;
        }


    }


}

