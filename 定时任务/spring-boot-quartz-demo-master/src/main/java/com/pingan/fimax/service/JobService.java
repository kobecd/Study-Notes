package xxx.xxx.xxx.service;

import java.util.Date;

import org.springframework.scheduling.quartz.QuartzJobBean;

public interface JobService {
	void addJob(String jobName, String groupKey, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression);
	void updateCronJob(String jobName,String groupKey, Date date, String cronExpression);
	void deleteJob(String jobName,String groupKey);
	void pauseJob(String jobName,String groupKey);
	void resumeJob(String jobName,String groupKey);
	void startJobNow(String jobName,String groupKey);
	boolean isJobRunning(String jobName,String groupKey);
	boolean isJobWithNamePresent(String jobName,String groupKey);
	String getJobState(String jobName,String groupKey);
}
