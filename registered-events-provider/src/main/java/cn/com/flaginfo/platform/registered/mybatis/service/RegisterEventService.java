package cn.com.flaginfo.platform.registered.mybatis.service;

import cn.com.flaginfo.platform.registered.commons.resp.BaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEvent;
import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEventExample;
import cn.com.flaginfo.platform.registered.mybatis.vo.RegisterEventVo;

public interface RegisterEventService extends  BaseService<RegisterEvent,RegisterEventExample> {

    BaseResponse<Boolean> save(RegisterEventVo item);

    BaseResponse<Boolean>  updateEvent(RegisterEventVo vo);
}
