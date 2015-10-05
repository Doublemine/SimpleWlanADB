package xiamo.tk.wifiadb.utils;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by wangh on 2015-9-29-0029.
 */
public class UIUtils {
    /**
     *
     * @param container Snackbar显示容器
     * @param msg  显示的消息
     * @param duration 显示的时长
     * @param maxLineNum 最多显示几行
     * @param snackbarBgColor Snackbar的背景色 使用#ffff类似的值
     */
    public static void showSnackbar(CoordinatorLayout container,String msg,int duration,int maxLineNum,String
            snackbarBgColor){
        Snackbar snackbar=Snackbar.make(container,msg,duration);
        View view=snackbar.getView();
        view.setBackgroundColor(Color.parseColor(snackbarBgColor));
        if(maxLineNum>=4) {
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(maxLineNum);
        }
        snackbar.setAction(null,null);
        snackbar.show();
    }

    public static void showErrSnackbar(CoordinatorLayout container,String msg){
        UIUtils.showSnackbar(container,msg,Snackbar.LENGTH_LONG,10,"#D73F3F");
    }


}
