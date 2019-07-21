package cn.com.flaginfo.platform.registered.mybatis.service;

import cn.com.flaginfo.platform.registered.commons.resp.BaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationServiceExample;
import cn.com.flaginfo.platform.registered.mybatis.vo.ApplicationServiceVo;

import java.util.List;

public interface ApplicationServiceService extends BaseService<ApplicationService,ApplicationServiceExample> {
    BaseResponse<Boolean> saveOrUpdate(ApplicationServiceVo record);

    ApplicationService getAppInfoByAppKey(String appkey);

    List<ApplicationService> getAppInfoByEventType(String eventType);


    BaseResponse registerCallbakcUrlAndEvents(ApplicationServiceVo item);
}
