package cn.com.flaginfo.platform.registered.mybatis.service.impl;

import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.registered.commons.resp.TerminalBaseResponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecordExample;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.service.EventFailureRecordService;
import cn.com.flaginfo.platform.registered.mybatis.vo.EventFailureRecordVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class EventFailureRecordServiceImpl extends BaseServiceImpl<EventFailureRecord, EventFailureRecordExample>
        implements EventFailureRecordService{
    private Logger log= LoggerFactory.getLogger(EventFailureRecordServiceImpl.class);
    @Autowired
    private ApplicationServiceService appService;
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
    public BaseResponse<List<EventFailureRecordVo>> list(EventFailureRecordVo record) {
        // 验证当前appkey是否合法
       if(record.getAppKey()==null){
            log.info("the appkey is null!");
            return TerminalBaseResponse.error("appkey 为空！");
       }
       ApplicationService appInfo=appService.getAppInfoByAppKey(record.getAppKey());
       if(appInfo==null){
           log.info("appkey {} 不合法",record.getAppKey());
           return TerminalBaseResponse.error("appKey"+record.getAppKey()+"不合法");
       }
        List<EventFailureRecordVo> list1=null;
        EventFailureRecordExample example=new EventFailureRecordExample();
        example.createCriteria().andAppKeyEqualTo(record.getAppKey()).andStatusEqualTo(record.getStatus());
        List<EventFailureRecord> list=this.selectByExample(example);
        if(list!=null && list.size()>0){
            list1=new ArrayList<>(list.size());
            for(EventFailureRecord item :list){
                EventFailureRecordVo vo=new EventFailureRecordVo();
                BeanUtils.copyProperties(item,vo);
                list1.add(vo);
            }
        }
        return TerminalBaseResponse.success(list1);
    }

    @Override
    public BaseResponse<Boolean> delRecords(EventFailureRecordVo vo) {
        if(vo.getIds()==null ||vo.getIds().length==0){
            TerminalBaseResponse.error("删除记录的id为空");
        }
        EventFailureRecordExample example=new EventFailureRecordExample();
        example.createCriteria().andIdIn(new ArrayList<Long>(Arrays.asList(vo.getIds())));
        int i=this.deleteByExample(example);
        return TerminalBaseResponse.success(true);
    }
}
