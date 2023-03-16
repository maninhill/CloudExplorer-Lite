import elementZhTwLocale from "element-plus/lib/locale/lang/zh-tw";
import fit2cloudZhTwLocale from "fit2cloud-ui-plus/src/locale/lang/zh-tw";
import subModuleTwLocale from "@/locales/lang/zh-tw";

const message = {
  commons: {
    home: "首頁",
    notice: "通知",
    to_do_list: "待辦清單",
    view_all: "查看全部",
    operation: "操作",
    name: "名稱",
    tag: "標簽",
    org: "組織",
    workspace: "工作空間",
    os: "操作系統",
    os_version: "操作系統版本",
    status: "狀態",
    create_time: "創建時間",
    operate_time: "操作時間",
    update_time: "更新時間",
    delete_time: "刪除時間",
    description: "描述",
    basic_info: "基本信息",
    grant: "授權",
    cancel_grant: "取消授權",
    org_workspace: "組織或工作空間",
    cloud_account: {
      native: "雲賬號",
      name: "雲賬號名稱",
      data_center: "數據中心",
      cluster: "集群",
      region: "區域",
      zone: "可用區",
      host: "宿主機",
      storage: "存儲器",
      disk: "磁盤",
      vm: "雲主機",
      image: "鏡像",
    },
    cloud_server: {
      instance_type: "實例規格",
      applicant: "申請人",
      creator: "創建人",
      more: "更多",
    },
    message_box: {
      alert: "警告",
      confirm: "確認",
      prompt: "提示",
      confirm_delete: "確認刪除",
    },
    btn: {
      login: "登錄",
      yes: "是",
      no: "否",
      ok: "確定",
      add: "添加",
      create: "創建",
      delete: "刪除",
      edit: "編輯",
      save: "保存",
      close: "關閉",
      submit: "提交",
      publish: "發布",
      cancel: "取消",
      return: "返回",
      grant: "授權",
      hide: "隱藏",
      display: "顯示",
      enable: "啟用",
      disable: "禁用",
      copy: "複制",
      sync: "同步",
      view_api: "查看 API",
      prev: "上一步",
      next: "下一步",
      switch_lang: "切換語言",
      add_favorites: "收藏",
      cancel_favorites: "取消收藏",
      search: "查找",
      refresh: "刷新",
      import: "導入",
      export: "導出",
      upload: "上傳",
      download: "下載",
      more_actions: "更多操作",
      filter: "篩選",
      reset: "重置",
      grope: "搜索",
      clear: "清空",
    },
    msg: {
      success: "{0}成功",
      op_success: "操作成功",
      save_success: "保存成功",
      delete_success: "刪除成功",
      fail: "{0}失敗",
      delete_canceled: "已取消刪除",
      at_least_select_one: "至少選擇一條記錄",
    },
    validate: {
      required: "{0}必填",
      limit: "長度在 {0} 到 {1} 個字符",
      input: "請輸入{0}",
      select: "請選擇{0}",
      confirm_pwd: "兩次輸入的密碼不壹致",
      pwd: "有效密碼：8-30比特，英文大小寫字母+數位+特殊字元",
    },
    personal: {
      personal_info: "個人信息",
      edit_pwd: "修改密碼",
      help_document: "幫助文檔",
      exit_system: "退出系統",
      old_password: "原密碼",
      new_password: "新密碼",
      confirm_password: "確認密碼",
      login_identifier: "登錄標識",
      username: "用戶名",
      phone: "手機號碼",
      wechat: "企業微信號碼",
    },
    date: {
      select_date: "選擇日期",
      start_date: "開始日期",
      end_date: "結束日期",
      select_time: "選擇時間",
      start_time: "開始時間",
      end_time: "結束時間",
      select_date_time: "選擇日期時間",
      start_date_time: "開始日期時間",
      end_date_time: "結束日期時間",
      range_separator: "至",
      date_time_error: "開始日期不能大於結束日期",
    },
    login: {
      username: "用戶名",
      password: "密碼",
      please_input_username: "請輸入用戶名",
      please_input_password: "請輸入密碼",
      title: "CloudExplorer 雲服務平臺",
      welcome: "歡迎使用 CloudExplorer 雲管理平臺",
      expires: "認證信息已過期，請重新登錄",
    },
    charge_type: {
      native: "付費方式",
      prepaid: "包年包月",
      postpaid: "按量付費",
    },
  },
};

const permissions = {
  permission: {
    manage: {
      user: {
        base: "用戶",
        read: "查看",
        create: "創建",
        edit: "編輯",
        edit_password: "修改密碼",
        delete: "刪除",
        notification_setting: "通知設置",
      },
      role: {
        base: "角色",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
      },
      organization: {
        base: "組織",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
      },
      workspace: {
        base: "工作空間",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
      },
      cloud_account: {
        base: "云賬號",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
        sync_resource: "同步資源",
        sync_bill: "同步賬單",
        sync_setting: "同步設置",
      },
      sys_log: {
        base: "系統日志",
        read: "查看",
        clear_policy: "清空策略設置",
      },
      operated_log: {
        base: "操作日志",
        read: "查看",
        clear_policy: "清空策略設置",
      },
      params_setting: {
        base: "参数设置",
        read: "查看",
        edit: "编辑",
      },
      module: {
        base: "模塊管理",
        read: "查看",
        edit: "編輯",
      },
    },
    vm: {
      cloud_server: {
        base: "云主機",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
        start: "啟動",
        stop: "停止",
        restart: "重啟",
        resize: "配置變更",
        auth: "授權",
      },
      cloud_disk: {
        base: "磁盤",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "刪除",
        attach: "掛載",
        detach: "卸載",
        resize: "擴容",
        auth: "授權",
      },
      cloud_image: {
        base: "鏡像",
        read: "查看",
      },
      jobs: {
        base: "任務",
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
        base: "總覽",
        read: "查看",
      },
      base_resource_analysis: {
        base: "基礎資源分析",
        read: "查看",
      },
      server_analysis: {
        base: "雲主機分析",
        read: "查看",
      },
      disk_analysis: {
        base: "雲磁片分析",
        read: "查看",
      },
      resource_optimization: {
        base: "雲主機優化",
        read: "查看",
      },
    },
    bill: {
      view: {
        base: "帳單總覽",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
      detailed: {
        base: "帳單明細",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
      custom_bill: {
        base: "自定義帳單",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
      dimension_setting: {
        base: "分賬設定",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
    },
    security: {
      overview: {
        base: "總覽",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
      scan: {
        base: "合規掃描",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
        send_job: "扫描",
      },
      rule: {
        base: "合規規則",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
      insurance: {
        base: "風險條例",
        read: "查看",
        create: "創建",
        edit: "編輯",
        delete: "删除",
      },
    },
  },
};

export default {
  ...elementZhTwLocale,
  ...fit2cloudZhTwLocale,
  ...message,
  ...permissions,
  ...subModuleTwLocale,
};
