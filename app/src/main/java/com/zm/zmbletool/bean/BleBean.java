package com.zm.zmbletool.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 张明_ on 2018/1/2.
 * Email 741183142@qq.com
 */

public class BleBean {
    private String name;
    private String uuid;
    private ArrayList<HashMap<String, String>> gattCharacteristicGroupData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ArrayList<HashMap<String, String>> getGattCharacteristicGroupData() {
        return gattCharacteristicGroupData;
    }

    public void setGattCharacteristicGroupData(ArrayList<HashMap<String, String>> gattCharacteristicGroupData) {
        this.gattCharacteristicGroupData = gattCharacteristicGroupData;
    }
}
