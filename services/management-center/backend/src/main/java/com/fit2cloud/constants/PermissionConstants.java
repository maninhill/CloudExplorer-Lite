package com.fit2cloud.constants;

import com.fit2cloud.common.constants.RoleConstants;
import com.fit2cloud.dto.permission.ModulePermission;
import com.fit2cloud.dto.permission.Permission;
import com.fit2cloud.dto.permission.PermissionGroup;
import com.fit2cloud.service.PermissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模块内权限
 * 需要实现 MODULE_PERMISSION_BUILDER
 * 当前模块内判断主要是 groupId 和 operate
 * 也可以通过判断id，但是id需要带上模块名与require组合，如："[management-center]USER:READ"
 */
@Component
public class PermissionConstants {

    public static class GROUP {
        public static final String USER = "USER";
        public static final String ROLE = "ROLE";

        public static final String CLOUD_ACCOUNT = "CLOUD_ACCOUNT";
        public static final String ORGANIZATION = "ORGANIZATION";
        public static final String SYSTEM_SETTING = "SYSTEM_SETTING";
        //...
    }

    public static class OPERATE {
        public static final String READ = "READ";
        public static final String EDIT = "EDIT";
        public static final String CREATE = "CREATE";
        //...
    }

    public static ModulePermission MODULE_PERMISSION;

    /**
     * 可以通过id找到对应权限
     */
    public static Map<String, Permission> PERMISSION_MAP = null;

    @Resource
    private PermissionService permissionService;

    @Value("${spring.application.name}")
    public void setModule(String module) {
        PermissionConstants.MODULE_PERMISSION = MODULE_PERMISSION_BUILDER
                .module(module)
                .build();

        PERMISSION_MAP = PermissionConstants.MODULE_PERMISSION.getGroups().stream()
                .map(PermissionGroup::getPermissions)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Permission::getId, permission -> permission));

        //推送到redis
        permissionService.init(module, PermissionConstants.MODULE_PERMISSION);
    }

    private static final ModulePermission.Builder MODULE_PERMISSION_BUILDER = new ModulePermission.Builder()
            .group(
                    //用户管理
                    new PermissionGroup.Builder()
                            .id(GROUP.USER)
                            .name("i18n_permission_user")
                            .permission(
                                    //查看用户
                                    new Permission.Builder()
                                            .operate(OPERATE.READ)
                                            .name("i18n_permission_user_read")
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .role(RoleConstants.ROLE.ORGADMIN)
                            )
                            .permission(
                                    //新建用户
                                    new Permission.Builder()
                                            .require(OPERATE.READ)
                                            .operate(OPERATE.CREATE)
                                            .name("i18n_permission_user_create")
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .role(RoleConstants.ROLE.ORGADMIN)
                            )
                            .permission(
                                    //编辑用户
                                    new Permission.Builder()
                                            .require(OPERATE.READ)
                                            .operate(OPERATE.EDIT)
                                            .name("i18n_permission_user_edit")
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .role(RoleConstants.ROLE.USER)
                            )
                    //...
            )
            .group(
                    //权限管理
                    new PermissionGroup.Builder()
                            .id(GROUP.ROLE)
                            .name("i18n_permission_role")
                            .permission(
                                    new Permission.Builder()
                                            .operate(OPERATE.READ)
                                            .name("i18n_permission_role_read")
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .role(RoleConstants.ROLE.ORGADMIN)
                            )
            )
            // 组织相关权限配置
            .group(
                    new PermissionGroup.Builder()
                            .id(GROUP.ORGANIZATION)
                            .name("i18n_permission_organization")
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.READ)
                                    .name("i18n_permission_organization_red")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN))
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.EDIT)
                                    .name("i18n_permission_organization_edit")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN))
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.CREATE)
                                    .name("i18n_permission_organization_create")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN)))
            // 云账号相关权限
            .group(
                    new PermissionGroup.Builder()
                            .id(GROUP.CLOUD_ACCOUNT)
                            .name("i18n_permission_cloud_account")
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.READ).name("i18n_permission_cloud_account_red")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN))
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.EDIT).name("i18n_permission_cloud_account_edit")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN))
                            .permission(new Permission.Builder()
                                    .operate(OPERATE.CREATE).name("i18n_permission_cloud_account_create")
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .role(RoleConstants.ROLE.ORGADMIN))
            )
            //...
            ;


}