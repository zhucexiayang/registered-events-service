package cn.com.flaginfo.platform.registered.commons.diamond;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 初始化配置
 * @author Meng.Liu
 * @create 2017-09-26 10:54
 **/
public class PropertiesSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public PropertiesSourceInitializer() {
    }

    public void initialize(ConfigurableApplicationContext applicationContext) {
        DiamondProperties.initInstance(applicationContext.getEnvironment());
    }
}