package xiamo.tk.wifiadb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import xiamo.tk.wifiadb.R;
import xiamo.tk.wifiadb.utils.ShellUtils;
import xiamo.tk.wifiadb.utils.UIUtils;
import xiamo.tk.wifiadb.utils.WifiUtils;


public class RootDeviceFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private FloatingActionButton mFloatButton;
    private View mFragmentView;
    private CoordinatorLayout mSnackbarContainer;
    private Toolbar toolbar;
    private TextView mInfo;
    public static MyHandler mHandler;
    private ImageSwitcher mImageSwitcher;
    private Timer mTimer;

    private SharedPreferences sharedPreferences;
    private static final int[] imgArrary = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4,
            R.drawable.image_5, R.drawable.image_6, R.drawable.image_7, R.drawable.image_8, R.drawable.image_9, R.drawable.image_10,
            R.drawable.image_11, R.drawable.image_12, R.drawable.image_13, R.drawable.image_14};


    private void findView() {
        mFloatButton = (FloatingActionButton) mFragmentView.findViewById(R.id.fragment_float_button);
        mSnackbarContainer = (CoordinatorLayout) mFragmentView.findViewById(R.id.fragment_rooot_device_sb_containger);
        toolbar = (Toolbar) mFragmentView.findViewById(R.id.fragment_root_toolbar);
        mInfo = (TextView) mFragmentView.findViewById(R.id.fragment_root_info);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appCompatActivity.getSupportActionBar().setTitle(R.string.app_name);
        mImageSwitcher = (ImageSwitcher) mFragmentView.findViewById(R.id.fragment_image_switcher);
        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_out));

        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView iv = new ImageView(RootDeviceFragment.this.getContext());
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//                ImageSwitcher.LayoutParams lp = new ImageSwitcher.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                lp.gravity = Gravity.CENTER;
//                iv.setLayoutParams(lp);

                return iv;
            }
        });


        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShellUtils.isActived()) {//关闭adbd
                    if (ShellUtils.isRoot()) {
                        if (ShellUtils.closeWifiDebug()) {
                            mInfo.setText(R.string.fragment_show_info_text_view);
                            mInfo.setTextColor(getResources().getColor(R.color.red));
                            mFloatButton.setImageResource(R.drawable.ic_adbw_off);
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
                                mFloatButton.setImageResource(R.drawable.ic_adbw_on);
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

    public RootDeviceFragment() {
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

        mFragmentView = inflater.inflate(R.layout.fragment_root_device, container, false);
        findView();
        // Inflate the layout for this fragment
        return mFragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            Message message;

            @Override
            public void run() {

                message = new Message();
                message.arg1 = new Random().nextInt(14);
                message.obj = getString(R.string.fragment_change_switch_image);
                mHandler.sendMessage(message);

            }
        }, 10, 5000);
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
                mFloatButton.setImageResource(R.drawable.ic_adbw_on);
                mInfo.setTextColor(getResources().getColor(R.color.green));
                if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                    mListener.showNotification(getContext(), "ADB CONNECT" + WifiUtils.getWifiLocalIpAdress() + ":" + ShellUtils.getAdbdPort(), true);
                }
            } else {
                mInfo.setText("检测到未连接到WIFI\n\n" + "WIFI ADB将在连接到WIFI之后可用！");
                mInfo.setTextColor(getResources().getColor(R.color.yellow));
                mFloatButton.setImageResource(R.drawable.ic_adbw_on);
                if (sharedPreferences.getBoolean(getString(R.string.preference_notify_when_active), false)) {
                    mListener.showNotification(getContext(), "WIFI ADB将在连接到WIFI之后可用！", true);
                }
            }
        } else {
            mInfo.setText(R.string.fragment_show_info_text_view);
            mFloatButton.setImageResource(R.drawable.ic_adbw_off);
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
        WeakReference<RootDeviceFragment> mFragment;

        MyHandler(RootDeviceFragment Fragment) {
            mFragment = new WeakReference<RootDeviceFragment>(Fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RootDeviceFragment theFragment = mFragment.get();
            if (theFragment.getString(R.string.service_close_adb_success).equals(msg.obj)) {
                theFragment.updateUI();
            } else if (theFragment.getString(R.string.fragment_change_switch_image).equals(msg.obj)) {
                theFragment.mImageSwitcher.setImageResource(imgArrary[msg.arg1]);
//                theFragment.mImageSwitcher.setBackgroundResource(imgArrary[msg.arg1]);

            }
        }
    }

    @Override
    public void onPause() {
        mTimer.cancel();
        super.onPause();

    }
}
