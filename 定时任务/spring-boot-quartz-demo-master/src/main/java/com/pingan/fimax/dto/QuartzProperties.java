package xxx.xxx.xxx.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author:xxx
 * @Date: 2019/3/11 14:54
 */
@Data
@Component
@ConfigurationProperties(ignoreUnknownFields = false, prefix = QuartzProperties.PREFIX)
public class QuartzProperties {

    public final static String PREFIX = "org.quartz";

    private Scheduler scheduler;
    private ThreadPool threadPool;
    private JobStore jobStore;
    private Plugin plugin;

    @Data
    public static class Scheduler {
        private String instanceName;
        private String instanceId;
    }

    @Data
    public static class ThreadPool {
        private int threadCount;
        private int threadPriority;
    }

    @Data
    public static class JobStore {

        @JSONField(name="class")
        private String className;
        private String driverDelegateClass;
        private String useProperties;
        private int misfireThreshold;
        private String tablePrefix;
        private String isClustered;

    }

    @Data
    public static class Plugin {
        private ShutdownHook shutdownHook;

        @Data
        public static class ShutdownHook {
            @JSONField(name="class")
            private String className;
            private String cleanShutdown;
        }
    }

}
