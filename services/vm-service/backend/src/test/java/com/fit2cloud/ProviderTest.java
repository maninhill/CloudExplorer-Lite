package com.fit2cloud;

import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.common.scheduler.SchedulerService;
import com.fit2cloud.service.ISyncProviderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author:张少虎
 * @Date: 2022/9/20  4:15 PM
 * @Version 1.0
 * @注释:
 */
@SpringBootTest(classes = VmServiceApplication.class)
@TestPropertySource(locations = {
        "classpath:commons.properties",
        "file:${ce.config.file}"
})
public class ProviderTest {
    @Resource
    private ISyncProviderService syncProviderService;
    @Resource
    private SchedulerService schedulerService;
    @Resource
    private IBaseCloudAccountService cloudAccountService;
    @Test
    public void init(){
        cloudAccountService.initCloudAccountJob("9c320c339c56b62707b4fe440163fbd1");
    }

    @Test
    public void syncServer() {
        syncProviderService.syncCloudServer(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getJobDetailsJobData());
    }

    @Test
    public void syncImag() {
        syncProviderService.syncCloudImage(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getJobDetailsJobData());
    }

    @Test
    public void syncDisk() {
        syncProviderService.syncCloudDisk(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getJobDetailsJobData());
    }

    @Test
    public void syncServerImageDisk() {
        Map<String, Object> params = schedulerService.getJobDetails("SYNC_NETWORK_18a851d307016c9cc7fefd7c45f3b6d6", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getTriggerJobData();
        syncProviderService.syncCloudServer(params);
//        syncProviderService.syncCloudImage(params);
        syncProviderService.syncCloudDisk(params);
    }
    @Test
    public void syncTencent(){
        syncProviderService.syncCloudServer("e6867e8dfb2b19747117cdc698f58cbc");
//        syncProviderService.syncCloudImage("e6867e8dfb2b19747117cdc698f58cbc");
//        syncProviderService.syncCloudDisk("e6867e8dfb2b19747117cdc698f58cbc");
    }
}
