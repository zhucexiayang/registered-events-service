package cn.com.flaginfo.platform.registered.commons.resp;

public class ResultReponse {
    private Object data;
    private String appkey;
    private String status="SUCCESS";

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
