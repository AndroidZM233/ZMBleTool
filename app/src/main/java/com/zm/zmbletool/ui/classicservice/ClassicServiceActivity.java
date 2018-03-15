package com.zm.zmbletool.ui.classicservice;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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
import com.zm.utilslib.utils.DateUtils;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.utilslib.utils.data.ByteUtils;
import com.zm.utilslib.utils.data.StringUtils;
import com.zm.zmbletool.R;
import com.zm.zmbletool.adapter.MsgAdapter;
import com.zm.zmbletool.bean.ChatBean;
import com.zm.zmbletool.mvp.MVPBaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class ClassicServiceActivity extends MVPBaseActivity<ClassicServiceContract.View, ClassicServicePresenter> implements ClassicServiceContract.View {
    private static final int REQUEST_ENABLE_BT = 1;
    private Toolbar mToolbar;
    private RecyclerView mRvContent;
    private EditText mEtSendmessage;
    private Button mBtnMore;
    private Button mBtnSend;
    private CheckBox mCbSend;
    private CheckBox mCbReceive;
    private LinearLayout mRlBottom;
    private MsgAdapter mAdapter;
    private List<ChatBean> mList;

    @Override
    public void initData(Bundle bundle) {
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    @Override
    public int bindLayout() {
        return R.layout.activity_service;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("服务端");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassicServiceActivity.this.finish();
            }
        });

        mRvContent = findViewById(R.id.rv_content);
        mEtSendmessage = findViewById(R.id.et_sendmessage);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mCbSend = findViewById(R.id.cb_send);
        mCbSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedXmlUtil.getInstance(ClassicServiceActivity.this).write("CB_SEND", b);
            }
        });
        mCbReceive = findViewById(R.id.cb_receive);
        mCbReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedXmlUtil.getInstance(ClassicServiceActivity.this).write("CB_RECEIVE", b);
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

        boolean cbSend = SharedXmlUtil.getInstance(this).read("CB_SEND", false);
        mCbSend.setChecked(cbSend);
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("CB_RECEIVE", false);
        mCbReceive.setChecked(cbReceive);
    }


    private void initRV() {
        mAdapter = new MsgAdapter(ClassicServiceActivity.this
                , R.layout.rv_service, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutManager);
        mRvContent.setAdapter(mAdapter);
    }


    @Override
    public void doBusiness() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.startBluetooth(getApplicationContext());
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
        mPresenter.stopBluetooth();
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
                boolean cbSend = SharedXmlUtil.getInstance(this).read("CB_SEND", false);
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
                mPresenter.sendMsg(bytes);
                handler.sendMessage(handler.obtainMessage());
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void showReceiveData(byte[] msg) {
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("CB_RECEIVE", false);
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

    @Override
    public void showStatus(String msg) {
        ChatBean chatBean = new ChatBean();
        chatBean.setTime(msg);
        mList.add(chatBean);
        handler.sendMessage(handler.obtainMessage());
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
            mRvContent.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    };
}
