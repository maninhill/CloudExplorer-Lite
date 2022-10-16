package com.fit2cloud;

import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.common.scheduler.SchedulerService;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.provider.impl.aliyun.AliyunCloudProvider;
import com.fit2cloud.provider.impl.aliyun.entity.credential.AliyunVmCredential;
import com.fit2cloud.provider.impl.aliyun.entity.request.AliyunInstanceRequest;
import com.fit2cloud.provider.impl.aliyun.entity.request.ListVirtualMachineRequest;
import com.fit2cloud.service.ISyncProviderService;
import com.fit2cloud.service.IVmCloudServerService;
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

    @Resource
    private IVmCloudServerService serverService;
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

    @Test
    public void operate(){
        AliyunVmCredential credential = new AliyunVmCredential();
        credential.setAccessKeyId("LTAI4GDCfBgxQQaD6av4zwxP");
        credential.setAccessKeySecret("glUNNlZhTKylD5DbexuPAJwzTSwYNv");
        AliyunCloudProvider cp = new AliyunCloudProvider();
        AliyunInstanceRequest req = new AliyunInstanceRequest();
        req.setCredential(JsonUtil.toJSONString(credential));
        req.setRegionId("cn-guangzhou");
        req.setUuId("i-7xv5tev4y2j9fpc84dgz");
        //System.out.println("关机："+cp.powerOff(JsonUtil.toJSONString(req)));
        //System.out.println("开机："+cp.powerOn(JsonUtil.toJSONString(req)));
        //System.out.println("重启："+cp.rebootInstance(JsonUtil.toJSONString(req)));
        System.out.println("删除："+cp.deleteInstance(JsonUtil.toJSONString(req)));

    }

    @Test
    public void searchVM(){
        AliyunVmCredential credential = new AliyunVmCredential();
        credential.setAccessKeyId("LTAI4GDCfBgxQQaD6av4zwxP");
        credential.setAccessKeySecret("glUNNlZhTKylD5DbexuPAJwzTSwYNv");
        AliyunCloudProvider cp = new AliyunCloudProvider();
        ListVirtualMachineRequest request = new ListVirtualMachineRequest();
        request.setCredential(JsonUtil.toJSONString(credential));
        request.setRegionId("cn-beijing");
        System.out.println(JsonUtil.toJSONString(cp.listVirtualMachine(JsonUtil.toJSONString(request))));
    }
}
