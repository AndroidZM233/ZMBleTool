package com.zm.zmbletool.ui.bleclient;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zm.utilslib.bean.MsgEvent;
import com.zm.utilslib.utils.DateUtils;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.utilslib.utils.data.ByteUtils;
import com.zm.utilslib.utils.data.StringUtils;
import com.zm.zmbletool.MyApplication;
import com.zm.zmbletool.R;
import com.zm.zmbletool.adapter.MsgAdapter;
import com.zm.zmbletool.bean.ChatBean;
import com.zm.zmbletool.mvp.MVPBaseActivity;
import com.zm.zmbletool.services.BluetoothLeService;
import com.zm.zmbletool.ui.ble.BleActivity;
import com.zm.zmbletool.ui.ble.BleScanActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */
@SuppressLint("NewApi")
public class BleClientActivity extends MVPBaseActivity<BleClientContract.View, BleClientPresenter>
        implements BleClientContract.View {
    private Toolbar mToolbar;
    private RecyclerView mRvContent;
    private EditText mEtSendmessage;
    private Button mBtnMore;
    private Button mBtnSend;
    private Button mBtnRead;
    private CheckBox mCbSend;
    private CheckBox mCbReceive;
    private CheckBox mCbNotification;
    private LinearLayout mRlBottom;
    private MsgAdapter mAdapter;
    private List<ChatBean> mList;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothLeService bluetoothLeService;


    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_ble_client;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("低功耗蓝牙");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleClientActivity.this.finish();
            }
        });

        characteristic = MyApplication.getInstance().getCharacteristic();
        bluetoothLeService = MyApplication.getInstance().getmBluetoothLeService();

        mRvContent = findViewById(R.id.rv_content);
        mEtSendmessage = findViewById(R.id.et_sendmessage);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnRead = findViewById(R.id.btn_read);
        mBtnRead.setOnClickListener(this);
        mCbSend = findViewById(R.id.cb_send);
        mCbSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedXmlUtil.getInstance(BleClientActivity.this).write("BLE_CB_SEND", b);
            }
        });
        mCbReceive = findViewById(R.id.cb_receive);
        mCbReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedXmlUtil.getInstance(BleClientActivity.this).write("BLE_CB_RECEIVE", b);
            }
        });
        mCbNotification = findViewById(R.id.cb_notification);
        mCbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    bluetoothLeService.setCharacteristicNotification(
                            characteristic, true);
                }
            }
        });
        mRlBottom = findViewById(R.id.rl_bottom);
        mList = new ArrayList<>();
        initRV();

        // 监听文字框
        mEtSendmessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnMore.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                } else {
                    mBtnMore.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean cbSend = SharedXmlUtil.getInstance(this).read("BLE_CB_SEND", true);
        mCbSend.setChecked(cbSend);
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("BLE_CB_RECEIVE", true);
        mCbReceive.setChecked(cbReceive);


        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            mBtnRead.setVisibility(View.VISIBLE);
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mCbNotification.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_more:
                int visibility = mRlBottom.getVisibility();
                if (visibility == View.GONE) {
                    mRlBottom.setVisibility(View.VISIBLE);
                } else {
                    mRlBottom.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_send:
                String toString = mEtSendmessage.getText().toString();
                boolean cbSend = SharedXmlUtil.getInstance(this).read("BLE_CB_SEND", true);
                byte[] bytes;
                if (cbSend) {
                    bytes = StringUtils.hexStringToByteArray(toString);
                } else {
                    bytes = toString.getBytes();
                }
                ChatBean chatBean = new ChatBean();
                chatBean.setTime(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS_CN));
                chatBean.setSendVisibility(true);
                chatBean.setSend(toString);
                mList.add(chatBean);

                characteristic.setValue(bytes);
                bluetoothLeService.wirteCharacteristic(characteristic);
                handler.sendMessage(handler.obtainMessage());
                break;

            case R.id.btn_read:
                bluetoothLeService.readCharacteristic(characteristic);
                break;
            default:
                break;
        }
    }


    private void initRV() {
        mAdapter = new MsgAdapter(BleClientActivity.this
                , R.layout.rv_service, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutManager);
        mRvContent.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter.registerReceiver(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.unregisterReceiver(getApplicationContext());
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
            mRvContent.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    };


    @Override
    public void finishAct() {
        BleClientActivity.this.finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MyEventBus(MsgEvent msgEvent) {
        String type = msgEvent.getType();
        if ("EXTRA_DATA".equals(type)) {
            byte[] msg = (byte[]) msgEvent.getMsg();
            showReceiveData(msg);
        } else if ("GATT_WRITE".equals(type)) {
            ChatBean chatBean = new ChatBean();
            chatBean.setTime(String.valueOf(msgEvent.getMsg()));
            mList.add(chatBean);
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public void showReceiveData(byte[] msg) {
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("BLE_CB_RECEIVE", true);
        String string = "";
        if (cbReceive) {
            string = ByteUtils.toHexString(msg);
        } else {
            string = new String(msg);
        }
        ChatBean chatBean = new ChatBean();
        chatBean.setTime(DateUtils.getCurrentTimeMillis(DateUtils.FORMAT_YMDHMS_CN));
        chatBean.setReceiveVisibility(true);
        chatBean.setReceive(string);
        mList.add(chatBean);
        handler.sendMessage(handler.obtainMessage());
    }
}
