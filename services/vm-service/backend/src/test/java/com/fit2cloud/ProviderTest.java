package com.fit2cloud;

import com.fit2cloud.base.entity.CloudAccount;
import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.common.scheduler.SchedulerService;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.provider.impl.aliyun.AliyunCloudProvider;
import com.fit2cloud.provider.impl.aliyun.entity.credential.AliyunVmCredential;
import com.fit2cloud.provider.impl.aliyun.entity.request.AliyunInstanceRequest;
import com.fit2cloud.provider.impl.aliyun.entity.request.ListVirtualMachineRequest;
import com.fit2cloud.provider.ICloudProvider;
import com.fit2cloud.provider.constants.DeleteWithInstance;
import com.fit2cloud.provider.entity.F2CDisk;
import com.fit2cloud.provider.impl.tencent.TencentCloudProvider;
import com.fit2cloud.provider.impl.tencent.entity.request.TencentAttachDiskRequest;
import com.fit2cloud.provider.impl.tencent.entity.request.TencentDeleteDiskRequest;
import com.fit2cloud.provider.impl.tencent.entity.request.TencentDetachDiskRequest;
import com.fit2cloud.provider.impl.tencent.entity.request.TencentResizeDiskRequest;
import com.fit2cloud.service.ISyncProviderService;
import com.fit2cloud.service.IVmCloudServerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void createTencentDisk(){
       CloudAccount cloudAccount =  cloudAccountService.getById("e6867e8dfb2b19747117cdc698f58cbc");
        HashMap<String, Object> params = new HashMap<>();
        params.put("credential", cloudAccount.getCredential());
        params.put("regionId", "ap-shanghai");
        F2CDisk f2CDisk = new F2CDisk();
        f2CDisk.setDiskType("CLOUD_SSD");
        f2CDisk.setDiskName("testDisk3");
        f2CDisk.setRegion("ap-shanghai");
        f2CDisk.setZone("ap-shanghai-4");
        f2CDisk.setSize(20l);
        f2CDisk.setProjectId("0");
        f2CDisk.setDiskChargeType("POSTPAID_BY_HOUR");

        // 自动挂载
        f2CDisk.setBootable(true);
        f2CDisk.setInstanceUuid("ins-dr2mti25");
        f2CDisk.setMountPoint("/data3");
        f2CDisk.setFileSystemType("ext4");
        List<F2CDisk>  disks = new ArrayList<>();
        disks.add(f2CDisk);
        params.put("disks",disks);
        ICloudProvider tencentCloudProvider = new TencentCloudProvider();
        List<F2CDisk>  result = tencentCloudProvider.createDisks(JsonUtil.toJSONString(params));
        System.out.println("success");
    }

    @Test
    public void AttachTencentDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("e6867e8dfb2b19747117cdc698f58cbc");
        TencentAttachDiskRequest request = new TencentAttachDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("ap-shanghai");
        request.setDeleteWithInstance(DeleteWithInstance.YES.name());
        request.setInstanceUuid("ins-dr2mti25");
        request.setDiskIds(new String[]{"disk-oyzoaudl"});

        ICloudProvider tencentCloudProvider = new TencentCloudProvider();
         tencentCloudProvider.attachDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    @Test
    public void detachTencentDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("e6867e8dfb2b19747117cdc698f58cbc");
        TencentDetachDiskRequest request = new TencentDetachDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("ap-shanghai");
        request.setInstanceUuid("ins-dr2mti25");
        request.setDiskIds(new String[]{"disk-oyzoaudl"});

        ICloudProvider tencentCloudProvider = new TencentCloudProvider();
        tencentCloudProvider.detachDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    @Test
    public void enlargeTencentDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("e6867e8dfb2b19747117cdc698f58cbc");
        TencentResizeDiskRequest request = new TencentResizeDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("ap-shanghai");
        request.setDiskIds(new String[]{"disk-oyzoaudl"});
        request.setNewDiskSize(30);

        ICloudProvider tencentCloudProvider = new TencentCloudProvider();
        tencentCloudProvider.enlargeDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }


    @Test
    public void deleteTencentDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("e6867e8dfb2b19747117cdc698f58cbc");
        TencentDeleteDiskRequest request = new TencentDeleteDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("ap-shanghai");
        request.setDiskIds(new String[]{"disk-ob3jcco5"});

        ICloudProvider tencentCloudProvider = new TencentCloudProvider();
        tencentCloudProvider.deleteDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    /**
     * 获取函数执行参数
     *
     * @param credential 认证信息
     * @param region     区域信息
     * @return json参数
     */
    private String getParams(String credential, String region) {
        HashMap<String, String> params = new HashMap<>();
        params.put("credential", credential);
        params.put("regionId", region);
        return JsonUtil.toJSONString(params);
    }


    @Test
    public void createAliyunDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        HashMap<String, Object> params = new HashMap<>();
        params.put("credential", cloudAccount.getCredential());
        params.put("regionId", "cn-beijing");
        F2CDisk f2CDisk = new F2CDisk();
        f2CDisk.setDiskType("cloud_essd");
        f2CDisk.setDiskName("testDisk5");
        f2CDisk.setRegion("cn-beijing");
        f2CDisk.setZone("cn-beijing-k");
        f2CDisk.setSize(20l);
        f2CDisk.setProjectId("0");
        f2CDisk.setDiskChargeType("PostPaid");
        // 如果不挂载默认创建出来的都是后付费的，如果挂载到机器则该机型需要时预付费的，对应创建出来的磁盘也是预付费的，
        // 后付费的机器，可以分为两步。1.先创建出来一块空盘 2.调用挂载接口

        // 自动挂载(只有预付费的才能自动挂载，)
        f2CDisk.setBootable(true);
        f2CDisk.setInstanceUuid("i-2zeh6x9msmk3tr1f3fz4");
        List<F2CDisk>  disks = new ArrayList<>();
        disks.add(f2CDisk);
        params.put("disks",disks);
        ICloudProvider aliyunCloudProvider = new AliyunCloudProvider();
        List<F2CDisk>  result = aliyunCloudProvider.createDisks(JsonUtil.toJSONString(params));
        System.out.println("success");
    }


    @Test
    public void AttachAliyunDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        ICloudProvider iCloudProvider =  new AliyunCloudProvider();

        TencentAttachDiskRequest request = new TencentAttachDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("cn-beijing");
        request.setDeleteWithInstance(DeleteWithInstance.YES.name());
        request.setInstanceUuid("i-2zeh6x9msmk3tr1f3fz4");
        request.setDiskIds(new String[]{"d-2zeg51u36fdh0htzz69m"});

        iCloudProvider.attachDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    @Test
    public void detachAliyunDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        ICloudProvider iCloudProvider =  new AliyunCloudProvider();

        TencentDetachDiskRequest request = new TencentDetachDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("cn-beijing");
        request.setInstanceUuid("i-2zeh6x9msmk3tr1f3fz4");
        request.setDiskIds(new String[]{"d-2zeg51u36fdh0htzz69m"});

        iCloudProvider.detachDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    @Test
    public void enlargeAliyunDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        ICloudProvider iCloudProvider =  new AliyunCloudProvider();
        TencentResizeDiskRequest request = new TencentResizeDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("cn-beijing");
        request.setDiskIds(new String[]{"d-2zeg51u36fdh0htzz69m"});
        request.setNewDiskSize(30);

        iCloudProvider.enlargeDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }

    @Test
    public void deleteAliyunDisk(){
        CloudAccount cloudAccount =  cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        ICloudProvider iCloudProvider =  new AliyunCloudProvider();
        TencentDeleteDiskRequest request = new TencentDeleteDiskRequest();
        request.setCredential(cloudAccount.getCredential());
        request.setRegionId("cn-beijing");
        request.setDiskIds(new String[]{"d-2zeg51u36fdh0htzz69m"});
        iCloudProvider.deleteDisk(JsonUtil.toJSONString(request));
        System.out.println("success");
    }
}
