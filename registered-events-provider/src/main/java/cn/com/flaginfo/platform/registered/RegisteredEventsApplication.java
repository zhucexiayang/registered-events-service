package cn.com.flaginfo.platform.registered;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Liang.Zhang on 2018/12/27.
 **/
@Configuration
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
@ImportResource(locations = {"classpath*:spring/*.xml"})
@MapperScan("cn.com.flaginfo.platform.registered.dao")
public class RegisteredEventsApplication {

    public static void main(String[] args){
        try {
            //从Diamond配置中心加载配置数据到环境中
            SpringApplication springApplication = new SpringApplication(RegisteredEventsApplication.class);
           // springApplication.addInitializers(new PropertiesSourceInitializer());
            springApplication.run(args);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
