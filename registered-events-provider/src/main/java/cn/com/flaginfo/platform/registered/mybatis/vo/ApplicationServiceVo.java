package cn.com.flaginfo.platform.registered.mybatis.vo;

import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;

public class ApplicationServiceVo extends ApplicationService {
    private String[] regsiterEvent;


    public String[] getRegsiterEvent() {
        return regsiterEvent;
    }

    public void setRegsiterEvent(String[] regsiterEvent) {
        this.regsiterEvent = regsiterEvent;
    }
}
