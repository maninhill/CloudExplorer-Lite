package com.fit2cloud.dto;

import com.fit2cloud.base.entity.VmCloudDisk;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jianneng
 * @date 2022/9/27 14:39
 **/
@Data
public class VmCloudDiskDTO extends VmCloudDisk {

    @ApiModelProperty("组织名称")
    private String organizationName;
    @ApiModelProperty("工作空间名称")
    private String workspaceName;
    @ApiModelProperty("组织ID")
    private String organizationId;
    @ApiModelProperty("工作空间ID")
    private String workspaceId;
    @ApiModelProperty("云账号名称")
    private String accountName;
    @ApiModelProperty("所属云主机")
    private String vmInstanceName;
    @ApiModelProperty("所属云主机 ID")
    private String serverId;
    @ApiModelProperty("所属云平台")
    private String platform;
    @ApiModelProperty("磁盘属性")
    private String bootableText;
}
