package xiamo.tk.wifiadb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import xiamo.tk.wifiadb.R;
import xiamo.tk.wifiadb.utils.ShellUtils;
import xiamo.tk.wifiadb.utils.UIUtils;
import xiamo.tk.wifiadb.utils.WifiUtils;


public class SwitcherFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private View mFragmentView;
    private CoordinatorLayout mSnackbarContainer;
    private Toolbar toolbar;
    private TextView mInfo;
    public static MyHandler mHandler;
    private SharedPreferences sharedPreferences;
    private ImageButton mImgBtnToggle;



    private void findView() {

        mSnackbarContainer = (CoordinatorLayout) mFragmentView.findViewById(R.id.fragment_sb_containger);
        mImgBtnToggle= (ImageButton) mFragmentView.findViewById(R.id.fragment_root_no_switcher_button);
        toolbar = (Toolbar) mFragmentView.findViewById(R.id.fragment_root_no_switcher_toolbar);
        mInfo = (TextView) mFragmentView.findViewById(R.id.fragment_root_no_switcher_info);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setTitle(R.string.app_name);



        mImgBtnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShellUtils.isActived()) {//关闭adbd
                    if (ShellUtils.isRoot()) {
                        if (ShellUtils.closeWifiDebug()) {
                            mInfo.setText(R.string.fragment_show_info_text_view);
                            mInfo.setTextColor(getResources().getColor(R.color.red));
                            mImgBtnToggle.setImageResource(R.drawable.big_icon_gray);
                            if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                                mListener.showNotification(getContext(), "WIFI ADB 服务已关闭", false);
                            }

                        } else {
                            if (ShellUtils.isRoot()) {
                                UIUtils.showErrSnackbar(mSnackbarContainer, "该功能需要您授权ROOT" +
                                        "权限访问才能正常工作！");
                            }
                        }
                    }else {
                        UIUtils.showErrSnackbar(mSnackbarContainer, "您的设备没有ROOT权限，该功能无法使用！");
                    }

                } else {//开启adbd
                    if (ShellUtils.isRoot()) {
                        if (WifiUtils.isWifiConnected()) {
                            int port = sharedPreferences.getInt(getString(R.string.preference_adb_server_port), -1);
                            if (ShellUtils.activeWifiDebug(port)) {
                                mInfo.setText("请在电脑上运行：\n\nadb connect " + WifiUtils.getWifiLocalIpAdress() + ":" + port);
                                mInfo.setTextColor(getResources().getColor(R.color.green));
                                mImgBtnToggle.setImageResource(R.drawable.big_icon);
                                if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                                    mListener.showNotification(getContext(), "ADB CONNECT" + WifiUtils.getWifiLocalIpAdress() + ":" + String.valueOf(port), true);
                                }
                            } else {
                                if (ShellUtils.isRoot()) {
                                    UIUtils.showErrSnackbar(mSnackbarContainer, "该功能需要您授权ROOT" +
                                            "权限访问才能正常工作！");
                                }

                            }
                        } else {
                            UIUtils.showErrSnackbar(mSnackbarContainer, getString(R.string.fragment_start_wifiadb_no_wifi));
                        }
                    }else {
                        UIUtils.showErrSnackbar(mSnackbarContainer, "您的设备没有ROOT权限，该功能无法使用！");
                    }
                }
            }
        });

    }

    public SwitcherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = mListener.getSharedPreferencesToFragement();
    }

    @Override
    /**
     * 为Fragment加载布局时调用。
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_root_no_switcher, container, false);
        findView();
        // Inflate the layout for this fragment
        return mFragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    /**
     * Fragment和Activity建立关联的时候调用。
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }


    @Override
    /**
     * Fragment和Activity解除关联的时候调用。
     */
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        SharedPreferences getSharedPreferencesToFragement();

        void showNotification(Context context, String addressMsg, boolean isActived);
    }

    @Override
    /**
     * 当Activity中的onCreate方法执行完后调用。
     */
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mHandler = new MyHandler(this);
    }

    @Override
    /**
     * Fragment中的布局被移除时调用。
     */
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updateUI() {
        if (ShellUtils.isActived()) {
            if (WifiUtils.isWifiConnected()) {
                mInfo.setText("请在电脑上运行：\n\nadb connect " + WifiUtils.getWifiLocalIpAdress() + ":" + ShellUtils.getAdbdPort());
                mImgBtnToggle.setImageResource(R.drawable.big_icon);
                mInfo.setTextColor(getResources().getColor(R.color.green));
                if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                    mListener.showNotification(getContext(), "ADB CONNECT" + WifiUtils.getWifiLocalIpAdress() + ":" + ShellUtils.getAdbdPort(), true);
                }
            } else {
                mInfo.setText("检测到未连接到WIFI\n\n" + "WIFI ADB将在连接到WIFI之后可用！");
                mInfo.setTextColor(getResources().getColor(R.color.yellow));
                mImgBtnToggle.setImageResource(R.drawable.big_icon);
                if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                    mListener.showNotification(getContext(), "WIFI ADB将在连接到WIFI之后可用！", true);
                }
            }
        } else {
            mInfo.setText(R.string.fragment_show_info_text_view);
            mImgBtnToggle.setImageResource(R.drawable.big_icon_gray);
            mInfo.setTextColor(getResources().getColor(R.color.red));
            if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                mListener.showNotification(getContext(), "WIFI ADB 服务已关闭", false);
            }
        }
    }

    /**
     * 消除可能引发的内存泄露问题
     */
    public static class MyHandler extends Handler {
        WeakReference<SwitcherFragment> mFragment;

        MyHandler(SwitcherFragment Fragment) {
            mFragment = new WeakReference<SwitcherFragment>(Fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SwitcherFragment theFragment = mFragment.get();
            if (theFragment.getString(R.string.service_close_adb_success).equals(msg.obj)) {
                theFragment.updateUI();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
