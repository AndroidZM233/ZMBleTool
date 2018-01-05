package com.zm.zmbletool.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.zm.zmbletool.bean.BleBean;

import java.util.List;

import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2018/1/2.
 * Email 741183142@qq.com
 */

public class BleRVAdapter extends CommonRvAdapter<BluetoothGattService> {
    public BleRVAdapter(Context context, int layoutResId, List<BluetoothGattService> data) {
        super(context, layoutResId, data);
    }

    @Override
    public void convert(BaseAdapterHelper helper, BluetoothGattService item, int position) {

    }
}
