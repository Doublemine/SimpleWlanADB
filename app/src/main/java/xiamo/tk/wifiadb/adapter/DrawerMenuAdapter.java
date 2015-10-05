package xiamo.tk.wifiadb.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import xiamo.tk.wifiadb.R;

/**
 * Created by wangh on 2015-9-22-0022.
 */
public class DrawerMenuAdapter extends BaseAdapter {

    private LayoutInflater minflater;
    private static final int TYPE_NAV = 0;
    private static final int TYPE_TITLE_SUMMARY=1;
    private static final int TYPE_ITEM_SW=2;
    private static final int TYPE_ITEM_TEXT=3;
    private SparseBooleanArray mCheckBoxStatus;
    private SwitchItemViewHolder viewHolderItemMenu;
    private DividerViewHolder mDividerViewHolder;
    private TextItemViewHolder mTextItemViewHolder;
    private SharedPreferences mConfigControl;
    private SharedPreferences.Editor mSaveConfig;
    private CoordinatorLayout mainSnackerBar;
    private Context mContext;
//    public DrawerMenuAdapter(Context mcontext) {
//
//        minflater = LayoutInflater.from(mcontext);
//        initCheckBox();
//    }

    public DrawerMenuAdapter(Context mcontext,SharedPreferences configEditor,CoordinatorLayout mainSnackerBar){
        minflater = LayoutInflater.from(mcontext);
        mContext=mcontext;
        this.mConfigControl=configEditor;
        this.mSaveConfig=this.mConfigControl.edit();
        this.mainSnackerBar=mainSnackerBar;
//        initCheckBox();

    }


    private void initCheckBox() {
        mCheckBoxStatus = new SparseBooleanArray();
        for (int i = 0; i < 7; i++) {
            mCheckBoxStatus.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Log.d("DrawerMenuAdapter", "type=" + type);
        if (convertView == null) {
            switch (type) {
                case TYPE_NAV:
                    Log.d("DrawerMenuAdapter", "type=" + type);
                    convertView = minflater.inflate(R.layout.nav_header, null);
                    return convertView;

                case TYPE_TITLE_SUMMARY:
                    convertView=minflater.inflate(R.layout.item_divider_summary,null);
                    mDividerViewHolder=new DividerViewHolder();
                    mDividerViewHolder.textView= (TextView) convertView.findViewById(R.id.item_divider_summary_tv);
                    convertView.setTag(mDividerViewHolder);
                    break;
//
                case TYPE_ITEM_SW:
                    convertView=minflater.inflate(R.layout.item_switch_menu,null);
                    viewHolderItemMenu=new SwitchItemViewHolder();
                    viewHolderItemMenu.title= (TextView) convertView.findViewById(R.id.item_switch_menu_title);
                    viewHolderItemMenu.summary= (TextView) convertView.findViewById(R.id.item_switch_menu_summary);
                    viewHolderItemMenu.switchCompat= (SwitchCompat) convertView.findViewById(R.id.item_switch_menu_switch);
                    convertView.setTag(viewHolderItemMenu);
                    break;
                case TYPE_ITEM_TEXT:
                    convertView=minflater.inflate(R.layout.item_text_menu,null);
                    mTextItemViewHolder=new TextItemViewHolder();
                    mTextItemViewHolder.imageView= (ImageView) convertView.findViewById(R.id.item_text_menu_iv);
                    mTextItemViewHolder.textView= (TextView) convertView.findViewById(R.id.item_text_menu_tv);
                    convertView.setTag(mTextItemViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_ITEM_SW:
                    viewHolderItemMenu= (SwitchItemViewHolder) convertView.getTag();
                    break;
                case TYPE_TITLE_SUMMARY:
                    mDividerViewHolder= (DividerViewHolder) convertView.getTag();
                    break;
                case TYPE_ITEM_TEXT:
                    mTextItemViewHolder= (TextItemViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE_ITEM_SW:
                switch (position){
                    case 2:
                        viewHolderItemMenu.title.setText(R.string.drawer_menu_actived_notify_title);
                        viewHolderItemMenu.summary.setText(R.string.drawer_menu_actived_notify_summary);
                        viewHolderItemMenu.switchCompat.setChecked(mConfigControl.getBoolean("notify_when_active", false));
                        viewHolderItemMenu.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mSaveConfig.putBoolean("notify_when_active", isChecked);
                                mSaveConfig.commit();
                            }
                        });

                        break;
                    case 3:
                        viewHolderItemMenu.title.setText(R.string.drawer_menu_disable_when_wifi_off_title);
                        viewHolderItemMenu.summary.setText(R.string.drawer_menu_disable_when_wifi_off_summary);
                        viewHolderItemMenu.switchCompat.setChecked(mConfigControl.getBoolean("disable_when_wifi_disconnect", false));
                        viewHolderItemMenu.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mSaveConfig.putBoolean("disable_when_wifi_disconnect", isChecked);
                                mSaveConfig.commit();
                            }
                        });

                        break;
                    case 4:
                        viewHolderItemMenu.title.setText(R.string.drawer_menu_lock_wifi_title);
                        viewHolderItemMenu.summary.setText(R.string.drawer_menu_lock_wifi_summary);
                        viewHolderItemMenu.switchCompat.setChecked(mConfigControl.getBoolean("disable_wifi_sleep", false));
                        viewHolderItemMenu.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mSaveConfig.putBoolean("disable_wifi_sleep", isChecked);
                                mSaveConfig.commit();
                            }
                        });

                        break;
                    case 5:
                        viewHolderItemMenu.title.setText(R.string.drawer_menu_exit_disable_title);
                        viewHolderItemMenu.summary.setText(R.string.drawer_menu_exit_disable_summary);
                        viewHolderItemMenu.switchCompat.setChecked(mConfigControl.getBoolean("disable_when_app_exit", false));
                        viewHolderItemMenu.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mSaveConfig.putBoolean("disable_when_app_exit", isChecked);
                                mSaveConfig.commit();
                            }
                        });


                        break;
                }

                break;
            case TYPE_TITLE_SUMMARY:
                switch (position){
                    case 1:
                        mDividerViewHolder.textView.setText(R.string.drawer_menu_divider_switch);
                        convertView.setEnabled(false);
                        break;
                    case 6:
                        mDividerViewHolder.textView.setText(R.string.drawer_menu_divider_other);
                        convertView.setEnabled(false);
                        break;
                }
                break;
            case TYPE_ITEM_TEXT:
                switch (position){
                    case 7:
                        mTextItemViewHolder.imageView.setImageResource(R.drawable.ic_create_black_24dp);
                        mTextItemViewHolder.textView.setText("更改ADB端口号");
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Snackbar sb = Snackbar.make(mainSnackerBar, "这是一个危险且不常用的操作，可能会因此带来一些安全风险，你要继续吗？", Snackbar.LENGTH_INDEFINITE);
                                 View sbv = sb.getView();
                                 sbv.setBackgroundColor(Color.parseColor("#673AB7"));
                                TextView sbt= (TextView) sbv.findViewById(android.support.design.R.id.snackbar_text);
                                sbt.setMaxLines(20);

                                 sb.setAction("我要继续", new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         createInputPortAlert(v, mConfigControl, mSaveConfig);
                                     }
                                 });

                                 sb.setActionTextColor(Color.parseColor("#DF8530"));
                                 sb.show();
                            }
                        });
                        break;
                    case 8:
                        mTextItemViewHolder.imageView.setImageResource(R.drawable.ic_info_outline_black_24dp);
                        mTextItemViewHolder.textView.setText("关于");
                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.startActivity(new Intent().setAction("android.intent.action.VIEW").setData(Uri.parse("http://notes.xiamo.tk/about")).setClassName("com.android.browser", "com.android.browser.BrowserActivity"));
                            }
                        });
                        break;
                }
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_NAV;
        } else if (position == 1 || position == 6) {
            return TYPE_TITLE_SUMMARY;
        } else if (position > 1 && position <= 5) {
            return TYPE_ITEM_SW;
        } else if (position == 7 || position == 8) {
            return TYPE_ITEM_TEXT;
        } else {
            return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    private class DividerViewHolder{
        TextView textView;
    }
    private class SwitchItemViewHolder {
        TextView title;
        TextView summary;
        SwitchCompat switchCompat;
    }
    private class TextItemViewHolder{
        ImageView imageView;
        TextView  textView;
    }

    private void showInfo(View view,String summary,String hexColor,int showTime){
        Snackbar sb = Snackbar.make(view, summary, showTime);
        View sbv = sb.getView();
        sbv.setBackgroundColor(Color.parseColor(hexColor));
        sb.show();
    }

    private void createInputPortAlert(View view, final SharedPreferences getPort, final SharedPreferences.Editor setPort){
//       View temp=view;
//        Context context=temp.getContext();
//        LayoutInflater inflater=LayoutInflater.from(context);
        final View alertContent=minflater.inflate(R.layout.alert_input_port, null);
        final EditText input= (EditText) alertContent.findViewById(R.id.alert_input_port_et);

        input.setHint("当前端口为:" + String.valueOf(getPort.getInt("adb_server_port", -1)));

        AlertDialog.Builder inputPortAlert=new AlertDialog.Builder(view.getContext());

        inputPortAlert.setTitle("请输入新的端口号：");
        inputPortAlert.setCancelable(false);
        inputPortAlert.setView(alertContent);
        inputPortAlert.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    int afterPort;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            afterPort = Integer.parseInt(input.getText().toString());
                            if (afterPort < 1024) {
                                showInfo(mainSnackerBar, "端口应该大于1024！" , "#ECA543", Snackbar.LENGTH_LONG);
                            } else if (afterPort >= 1000 && afterPort < 65535) {
                                setPort.putInt("adb_server_port", afterPort);
                                setPort.commit();
                                showInfo(mainSnackerBar, "端口修改成功，当前端口为：" + String.valueOf(getPort.getInt("adb_server_port", -1)), "#3EB55D", Snackbar.LENGTH_LONG);
                            } else {
                                showInfo(mainSnackerBar, "端口号超过上限！", "#ECA543", Snackbar.LENGTH_LONG);
                            }

                        } catch (Exception e) {
                            Log.e("重置adb服务端口号", "重置时产生错误-->获取到的值为【" + afterPort + "】错误详情为：" + e.toString());
                            showInfo(mainSnackerBar, "非法输入！修改失败！", "#D73F3F", Snackbar.LENGTH_LONG);
                        }
                    }
                }

        );
            inputPortAlert.setNegativeButton("取消", null);
            inputPortAlert.show();


        }
    }
