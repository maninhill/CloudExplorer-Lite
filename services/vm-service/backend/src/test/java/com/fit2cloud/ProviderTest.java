package com.fit2cloud;

import com.fit2cloud.base.entity.CloudAccount;
import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.provider.ICloudProvider;
import com.fit2cloud.provider.constants.ProviderConstants;
import com.fit2cloud.provider.entity.F2CVirtualMachine;
import com.fit2cloud.provider.impl.aliyun.AliyunCloudProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

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
    private IBaseCloudAccountService cloudAccountService;

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        CloudAccount account = cloudAccountService.getById("9473809a9cbf7b1074b5472ac039f96c");
        Class<? extends ICloudProvider> cloudProvider = ProviderConstants.valueOf(account.getPlatform()).getCloudProvider();
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("credential", account.getCredential());
        stringObjectHashMap.put("regionId", "cn-beijing");
        String jsonString = JsonUtil.toJSONString(stringObjectHashMap);
        List<F2CVirtualMachine> f2CVirtualMachines = cloudProvider.getConstructor().newInstance().listVirtualMachine(jsonString);
        System.out.println(f2CVirtualMachines);
    }

}
