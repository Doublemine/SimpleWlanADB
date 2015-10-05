package xiamo.tk.wifiadb.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by wangh on 2015-9-28-0028.
 */
public class WifiUtils {


    private static WifiManager wifiManager;
    private static WifiManager.WifiLock wifiLock;
    private static ConnectivityManager connManager;
    private static NetworkInfo wifiInfo;


    public static void setConnectivityManager(ConnectivityManager connManager) {
        WifiUtils.connManager = connManager;
    }

    /**
     * @return 判断wifi是否连接，连接返回为true，否则false
     */
    public static boolean isWifiConnected() {
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }


    public static void setWifiManager(WifiManager wifiManager) {
        WifiUtils.wifiManager = wifiManager;
        wifiLock = wifiManager.createWifiLock("xiamo.wifiutils.wifilock");
    }

    /**
     * @param isSleep 禁用wifi休眠 true-->禁用  false-->启用
     * @return 设置成功返回为true，否则false
     */
    public static boolean setWifiSleep(boolean isSleep) {
        if (wifiLock != null) {
            if (isSleep) {//加锁
                if (!wifiLock.isHeld()) {
                    wifiLock.acquire();
                }
            } else {//解锁
                if (wifiLock.isHeld()) {
                    wifiLock.release();
                }
            }
            return true;
        }
        return false;

    }

    /**
     *
     * @return 返回当前连接wifi的ip地址
     */

    public static String getWifiLocalIpAdress() {
            if(WifiUtils.isWifiConnected()){
                WifiInfo wifiInfo=wifiManager.getConnectionInfo();
                int ipAdress=wifiInfo.getIpAddress();
                return formatIpAdress(ipAdress);
            }else {
                return "未连接到任何wifi";
            }

    }

    /**
     *
     * @param ipAdress int类型的ip地址参数
     * @return 返回字符串的ip地址
     */
    private static String formatIpAdress(int ipAdress){
        return (ipAdress & 0xFF ) + "." +((ipAdress >> 8 ) & 0xFF) + "." + ((ipAdress >> 16 ) & 0xFF) + "." + ( ipAdress >> 24 & 0xFF) ;
    }


}
