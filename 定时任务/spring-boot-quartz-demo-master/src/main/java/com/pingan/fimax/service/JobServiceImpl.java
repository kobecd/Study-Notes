package com.pingan.fimax.service;


import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pingan.fimax.dto.JobException;
import com.pingan.fimax.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.quartz.Trigger.TriggerState;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class JobServiceImpl implements JobService{

	@Autowired
	@Lazy
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private ApplicationContext context;

	/**
	 * Schedule a job by jobName at given date.
	 */
	@Override
	public void addJob(String jobName, String groupKey, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression){
        log.info("JobServiceImpl_addJob_jobName={}", JSON.toJSONString(jobName));
		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			throw new JobException(ResponseCode.JOB_NAME_IS_EMPTY.getVal(), ResponseCode.JOB_NAME_IS_EMPTY.getMsg());
		}
		//Check if job Name is unique;
		if(!isJobWithNamePresent(jobName,groupKey)){
			JobDetail jobDetail = JobUtil.createJob(jobClass, false, context, jobName, groupKey);
            log.info("creating trigger for key :"+jobName + " at date :"+date);
			Trigger cronTriggerBean = JobUtil.createCronTrigger(jobName, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

			try {
				Scheduler scheduler = schedulerFactoryBean.getScheduler();
				Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
				log.info("Job with key jobKey :"+jobName+ " and group :"+groupKey+ " scheduled successfully for date :"+dt);
			} catch (Exception e) {
				log.info("SchedulerException while scheduling job with key :"+jobName + " message :"+e.getMessage());
				e.printStackTrace();
			}
		}else{
			throw new JobException(ResponseCode.JOB_KEY_EXIST.getVal(), ResponseCode.JOB_KEY_EXIST.getMsg());
		}
	}

	@Override
	public void deleteJob(String jobName,String groupKey)  {
		log.info("JobServiceImpl_deleteJob_jobName={}", JSON.toJSONString(jobName));
		JobKey jobKey = new JobKey(jobName, groupKey);
		if(isJobWithNamePresent(jobName,groupKey)) {
			try {
				boolean status = schedulerFactoryBean.getScheduler().deleteJob(jobKey);
				if (!status) {
					throw new JobException(ResponseCode.DELETE_FAILED.getVal(), ResponseCode.DELETE_FAILED.getMsg());
				}
			} catch (Exception e) {
				log.info("SchedulerException while deleting job with key :"+jobKey + " message :"+e.getMessage());
				e.printStackTrace();
			}
		}else {
			//Job doesn't exist
			throw new JobException(ResponseCode.JOB_DOESNT_EXIS.getVal(), ResponseCode.JOB_DOESNT_EXIS.getMsg());
		}
	}
	/**
	 * Check job exist with given name
	 */
	@Override
	public boolean isJobWithNamePresent(String jobName,String groupKey) {
		try {
			JobKey jobKey = new JobKey(jobName, groupKey);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)){
				return true;
			}
		} catch (Exception e) {
			log.info("SchedulerException while checking job with name and group exist:"+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}



	/**
	 * Update scheduled cron job.
	 */
	@Override
	public void updateCronJob(String jobName,String groupKey, Date date, String cronExpression) {
		log.info("JobServiceImpl_updateCronJob_jobName={}", JSON.toJSONString(jobName));
		if(isJobWithNamePresent(jobName,groupKey)) {
			log.info("Parameters received for updating cron job : jobKey :" + jobName + ", date: " + date);
			try {
				Trigger newTrigger = JobUtil.createCronTrigger(jobName, date, cronExpression, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

				Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobName), newTrigger);
				log.info("Trigger associated with jobKey :" + jobName + " rescheduled successfully for date :" + dt);

			} catch (Exception e) {
				log.info("SchedulerException while updating cron job with key :" + jobName + " message :" + e.getMessage());
				e.printStackTrace();
			}
		}else {
               //Job doesn't exist
			throw new JobException(ResponseCode.JOB_DOESNT_EXIS.getVal(), ResponseCode.JOB_DOESNT_EXIS.getMsg());
		}
	}


	/**
	 * Check if job is already running
	 */
	@Override
	public boolean isJobRunning(String jobName,String groupKey) {
		log.info("JobServiceImpl_isJobRunning_jobName={}", JSON.toJSONString(jobName));
		try {

			List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
			if(currentJobs!=null){
				for (JobExecutionContext jobCtx : currentJobs) {
					String jobNameDB = jobCtx.getJobDetail().getKey().getName();
					String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
					if (jobName.equalsIgnoreCase(jobNameDB) && groupKey.equalsIgnoreCase(groupNameDB)) {
						return true;
					}
				}
			}
		} catch (SchedulerException e) {
			log.info("SchedulerException while checking job with key :"+jobName+ " is running. error message :"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Start a job now
	 */
	@Override
	public void startJobNow(String jobName,String groupKey) {
		log.info("JobServiceImpl_startJobNow_jobName={}", JSON.toJSONString(jobName));
		if(isJobWithNamePresent(jobName,groupKey)){

			if(!isJobRunning(jobName,groupKey)){
				String jobKey = jobName;
				JobKey jKey = new JobKey(jobKey, groupKey);
				log.info("Parameters received for starting job now : jobKey :"+jobKey);

				try {
					schedulerFactoryBean.getScheduler().triggerJob(jKey);
					log.info("Job with jobKey :"+jobKey+ " started now succesfully.");

				} catch (SchedulerException e) {
					log.info("SchedulerException while starting job now with key :"+jobKey+ " message :"+e.getMessage());
					e.printStackTrace();
				}
			}else{
				//Job already running
				throw new JobException(ResponseCode.JOB_ALREADY_RUNNING.getVal(), ResponseCode.JOB_ALREADY_RUNNING.getMsg());

			}
		}else{
			//Job doesn't exist
			throw new JobException(ResponseCode.JOB_DOESNT_EXIS.getVal(), ResponseCode.JOB_DOESNT_EXIS.getMsg());
		}
	}

	/**
	 * Pause a job
	 */
	@Override
	public void pauseJob(String jobName,String groupKey) {
		log.info("JobServiceImpl_pauseJob_jobName={}", JSON.toJSONString(jobName));

		JobKey jkey = new JobKey(jobName, groupKey);
		boolean isJobRunning = isJobRunning(jobName,groupKey);
		if(isJobWithNamePresent(jobName,groupKey)){
		if(!isJobRunning){
		try {
			schedulerFactoryBean.getScheduler().pauseJob(jkey);
			log.info("Job with jobKey :"+jobName+ " paused succesfully.");

		} catch (SchedulerException e) {
			log.info("SchedulerException while pausing job with key :"+jobName + " message :"+e.getMessage());
			e.printStackTrace();
		}
		}else{
			throw new JobException(ResponseCode.JOB_ALREADY_RUNNING.getVal(), ResponseCode.JOB_ALREADY_RUNNING.getMsg());
			}
		}else{
			//Job doesn't exist
			throw new JobException(ResponseCode.JOB_DOESNT_EXIS.getVal(), ResponseCode.JOB_DOESNT_EXIS.getMsg());
		}

	}

	/**
	 * Get the current state of job
	 */
	@Override
	public String getJobState(String jobName,String groupKey) {
		log.info("JobServiceImpl_getJobState_jobName={}", JSON.toJSONString(jobName));

		try {
			JobKey jobKey = new JobKey(jobName, groupKey);

			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);

			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
			if(triggers != null && triggers.size() > 0){
				for (Trigger trigger : triggers) {
					TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

					if (TriggerState.PAUSED.equals(triggerState)) {
						return "PAUSED";
					}else if (TriggerState.BLOCKED.equals(triggerState)) {
						return "BLOCKED";
					}else if (TriggerState.COMPLETE.equals(triggerState)) {
						return "COMPLETE";
					}else if (TriggerState.ERROR.equals(triggerState)) {
						return "ERROR";
					}else if (TriggerState.NONE.equals(triggerState)) {
						return "NONE";
					}else if (TriggerState.NORMAL.equals(triggerState)) {
						return "SCHEDULED";
					}
				}
			}
		} catch (SchedulerException e) {
			log.info("SchedulerException while checking job with name and group exist:"+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Resume paused job
	 */
	@Override
	public void resumeJob(String jobName,String groupKey) {
		log.info("JobServiceImpl_resumeJob_jobName={}", JSON.toJSONString(jobName));
		String jobKey = jobName;
		JobKey jKey = new JobKey(jobKey, groupKey);
		if(isJobWithNamePresent(jobName,groupKey)){
			String jobState = getJobState(jobName,groupKey);
			if(jobState.equals("PAUSED")){
				log.info("Job current state is PAUSED, Resuming job...");
				try {
					schedulerFactoryBean.getScheduler().resumeJob(jKey);
					log.info("Job with jobKey :"+jobKey+ " resumed succesfully.");

				} catch (SchedulerException e) {
					log.info("SchedulerException while resuming job with key :"+jobKey+ " message :"+e.getMessage());
					e.printStackTrace();
				}

			}else{
				throw new JobException(ResponseCode.JOB_NOT_IN_PAUSED_STATE.getVal(), ResponseCode.JOB_NOT_IN_PAUSED_STATE.getMsg());
			}
		}else{
			//Job doesn't exist
			throw new JobException(ResponseCode.JOB_DOESNT_EXIS.getVal(), ResponseCode.JOB_DOESNT_EXIS.getMsg());

		}
	}

}

