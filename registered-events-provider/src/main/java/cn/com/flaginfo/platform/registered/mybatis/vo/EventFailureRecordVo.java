package cn.com.flaginfo.platform.registered.mybatis.vo;

import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;

public class EventFailureRecordVo extends EventFailureRecord {
    private Long[] ids;

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
}
