package cn.com.flaginfo.platform.registered.mybatis.service;

import cn.com.flaginfo.platform.registered.commons.resp.BaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecordExample;
import cn.com.flaginfo.platform.registered.mybatis.vo.EventFailureRecordVo;

import java.util.List;

public interface EventFailureRecordService extends BaseService<EventFailureRecord, EventFailureRecordExample> {
    BaseResponse<Boolean> saveOrUpdate(EventFailureRecord record);

    BaseResponse<Boolean> updateStatus(EventFailureRecord record);

    BaseResponse<List<EventFailureRecordVo>> list(EventFailureRecord record);
}
