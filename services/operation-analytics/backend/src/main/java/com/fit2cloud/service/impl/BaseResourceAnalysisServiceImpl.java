package com.fit2cloud.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fit2cloud.base.entity.*;
import com.fit2cloud.base.service.*;
import com.fit2cloud.common.constants.PlatformConstants;
import com.fit2cloud.common.es.constants.IndexConstants;
import com.fit2cloud.common.log.utils.LogUtil;
import com.fit2cloud.common.provider.entity.F2CEntityType;
import com.fit2cloud.common.utils.ColumnNameUtil;
import com.fit2cloud.common.utils.DateUtil;
import com.fit2cloud.common.utils.PageUtil;
import com.fit2cloud.common.utils.QueryUtil;
import com.fit2cloud.controller.request.base.resource.analysis.ResourceAnalysisRequest;
import com.fit2cloud.controller.request.base.resource.analysis.ResourceUsedTrendRequest;
import com.fit2cloud.controller.request.datastore.PageDatastoreRequest;
import com.fit2cloud.controller.request.host.PageHostRequest;
import com.fit2cloud.controller.response.ChartData;
import com.fit2cloud.controller.response.ResourceAllocatedInfo;
import com.fit2cloud.dao.mapper.VmCloudDatastoreMapper;
import com.fit2cloud.dao.mapper.VmCloudHostMapper;
import com.fit2cloud.dto.KeyValue;
import com.fit2cloud.dto.VmCloudDatastoreDTO;
import com.fit2cloud.dto.VmCloudHostDTO;
import com.fit2cloud.es.entity.PerfMetricMonitorData;
import com.fit2cloud.service.IBaseResourceAnalysisService;
import com.fit2cloud.utils.OperationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jianneng
 * @date 2022/12/11 18:43
 **/
@Service
public class BaseResourceAnalysisServiceImpl implements IBaseResourceAnalysisService {
    @Resource
    private VmCloudHostMapper vmCloudHostMapper;
    @Resource
    private VmCloudDatastoreMapper vmCloudDatastoreMapper;
    @Resource
    private IBaseCloudAccountService iBaseCloudAccountService;
    @Resource
    private IBaseVmCloudHostService iBaseVmCloudHostService;
    @Resource
    private IBaseVmCloudDatastoreService iBaseVmCloudDatastoreService;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private IBaseVmCloudServerService iBaseVmCloudServerService;
    @Resource
    private IBaseVmCloudDiskService iBaseVmCloudDiskService;

    public IPage<VmCloudHostDTO> pageHost(PageHostRequest request) {
        Page<VmCloudHostDTO> page = PageUtil.of(request, VmCloudHostDTO.class, new OrderItem(ColumnNameUtil.getColumnName(VmCloudHostDTO::getCreateTime, true), false), true);
        // 构建查询参数
        QueryWrapper<VmCloudHostDTO> wrapper = addHostQuery(request);
        IPage<VmCloudHostDTO> result = vmCloudHostMapper.pageList(page, wrapper);
        return result;
    }

    private QueryWrapper<VmCloudHostDTO> addHostQuery(PageHostRequest request) {
        QueryWrapper<VmCloudHostDTO> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(request.getHostName()), ColumnNameUtil.getColumnName(VmCloudHostDTO::getHostName, true), request.getHostName());
        return wrapper;
    }

    @Override
    public IPage<VmCloudDatastoreDTO> pageDatastore(PageDatastoreRequest request) {
        Page<VmCloudDatastoreDTO> page = PageUtil.of(request, VmCloudDatastoreDTO.class, new OrderItem(ColumnNameUtil.getColumnName(VmCloudDatastoreDTO::getCreateTime, true), false), true);
        // 构建查询参数
        QueryWrapper<VmCloudDatastoreDTO> wrapper = addQueryDatastore(request);
        IPage<VmCloudDatastoreDTO> result = vmCloudDatastoreMapper.pageList(page, wrapper);
        return result;
    }

    private QueryWrapper<VmCloudDatastoreDTO> addQueryDatastore(PageDatastoreRequest request) {
        QueryWrapper<VmCloudDatastoreDTO> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(request.getDatastoreName()), ColumnNameUtil.getColumnName(VmCloudDatastoreDTO::getDatastoreName, true), request.getDatastoreName());
        return wrapper;
    }

    /**
     * 私有云账号，VMWare vSphere、OpenStack
     *
     * @return
     */
    @Override
    public List<CloudAccount> getAllPrivateCloudAccount() {
        QueryWrapper<CloudAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(ColumnNameUtil.getColumnName(CloudAccount::getPlatform, true), Arrays.asList(PlatformConstants.fit2cloud_vsphere_platform, PlatformConstants.fit2cloud_openstack_platform));
        List<CloudAccount> accountList = iBaseCloudAccountService.list(queryWrapper);
        return accountList;
    }

    /**
     * 集群
     *
     * @param request
     * @return
     */
    @Override
    public List<Map<String, String>> getCluster(ResourceAnalysisRequest request) {
        allPrivateCloudAccount(request);
        List<Map<String, String>> result = new ArrayList<>();
        QueryWrapper<VmCloudHost> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
        List<VmCloudHost> vmCloudHosts = iBaseVmCloudHostService.list(queryWrapper);
        if (vmCloudHosts.size() > 0) {
            Map<String, List<VmCloudHost>> zoneMap = vmCloudHosts.stream().filter(v -> StringUtils.isNotEmpty(v.getZone())).collect(Collectors.groupingBy(VmCloudHost::getZone));
            zoneMap.forEach((k, v) -> {
                if (v.size() > 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", v.get(0).getZone());
                    map.put("name", v.get(0).getZone());
                    result.add(map);
                }
            });
        }
        return result;
    }

    private void allPrivateCloudAccount(ResourceAnalysisRequest request) {
        if (CollectionUtils.isEmpty(request.getAccountIds())) {
            request.setAccountIds(getAllPrivateCloudAccount().stream().map(CloudAccount::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 宿主机
     *
     * @param request
     * @return
     */
    @Override
    public List<VmCloudHost> getVmHost(ResourceAnalysisRequest request) {
        allPrivateCloudAccount(request);
        QueryWrapper<VmCloudHost> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
        queryWrapper.in(CollectionUtils.isNotEmpty(request.getClusterIds()), ColumnNameUtil.getColumnName(VmCloudHost::getZone, true), request.getClusterIds());
        return iBaseVmCloudHostService.list(queryWrapper);
    }

    /**
     * 存储器
     *
     * @param request
     * @return
     */
    @Override
    public List<VmCloudDatastore> getVmCloudDatastore(ResourceAnalysisRequest request) {
        allPrivateCloudAccount(request);
        QueryWrapper<VmCloudDatastore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudDatastore::getAccountId, true), request.getAccountIds());
        queryWrapper.in(CollectionUtils.isNotEmpty(request.getClusterIds()), ColumnNameUtil.getColumnName(VmCloudDatastore::getZone, true), request.getClusterIds());
        queryWrapper.groupBy(ColumnNameUtil.getColumnName(VmCloudDatastore::getDatastoreId, true));
        return iBaseVmCloudDatastoreService.list(queryWrapper);
    }

    /**
     * 分配情况
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, ResourceAllocatedInfo> getResourceAllocatedInfo(ResourceAnalysisRequest request) {
        Map<String, ResourceAllocatedInfo> result = new HashMap<>();
        List<VmCloudHost> hosts = getVmHost(request);
        if (CollectionUtils.isNotEmpty(hosts)) {
            hosts = hosts.stream().filter(v -> CollectionUtils.isNotEmpty(request.getHostIds()) ? request.getHostIds().contains(v.getId()) : true).collect(Collectors.toList());
            BigDecimal cpuTotal = new BigDecimal(hosts.stream().mapToLong(VmCloudHost::getNumCpuCores).sum());
            BigDecimal cpuAllocated = new BigDecimal(hosts.stream().mapToLong(VmCloudHost::getVmCpuCores).sum());
            BigDecimal cpuAllocatedRate = cpuAllocated.divide(cpuTotal, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            result.put("cpu", ResourceAllocatedInfo.builder()
                    .total(cpuTotal)
                    .allocated(cpuAllocated)
                    .allocatedRate(cpuAllocatedRate.compareTo(new BigDecimal(100)) == 1 ? new BigDecimal(100) : cpuAllocatedRate)
                    .free(cpuTotal.subtract(cpuAllocated))
                    .build());
            BigDecimal memoryTotal = new BigDecimal(hosts.stream().mapToLong(VmCloudHost::getMemoryTotal).sum());
            BigDecimal memoryAllocated = new BigDecimal(hosts.stream().mapToLong(VmCloudHost::getMemoryAllocated).sum());
            BigDecimal memoryAllocatedRate = memoryAllocated.divide(memoryTotal, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            result.put("memory", ResourceAllocatedInfo.builder()
                    .total(memoryTotal.divide(new BigDecimal(1024)).setScale(2, RoundingMode.HALF_UP))
                    .allocated(memoryAllocated.divide(new BigDecimal(1024)).setScale(2, RoundingMode.HALF_UP))
                    .allocatedRate(memoryAllocatedRate.compareTo(new BigDecimal(100)) == 1 ? new BigDecimal(100) : memoryAllocatedRate)
                    .free((memoryTotal.subtract(memoryAllocated)).divide(new BigDecimal(1024)).setScale(2, RoundingMode.HALF_UP))
                    .build());

        }
        List<VmCloudDatastore> datastores = getVmCloudDatastore(request);
        if (CollectionUtils.isNotEmpty(datastores)) {
            datastores = datastores.stream().filter(v -> CollectionUtils.isNotEmpty(request.getDatastoreIds()) ? request.getDatastoreIds().contains(v.getId()) : true).collect(Collectors.toList());
            BigDecimal capacity = new BigDecimal(datastores.stream().mapToLong(VmCloudDatastore::getCapacity).sum());
            BigDecimal freeSpace = new BigDecimal(datastores.stream().mapToLong(VmCloudDatastore::getFreeSpace).sum());
            BigDecimal memoryAllocatedRate = capacity.subtract(freeSpace).divide(capacity, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            result.put("datastore", ResourceAllocatedInfo.builder()
                    .total(capacity)
                    .allocated(capacity.subtract(freeSpace))
                    .allocatedRate(memoryAllocatedRate)
                    .free(freeSpace)
                    .build());
        }
        return result;
    }

    /**
     * 资源分布情况
     *
     * @param request
     */
    @Override
    public Map<String, List<KeyValue>> getResourceSpreadInfo(ResourceAnalysisRequest request) {
        Map<String, List<KeyValue>> result = new HashMap<>();
        List<CloudAccount> accountList = getAllPrivateCloudAccount();
        Map<String, CloudAccount> accountMap = accountList.stream().collect(Collectors.toMap(CloudAccount::getId, v -> v, (k1, k2) -> k1));
        if (accountMap.size() == 0) {
            return result;
        }
        List<VmCloudHost> hosts = getVmHost(request);
        if (CollectionUtils.isEmpty(hosts)) {
            return result;
        }
        hosts = hosts.stream().filter(v -> CollectionUtils.isNotEmpty(request.getHostIds()) ? request.getHostIds().contains(v.getId()) : true).collect(Collectors.toList());
        // 主机在云账号上面的分布情况
        Map<String, Long> hostSpread = hosts.stream().filter(v -> StringUtils.isNotEmpty(v.getAccountId())).collect(Collectors.groupingBy(VmCloudHost::getAccountId, Collectors.counting()));
        result.put("host", hostSpread.entrySet().stream().map(c -> new KeyValue(StringUtils.isEmpty(accountMap.get(c.getKey()).getName()) ? c.getKey() : accountMap.get(c.getKey()).getName(), c.getValue()) {
        }).collect(Collectors.toList()));
        List<VmCloudDatastore> datastores = getVmCloudDatastore(request);
        // 存储器在云账号上面的分布情况
        Map<String, Long> datastoreSpread = datastores.stream().filter(v -> StringUtils.isNotEmpty(v.getAccountId())).collect(Collectors.groupingBy(VmCloudDatastore::getAccountId, Collectors.counting()));
        result.put("datastore", datastoreSpread.entrySet().stream().map(c -> new KeyValue(StringUtils.isEmpty(accountMap.get(c.getKey()).getName()) ? c.getKey() : accountMap.get(c.getKey()).getName(), c.getValue()) {
        }).collect(Collectors.toList()));
        List<KeyValue> vms = new ArrayList<>();
        // 云主机在宿主机上的分布情况
        hosts.forEach(v -> {
            KeyValue kv = new KeyValue();
            kv.setName(v.getHostName());
            switch (request.getVmStatus()) {
                case "running":
                    kv.setValue(v.getVmRunning());
                    break;
                case "stopped":
                    kv.setValue(v.getVmStopped());
                    break;
                default:
                    kv.setValue(v.getVmTotal());
            }
            vms.add(kv);
        });
        result.put("vm", vms);
        return result;
    }

    /**
     * 资源使用趋势数据
     *
     * @param request
     * @return
     */
    @Override
    public List<ChartData> getResourceUsedTrendData(ResourceUsedTrendRequest request) {
        List<ChartData> result = new ArrayList<>();
        CalendarInterval intervalUnit = OperationUtils.getCalendarIntervalUnit(request.getStartTime(), request.getEndTime());
        try {
            request.setIntervalPosition(intervalUnit);
            SearchHits<PerfMetricMonitorData> response = elasticsearchTemplate.search(getSearchResourceTrendDataQuery(request), PerfMetricMonitorData.class, IndexCoordinates.of(IndexConstants.CE_PERF_METRIC_MONITOR_DATA.getCode()));
            ElasticsearchAggregations aggregations = (ElasticsearchAggregations) response.getAggregations();
            ElasticsearchAggregation aggregation = (ElasticsearchAggregation) aggregations.aggregations().get(0);
            List<DateHistogramBucket> dateHistogramBucketList = aggregation.aggregation().getAggregate().dateHistogram().buckets().array();
            Map<String, Map<String, Long>> groupDateAndRange = new HashMap<>();
            dateHistogramBucketList.forEach(dateHistogramBucket -> {
                Arrays.asList(0, 20, 40, 60, 80).stream().forEach(interval -> {
                    String rangeKey = interval + "~" + (interval + 20) + "%";
                    String key = dateHistogramBucket.key() + "-" + rangeKey;
                    Map<String, Long> map = new HashMap<>();
                    map.put(rangeKey, 0L);
                    if (!groupDateAndRange.containsKey(key)) {
                        List<StringTermsBucket> averageRanges = dateHistogramBucket.aggregations().get("instanceIds").sterms().buckets().array();
                        if (CollectionUtils.isNotEmpty(averageRanges)) {
                            Long count = 0L;
                            //资源时间段内平均值
                            for (StringTermsBucket termsBucket : averageRanges) {
                                double avgValue = termsBucket.aggregations().get("average").max().value();
                                //在区间里面
                                if (avgValue > interval && avgValue < (interval + 20)) {
                                    count++;
                                }
                            }
                            map.put(rangeKey, count);
                        }
                    }
                    groupDateAndRange.put(key, map);
                });
            });
            groupDateAndRange.keySet().forEach(k -> {
                Map<String, Long> map = groupDateAndRange.get(k);
                map.keySet().forEach(r -> {
                    ChartData chartData = new ChartData();
                    chartData.setXAxis(OperationUtils.getTimeFormat(DateUtil.dateToString(Long.valueOf(k.split("-")[0]), null), intervalUnit));
                    chartData.setGroupName(r);
                    chartData.setYAxis(new BigDecimal(map.get(r)));
                    result.add(chartData);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("获取云主机资源使用趋势异常:" + e.getMessage());
        }
        return result;
    }

    private org.springframework.data.elasticsearch.core.query.Query getSearchResourceTrendDataQuery(ResourceUsedTrendRequest request) {
        List<QueryUtil.QueryCondition> queryConditions = new ArrayList<>();
        //时间参数
        QueryUtil.QueryCondition startTimesTamp = new QueryUtil.QueryCondition(true, "timestamp", request.getStartTime(), QueryUtil.CompareType.GTE);
        queryConditions.add(startTimesTamp);
        QueryUtil.QueryCondition endTimesTamp = new QueryUtil.QueryCondition(true, "timestamp", request.getEndTime(), QueryUtil.CompareType.LTE);
        queryConditions.add(endTimesTamp);
        QueryUtil.QueryCondition entityType = new QueryUtil.QueryCondition(true, "entityType.keyword", request.getEntityType(), QueryUtil.CompareType.EQ);
        queryConditions.add(entityType);
        QueryUtil.QueryCondition metricName = new QueryUtil.QueryCondition(true, "metricName.keyword", request.getMetricName(), QueryUtil.CompareType.EQ);
        queryConditions.add(metricName);
        if (CollectionUtils.isNotEmpty(request.getAccountIds())) {
            QueryUtil.QueryCondition accountId = new QueryUtil.QueryCondition(true, "cloudAccountId.keyword", request.getAccountIds(), QueryUtil.CompareType.IN);
            queryConditions.add(accountId);
        }
        if (CollectionUtils.isNotEmpty(request.getClusterIds())) {
            QueryUtil.QueryCondition clusterNames = new QueryUtil.QueryCondition(true, "clusterName.keyword", request.getClusterIds(), QueryUtil.CompareType.IN);
            queryConditions.add(clusterNames);
        }
        if (CollectionUtils.isNotEmpty(request.getResourceIds())) {
            QueryUtil.QueryCondition resourceIds = new QueryUtil.QueryCondition(true, "instanceId.keyword", getResourceIds(request), QueryUtil.CompareType.IN);
            queryConditions.add(resourceIds);
        }
        BoolQuery.Builder boolQuery = QueryUtil.getQuery(queryConditions);
        NativeQueryBuilder query = new NativeQueryBuilder()
                .withPageable(PageRequest.of(0, 1))
                .withQuery(new co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder().bool(boolQuery.build()).build())
                .withSourceFilter(new FetchSourceFilter(new String[]{}, new String[]{"@version", "@timestamp", "host", "tags"}))
                .withAggregation("timestamp", new Aggregation.Builder().dateHistogram(new DateHistogramAggregation.Builder().field("timestamp").calendarInterval(request.getIntervalPosition()).build())
                        .aggregations("instanceIds", new Aggregation.Builder().terms(new TermsAggregation.Builder().field("instanceId.keyword").size(Integer.MAX_VALUE).build())
                                .aggregations("average", new Aggregation.Builder().max(new MaxAggregation.Builder().field("average").build()).build()).build()).build());
        return query.build();
    }

    /**
     * 获取资源真实ID
     *
     * @param request
     * @return
     */
    private List<String> getResourceIds(ResourceUsedTrendRequest request) {
        List<String> resourceIds = new ArrayList<>();
        try {
            switch (F2CEntityType.valueOf(request.getEntityType())) {
                case HOST:
                    QueryWrapper<VmCloudHost> queryHostWrapper = new QueryWrapper<>();
                    queryHostWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
                    queryHostWrapper.in(true, ColumnNameUtil.getColumnName(VmCloudHost::getId, true), request.getResourceIds());
                    List<VmCloudHost> vmCloudHosts = iBaseVmCloudHostService.list(queryHostWrapper);
                    if (CollectionUtils.isNotEmpty(vmCloudHosts)) {
                        resourceIds = vmCloudHosts.stream().map(VmCloudHost::getHostId).collect(Collectors.toList());
                    }
                    break;
                case DATASTORE:
                    QueryWrapper<VmCloudDatastore> queryDatastoreWrapper = new QueryWrapper<>();
                    queryDatastoreWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
                    queryDatastoreWrapper.in(true, ColumnNameUtil.getColumnName(VmCloudDatastore::getId, true), request.getResourceIds());
                    List<VmCloudDatastore> vmCloudDatastores = iBaseVmCloudDatastoreService.list(queryDatastoreWrapper);
                    if (CollectionUtils.isNotEmpty(vmCloudDatastores)) {
                        resourceIds = vmCloudDatastores.stream().map(VmCloudDatastore::getDatastoreId).collect(Collectors.toList());
                    }
                    break;
                case VIRTUAL_MACHINE:
                    QueryWrapper<VmCloudServer> queryServerWrapper = new QueryWrapper<>();
                    queryServerWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
                    queryServerWrapper.in(true, ColumnNameUtil.getColumnName(VmCloudServer::getId, true), request.getResourceIds());
                    List<VmCloudServer> vmCloudServers = iBaseVmCloudServerService.list(queryServerWrapper);
                    if (CollectionUtils.isNotEmpty(vmCloudServers)) {
                        resourceIds = vmCloudServers.stream().map(VmCloudServer::getInstanceUuid).collect(Collectors.toList());
                    }
                    break;
                case DISK:
                    QueryWrapper<VmCloudDisk> queryDiskWrapper = new QueryWrapper<>();
                    queryDiskWrapper.in(CollectionUtils.isNotEmpty(request.getAccountIds()), ColumnNameUtil.getColumnName(VmCloudHost::getAccountId, true), request.getAccountIds());
                    queryDiskWrapper.in(true, ColumnNameUtil.getColumnName(VmCloudDisk::getId, true), request.getResourceIds());
                    List<VmCloudDisk> vmCloudDisks = iBaseVmCloudDiskService.list(queryDiskWrapper);
                    if (CollectionUtils.isNotEmpty(vmCloudDisks)) {
                        resourceIds = vmCloudDisks.stream().map(VmCloudDisk::getDiskId).collect(Collectors.toList());
                    }
                    break;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CollectionUtils.isEmpty(resourceIds) ? request.getResourceIds() : resourceIds;
    }
}