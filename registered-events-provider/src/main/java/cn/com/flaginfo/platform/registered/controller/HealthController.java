package cn.com.flaginfo.platform.registered.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Liang.Zhang on 2018/12/26.
 **/

@Controller
@RequestMapping(value = "/")
@ResponseBody
public class HealthController {

    @RequestMapping(value = "/health/info")
    public Object health(){
        JSONObject jsonObject=new JSONObject(2);
        jsonObject.put("name","spdier-data-modify");
        jsonObject.put("status","up");
        jsonObject.put("remark","爬虫数据清洗服务");
        return jsonObject;
    }

}
