package xiamo.tk.wifiadb.ui;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import xiamo.tk.wifiadb.R;
import xiamo.tk.wifiadb.adapter.DrawerMenuAdapter;
import xiamo.tk.wifiadb.service.ToggleAdbService;
import xiamo.tk.wifiadb.utils.ShellUtils;
import xiamo.tk.wifiadb.utils.WifiUtils;

public class MainActivity extends AppCompatActivity implements SwitcherFragment.OnFragmentInteractionListener,RootDeviceFragment.OnFragmentInteractionListener {


    private DrawerLayout mDrawerLayout;
    private ListView mlvLeftMenu;
    private DrawerMenuAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences mConfigCtrol;
    private SharedPreferences.Editor mConfigEditor;


    private boolean runFirstTime;
    private CoordinatorLayout adapterSnackerBar;


    private void findView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        mlvLeftMenu = (ListView) findViewById(R.id.nav_lv);
        adapterSnackerBar = (CoordinatorLayout) findViewById(R.id.main_layout_layoutRoot);
    }

    private void initAdapter() {
        mAdapter = new DrawerMenuAdapter(this, mConfigCtrol, adapterSnackerBar);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setCheckable(true);//改变Item选中状态
                mDrawerLayout.closeDrawers();//关闭导航菜单
                return true;
            }
        });
    }

    private void initConfigData() {
        mConfigCtrol = getSharedPreferences(getResources().getString(R.string.preference_config_name), MODE_PRIVATE);
        mConfigEditor = mConfigCtrol.edit();
        runFirstTime = mConfigCtrol.getBoolean(getResources().getString(R.string.preference_is_first_run), true);
        Log.d("获取is_first_run的值", "is_first_run的值为+" + runFirstTime);
        if (runFirstTime) {//第一次运行
            setRunFirstTimeData(mConfigEditor);
        }
    }

    /**
     * 没有保存是否Root的状态
     *
     * @param editor
     */
    private void setRunFirstTimeData(SharedPreferences.Editor editor) {
        try {
            editor.putBoolean(getResources().getString(R.string.preference_is_first_run), false);
            editor.putBoolean(getResources().getString(R.string.preference_notify_when_active), true);
            editor.putBoolean(getResources().getString(R.string.preference_disable_when_wifi_disconnect), false);
            editor.putBoolean(getResources().getString(R.string.preference_disable_wifi_sleep), false);
            editor.putBoolean(getResources().getString(R.string.preference_disable_when_app_exit), false);
            editor.putInt(getResources().getString(R.string.preference_adb_server_port), 5555);
            editor.commit();
        } catch (Exception e) {
            Log.e("setRunFirstTimeData", "初始化数据错误，错误信息为：" + e.toString());
        }

    }

    /**
     * 声明注册事件，并在Activity的onResume生命周期中注册，在onPause生命周期中取消注册来防止gc的回收。
     */
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferneceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "notify_when_active":
                    if (!sharedPreferences.getBoolean("notify_when_active", false)) {
                        cancalNotification(MainActivity.this, Integer.parseInt(getString(R.string.notification_id)));
                    } else {
                        if (ShellUtils.isActived()) {
                            showNotification(MainActivity.this, "ADB CONNECT" + WifiUtils.getWifiLocalIpAdress() + ":" + String.valueOf(mConfigCtrol.getInt(getString(R.string.preference_adb_server_port), -2)), true);
                        }
                    }
                    break;
                case "disable_wifi_sleep":
                    WifiUtils.setWifiSleep(sharedPreferences.getBoolean("disable_wifi_sleep", false));
                    break;

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initConfigData();
        WifiUtils.setWifiManager((WifiManager) getSystemService(Context.WIFI_SERVICE));
        WifiUtils.setConnectivityManager((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        initAdapter();
        mlvLeftMenu.setAdapter(mAdapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        RootDeviceFragment fragment = new RootDeviceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, fragment).commit();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public SharedPreferences getSharedPreferencesToFragement() {
        return mConfigCtrol;
    }

    @Override
    public void showNotification(Context context, String addressMsg, boolean isActived) {

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context, ToggleAdbService.class);
        if (isActived) {
            intent.setAction(getString(R.string.notification_close_adb_service));
            nBuilder.setContentTitle("服务已启动");
            nBuilder.setContentText(addressMsg);
            nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(addressMsg));
            nBuilder.addAction(R.drawable.ic_stat_inactive, "关闭服务", PendingIntent.getService(context, 0, intent, 0));
        } else {
            intent.setAction(getString(R.string.notification_start_adb_service));
//            intent.putExtra("adb_port",mConfigCtrol.getInt(getString(R.string.preference_adb_server_port),-1));
            nBuilder.setContentTitle("服务未启动");
            nBuilder.setContentText("猛戳按钮启动服务。");
            nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("猛戳按钮启动服务。"));
            nBuilder.addAction(R.drawable.ic_stat_active, "启动服务", PendingIntent.getService(context, 0, intent, 0));
        }
        nBuilder.setOngoing(true);//不允许滑动删除通知
        nBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//优先级越高越靠前
//        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setSmallIcon(R.drawable.ic_bug_report_red_800_36dp);
        notification = nBuilder.build();
        notificationManagerCompat.notify(Integer.parseInt(getString(R.string.notification_id)), notification);
    }

    /**
     * @param context
     * @param id      取消指定id的通知
     */
    private void cancalNotification(Context context, int id) {
        NotificationManager notificationCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCompat.cancel(id);
    }

    @Override
    protected void onDestroy() {
        if (mConfigCtrol.getBoolean(getString(R.string.preference_disable_when_app_exit), false)) {
            startService(new Intent(this, ToggleAdbService.class).setAction("onDestory_exit_app_disable_wifiadb"));
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
        mConfigCtrol.unregisterOnSharedPreferenceChangeListener(mPreferneceChangeListener);
    }

    @Override
    protected void onResume() {

        super.onResume();
        mConfigCtrol.registerOnSharedPreferenceChangeListener(mPreferneceChangeListener);
    }

}
