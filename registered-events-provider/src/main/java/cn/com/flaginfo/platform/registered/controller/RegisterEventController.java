package cn.com.flaginfo.platform.registered.controller;

import cn.com.flaginfo.platform.registered.mybatis.service.RegisterEventService;
import cn.com.flaginfo.platform.registered.mybatis.vo.RegisterEventVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registerEvent")
public class RegisterEventController {
    @Autowired
    RegisterEventService service;

    @RequestMapping("/registerEvent")
    public Object registerEvent(@RequestBody RegisterEventVo vo){
        return service.save(vo);
    }
    @RequestMapping("/updateRegisterEvent")
    public Object updateRegisgerEvent(@RequestBody RegisterEventVo vo){
        return service.updateEvent(vo);
    }
}
