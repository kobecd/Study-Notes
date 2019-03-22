package com.pingan.fimax.config;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.fastjson.JSONObject;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.pingan.fimax.dto.QuartzProperties;
import com.pingan.fimax.service.JobsListener;
import com.pingan.fimax.service.TriggerListner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzSchedulerConfig {

    private final QuartzProperties quartzProperties;

    @Autowired
    DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TriggerListner triggerListner;

    @Autowired
    private JobsListener jobsListener;

    public QuartzSchedulerConfig(QuartzProperties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    /**
     * create scheduler
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());

        //Register listeners to get notification on Trigger misfire etc
        factory.setGlobalTriggerListeners(triggerListner);
        factory.setGlobalJobListeners(jobsListener);

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        factory.setJobFactory(jobFactory);

        return factory;
    }

    /**
     * Configure quartz using properties file
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();

        String string = JSONObject.toJSONString(quartzProperties);
        Map<String, Object> map = JsonFlattener.flattenAsMap(string);
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            properties.setProperty(QuartzProperties.PREFIX + "." + entry.getKey(), entry.getValue().toString());
        }
        propertiesFactoryBean.setProperties(properties);

        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


}