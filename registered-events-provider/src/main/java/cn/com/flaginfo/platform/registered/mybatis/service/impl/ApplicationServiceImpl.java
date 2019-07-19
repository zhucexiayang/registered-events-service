package cn.com.flaginfo.platform.registered.mybatis.service.impl;

import cn.com.flaginfo.platform.api.common.base.BaseResponse;
import cn.com.flaginfo.platform.registered.commons.resp.TerminalBaseResponse;
import cn.com.flaginfo.platform.registered.commons.util.DateUtil;
import cn.com.flaginfo.platform.registered.commons.util.RandomSecretUtil;
import cn.com.flaginfo.platform.registered.commons.util.Uuid16;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationService;
import cn.com.flaginfo.platform.registered.mybatis.entity.ApplicationServiceExample;
import cn.com.flaginfo.platform.registered.mybatis.service.ApplicationServiceService;
import cn.com.flaginfo.platform.registered.mybatis.vo.ApplicationServiceVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl extends BaseServiceImpl<ApplicationService,ApplicationServiceExample> implements ApplicationServiceService {
   private Logger log= LoggerFactory.getLogger(ApplicationService.class);

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
}
