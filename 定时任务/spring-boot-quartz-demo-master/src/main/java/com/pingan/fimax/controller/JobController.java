package xxx.xxx.xxx.controller;

import java.util.Date;

import xxx.xxx.xxx.job.JobTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import xxx.xxx.xxx.dto.ServerResponse;
import xxx.xxx.xxx.service.JobService;


@RestController
@RequestMapping("/scheduler/")
public class JobController {

	@Autowired
	@Lazy
	JobService jobService;


	@RequestMapping(value = "add")
	public ServerResponse schedule(@RequestParam("jobName") String jobName,
                                   @RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime,
                                   @RequestParam("cronExpression") String cronExpression,
                                   @RequestParam("groupKey") String groupKey){
		System.out.println("JobController.schedule()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			//return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		//Check if job Name is unique;
		if(!jobService.isJobWithNamePresent(jobName,groupKey)){

				//Cron Trigger
				jobService.addJob(jobName,groupKey, JobTest.class, jobScheduleTime, cronExpression);

		}else{
			//return getServerResponse(ServerResponseCode.JOB_WITH_SAME_NAME_EXIST, false);
		}
		return null;
	}


    @RequestMapping("pause")
	public ServerResponse pause(@RequestParam("jobName") String jobName,@RequestParam("groupKey") String groupKey) {
		System.out.println("JobController.pause()");

		if(jobService.isJobWithNamePresent(jobName,groupKey)){

			boolean isJobRunning = jobService.isJobRunning(jobName,groupKey);

			if(!isJobRunning){
				jobService.pauseJob(jobName,groupKey);

			}else{
				//return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}

		}else{
			//Job doesn't exist
			//return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
		return  null;
	}

    	@RequestMapping("resume")
	public ServerResponse resume(@RequestParam("jobName") String jobName,@RequestParam("groupKey") String groupKey) {
		System.out.println("JobController.resume()");

		if(jobService.isJobWithNamePresent(jobName,groupKey)){
			String jobState = jobService.getJobState(jobName,groupKey);

			if(jobState.equals("PAUSED")){
				System.out.println("Job current state is PAUSED, Resuming job...");
				jobService.resumeJob(jobName,groupKey);

			}else{
				//return getServerResponse(ServerResponseCode.JOB_NOT_IN_PAUSED_STATE, false);
			}

		}else{
			//Job doesn't exist
			//return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
		return null;
	}

    	@RequestMapping("update")
	public ServerResponse updateJob(@RequestParam("jobName") String jobName,@RequestParam("groupKey") String groupKey,
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime,
			@RequestParam("cronExpression") String cronExpression){
		System.out.println("JobController.updateJob()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			//return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
		}

		//Edit Job
		if(jobService.isJobWithNamePresent(jobName,groupKey)){

				//Cron Trigger
				jobService.updateCronJob(jobName,groupKey,jobScheduleTime, cronExpression);

		}else{
			//return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
		return null;
	}

	@RequestMapping("delete")
	public ServerResponse delete(@RequestParam("jobName") String jobName,@RequestParam("groupKey") String groupKey) {
		System.out.println("JobController.delete()");

		if(jobService.isJobWithNamePresent(jobName,groupKey)){
			boolean isJobRunning = jobService.isJobRunning(jobName,groupKey);

			if(!isJobRunning){
			jobService.deleteJob(jobName,groupKey);

			}else{
				//return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
			}
		}else{
			//Job doesn't exist
			//return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
		}
		return null;
	}


//

//

//	@RequestMapping("unschedule")
//	public void unschedule(@RequestParam("jobName") String jobName) {
//		System.out.println("JobController.unschedule()");
//		jobService.unScheduleJob(jobName);
//	}
//
//

//
//	@RequestMapping("jobs")
//	public ServerResponse getAllJobs(){
//		System.out.println("JobController.getAllJobs()");
//
//		List<Map<String, Object>> list = jobService.getAllJobs();
//		return getServerResponse(ServerResponseCode.SUCCESS, list);
//	}
//
//	@RequestMapping("checkJobName")
//	public ServerResponse checkJobName(@RequestParam("jobName") String jobName){
//		System.out.println("JobController.checkJobName()");
//
//		//Job Name is mandatory
//		if(jobName == null || jobName.trim().equals("")){
//			return getServerResponse(ServerResponseCode.JOB_NAME_NOT_PRESENT, false);
//		}
//
//		boolean status = jobService.isJobWithNamePresent(jobName);
//		return getServerResponse(ServerResponseCode.SUCCESS, status);
//	}
//
//	@RequestMapping("isJobRunning")
//	public ServerResponse isJobRunning(@RequestParam("jobName") String jobName) {
//		System.out.println("JobController.isJobRunning()");
//
//		boolean status = jobService.isJobRunning(jobName);
//		return getServerResponse(ServerResponseCode.SUCCESS, status);
//	}
//
//	@RequestMapping("jobState")
//	public ServerResponse getJobState(@RequestParam("jobName") String jobName) {
//		System.out.println("JobController.getJobState()");
//
//		String jobState = jobService.getJobState(jobName);
//		return getServerResponse(ServerResponseCode.SUCCESS, jobState);
//	}
//

//
//	@RequestMapping("start")
//	public ServerResponse startJobNow(@RequestParam("jobName") String jobName) {
//		System.out.println("JobController.startJobNow()");
//
//		if(jobService.isJobWithNamePresent(jobName)){
//
//			if(!jobService.isJobRunning(jobName)){
//				boolean status = jobService.startJobNow(jobName);
//
//				if(status){
//					//Success
//					return getServerResponse(ServerResponseCode.SUCCESS, true);
//
//				}else{
//					//Server error
//					return getServerResponse(ServerResponseCode.ERROR, false);
//				}
//
//			}else{
//				//Job already running
//				return getServerResponse(ServerResponseCode.JOB_ALREADY_IN_RUNNING_STATE, false);
//			}
//
//		}else{
//			//Job doesn't exist
//			return getServerResponse(ServerResponseCode.JOB_DOESNT_EXIST, false);
//		}
//	}

}
