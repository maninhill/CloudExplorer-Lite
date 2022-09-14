package com.fit2cloud.common.scheduler.impl;

import com.fit2cloud.common.exception.Fit2cloudException;
import com.fit2cloud.common.scheduler.impl.entity.QuzrtzJobDetail;
import com.fit2cloud.common.scheduler.SchedulerService;
import jdk.jfr.Name;
import org.apache.commons.collections4.MapUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CalendarIntervalTriggerImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.DailyTimeIntervalTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.Calendar;
import java.util.stream.Collectors;

/**
 * @Author:张少虎
 * @Date: 2022/9/8  4:03 PM
 * @Version 1.0
 * @注释:
 */
@Service
public class QuartzSchedulerServiceImpl implements SchedulerService {
    @Resource
    private Scheduler scheduler;

    @Override
    public void addJob(Class<? extends Job> jobHandler, String jobName, String groupName, String description, String cronExp, Map<String, Object> param) {
        try {
            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
            addJob(jobHandler, jobName, groupName, description, scheduleBuilder, param);
        } catch (Exception e) {
            throw new Fit2cloudException(01111, "定时任务创建失败");
        }

    }

    @Override
    public void addJob(Class<? extends Job> jobHandler, String jobName, String groupName, String description, int amount, int unit, Map<String, Object> param) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(0));
        instance.add(unit, amount);
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(instance.getTimeInMillis()).repeatForever();
        addJob(jobHandler, jobName, groupName, description, simpleScheduleBuilder, param);
    }

    @Override
    public void addJob(Class<? extends Job> jobHandler, String jobName, String groupName, String description, Map<String, Object> param, TimeOfDay startTimeDay, TimeOfDay endTimeDay, int timeInterval, DateBuilder.IntervalUnit unit, int repeatCount, Integer... weeks) {
        DailyTimeIntervalScheduleBuilder dailyTimeIntervalScheduleBuilder = getDailyTimeIntervalScheduleBuilder(startTimeDay, endTimeDay, timeInterval, unit, repeatCount, weeks);
        addJob(jobHandler, jobName, groupName, description, dailyTimeIntervalScheduleBuilder, param);
    }

    private DailyTimeIntervalScheduleBuilder getDailyTimeIntervalScheduleBuilder(TimeOfDay startTimeDay, TimeOfDay endTimeDay, int timeInterval, DateBuilder.IntervalUnit unit, int repeatCount, Integer... weeks) {
        DailyTimeIntervalScheduleBuilder dailyTimeIntervalScheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
        if (startTimeDay != null) dailyTimeIntervalScheduleBuilder.startingDailyAt(startTimeDay);
        if (endTimeDay != null) dailyTimeIntervalScheduleBuilder.endingDailyAt(endTimeDay);
        // 如果没有设置,默认值为1 分钟
        if (timeInterval > 0 && unit != null) dailyTimeIntervalScheduleBuilder.withInterval(timeInterval, unit);
        // 反之为-1 无限
        if (repeatCount > 0) dailyTimeIntervalScheduleBuilder.withRepeatCount(repeatCount);
        if (weeks != null)
            dailyTimeIntervalScheduleBuilder.onDaysOfTheWeek(Arrays.stream(weeks).collect(Collectors.toSet()));
        return dailyTimeIntervalScheduleBuilder;
    }

    @Override
    public void addJob(Class<? extends Job> jobHandler, String jobName, String groupName, String description, Map<String, Object> param, int timeInterval, DateBuilder.IntervalUnit unit) {
        CalendarIntervalScheduleBuilder calendarIntervalScheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(timeInterval, unit);
        addJob(jobHandler, jobName, groupName, description, calendarIntervalScheduleBuilder, param);
    }

    /**
     * 添加一个定时任务
     *
     * @param jobHandler      定时任务处理class
     * @param jobName         定时任务名称
     * @param groupName       定时任务分组名称
     * @param description     描述
     * @param scheduleBuilder 定时处理器
     * @param param           定时任务参数
     */
    public void addJob(Class<? extends Job> jobHandler, String jobName, String groupName, String description, ScheduleBuilder scheduleBuilder, Map<String, Object> param) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(jobHandler).withIdentity(jobName, groupName).build();
            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder).withIdentity(jobName, groupName).withDescription(description).build();
            if (MapUtils.isNotEmpty(param)) {
                trigger.getJobDataMap().putAll(param);
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new Fit2cloudException(01111, "定时任务创建失败");
        }

    }


    public void updateJob(String jobName, String groupName, String description, Map<String, Object> param, TimeOfDay startTimeDay, TimeOfDay endTimeDay, int timeInterval, DateBuilder.IntervalUnit unit, int repeatCount, Trigger.TriggerState triggerState, Integer... weeks) {
        DailyTimeIntervalScheduleBuilder dailyTimeIntervalScheduleBuilder = getDailyTimeIntervalScheduleBuilder(startTimeDay, endTimeDay, timeInterval, unit, repeatCount, weeks);
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withSchedule(dailyTimeIntervalScheduleBuilder).withIdentity(jobName, groupName).withDescription(description).build();
        if (MapUtils.isNotEmpty(param)) {
            trigger.getJobDataMap().putAll(param);
        }
        try {
            scheduler.rescheduleJob(TriggerKey.triggerKey(jobName, groupName), trigger);
            if (triggerState.equals(Trigger.TriggerState.PAUSED)) {
                pauseJob(jobName, groupName);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public QuzrtzJobDetail getJobDetails(String jobName, String groupName) {
        return getJobDetails(TriggerKey.triggerKey(jobName, groupName));
    }

    @Override
    public boolean inclusionJobDetails(String jobName, String groupName) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, groupName));
            if (jobDetail != null) {
                return true;
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 获取job详细信息
     *
     * @param triggerKey TriggerKey对象
     * @return job详细信息
     */
    public QuzrtzJobDetail getJobDetails(TriggerKey triggerKey) {
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger == null) return null;
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
            return mapQuzrtzJobDetail(trigger, jobDetail, triggerState);
        } catch (SchedulerException e) {
            throw new Fit2cloudException(100000, e.getMessage());
        }
    }


    /**
     * 将对象转换为任务详细信息
     *
     * @param trigger      trigger信息
     * @param jobDetail    任务详情信息
     * @param triggerState 任务状态
     * @return 任务详细信息
     */
    private QuzrtzJobDetail mapQuzrtzJobDetail(Trigger trigger, JobDetail jobDetail, Trigger.TriggerState triggerState) {
        Class<? extends Job> jobClass = jobDetail.getJobClass();
        String className = jobClass.getName();
        String nickName = "";
        try {
            Name annotation = jobClass.getAnnotation(Name.class);
            nickName = annotation.value();
        } catch (Exception e) {
            nickName = jobClass.getName();
        }
        DateBuilder.IntervalUnit unit = null;
        Long repeatInterval = null;
        String cronEx = null;
        if (trigger instanceof SimpleTriggerImpl) {
            SimpleTriggerImpl simpleTrigger = (SimpleTriggerImpl) trigger;
            repeatInterval = simpleTrigger.getRepeatInterval();
            unit = DateBuilder.IntervalUnit.MILLISECOND;
        }
        if (trigger instanceof CalendarIntervalTriggerImpl) {
            CalendarIntervalTriggerImpl calendarIntervalTrigger = (CalendarIntervalTriggerImpl) trigger;
            unit = calendarIntervalTrigger.getRepeatIntervalUnit();
            repeatInterval = (long) calendarIntervalTrigger.getRepeatInterval();
        }
        if (trigger instanceof DailyTimeIntervalTriggerImpl) {
            DailyTimeIntervalTriggerImpl dailyTimeIntervalTrigger = (DailyTimeIntervalTriggerImpl) trigger;
            unit = dailyTimeIntervalTrigger.getRepeatIntervalUnit();
            repeatInterval = (long) dailyTimeIntervalTrigger.getRepeatInterval();
        }
        if (trigger instanceof CronTriggerImpl) {
            CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
            cronEx = cronTrigger.getCronExpression();
        }
        return new QuzrtzJobDetail(trigger.getJobKey().getName(), trigger.getJobKey().getGroup(), trigger.getJobDataMap().getWrappedMap(), trigger.getDescription(), trigger.getNextFireTime(), trigger.getPreviousFireTime(), triggerState, className, nickName, cronEx, unit, repeatInterval);
    }

    @Override
    public List<QuzrtzJobDetail> list() {
        try {
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup());
            List<QuzrtzJobDetail> quzrtzJobDetails = triggerKeys.stream().map(this::getJobDetails).toList();
            return quzrtzJobDetails;
        } catch (Exception e) {
            throw new Fit2cloudException(011111, e.getMessage());
        }

    }


    @Override
    public void pauseJob(String jobName, String groupName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new Fit2cloudException(11111, "暂停任务失败");
        }
    }

    @Override
    public void resumeJob(String jobName, String groupName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new Fit2cloudException(11111, "恢复任务失败");
        }
    }


    @Override
    public void deleteJob(String jobName, String groupName) {
        try {
            //暂停、移除、删除
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, groupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, groupName));
            scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
        } catch (Exception e) {
            throw new Fit2cloudException(10000, e.getMessage());
        }
    }

    @Override
    public void startAllJobs() {

        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new Fit2cloudException(10000, "开启所有的任务失败");
        }
    }

    @Override
    public void pauseAllJobs() {

        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new Fit2cloudException(10000, "暂停所有任务失败");
        }
    }

    @Override
    public void resumeAllJobs() {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            throw new Fit2cloudException(10000, "恢复所有任务失败");
        }
    }

    @Override
    public void shutdownAllJobs() {
        try {
            // 需谨慎操作关闭scheduler容器
            // scheduler生命周期结束，无法再 start() 启动scheduler
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            throw new Fit2cloudException(10000, "关闭所有的任务失败");
        }
    }
}
