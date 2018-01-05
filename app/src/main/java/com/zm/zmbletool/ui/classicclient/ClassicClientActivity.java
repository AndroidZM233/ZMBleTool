package com.zm.zmbletool.ui.classicclient;


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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

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

public class ClassicClientActivity extends MVPBaseActivity<ClassicClientContract.View, ClassicClientPresenter> implements ClassicClientContract.View {
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

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_service;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("客户端");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassicClientActivity.this.finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAct(getApplicationContext(), ClassicScanActivity.class);
                ClassicClientActivity.this.finish();
                return true;
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
                SharedXmlUtil.getInstance(ClassicClientActivity.this).write("Client_CB_SEND", b);
            }
        });
        mCbReceive = findViewById(R.id.cb_receive);
        mCbReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedXmlUtil.getInstance(ClassicClientActivity.this).write("Client_CB_RECEIVE", b);
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

        boolean cbSend = SharedXmlUtil.getInstance(this).read("Client_CB_SEND", false);
        mCbSend.setChecked(cbSend);
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("Client_CB_RECEIVE", false);
        mCbReceive.setChecked(cbReceive);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classic_toolbar, menu);
        return true;
    }

    private void initRV() {
        mAdapter = new MsgAdapter(ClassicClientActivity.this
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
                boolean cbSend = SharedXmlUtil.getInstance(this).read("Client_CB_SEND", false);
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
    public void showReceiveData(byte[] msg) {
        boolean cbReceive = SharedXmlUtil.getInstance(this).read("Client_CB_RECEIVE", false);
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
