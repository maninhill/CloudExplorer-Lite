package com.fit2cloud.provider.impl.huawei.api;

import com.fit2cloud.common.exception.Fit2cloudException;
import com.fit2cloud.common.provider.exception.ReTryException;
import com.fit2cloud.common.provider.util.PageUtil;
import com.fit2cloud.provider.impl.huawei.entity.request.*;
import com.huaweicloud.sdk.css.v1.CssClient;
import com.huaweicloud.sdk.css.v1.model.ClusterList;
import com.huaweicloud.sdk.css.v1.model.ListClustersDetailsResponse;
import com.huaweicloud.sdk.dcs.v2.DcsClient;
import com.huaweicloud.sdk.dcs.v2.model.InstanceListInfo;
import com.huaweicloud.sdk.dds.v3.DdsClient;
import com.huaweicloud.sdk.dds.v3.model.QueryInstanceResponse;
import com.huaweicloud.sdk.ecs.v2.EcsClient;
import com.huaweicloud.sdk.ecs.v2.model.ListServersDetailsResponse;
import com.huaweicloud.sdk.ecs.v2.model.ServerDetail;
import com.huaweicloud.sdk.eip.v3.model.ListPublicipsResponse;
import com.huaweicloud.sdk.eip.v3.EipClient;
import com.huaweicloud.sdk.eip.v3.model.PublicipSingleShowResp;
import com.huaweicloud.sdk.evs.v2.EvsClient;
import com.huaweicloud.sdk.evs.v2.model.ListVolumesResponse;
import com.huaweicloud.sdk.evs.v2.model.VolumeDetail;
import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListUsersResponse;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListUsersResult;
import com.huaweicloud.sdk.iam.v3.model.LoginProtectResult;
import com.huaweicloud.sdk.rds.v3.RdsClient;
import com.huaweicloud.sdk.rds.v3.model.InstanceResponse;
import com.huaweicloud.sdk.rds.v3.model.ListInstancesRequest;
import com.huaweicloud.sdk.rds.v3.model.ListInstancesResponse;
import com.huaweicloud.sdk.vpc.v3.VpcClient;
import com.huaweicloud.sdk.vpc.v3.model.ListVpcsResponse;
import com.huaweicloud.sdk.vpc.v3.model.Vpc;
import org.apache.commons.lang3.StringUtils;
import com.huaweicloud.sdk.elb.v3.*;
import com.huaweicloud.sdk.elb.v3.model.*;

import java.util.List;
import java.util.Objects;

/**
 * {@code @Author:张少虎}
 * {@code @Date: 2022/12/6  12:01}
 * {@code @Version 1.0}
 * {@code @注释: }
 */
public class HuaweiApi {
    /**
     * 获取华为云ecs实例列表
     *
     * @param request 请求对象
     * @return ecs实例列表
     */
    public static List<ServerDetail> listEcsInstance(ListEcsInstanceRequest request) {
        EcsClient ecsClient = request.getCredential().getEcsClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setOffset(PageUtil.DefaultCurrentPage);
        return PageUtil.page(request, req -> {
                    try {
                        return ecsClient.listServersDetails(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListServersDetailsResponse::getServers,
                (req, res) -> req.getLimit() <= res.getServers().size(),
                req -> req.setOffset(req.getOffset() + 1));
    }

    /**
     * 获取华为云 云数据库 SqlServer 实例列表
     *
     * @param request 请求对象
     * @return sqlServer 实例列表
     */
    public static List<InstanceResponse> listSqlServer(ListRdsInstanceRequest request) {
        request.setDatastoreType(ListInstancesRequest.DatastoreTypeEnum.SQLSERVER);
        return listRdsInstance(request);
    }

    /**
     * 获取华为云 云数据库 portGreSql 实例列表
     *
     * @param request 请求对象
     * @return postGreSql 实例列表
     */
    public static List<InstanceResponse> listPostGreSqlInstance(ListRdsInstanceRequest request) {
        request.setDatastoreType(ListInstancesRequest.DatastoreTypeEnum.POSTGRESQL);
        return listRdsInstance(request);
    }

    /**
     * 获取华为云 云数据库 mysql实例对象
     *
     * @param request 请求对象
     * @return mysql实例对象
     */
    public static List<InstanceResponse> listMysqlInstance(ListRdsInstanceRequest request) {
        request.setDatastoreType(ListInstancesRequest.DatastoreTypeEnum.MYSQL);
        return listRdsInstance(request);
    }

    /**
     * 查询华为云 云数据库 Rds实例列表
     *
     * @param request 请求对象
     * @return Rds实例列表
     */
    private static List<InstanceResponse> listRdsInstance(ListRdsInstanceRequest request) {
        RdsClient rdsClient = request.getCredential().getRdsClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setOffset(PageUtil.DefaultCurrentPage);
        return PageUtil.page(request, req -> {
                    try {
                        return rdsClient.listInstances(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListInstancesResponse::getInstances,
                (req, res) -> req.getLimit() <= res.getInstances().size(),
                req -> req.setOffset(req.getOffset() + 1));

    }

    /**
     * 获取华为云 云数据库 redis实例列表
     *
     * @param request 请求对象
     * @return redis实例列表
     */
    public static List<InstanceListInfo> listRedisInstance(ListRedisInstanceRequest request) {
        DcsClient dcsClient = request.getCredential().getDcsClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setOffset(PageUtil.DefaultCurrentPage);
        return PageUtil.page(request, req -> {
                    try {
                        return dcsClient.listInstances(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                com.huaweicloud.sdk.dcs.v2.model.ListInstancesResponse::getInstances,
                (req, res) -> req.getLimit() <= res.getInstances().size(),
                req -> req.setOffset(req.getOffset() + 1));
    }

    /**
     * 获取华为云 云数据库 Mongodb 实例列表
     *
     * @param request 请求对象
     * @return mongodb 实例列表
     */
    public static List<QueryInstanceResponse> listMongodbInstance(ListMongodbInstanceRequest request) {
        DdsClient ddsClient = request.getCredential().getDdsClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setOffset(PageUtil.DefaultCurrentPage);
        return PageUtil.page(request, req -> {
                    try {
                        return ddsClient.listInstances(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                com.huaweicloud.sdk.dds.v3.model.ListInstancesResponse::getInstances,
                (req, res) -> req.getLimit() <= res.getInstances().size(),
                req -> req.setOffset(req.getOffset() + 1));
    }

    /**
     * 获取华为云 云数据库 elasticSearch 实例列表
     *
     * @param request 请求对象
     * @return elasticsearch 实例列表
     */
    public static List<ClusterList> listElasticSearchInstance(ListElasticSearchInstanceRequest request) {
        CssClient cssClient = request.getCredential().getCssClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setStart(1);
        return PageUtil.page(request, req -> {
                    try {
                        return cssClient.listClustersDetails(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListClustersDetailsResponse::getClusters,
                (req, res) -> req.getLimit() <= res.getClusters().size(),
                req -> req.setStart(req.getStart() + request.getLimit()));
    }

    /**
     * 获取华为云 云磁盘 实例列表
     *
     * @param request 请求对象
     * @return 云磁盘 实例列表
     */
    public static List<VolumeDetail> listDiskInstance(ListDiskInstanceRequest request) {
        EvsClient evsClient = request.getCredential().getEvsClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        request.setOffset(PageUtil.DefaultCurrentPage);
        return PageUtil.page(request, req -> {
                    try {
                        return evsClient.listVolumes(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListVolumesResponse::getVolumes,
                (req, res) -> req.getLimit() <= res.getVolumes().size(),
                req -> req.setOffset(req.getOffset() + 1));
    }

    /**
     * 获取华为云 负载均衡 实例列表
     *
     * @param request 请求对象
     * @return 负载均衡 实例列表
     */
    public static List<LoadBalancer> listLoadBalancerInstance(ListLoadBalancerInstanceRequest request) {
        ElbClient elbClient = request.getCredential().getElbClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        return PageUtil.page(request,
                req -> {
                    try {
                        return elbClient.listLoadBalancers(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListLoadBalancersResponse::getLoadbalancers,
                (req, res) -> StringUtils.isNotEmpty(res.getPageInfo().getNextMarker()),
                (req, res) -> req.setMarker(res.getPageInfo().getNextMarker()));
    }

    /**
     * 获取华为云 弹性公网ip 实例列表
     *
     * @param request 请求对象
     * @return 弹性公网ip实例列表
     */
    public static List<PublicipSingleShowResp> listPublicIpInstance(ListPublicIpInstanceRequest request) {
        EipClient eipClient = request.getCredential().getEipClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        return PageUtil.page(request,
                req -> {
                    try {
                        return eipClient.listPublicips(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListPublicipsResponse::getPublicips,
                (req, res) -> StringUtils.isNotEmpty(res.getPageInfo().getNextMarker()),
                (req, res) -> req.setMarker(res.getPageInfo().getNextMarker()));
    }

    /**
     * 获取 华为云 vpc 实例列表
     *
     * @param request 请求对象
     * @return vpc 实例列表
     */
    public static List<Vpc> listVpcInstance(ListVpcInstanceRequest request) {
        VpcClient vpcClient = request.getCredential().getVpcClient(request.getRegionId());
        request.setLimit(PageUtil.DefaultPageSize);
        return PageUtil.page(request,
                req -> {
                    try {
                        return vpcClient.listVpcs(request);
                    } catch (Exception e) {
                        ReTryException.throwHuaweiReTry(e);
                        throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
                    }
                },
                ListVpcsResponse::getVpcs,
                (req, res) -> StringUtils.isNotEmpty(res.getPageInfo().getNextMarker()),
                (req, res) -> req.setMarker(res.getPageInfo().getNextMarker()));
    }

    /**
     * 获取 华为云 IAM 用户列表
     *
     * @param request 请求对象
     * @return 华为云 IAM 用户列表
     */
    public static List<KeystoneListUsersResult> listRamInstance(ListRamInstanceRequest request) {
        IamClient iamClient = request.getCredential().getIamClient();
        return Objects.requireNonNull(PageUtil.reTry(() -> {
            try {
                return iamClient.keystoneListUsers(request);
            } catch (Exception e) {
                ReTryException.throwHuaweiReTry(e);
                throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
            }
        }, 3)).getUsers();
    }

    /**
     * 查询IAM用户的登录保护状态信息列表
     *
     * @param request 请求对象
     * @return IAM 用户的登录的保护状态信息列表
     */
    public static List<LoginProtectResult> listLoginProfileInstance(ListLoginProfileInstanceRequest request) {
        IamClient iamClient = request.getCredential().getIamClient();
        return Objects.requireNonNull(PageUtil.reTry(() -> {
            try {
                return iamClient.listUserLoginProtects(request);
            } catch (Exception e) {
                ReTryException.throwHuaweiReTry(e);
                throw new Fit2cloudException(10000, "获取数据失败" + e.getMessage());
            }
        }, 3)).getLoginProtects();

    }

}