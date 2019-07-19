package cn.com.flaginfo.platform.registered.mybatis.service;

import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationServiceExample;
import cn.com.flaginfo.platform.registered.mybatis.vo.ApplicationServiceVo;
import com.fasterxml.jackson.databind.ser.Serializers;

public interface ApplicationServiceService extends BaseService<ApplicationService,ApplicationServiceExample> {
    BaseResponse<Boolean> saveOrUpdate(ApplicationServiceVo record);

    ApplicationService getAppInfoByAppKey(String appkey);
}
