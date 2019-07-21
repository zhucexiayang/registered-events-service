package cn.com.flaginfo.platform.registered.controller;

import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.vo.ApplicationServiceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appService")
public class ApplicationServiceController {
    @Autowired
    ApplicationServiceService service;

    @RequestMapping("/saveOrUpdate")
    public Object saveOrUpdateInfo(@RequestBody ApplicationServiceVo item){
        return service.saveOrUpdate(item);
    }

    @RequestMapping("registerUrlAndEvents")
    public Object registerCallbackUrlAndEvents(@RequestBody ApplicationServiceVo item){
        return service.registerCallbakcUrlAndEvents(item);
    }
}
