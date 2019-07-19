package cn.com.flaginfo.platform.registered.mybatis.service.impl;

import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.registered.commons.resp.TerminalBaseResponse;
import cn.com.flaginfo.platform.registered.commons.util.DateUtil;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationServiceExample;
import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEvent;
import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEventExample;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.service.RegisterEventService;
import cn.com.flaginfo.platform.registered.mybatis.vo.RegisterEventVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterEventServiceImpl extends  BaseServiceImpl<RegisterEvent,RegisterEventExample> implements RegisterEventService {
    private Logger log= LoggerFactory.getLogger(RegisterEventServiceImpl.class);
    @Autowired
    ApplicationServiceService appService;

    @Override
    public BaseResponse<Boolean> save(RegisterEventVo item) {
        return this.saveEvent(item);
    }

    @Override
    public BaseResponse<Boolean> updateEvent(RegisterEventVo vo) {
        ApplicationService app=this.getAppInfo(vo);
        if(app==null){
            return TerminalBaseResponse.error("appkey"+vo.getAppKey()+" is nonexisten" );
        }
        //删除原来的注册事件
        RegisterEventExample example=new RegisterEventExample();
        example.createCriteria().andAppIdEqualTo(app.getId());
        RegisterEvent item=new RegisterEvent();
        this.deleteByExample(example);
        //新增注册事件
        this.saveEvent(vo);
        return TerminalBaseResponse.success(true);
    }

    private BaseResponse<Boolean> saveEvent(RegisterEventVo item){
        log.info("the register events is {}",item);
        Assert.notNull(item.getAppKey(),"应用appKey不为空！");
        if(item.getEnvents()==null || item.getEnvents().length==0){
            return TerminalBaseResponse.error("注册事件不能为空！");
        }
        ApplicationService app=this.getAppInfo(item);
        if(app==null){
            return TerminalBaseResponse.error("appkey"+item.getAppKey()+" is nonexisten" );
        }
        List<RegisterEvent> list=new ArrayList<>(item.getEnvents().length);
        for(String event:item.getEnvents()){
            RegisterEvent event1=new RegisterEvent();
            event1.setAppId(app.getId());
            event1.setEventType(event);
            event1.setStatus("use");
            event1.setCreateDate(DateUtil.getDates());
            list.add(event1);
        }
        this.insertBatch(list);
        return TerminalBaseResponse.success(true);
    }

    private ApplicationService getAppInfo(RegisterEventVo item){
        //验证当前app key是否存在或者
        return appService.getAppInfoByAppKey(item.getAppKey());
    }
}
