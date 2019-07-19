package cn.com.flaginfo.platform.registered.mybatis.vo;

import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEvent;

import java.util.Arrays;

public class RegisterEventVo extends RegisterEvent {
    private String[] envents;
    private String appKey;

    public String[] getEnvents() {
        return envents;
    }

    public void setEnvents(String[] envents) {
        this.envents = envents;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public String toString() {
        return "RegisterEventVo{" +
                "envents=" + Arrays.toString(envents) +
                ", appKey='" + appKey + '\'' +
                '}';
    }
}
