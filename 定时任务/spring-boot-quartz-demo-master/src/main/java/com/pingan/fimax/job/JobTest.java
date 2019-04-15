package xxx.xxx.xxx.job;

import xxx.xxx.xxx.service.JobService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Author:xxx
 * @Date: 2019/3/7 9:36
 */
public class JobTest extends QuartzJobBean implements InterruptableJob {

   	@Autowired
    JobService jobService;
    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        System.out.println("Thread: "+ Thread.currentThread().getName() +" stopped."+new SimpleDateFormat("yyyy年MM月dd日 KK:mm:ss").format(new Date()));
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("Thread: "+ Thread.currentThread().getName() +" stopped."+new SimpleDateFormat("yyyy年MM月dd日 KK:mm:ss").format(new Date()));
       //jobService.addJob("job1","jobGroup1",JobTest.class,new Date(),"0/5 * * * * ? ");
    }
}
