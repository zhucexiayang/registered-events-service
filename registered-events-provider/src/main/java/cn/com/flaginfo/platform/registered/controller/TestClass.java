package cn.com.flaginfo.platform.registered.controller;

import cn.com.flaginfo.platform.registered.commons.reqs.RequestParamInfo;
import cn.com.flaginfo.platform.registered.commons.resp.ResultReponse;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestClass {
    private Logger log= LoggerFactory.getLogger(TestClass.class);
    @Autowired
    private ApplicationServiceService appService;
    @RequestMapping("testUrl")
    public Object testUrl(@RequestBody RequestParamInfo info){
        System.out.println("the pamra is "+info.toString());
        log.info("the param is {}",info);
        Assert.notNull(info.getAppKey(),"appKey 不能为空！");
        Assert.notNull(info.getData(),"数据不能为空！");
        JSONObject json= JSON.parseObject(info.getData().toString());
        ResultReponse reponse1=new ResultReponse();
        if(json.containsKey("test_status") && json.getString("test_status").equals("SUCCESS")){
            reponse1.setAppkey(info.getAppKey());
            reponse1.setStatus("SUCCESS");
            return reponse1;
        }else{
            log.info("解析data数据。。。{}，",info.getData().toString());
            //业务处理代码
            reponse1.setAppkey(info.getAppKey());
            reponse1.setStatus("SUCCESS");
            return  reponse1;
        }
    }

}
