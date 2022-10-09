package com.fit2cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fit2cloud.base.entity.*;
import com.fit2cloud.base.service.IBaseAccountJobService;
import com.fit2cloud.base.service.IBaseCloudAccountService;
import com.fit2cloud.base.service.IBaseJobRecordService;
import com.fit2cloud.common.constants.JobConstants;
import com.fit2cloud.common.constants.JobStatusConstants;
import com.fit2cloud.common.constants.JobTypeConstants;
import com.fit2cloud.common.constants.PlatformConstants;
import com.fit2cloud.common.exception.Fit2cloudException;
import com.fit2cloud.common.platform.credential.Credential;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.provider.ICloudProvider;
import com.fit2cloud.provider.constants.F2CDiskStatus;
import com.fit2cloud.provider.constants.F2CImageStatus;
import com.fit2cloud.provider.constants.ProviderConstants;
import com.fit2cloud.provider.entity.F2CDisk;
import com.fit2cloud.provider.entity.F2CImage;
import com.fit2cloud.provider.entity.F2CVirtualMachine;
import com.fit2cloud.provider.exception.SkipPageException;
import com.fit2cloud.service.ISyncProviderService;
import com.fit2cloud.service.IVmCloudDiskService;
import com.fit2cloud.service.IVmCloudImageService;
import com.fit2cloud.service.IVmCloudServerService;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * @Author:张少虎
 * @Date: 2022/9/21  11:37 AM
 * @Version 1.0
 * @注释:
 */
@Service
public class SyncProviderServiceImpl implements ISyncProviderService {
    @Resource
    private IBaseCloudAccountService cloudAccountService;
    @Resource
    private IVmCloudServerService vmCloudServerService;
    @Resource
    private IVmCloudImageService vmCloudImageService;
    @Resource
    private IVmCloudDiskService vmCloudDiskService;
    @Resource
    private IBaseAccountJobService baseAccountJobService;
    @Resource
    private IBaseJobRecordService baseJobRecordService;
    @Resource
    private IBaseCloudAccountService baseCloudAccountService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void syncCloudServer(String cloudAccountId) {
        List<Credential.Region> regions = getRegions(cloudAccountId);
        syncCloudServer(cloudAccountId, regions);
    }

    @Override
    public void syncCloudServer(String cloudAccountId, List<Credential.Region> regions) {
        proxy(cloudAccountId, regions, "同步虚拟机", ICloudProvider::listVirtualMachine, this::cloudServerSaveOrUpdate, this::writeJobRecord, () -> vmCloudServerService.remove(new LambdaUpdateWrapper<VmCloudServer>().eq(VmCloudServer::getAccountId, cloudAccountId)));
    }

    @Override
    public void syncCloudImage(String cloudAccountId, List<Credential.Region> regions) {
        proxy(cloudAccountId, regions, "同步镜像", ICloudProvider::listImage, this::imageSaveOrUpdate, this::writeJobRecord, () -> vmCloudImageService.remove(new LambdaUpdateWrapper<VmCloudImage>().eq(VmCloudImage::getAccountId, cloudAccountId)));
    }

    @Override
    public void syncCloudDisk(String cloudAccountId, List<Credential.Region> regions) {
        proxy(cloudAccountId, regions, "同步磁盘", ICloudProvider::listDisk, this::diskSaveOrUpdate, this::writeJobRecord, () -> vmCloudDiskService.remove(new LambdaUpdateWrapper<VmCloudDisk>().eq(VmCloudDisk::getAccountId, cloudAccountId)));
    }

    /**
     * 虚拟机插入并且更新数据
     *
     * @param saveBatchOrUpdateParams 插入更新数据所需要的参数
     */
    private void cloudServerSaveOrUpdate(SaveBatchOrUpdateParams<F2CVirtualMachine> saveBatchOrUpdateParams) {
        List<VmCloudServer> vmCloudServers = saveBatchOrUpdateParams.syncRecord.stream().map(f2CVirtualMachine -> toVmCloudServer(f2CVirtualMachine, saveBatchOrUpdateParams.cloudAccountId, saveBatchOrUpdateParams.syncTime)).toList();
        LambdaUpdateWrapper<VmCloudServer> updateWrapper = new LambdaUpdateWrapper<VmCloudServer>().eq(VmCloudServer::getAccountId, saveBatchOrUpdateParams.cloudAccountId).eq(VmCloudServer::getRegion, saveBatchOrUpdateParams.region.getRegionId()).lt(VmCloudServer::getUpdateTime, saveBatchOrUpdateParams.syncTime).set(VmCloudServer::getInstanceStatus, "Deleted");
        saveBatchOrUpdate(vmCloudServerService, vmCloudServers, vmCloudServer -> new LambdaQueryWrapper<VmCloudServer>().eq(VmCloudServer::getAccountId, vmCloudServer.getAccountId()).eq(VmCloudServer::getInstanceId, vmCloudServer.getInstanceId()).eq(VmCloudServer::getRegion, saveBatchOrUpdateParams.region.getRegionId()), updateWrapper);
    }

    /**
     * 镜像插入并且更新数据
     *
     * @param saveBatchOrUpdateParams 插入更新数据所需要的参数
     */
    private void imageSaveOrUpdate(SaveBatchOrUpdateParams<F2CImage> saveBatchOrUpdateParams) {
        List<VmCloudImage> vmCloudImages = saveBatchOrUpdateParams.syncRecord.stream().map(img -> toVmImage(img, saveBatchOrUpdateParams.region, saveBatchOrUpdateParams.cloudAccountId, saveBatchOrUpdateParams.syncTime)).toList();
        LambdaUpdateWrapper<VmCloudImage> updateWrapper = new LambdaUpdateWrapper<VmCloudImage>().eq(VmCloudImage::getAccountId, saveBatchOrUpdateParams.cloudAccountId).eq(VmCloudImage::getRegion, saveBatchOrUpdateParams.region.getRegionId()).lt(VmCloudImage::getUpdateTime, saveBatchOrUpdateParams.syncTime).set(VmCloudImage::getStatus, F2CImageStatus.deleted);
        saveBatchOrUpdate(vmCloudImageService, vmCloudImages, vmCloudImage -> new LambdaQueryWrapper<VmCloudImage>().eq(VmCloudImage::getAccountId, vmCloudImage.getAccountId()).eq(VmCloudImage::getImageId, vmCloudImage.getImageId()).eq(VmCloudImage::getRegion, saveBatchOrUpdateParams.region.getRegionId()), updateWrapper);
    }

    /**
     * 磁盘插入并且更新数
     *
     * @param saveBatchOrUpdateParams 插入更新数据所需要的参数
     */
    private void diskSaveOrUpdate(SaveBatchOrUpdateParams<F2CDisk> saveBatchOrUpdateParams) {
        List<VmCloudDisk> vmCloudDisks = saveBatchOrUpdateParams.syncRecord.stream().map(img -> toVmDisk(img, saveBatchOrUpdateParams.region, saveBatchOrUpdateParams.cloudAccountId, saveBatchOrUpdateParams.syncTime)).toList();
        LambdaUpdateWrapper<VmCloudDisk> updateWrapper = new LambdaUpdateWrapper<VmCloudDisk>().eq(VmCloudDisk::getAccountId, saveBatchOrUpdateParams.cloudAccountId).eq(VmCloudDisk::getRegion, saveBatchOrUpdateParams.region.getRegionId()).lt(VmCloudDisk::getUpdateTime, saveBatchOrUpdateParams.syncTime).set(VmCloudDisk::getStatus, F2CDiskStatus.DELETED);
        saveBatchOrUpdate(vmCloudDiskService, vmCloudDisks, vmCloudDisk -> new LambdaQueryWrapper<VmCloudDisk>().eq(VmCloudDisk::getAccountId, vmCloudDisk.getAccountId()).eq(VmCloudDisk::getDiskId, vmCloudDisk.getDiskId()).eq(VmCloudDisk::getRegion, saveBatchOrUpdateParams.region.getRegionId()), updateWrapper);
    }


    /**
     * 写入任务记录
     *
     * @param saveBatchOrUpdateParams 写入任务记录所需要的参数
     */
    private void writeJobRecord(SaveBatchOrUpdateParams<?> saveBatchOrUpdateParams) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("region", saveBatchOrUpdateParams.region);
        params.put("size", saveBatchOrUpdateParams.syncRecord.size());
        saveBatchOrUpdateParams.jobRecord.getParams().add(params);
        baseJobRecordService.updateById(saveBatchOrUpdateParams.jobRecord);
    }


    @Override
    public void syncCloudServer(Map<String, Object> params) {
        String cloudAccountId = getCloudAccountId(params);
        List<Credential.Region> regions = getRegions(params);
        if (params.containsKey(JobConstants.CloudAccount.CLOUD_ACCOUNT_ID.name()) && params.containsKey(JobConstants.CloudAccount.REGIONS.name())) {
            syncCloudServer(cloudAccountId, regions);
        }
    }

    @Override
    public void syncCloudImage(String cloudAccountId) {
        List<Credential.Region> regions = getRegions(cloudAccountId);
        syncCloudImage(cloudAccountId, regions);
    }


    @Override
    public void syncCloudImage(Map<String, Object> params) {
        String cloudAccountId = getCloudAccountId(params);
        List<Credential.Region> regions = getRegions(params);
        if (params.containsKey(JobConstants.CloudAccount.CLOUD_ACCOUNT_ID.name()) && params.containsKey(JobConstants.CloudAccount.REGIONS.name())) {
            syncCloudImage(cloudAccountId, regions);
        }
    }

    @Override
    public void syncCloudDisk(String cloudAccountId) {
        List<Credential.Region> regions = getRegions(cloudAccountId);
        syncCloudDisk(cloudAccountId, regions);
    }


    @Override
    public void syncCloudDisk(Map<String, Object> params) {
        String cloudAccountId = getCloudAccountId(params);
        List<Credential.Region> regions = getRegions(params);
        if (params.containsKey(JobConstants.CloudAccount.CLOUD_ACCOUNT_ID.name()) && params.containsKey(JobConstants.CloudAccount.REGIONS.name())) {
            syncCloudDisk(cloudAccountId, regions);
        }
    }

    /**
     * 执行函数
     *
     * @param providerClass 执行处理器
     * @param req           请求参数画
     * @param exec          执行函数
     * @param <T>           执行函数返回对象
     * @return 执行函数返回对象泛型
     */
    private <T> List<T> exec(Class<? extends ICloudProvider> providerClass, String req, BiFunction<ICloudProvider, String, List<T>> exec) {
        try {
            ICloudProvider iCloudProvider = providerClass.getConstructor().newInstance();
            return exec.apply(iCloudProvider, req);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }

    }


    /**
     * 代理执行同步动作
     *
     * @param cloudAccountId    云账号id
     * @param regions           区域
     * @param jobDescription    任务描述
     * @param execMethod        同步函数
     * @param saveBatchOrUpdate 插入更新函数
     * @param writeJobRecord    写入任务记录函数
     * @param remote            删除不存在的云账户函数
     * @param <T>               同步对象类型
     */
    private <T> void proxy(String cloudAccountId, List<Credential.Region> regions,
                           String jobDescription,
                           BiFunction<ICloudProvider, String, List<T>> execMethod,
                           Consumer<SaveBatchOrUpdateParams<T>> saveBatchOrUpdate,
                           Consumer<SaveBatchOrUpdateParams<T>> writeJobRecord,
                           Runnable remote) {
        RLock lock = redissonClient.getLock(cloudAccountId + jobDescription);
        try {
            if (lock.tryLock()) {
                CloudAccount cloudAccount = cloudAccountService.getById(cloudAccountId);
                if (Objects.nonNull(cloudAccount)) {
                    LocalDateTime syncTime = getSyncTime();
                    // 初始化一条定时任务记录
                    JobRecord jobRecord = initJobRecord(jobDescription, syncTime, cloudAccountId);
                    Arrays.stream(ProviderConstants.values()).filter(providerConstants -> providerConstants.name().equals(cloudAccount.getPlatform())).findFirst().ifPresent(providerConstants -> {
                        Class<? extends ICloudProvider> cloudProvider = ProviderConstants.valueOf(cloudAccount.getPlatform()).getCloudProvider();
                        try {
                            for (Credential.Region region : regions) {
                                try {
                                    // 同步数据
                                    List<T> syncRecord = exec(cloudProvider, getParams(cloudAccount.getCredential(), region.getRegionId()), execMethod);
                                    SaveBatchOrUpdateParams<T> tSaveBatchOrUpdateParams = new SaveBatchOrUpdateParams<>(cloudAccountId, syncTime, region, syncRecord, jobRecord);
                                    // 插入并且更新数据
                                    saveBatchOrUpdate.accept(tSaveBatchOrUpdateParams);
                                    // 记录同步日志
                                    writeJobRecord.accept(tSaveBatchOrUpdateParams);
                                } catch (SkipPageException ignored) { // 如果发生跳过异常,那么就不同步当前区域
                                    try {
                                        writeJobRecord.accept(new SaveBatchOrUpdateParams<>(cloudAccountId, syncTime, region, new ArrayList<>(), jobRecord));
                                    } catch (Throwable e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            // 修改同步状态为成功
                            baseJobRecordService.update(new LambdaUpdateWrapper<JobRecord>().eq(JobRecord::getId, jobRecord.getId()).set(JobRecord::getStatus, JobStatusConstants.SUCCESS));
                        } catch (Throwable e) {
                            baseJobRecordService.update(new LambdaUpdateWrapper<JobRecord>().eq(JobRecord::getId, jobRecord.getId()).set(JobRecord::getStatus, JobStatusConstants.FAILED));
                        }

                    });
                } else {
                    // 删除云账号相关的资源
                    remote.run();
                    // 删除定时任务
                    baseCloudAccountService.deleteJobByCloudAccountId(cloudAccountId);
                }
            }
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }

    }

    /**
     * 初始化任务记录
     *
     * @param jobDescription 任务描述
     * @param syncTime       同步时间
     * @param cloudAccountId 云账户id
     * @return 任务记录对象
     */
    private JobRecord initJobRecord(String jobDescription, LocalDateTime syncTime, String cloudAccountId) {
        JobRecord jobRecord = new JobRecord();
        jobRecord.setDescription(jobDescription);
        jobRecord.setStatus(JobStatusConstants.SYNCING);
        jobRecord.setParams(new ArrayList<>());
        jobRecord.setType(JobTypeConstants.CLOUD_ACCOUNT_SYNC_JOB);
        jobRecord.setCreateTime(syncTime);
        // 插入任务数据
        baseJobRecordService.save(jobRecord);
        // 插入关联关系
        AccountJob accountJob = new AccountJob();
        accountJob.setAccountId(cloudAccountId);
        accountJob.setJobRecordId(jobRecord.getId());
        baseAccountJobService.save(accountJob);
        return jobRecord;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SaveBatchOrUpdateParams<T> {
        /**
         * 云账户id
         */
        private String cloudAccountId;
        /**
         * 更新时间
         */
        private LocalDateTime syncTime;
        /**
         * 区域
         */
        private Credential.Region region;
        /**
         * 数据
         */
        private List<T> syncRecord;
        /**
         * 任务记录
         */
        private JobRecord jobRecord;
    }

    /**
     * 根据云账号获取区域
     *
     * @param accountId 云账号id
     * @return 云账号区域
     */
    private List<Credential.Region> getRegions(String accountId) {
        CloudAccount cloudAccount = cloudAccountService.getById(accountId);
        return Arrays.stream(PlatformConstants.values()).filter(platformConstants -> platformConstants.name().equals(cloudAccount.getPlatform())).findFirst().map(platformConstants -> {
            try {
                return platformConstants.getCredentialClass().getConstructor().newInstance().deCode(cloudAccount.getCredential()).regions();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new Fit2cloudException(10001, "获取区域错误");
            }
        }).orElseThrow(() -> new Fit2cloudException(10001, "获取区域错误"));
    }


    /**
     * 批量插入并且逻辑删除
     *
     * @param service             服务
     * @param dataList            需要插入的数据
     * @param getUpdateWrapper    获取更新的mapper
     * @param updateDeleteWarpper 删除mapper
     * @param <T>                 数据泛型
     */
    private <T> void saveBatchOrUpdate(IService<T> service, List<T> dataList, Function<T, Wrapper<T>> getUpdateWrapper, Wrapper<T> updateDeleteWarpper) {
        for (T entity : dataList) {
            Wrapper<T> updateWrapper = getUpdateWrapper.apply(entity);
            // 插入或者更新数据
            service.saveOrUpdate(entity, updateWrapper);
        }
        // 删除数据,因为是逻辑删除所以更新status字段
        service.update(updateDeleteWarpper);
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

    /**
     * 获取云账号
     *
     * @param map 参数Map
     * @return 云账号
     */
    private String getCloudAccountId(Map<String, Object> map) {
        return (String) map.get(JobConstants.CloudAccount.CLOUD_ACCOUNT_ID.name());
    }

    /**
     * 获取同步区域
     *
     * @param map 参数Map
     * @return 同步区域
     */
    private List<Credential.Region> getRegions(Map<String, Object> map) {
        // todo 这个地方不能强转,强转后会出现线程阻塞
        String jsonString = JsonUtil.toJSONString(map.get(JobConstants.CloudAccount.REGIONS.name()));
        return JsonUtil.parseArray(jsonString, Credential.Region.class);
    }

    /**
     * 将虚拟机同步对象转换为实体对
     *
     * @param f2CVirtualMachine 同步对象
     * @param cloudAccountId    实体对象
     * @return 实体对象
     */
    private VmCloudServer toVmCloudServer(F2CVirtualMachine f2CVirtualMachine, String cloudAccountId, LocalDateTime updateTime) {
        VmCloudServer vmCloudServer = new VmCloudServer();
        BeanUtils.copyProperties(f2CVirtualMachine, vmCloudServer);
        vmCloudServer.setAccountId(cloudAccountId);
        vmCloudServer.setInstanceUuid(f2CVirtualMachine.getInstanceUUID());
        vmCloudServer.setInstanceName(f2CVirtualMachine.getName());
        vmCloudServer.setRemoteIp(f2CVirtualMachine.getRemoteIP());
        vmCloudServer.setOsInfo(f2CVirtualMachine.getOsInfo());
        vmCloudServer.setUpdateTime(updateTime);
        vmCloudServer.setIpArray(JsonUtil.toJSONString(f2CVirtualMachine.getIpArray()));
        return vmCloudServer;
    }

    /**
     * 将虚拟机磁盘对象转换实体对象
     *
     * @param disk           磁盘对象
     * @param region         区域对象
     * @param cloudAccountId 云账号
     * @return 实体对象
     */
    private VmCloudDisk toVmDisk(F2CDisk disk, Credential.Region region, String cloudAccountId, LocalDateTime updateTime) {
        VmCloudDisk vmCloudDisk = new VmCloudDisk();
        vmCloudDisk.setAccountId(cloudAccountId);
        vmCloudDisk.setDescription(disk.getDescription());
        vmCloudDisk.setRegion(region.getRegionId());
        vmCloudDisk.setDiskId(disk.getDiskId());
        vmCloudDisk.setDiskName(disk.getDiskName());
        vmCloudDisk.setDiskType(disk.getDiskType());
        vmCloudDisk.setBootable(disk.isBootable());
        vmCloudDisk.setCategory(disk.getCategory());
        vmCloudDisk.setDatastoreId(disk.getDatastoreUniqueId());
        vmCloudDisk.setDevice(disk.getDevice());
        vmCloudDisk.setProjectId(disk.getProjectId());
        vmCloudDisk.setDeleteWithInstance(disk.getDeleteWithInstance());
        vmCloudDisk.setInstanceUuid(disk.getInstanceUuid());
        vmCloudDisk.setSize(disk.getSize());
        vmCloudDisk.setStatus(disk.getStatus());
        vmCloudDisk.setDiskChargeType(disk.getDiskChargeType());
        vmCloudDisk.setZone(disk.getZone());
        vmCloudDisk.setUpdateTime(updateTime);
        return vmCloudDisk;
    }

    /**
     * 将同步镜像转化为实体对象
     *
     * @param image          同步镜像对象
     * @param region         区域对象
     * @param cloudAccountId 云账号id
     * @return 实例对象
     */

    private VmCloudImage toVmImage(F2CImage image, Credential.Region region, String cloudAccountId, LocalDateTime updateTime) {
        VmCloudImage vmCloudImage = new VmCloudImage();
        vmCloudImage.setRegion(region.getRegionId());
        vmCloudImage.setRegionName(region.getName());
        vmCloudImage.setImageId(image.getId());
        vmCloudImage.setDiskSize(image.getDiskSize());
        vmCloudImage.setDescription(image.getDescription());
        vmCloudImage.setImageName(image.getName());
        vmCloudImage.setAccountId(cloudAccountId);
        vmCloudImage.setOs(image.getOs());
        vmCloudImage.setStatus(F2CImageStatus.normal);
        vmCloudImage.setUpdateTime(updateTime);
        return vmCloudImage;
    }


    /**
     * 更新的时候精确到秒 因为数据插入也是精确到秒
     *
     * @return 更新时间
     */
    private LocalDateTime getSyncTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(now.format(dateTimeFormatter), dateTimeFormatter);
    }
}
