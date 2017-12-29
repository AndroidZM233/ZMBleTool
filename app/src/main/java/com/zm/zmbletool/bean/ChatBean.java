package com.zm.zmbletool.bean;

/**
 * Created by 张明_ on 2017/12/25.
 * Email 741183142@qq.com
 */

public class ChatBean {
    private String time;
    private boolean receiveVisibility = false;
    private String receive = "";
    private boolean sendVisibility = false;
    private String send = "";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isReceiveVisibility() {
        return receiveVisibility;
    }

    public void setReceiveVisibility(boolean receiveVisibility) {
        this.receiveVisibility = receiveVisibility;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public boolean isSendVisibility() {
        return sendVisibility;
    }

    public void setSendVisibility(boolean sendVisibility) {
        this.sendVisibility = sendVisibility;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }
}
