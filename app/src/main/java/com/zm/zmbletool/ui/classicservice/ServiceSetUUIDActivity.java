package com.zm.zmbletool.ui.classicservice;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.zm.utilslib.base.BaseActivity;
import com.zm.utilslib.utils.SharedXmlUtil;
import com.zm.zmbletool.R;
import com.zm.zmbletool.ui.classicclient.ClassicScanActivity;

/**
 * Created by 张明_ on 2017/12/29.
 * Email 741183142@qq.com
 */

public class ServiceSetUUIDActivity extends BaseActivity {
    private Toolbar mToolbar;
    private EditText mEtUuid;
    private Button mBtnStart;
    private RelativeLayout mErrorInfo;

    @Override
    public void initData(Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_client_uuid;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("设置UUID");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceSetUUIDActivity.this.finish();
            }
        });

        mErrorInfo = findViewById(R.id.error_info);
        mErrorInfo.setVisibility(View.GONE);
        mEtUuid = findViewById(R.id.et_uuid);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);

        String uuid = SharedXmlUtil.getInstance(getApplicationContext())
                .read("SERVICE_UUID", "00001101-0000-1000-8000-00805F9B34FB");
        mEtUuid.setText(uuid);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                String uuid = mEtUuid.getText().toString();
                SharedXmlUtil.getInstance(getApplicationContext()).write("SERVICE_UUID", uuid);
                openAct(getApplicationContext(), ClassicServiceActivity.class);
                ServiceSetUUIDActivity.this.finish();
                break;
            default:
                break;
        }
    }

}
