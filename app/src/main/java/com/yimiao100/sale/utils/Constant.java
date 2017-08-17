package com.yimiao100.sale.utils;

import java.util.HashMap;

/**
 * 常量
 * Created by Michel on 2016/8/9.
 */
public interface Constant {
    boolean isTest = false;     // 是否在测试环境下

    // BaseURL
    String BASE_URL_TEST = "http://ymt.s1.natapp.cc/ymt";
//    String BASE_URL_TEST = "http://161p3p2316.iask.in/ymt";
//    String BASE_URL_TEST = "http://192.168.199.206:8080/ymt";
    String BASE_URL_OFFICAL = "http://123.56.203.55/ymt";
    String BASE_URL = isTest ? BASE_URL_TEST : BASE_URL_OFFICAL;

    // 分享URL
    String SHARE_URL_TEST = "http://161p3p2316.iask.in/ymt";
    String SHARE_URL_OFFICAL = "http://www.yimiaoquan100.com/ymt";
    String SHARE_URL = isTest ? SHARE_URL_TEST : SHARE_URL_OFFICAL;


    /**
     * Mob-AppKey
     */
    String MOB_APP_KEY = "15edb464463f1";
    /**
     * Mob-AppSecret
     */
    String MOB_APP_SECRET = "3510466f3b4c2a690f3b8d81e6fce3c1";
    /**
     * 美洽-AppKey
     */
    String MEI_QIA_APP_KEY = "73b657bcb9829a51600a8f04e3f3609f";

    /**
     *
     */
    String ACCESSTOKEN = "accessToken";

    /**
     * 用户id
     */
    String USERID = "userId";
    String WX_APP_ID = "wx6e6d290399bd8ec8";

    String WX_APP_SECRET = "29f35a49ce9ec5f8c5df0a0a61d1b383";
    String SINA_APP_KEY = "199335694";
    String SINA_APP_SECRET = "97c9a68faa6e0035d25d819cb943ddae";
    String QQ_APP_ID = "1105579589";
    String QQ_APP_KEY = "6rjh7D4gc5J36A4e";
    String SINA_SHARE_URL = "http://sns.whalecloud.com/sina2/callback";
    /**
     * 错误信息
     */
    HashMap<Integer, String> ERROR_INFORMATION = new HashMap<Integer, String>(){
        {
            put(101, "网络状况异常，请检查网络");
            put(102, "账号无效");
            put(103, "密码错误");
            put(104, "验证码无效");
            put(105, "用户已存在，请不要重复注册");
            put(106, "用户不存在");
            put(107, "刷新token无效");
            put(108, "用户姓名无效");
            put(109, "手机号无效");
            put(110, "邮箱无效");
            put(111, "身份证号无效");
            put(112, "用户头像无效");
            put(113, "上传文件失败");
            put(114, "原密码无效");
            put(115, "新密码无效");
            put(116, "账号已在别处登录");
            put(117, "银行名称无效");
            put(118, "银行账户名称无效");
            put(119, "银行账号无效");
            put(120, "企业营业执照无效");
            put(121, "区域信息无效");
            put(122, "评论内容无效");
            put(123, "已点赞");
            put(124, "订单信息无效");
            put(125, "订单协议文件无效");
            put(126, "对账确认状态无效");
            put(127, "对账确认内容无效");
            put(128, "批量处理的订单id无效");
            put(129, "申请可提现信息无效");
            put(130, "资源信息无效");
            put(131, "资源信息过期");
            put(132, "用户账户信息无效");
            put(133, "提现信息无效");
            put(134, "资金明细信息无效");
            put(135, "资讯信息无效");
            put(136, "评论信息无效");
            put(137, "重复下单");
            put(138, "课程无效");
            put(139, "积分余额不足");
            put(140, "当前用户已经参与该次竞标");
            put(141, "公司法人无效");
            put(142, "公司法人电话号码无效");
            put(143, "公司法人身份证号无效");
            put(144, "积分计算无效");
            put(145, "厂家id无效");
            put(146, "地址无效");
            put(147, "课程考试无效");
            put(148, "考试奖励提现无效");
            put(149, "轮播类型无效");
            put(150, "积分兑换商品无效");
            put(151, "提交对公账户信息无效");
            put(152, "支付无效");
            put(153, "获取协议文件无效");
            put(154, "操作过于频繁");
            put(155, "提交个人账户信息无效");
            put(156, "提现状态无效");
            put(157, "不良反应申报无效");
            put(158, "授权委托书申报无效");
            put(159, "在线下单无效");
        }
    };
    String DEFAULT_VIDEO = "http://oduhua0b1.bkt.clouddn.com/default_video.mp4";
    String CNNAME = "cnName";
    String PHONENUMBER = "phoneNumber";
    String EMAIL = "email";
    String IDNUMBER = "idNumber";
    String PROFILEIMAGEURL = "profileImageUrl";
    String REGION = "region";
    String SEARCH_HISTORY = "search_history";
    String CORPORATE_ACCOUNT_NUMBER = "corporate_account_number";
    String CORPORATE_PHONE_NUMBER = "corporate_phone_number";
    String CORPORATE_AMOUNT = "corporate_amount";
    String LOGIN_STATUS = "login_status";
    String DEFAULT_IMAGE = "http://oduhua0b1.bkt.clouddn.com/banner_placeholder.png";
    String CORPORATE_BANK_NAME = "corporate_bank_name";
    String DEFAULT_TYPE = "MTFfODcyNTc3MF8";
    String CORPORATE_EXIT = "corporate_exit";
    String PERSONAL_EXIT = "personal_exit";
    String CORPORATE_BIZ_LICENCE_URL = "corporate_biz_licence_url";
    String CORPORATE_ACCOUNT_NAME = "corporate_account_name";
    String PERSONAL_CN_NAME = "personal_cn_name";
    String PGYER_API_KEY = "6160ef2a74b29ad9a9d537866936fd79";
    String CORPORATE_CN_NAME = "corporate_cn_name";
    String CORPORATION_PERSONAL_PHONE_NUMBER = "corporation_personal_phone_number";
    String CORPORATE_ID_NUMBER = "corporate_id_number";
    String VERSION_IK = "od.lk/s/";
    String REGION_LIST = "region_list";
    String INTEGRAL = "integral";
    String TOTAL_AMOUNT = "total_amount";
    String TOTAL_EXAM_REWARD = "total_exam_reward";
    String TOTAL_SALE = "total_sale";
    String DEPOSIT = "deposit";
    String CONFIGURATION = "configuration";
    String EXAM_IS_NOTICE = "exam_is_notice";
    String CORPORATE_EMAIL = "corporate_email";
    String CORPORATE_PERSONAL_URL = "corporate_personal_url";
    String CORPORATE_ID_URL = "corporate_id_url";
    String CORPORATE_ACCOUNT_STATUS = "corporate_account_status";
    String CORPORATE_QQ = "corporate_qq";
    String IS_FIRST = "is_first";
    String DATA_VERSION_ = "data_version_";
    String ACCOUNT_NUMBER = "account_number";
    String PERSONAL_BANK_CARD_NUMBER = "personal_bank_card_number";
    String PERSONAL_ACCOUNT_STATUS = "PERSONAL_ACCOUNT_STATUS";
    String TYPE = "txt";
    String PERSONAL_ID_CARD = "personal_id_card";
    String PERSONAL_PHONE_NUMBER = "personal_phone_number";
    String TAX_RATE = "tax_rate";
    String SHORTCUT = "EH4S";
    // 更新key
    String UPDATE_API_KEY = "6160ef2a74b29ad9a9d537866936fd79";
}
