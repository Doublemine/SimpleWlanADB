package xiamo.tk.wifiadb.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wangh on 2015-9-29-0029.
 */
public class ShellUtils {

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
//            Log.i(TAG, str);
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }


    /**
     * 判断手机是否root，不弹出root请求框
     */
    public static boolean isRoot() {
        final String binPath = "/system/bin/su";
        final String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isExecutable(binPath))
            return true;
        if (new File(xBinPath).exists() && isExecutable(xBinPath))
            return true;
        return false;
    }

    /**
     *
     * @return 关闭wifiadb 成功返回为true否则为false
     */
    public static boolean closeWifiDebug(){
        final String[] closeCmd={"setprop service.adb.tcp.port -1","stop adbd","start adbd"};
        CommandResult result=exeCommand(closeCmd,true,true);
        if(result.result==0&&result.successMsg.length()==0){
            Log.e("closeWifiDebug--执行成功","successMsg:->"+result.successMsg+"<->result："+result.result);
            return true;
        }else {
            Log.e("closeWifiDebug--执行错误","错误信息:->"+result.errorMsg+"<->返回值："+result.result);
            return false;

        }
    }

    /**
     *
     * @param port adb端口号
     * @return 开启成功，返回为true 否则为false
     */
    public static boolean activeWifiDebug(int port){
        final String[] activeCmd={"setprop service.adb.tcp.port "+port,"stop adbd","start adbd"};
        CommandResult result=exeCommand(activeCmd,true,true);
        if(result.result==0&&result.successMsg.length()==0){
            Log.e("activeWifiDebug--执行成功","successMsg:->"+result.successMsg+"<->result："+result.result);
            return true;
        }else {
            Log.e("activeWifiDebug--执行错误","错误信息:->"+result.errorMsg+"<->返回值："+result.result);
            return false;
        }

    }


    /**
     *
     * @return 返回当前adbd的端口号
     */
    public static String getAdbdPort(){
        final String[] getPortCmd={"getprop service.adb.tcp.port"};
        CommandResult result=exeCommand(getPortCmd,false,true);

            return result.successMsg;



    }

    /**
     *
     * @return adbd服务已经启动返回true，否则返回false
     */
    public static boolean isActived(){
        String getStrPort=ShellUtils.getAdbdPort();
        if(getStrPort.length()<=0){
            return  false;
        }else {
            if (Integer.parseInt(ShellUtils.getAdbdPort()) != -1) {
                return true;
            } else {
                return false;
            }
        }

    }

    public static CommandResult exeCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {//命令为空
            return new CommandResult(result);
        }

        Runtime runtime = Runtime.getRuntime();
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder errorMsg = null;
        StringBuilder successMsg = null;
        Process shellProcess = null;
        try {
            if(isRoot) {
                shellProcess = runtime.exec("su");
            }else {
                shellProcess = runtime.exec("sh");
            }
            os = new DataOutputStream(shellProcess.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            result = shellProcess.waitFor();//等待shell命令进程执行结束
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(shellProcess.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(shellProcess.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (shellProcess != null) {
                    shellProcess.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null : errorMsg.toString());
    }

    public static class CommandResult  {

        private int result;
        private String successMsg;
        private String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.errorMsg = errorMsg;
            this.successMsg = successMsg;
        }


    }

}
