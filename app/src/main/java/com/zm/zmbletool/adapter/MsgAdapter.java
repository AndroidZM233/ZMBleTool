package com.zm.zmbletool.adapter;

import android.content.Context;

import com.zm.zmbletool.R;
import com.zm.zmbletool.bean.ChatBean;

import java.util.List;

import xyz.reginer.baseadapter.BaseAdapterHelper;
import xyz.reginer.baseadapter.CommonRvAdapter;

/**
 * Created by 张明_ on 2017/12/25.
 * Email 741183142@qq.com
 */

public class MsgAdapter extends CommonRvAdapter<ChatBean> {
    public MsgAdapter(Context context, int layoutResId, List<ChatBean> data) {
        super(context, layoutResId, data);
    }

    @Override
    public void convert(BaseAdapterHelper helper, ChatBean item, int position) {
        helper.setText(R.id.timestamp, item.getTime());
        boolean receiveVisibility = item.isReceiveVisibility();
        if (receiveVisibility) {
            helper.setVisible(R.id.tv_receive_content, true);
            helper.setText(R.id.tv_receive_content, item.getReceive());
        } else {
            helper.setVisible(R.id.tv_receive_content, false);
        }
        boolean sendVisibility = item.isSendVisibility();
        if (sendVisibility) {
            helper.setVisible(R.id.tv_send_content, true);
            helper.setText(R.id.tv_send_content, item.getSend());
        } else {
            helper.setVisible(R.id.tv_send_content, false);
        }
    }
}
