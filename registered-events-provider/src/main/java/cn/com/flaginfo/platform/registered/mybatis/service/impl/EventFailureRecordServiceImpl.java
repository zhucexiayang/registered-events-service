package cn.com.flaginfo.platform.registered.mybatis.service.impl;

import cn.com.flaginfo.platform.registered.commons.resp.BaseResponse;
import cn.com.flaginfo.platform.registered.commons.resp.TerminalBaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecordExample;
import cn.com.flaginfo.platform.registered.mybatis.service.EventFailureRecordService;
import cn.com.flaginfo.platform.registered.mybatis.vo.EventFailureRecordVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
@Service
public class EventFailureRecordServiceImpl extends BaseServiceImpl<EventFailureRecord, EventFailureRecordExample>
        implements EventFailureRecordService{
    private Logger log= LoggerFactory.getLogger(EventFailureRecordServiceImpl.class);
    @Override
    public BaseResponse<Boolean> saveOrUpdate(EventFailureRecord record) {
        int i=this.insertSelective(record);
        if (i>0){
            return TerminalBaseResponse.success(true);
        }else{
            return TerminalBaseResponse.error("插入记录失败！");
        }
    }

    @Override
    public BaseResponse<Boolean> updateStatus(EventFailureRecord record) {
        Assert.notNull(record.getId(),"id 不能为空！");
        EventFailureRecordExample example=new EventFailureRecordExample();
        example.createCriteria().andIdEqualTo(record.getId());
        int i=this.updateByExmapleSelective(record,example);
        if (i>0){
            return TerminalBaseResponse.success(true);
        }else{
            return TerminalBaseResponse.error("插入记录失败！");
        }
    }

    @Override
    public BaseResponse<List<EventFailureRecordVo>> list(EventFailureRecord record) {
        List<EventFailureRecordVo> list1=null;
        EventFailureRecordExample example=new EventFailureRecordExample();
        example.createCriteria().andIdEqualTo(record.getId());
        List<EventFailureRecord> list=this.selectByExample(example);
        if(list!=null && list.size()>0){
            for(EventFailureRecord item :list){
                EventFailureRecordVo vo=new EventFailureRecordVo();
                BeanUtils.copyProperties(item,vo);
                list1.add(vo);
            }
        }
        return TerminalBaseResponse.success(list1);
    }
}
