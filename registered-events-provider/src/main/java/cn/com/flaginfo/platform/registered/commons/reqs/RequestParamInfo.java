package cn.com.flaginfo.platform.registered.commons.reqs;

public class RequestParamInfo {
    private Object data;
    private String appKey;
    private String eventType;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "RequestParamInfo{" +
                "data=" + data +
                ", appKey='" + appKey + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
