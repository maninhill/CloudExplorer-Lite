package com.fit2cloud.constants;

import com.fit2cloud.common.constants.RoleConstants;
import com.fit2cloud.constants.PermissionConstants.GROUP;
import com.fit2cloud.constants.PermissionConstants.OPERATE;
import com.fit2cloud.dto.module.Menu;
import com.fit2cloud.dto.module.MenuPermission;
import com.fit2cloud.dto.module.Menus;
import com.fit2cloud.service.MenuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MenuConstants {

    public static List<Menu> MENUS;

    @Resource
    private MenuService menuService;

    @Value("${spring.application.name}")
    public void setModule(String module) {

        MENUS = MENUS_BUILDER.module(module).build().getMenus();

        //推送到redis
        menuService.init(module, MENUS);

    }

    private static final Menus.Builder MENUS_BUILDER = new Menus.Builder()
            .menu(new Menu.Builder()
                    .name("overview")
                    .title("总览")
                    .path("/overview")
                    .componentPath("/src/views/overview/index.vue")
                    .icon("icon_moments-categories_outlined")
                    .order(1)
                    .redirect("/overview/list")
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ADMIN)
                            .permission(GROUP.OVERVIEW, OPERATE.READ)
                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ORGADMIN)
                            .permission(GROUP.OVERVIEW, OPERATE.READ)
                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.USER)
                            .permission(GROUP.OVERVIEW, OPERATE.READ)
                    )
                    .childOperationRoute(new Menu.Builder()
                            .name("overview_list")
                            .path("/list")
                            .title("列表")
                            .saveRecent(true)
                            .componentPath("/src/views/overview/list.vue")
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .permission(GROUP.OVERVIEW, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ORGADMIN)
                                    .permission(GROUP.OVERVIEW, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.USER)
                                    .permission(GROUP.OVERVIEW, OPERATE.READ)
                            )
                    )

            )
            .menu(new Menu.Builder()
                    .name("resource_analysis")
                    .title("资源分析")
                    .path("/resource_analysis")
                    .icon("icon_operation-analysis_outlined")
                    .order(2)
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ADMIN)
                            .permission(GROUP.BASE_RESOURCE_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.RESOURCE_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)

                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ORGADMIN)
                            .permission(GROUP.RESOURCE_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.USER)
                            .permission(GROUP.RESOURCE_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                    )
                    .childMenu(new Menu.Builder()
                            .name("base_resource_analysis")
                            .title("基础资源分析")
                            .path("/base_resource_analysis")
                            .componentPath("/src/views/base_resource_analysis/index.vue")
                            .redirect("/resource_analysis/base_resource_analysis/list")
                            .order(1)
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .permission(GROUP.BASE_RESOURCE_ANALYSIS, OPERATE.READ)
                            )
                            .childOperationRoute(new Menu.Builder()
                                    .name("list")
                                    .title("列表")
                                    .path("/list")
                                    .saveRecent(true)
                                    .componentPath("/src/views/base_resource_analysis/list.vue")
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .permission(GROUP.BASE_RESOURCE_ANALYSIS, OPERATE.READ)
                                    )
                            )

                    )
                    .childMenu(new Menu.Builder()
                            .name("server_analysis")
                            .title("云主机分析")
                            .path("/server_analysis")
                            .componentPath("/src/views/server_analysis/index.vue")
                            .redirect("/resource_analysis/server_analysis/list")
                            .order(1)
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ORGADMIN)
                                    .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.USER)
                                    .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                            )
                            .childOperationRoute(new Menu.Builder()
                                    .name("server_analysis_list")
                                    .title("列表")
                                    .path("/list")
                                    .saveRecent(true)
                                    .componentPath("/src/views/server_analysis/list.vue")
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                                    )
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.ORGADMIN)
                                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                                    )
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.USER)
                                            .permission(GROUP.SERVER_ANALYSIS, OPERATE.READ)
                                    )
                            )

                    )
                    .childMenu(new Menu.Builder()
                            .name("disk_analysis")
                            .title("磁盘分析")
                            .path("/disk_analysis")
                            .componentPath("/src/views/disk_analysis/index.vue")
                            .redirect("/resource_analysis/disk_analysis/list")
                            .order(1)
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ORGADMIN)
                                    .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.USER)
                                    .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                            )
                            .childOperationRoute(new Menu.Builder()
                                    .name("disk_analysis_list")
                                    .title("列表")
                                    .path("/list")
                                    .saveRecent(true)
                                    .componentPath("/src/views/disk_analysis/list.vue")
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.ADMIN)
                                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                                    )
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.ORGADMIN)
                                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                                    )
                                    .requiredPermission(new MenuPermission.Builder()
                                            .role(RoleConstants.ROLE.USER)
                                            .permission(GROUP.DISK_ANALYSIS, OPERATE.READ)
                                    )
                            )

                    )
            )
            .menu(new Menu.Builder()
                    .name("server_optimization")
                    .title("云主机优化")
                    .path("/server_optimization")
                    .icon("icon_ecs_outlined")
                    .componentPath("/src/views/resource_optimization/index.vue")
                    .redirect("/server_optimization/list")
                    .order(1)
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ADMIN)
                            .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.ORGADMIN)
                            .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                    )
                    .requiredPermission(new MenuPermission.Builder()
                            .role(RoleConstants.ROLE.USER)
                            .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                    )
                    .childOperationRoute(new Menu.Builder()
                            .name("resource_optimization_list")
                            .path("/list")
                            .saveRecent(true)
                            .title("列表")
                            .componentPath("/src/views/resource_optimization/list.vue")
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ADMIN)
                                    .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.ORGADMIN)
                                    .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                            )
                            .requiredPermission(new MenuPermission.Builder()
                                    .role(RoleConstants.ROLE.USER)
                                    .permission(GROUP.SERVER_OPTIMIZATION, OPERATE.READ)
                            )
                    )
            );


}
