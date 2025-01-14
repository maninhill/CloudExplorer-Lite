import elementZhLocale from "element-plus/lib/locale/lang/zh-cn";
import fit2cloudZhLocale from "fit2cloud-ui-plus/src/locale/lang/zh-cn"; // 加载 fit2cloud 的国际化文件
import subModuleZhLocale from "@/locales/lang/zh-cn"; // 加载子模块自定义的国际化文件

const message = {
  commons: {
    home: "首页",
    notice: "通知",
    to_do_list: "待办列表",
    view_all: "查看全部",
    operation: "操作",
    name: "名称",
    tag: "标签",
    org: "组织",
    workspace: "工作空间",
    os: "操作系统",
    os_version: "操作系统版本",
    status: "状态",
    create_time: "创建时间",
    operate_time: "操作时间",
    update_time: "更新时间",
    delete_time: "删除时间",
    basic_info: "基本信息",
    description: "描述",
    grant: "授权",
    cancel_grant: "取消授权",
    org_workspace: "组织或工作空间",
    cloud_account: {
      native: "云账号",
      name: "云账号名称",
      data_center: "数据中心",
      cluster: "集群",
      region: "区域",
      zone: "可用区",
      host: "宿主机",
      storage: "存储器",
      disk: "磁盘",
      vm: "云主机",
      image: "镜像",
    },
    cloud_server: {
      instance_type: "实例规格",
      applicant: "申请人",
      creator: "创建人",
      more: "更多",
      status: {
        Running: "运行中",
        Deleted: "已删除",
        Stopped: "已关机",
        Starting: "启动中",
        Stopping: "关机中",
        Rebooting: "重启中",
        Deleting: "删除中",
        Creating: "创建中",
        Unknown: "未知",
        Failed: "失败",
        ToBeRecycled: "待回收",
        WaitCreating: "排队中",
        ConfigChanging: "配置变更中",
      },
    },
    message_box: {
      alert: "警告",
      confirm: "确认",
      prompt: "提示",
      confirm_delete: "确认删除",
    },
    btn: {
      login: "登录",
      yes: "是",
      no: "否",
      ok: "确定",
      add: "添加",
      create: "创建",
      delete: "删除",
      edit: "编辑",
      save: "保存",
      close: "关闭",
      submit: "提交",
      publish: "发布",
      cancel: "取消",
      return: "返回",
      grant: "授权",
      hide: "隐藏",
      display: "显示",
      enable: "启用",
      disable: "禁用",
      copy: "复制",
      sync: "同步",
      view_api: "查看 API",
      prev: "上一步",
      next: "下一步",
      switch_lang: "切换语言",
      add_favorites: "收藏",
      cancel_favorites: "取消收藏",
      search: "搜索",
      refresh: "刷新",
      import: "导入",
      export: "导出",
      upload: "上传",
      download: "下载",
      more_actions: "更多操作",
      filter: "筛选",
      reset: "重置",
      grope: "搜索",
      clear: "清空",
    },
    msg: {
      success: "{0}成功",
      op_success: "操作成功",
      save_success: "保存成功",
      delete_success: "删除成功",
      fail: "{0}失败",
      delete_canceled: "已取消删除",
      at_least_select_one: "至少选择一条数据",
    },
    validate: {
      required: "{0}必填",
      format_error: "{0}格式错误",
      limit: "长度在 {0} 到 {1} 个字符",
      input: "请输入{0}",
      select: "请选择{0}",
      confirm_pwd: "两次输入的密码不一致",
      pwd: "有效密码：8-30位，英文大小写字母+数字+特殊字符",
    },
    personal: {
      personal_info: "个人信息",
      edit_pwd: "修改密码",
      help_document: "帮助文档",
      exit_system: "退出系统",
      old_password: "原密码",
      new_password: "新密码",
      confirm_password: "确认密码",
      login_identifier: "登录标识",
      username: "用户名",
      email: "邮箱",
      phone: "手机号码",
      wechat: "企业微信号码",
    },
    date: {
      select_date: "选择日期",
      start_date: "开始日期",
      end_date: "结束日期",
      select_time: "选择时间",
      start_time: "开始时间",
      end_time: "结束时间",
      select_date_time: "选择日期时间",
      start_date_time: "开始日期时间",
      end_date_time: "结束日期时间",
      range_separator: "至",
      date_time_error: "开始日期不能大于结束日期",
    },
    login: {
      username: "用户名",
      password: "密码",
      please_input_username: "请输入用户名",
      please_input_password: "请输入密码",
      title: "CloudExplorer 云服务平台",
      welcome: "欢迎使用 CloudExplorer 云管理平台",
      expires: "认证信息已过期，请重新登录",
    },
    charge_type: {
      native: "付费方式",
      prepaid: "包年包月",
      postpaid: "按量付费",
    },
  },
};

const permissions = {
  permission: {
    manage: {
      user: {
        base: "用户",
        read: "查看",
        create: "创建",
        edit: "编辑",
        edit_password: "修改密码",
        delete: "删除",
        notification_setting: "通知设置",
      },
      role: {
        base: "角色",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      organization: {
        base: "组织",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      workspace: {
        base: "工作空间",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      cloud_account: {
        base: "云账号",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
        sync_resource: "同步资源",
        sync_bill: "同步账单",
        sync_setting: "同步设置",
      },
      sys_log: {
        base: "系统日志",
        read: "查看",
        clear_policy: "清空策略设置",
      },
      operated_log: {
        base: "操作日志",
        read: "查看",
        clear_policy: "清空策略设置",
      },
      params_setting: {
        base: "参数设置",
        read: "查看",
        edit: "编辑",
      },
      module: {
        base: "模块管理",
        read: "查看",
        edit: "编辑",
      },
      about: {
        base: "关于",
        read: "查看",
      },
    },
    vm: {
      cloud_server: {
        base: "云主机",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
        start: "启动",
        stop: "停止",
        restart: "重启",
        resize: "配置变更",
        auth: "授权",
      },
      cloud_disk: {
        base: "磁盘",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
        attach: "挂载",
        detach: "卸载",
        resize: "扩容",
        auth: "授权",
      },
      cloud_image: {
        base: "镜像",
        read: "查看",
      },
      jobs: {
        base: "任务",
        read: "查看",
      },
      recycle_bin: {
        base: "回收站",
        read: "查看",
        delete: "删除",
        recover: "恢复",
      },
    },
    operation: {
      overview: {
        base: "总览",
        read: "查看",
      },
      base_resource_analysis: {
        base: "基础资源分析",
        read: "查看",
      },
      server_analysis: {
        base: "云主机分析",
        read: "查看",
      },
      disk_analysis: {
        base: "云磁盘分析",
        read: "查看",
      },
      resource_optimization: {
        base: "云主机优化",
        read: "查看",
      },
    },
    bill: {
      view: {
        base: "账单总览",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      detailed: {
        base: "账单明细",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      custom_bill: {
        base: "自定义账单",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      dimension_setting: {
        base: "分账设置",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
    },
    security: {
      overview: {
        base: "总览",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      scan: {
        base: "合规扫描",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
        send_job: "扫描",
      },
      rule: {
        base: "合规规则",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
      insurance: {
        base: "风险条例",
        read: "查看",
        create: "创建",
        edit: "编辑",
        delete: "删除",
      },
    },
  },
};

export default {
  ...elementZhLocale,
  ...fit2cloudZhLocale,
  ...message,
  ...permissions,
  ...subModuleZhLocale,
};
