package com.fit2cloud;

import com.fit2cloud.base.entity.CloudAccount;
import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.common.platform.credential.Credential;
import com.fit2cloud.common.scheduler.SchedulerService;
import com.fit2cloud.common.scheduler.impl.entity.QuzrtzJobDetail;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.dao.entity.VmCloudImage;
import com.fit2cloud.provider.ICloudProvider;
import com.fit2cloud.provider.constants.ProviderConstants;
import com.fit2cloud.provider.entity.F2CVirtualMachine;
import com.fit2cloud.provider.impl.aliyun.AliyunCloudProvider;
import com.fit2cloud.service.ISyncProviderService;
import com.fit2cloud.service.IVmCloudImageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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

    @Test
    public void syncServer() {
        syncProviderService.syncCloudServer(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getParams());
    }

    @Test
    public void syncImag() {
        syncProviderService.syncCloudImage(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getParams());
    }

    @Test
    public void syncDisk() {
        syncProviderService.syncCloudDisk(schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getParams());
    }

    @Test
    public void syncServerImageDisk() {
        Map<String, Object> params = schedulerService.getJobDetails("SYNC_VIRTUAL_MACHINE_9473809a9cbf7b1074b5472ac039f96c", "CLOUD_ACCOUNT_RESOURCE_SYNC_GROUP").getParams();
        syncProviderService.syncCloudServer(params);
        syncProviderService.syncCloudImage(params);
        syncProviderService.syncCloudDisk(params);

    }
}