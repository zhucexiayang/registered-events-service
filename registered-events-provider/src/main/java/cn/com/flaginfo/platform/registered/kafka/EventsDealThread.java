package cn.com.flaginfo.platform.registered.kafka;


import cn.com.flaginfo.platform.registered.commons.util.DateUtil;
import cn.com.flaginfo.platform.registered.commons.util.HttpClientUtils;
import cn.com.flaginfo.platform.registered.commons.util.SpringUtil;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.service.EventFailureRecordService;
import cn.com.flaginfo.platform.registered.mybatis.service.impl.ApplicationServiceImpl;
import cn.com.flaginfo.platform.registered.mybatis.service.impl.EventFailureRecordServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 判断kafka读取的数据，并在此做对应的业务处理
 */
public class EventsDealThread implements  Runnable {
    private ApplicationServiceImpl appService= (ApplicationServiceImpl) SpringUtil.getBean(ApplicationServiceImpl.class);
    @Autowired
    private EventFailureRecordService eventService= (EventFailureRecordService) SpringUtil.getBean(EventFailureRecordServiceImpl.class);
    private Logger log= LoggerFactory.getLogger(EventsDealThread.class);
    private String message;
    EventsDealThread(String message){
        this.message=message;
    }
    @Override
    public void run() {
        //json数据格式化
        JSONObject jsonObject= JSON.parseObject(message);
        System.out.println("the info "+message);
        Integer logType=jsonObject.getInteger("logType");
        log.info("the log type is {}",logType);
        switch (logType){
            case 1004:
                String type=jsonObject.getJSONObject("logInfo").getString("type");
                if(type.equals("first-login")){
                    this.dealBusiFirstLoginEvent(jsonObject,type);
                }
                break;
            default:
                break;
        }
    }

    private void dealBusiFirstLoginEvent(JSONObject json,String eventType){
        //遍历所有注册当前事件的业务
        List<ApplicationService> list=appService.getAppInfoByEventType(eventType);
        if(list!=null && list.size()>0){
            //通知所有注册当前事件的服务器
            for(ApplicationService item: list){
                JSONObject jsonParam=this.packageJsonData(json,eventType);
                try {
                    jsonParam.put("appKey",item.getAppKey());
                    String result = HttpClientUtils.doPostWithNoParamsName(jsonParam.toJSONString(),item.getCallbackUrl());
                    if(result!=null ||result.length()>0){
                        // 解析返回值信息
                        JSONObject resultJson=JSON.parseObject(result);
                        if (resultJson.containsKey("status") && resultJson.getString("status").equals("SUCCESS")){
                            log.info("the app key {},the event type is {},callback url [{}] success,the content is:[{}]",
                                    item.getAppKey(),eventType,item.getCallbackUrl(),json.toString());
                        }else{
                            //失败数据处理
                            log.info("the app key {},the event type is {},callback url [{}] fault,the content is:[{}]",
                                    item.getAppKey(),eventType,item.getCallbackUrl(),json.toString());
                            this.dealFailureRecord(jsonParam,item.getId(),item.getAppKey(),eventType);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("the app key {},the event type is {},callback url [{}] fault,the content is:[{}]",
                            item.getAppKey(),eventType,item.getCallbackUrl(),json.toString());
                    //插入当前记录到失败记录表
                    this.dealFailureRecord(jsonParam,item.getId(),item.getAppKey(),eventType);
                }
            }
        }
    }

    public JSONObject packageJsonData(JSONObject json,String eventType){
        JSONObject jsonInfo=new JSONObject();
        for(String item:json.keySet()){
            jsonInfo.put(item,json.get(item));
        }
        JSONObject jsonT=new JSONObject();
        jsonT.put("data",jsonInfo.toJSONString());
        jsonT.put("eventType",eventType);
        return jsonT;
    }
    private void dealFailureRecord(JSONObject jsonObject,Long appId,String appkey,String eventType){
        EventFailureRecord record=new EventFailureRecord();
        record.setContent(jsonObject.toJSONString());
        record.setCreateDate(DateUtil.getDates());
        record.setStatus("noDeal");
        record.setAppId(appId);
        record.setAppKey(appkey);
        record.setEventType(eventType);
        eventService.saveOrUpdate(record);
    }
}
