package cn.com.flaginfo.platform.registered.controller;

import cn.com.flaginfo.platform.registered.mybatis.service.EventFailureRecordService;
import cn.com.flaginfo.platform.registered.mybatis.vo.EventFailureRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eventFailure")
public class EventFailureController {
    @Autowired
    private EventFailureRecordService servcie;
    @RequestMapping("list")
    public Object list(@RequestBody EventFailureRecordVo vo){
       return  servcie.list(vo);
    }
    @RequestMapping("updateStatus")
    public Object updateStatus(@RequestBody EventFailureRecordVo vo){
        return servcie.delRecords(vo);
    }
}
