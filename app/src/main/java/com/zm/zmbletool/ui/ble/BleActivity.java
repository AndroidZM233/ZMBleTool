package com.zm.zmbletool.ui.ble;


import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zm.zmbletool.R;
import com.zm.zmbletool.adapter.BleRVAdapter;
import com.zm.zmbletool.adapter.MsgAdapter;
import com.zm.zmbletool.bean.BleBean;
import com.zm.zmbletool.mvp.MVPBaseActivity;
import com.zm.zmbletool.services.BluetoothLeService;
import com.zm.zmbletool.ui.classicclient.ClassicClientActivity;
import com.zm.zmbletool.ui.classicclient.ClassicScanActivity;

import java.util.ArrayList;
import java.util.List;

import static com.zm.zmbletool.services.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_CONNECTED;
import static com.zm.zmbletool.services.BluetoothLeService.ACTION_GATT_DISCONNECTED;


/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class BleActivity extends MVPBaseActivity<BleContract.View, BlePresenter> implements BleContract.View {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private Toolbar mToolbar;
    private RecyclerView mRvContent;
    private BleRVAdapter mAdapter;
    private List<BluetoothGattService> mList;
    private String mDeviceName;
    private String mDeviceAddress;


    @Override
    public void initData(Bundle bundle) {
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.bindService(getApplicationContext());
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_ble;
    }

    @Override
    public void initView(Bundle bundle, View view) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("低功耗蓝牙");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleActivity.this.finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                openAct(getApplicationContext(), ClassicScanActivity.class);
                BleActivity.this.finish();
                return true;
            }
        });


        mRvContent = findViewById(R.id.rv_content);
        mList = new ArrayList<>();
        initRV();
    }

    private void initRV() {
        mAdapter = new BleRVAdapter(BleActivity.this
                , R.layout.rv_ble, mList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvContent.setLayoutManager(layoutManager);
        mRvContent.setAdapter(mAdapter);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onWidgetClick(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.classic_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.registerReceiver(getApplicationContext(), mDeviceAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unregisterReceiver(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindService(getApplicationContext());
    }


    @Override
    public void finishAct() {
        BleActivity.this.finish();
    }

    @Override
    public void openScanAct() {
        Toast.makeText(mActivity, "连接失败，重新选择设备", Toast.LENGTH_SHORT).show();
        openAct(getApplicationContext(), BleScanActivity.class);
        finishAct();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void backList(List<BluetoothGattService> supportedGattServices) {
        mList = supportedGattServices;
        mAdapter.notifyDataSetChanged();
    }
}
