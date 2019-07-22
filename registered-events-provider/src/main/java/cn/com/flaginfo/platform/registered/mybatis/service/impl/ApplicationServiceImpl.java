package cn.com.flaginfo.platform.registered.mybatis.service.impl;


import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.registered.commons.resp.TerminalBaseResponse;
import cn.com.flaginfo.platform.registered.commons.util.DateUtil;
import cn.com.flaginfo.platform.registered.commons.util.HttpClientUtils;
import cn.com.flaginfo.platform.registered.commons.util.RandomSecretUtil;
import cn.com.flaginfo.platform.registered.commons.util.Uuid16;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationServiceExample;
import cn.com.flaginfo.platform.registered.mybatis.entity.RegisterEvent;
import cn.com.flaginfo.platform.registered.mybatis.mapper.ApplicationServiceMapper;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.service.RegisterEventService;
import cn.com.flaginfo.platform.registered.mybatis.vo.ApplicationServiceVo;
import cn.com.flaginfo.platform.registered.mybatis.vo.RegisterEventVo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl extends BaseServiceImpl<ApplicationService,ApplicationServiceExample> implements ApplicationServiceService {
    private Logger log= LoggerFactory.getLogger(ApplicationService.class);
    @Autowired
    private ApplicationServiceMapper mapper;
    @Autowired
    private RegisterEventService eventService;
    @Override
    public BaseResponse<Boolean> saveOrUpdate(ApplicationServiceVo record) {
        ApplicationService item= new ApplicationService();
        BeanUtils.copyProperties(record,item);
        if(item.getId()==null){
            // 插入操作
            item.setCreateDate(DateUtil.getDates());
            item.setAppKey("app"+ Uuid16.create());
            item.setAppEncodingKey(RandomSecretUtil.getRandomStr(43));
            log.info("add the application :{}",record);
            this.insertSelective(item);
            return TerminalBaseResponse.success(true);

        }else{
            //修改操作
            ApplicationService vo=this.findOne(record.getId());
            if(vo ==null){
                log.info("the id {} is not existence,please confirm!",record.getId());
                return TerminalBaseResponse.error("当前记录id不存在！");
            }
            ApplicationServiceExample example=new ApplicationServiceExample();
            example.createCriteria().andIdEqualTo(item.getId());
            this.updateByExmapleSelective(item,example);
            log.info("modify the application info :{}",record);
            return TerminalBaseResponse.success(true);
        }
    }

    @Override
    public ApplicationService getAppInfoByAppKey(String appkey) {
        ApplicationServiceExample example=new ApplicationServiceExample();
        example.createCriteria().andAppKeyEqualTo(appkey).andFlagEqualTo("1");
        List<ApplicationService> list=this.selectByExample(example);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<ApplicationService> getAppInfoByEventType(String eventType) {
        ApplicationService record=new ApplicationService();
        record.setEventType(eventType);
        return mapper.getAppInfoByEventType(record);
    }

    @Override
    public BaseResponse registerCallbakcUrlAndEvents(ApplicationServiceVo item) {
        //验证appkey是否有效
        ApplicationService appInfo=this.getAppInfoByAppKey(item.getAppKey());
        if(appInfo==null){
            return TerminalBaseResponse.error("无效的appkey:"+item.getAppKey()+"");
        }
        //验证url是否可访问，设置回调url
        if(!this.validateUrl(item.getCallbackUrl(),item.getAppKey())){
            return TerminalBaseResponse.error("回调url:"+item.getCallbackUrl()+",连接超时！");
        }
        ApplicationServiceExample example=new ApplicationServiceExample();
        example.createCriteria().andIdEqualTo(appInfo.getId()).andFlagEqualTo("1");
        ApplicationService app=new ApplicationService();
        app.setId(appInfo.getId());
        this.updateByExmapleSelective(item,example);
        //注册回调事件
        if(item.getRegsiterEvent()!=null && item.getRegsiterEvent().length>0){
            RegisterEventVo vo=new RegisterEventVo();
            vo.setEnvents(item.getRegsiterEvent());
            vo.setAppKey(item.getAppKey());
            return eventService.save(vo);
        }else{
            return TerminalBaseResponse.error("注册回调事件失败！");
        }
    }

    private boolean validateUrl(String url,String appKey){
        JSONObject json=new JSONObject();
        json.put("appKey",appKey);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("test_status","SUCCESS");
        json.put("data",jsonObject.toJSONString());
        try {
            String result = HttpClientUtils.doPostWithNoParamsName(json.toJSONString(), url);
            JSONObject resultJson=JSONObject.parseObject(result);
            if(resultJson.containsKey("status") && resultJson.getString("status").equals("SUCCESS")){
                log.info("validate the url {"+url+"} 成功！");
                return true;
            }else{
                log.info("validate the url {"+url+"} 失败！");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("test connction fault with url:{}",url);
            return false;
        }
    }
}
