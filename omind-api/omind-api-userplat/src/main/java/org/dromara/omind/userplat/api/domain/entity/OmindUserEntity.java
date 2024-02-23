package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "小程序用户")
@Data
@TableName("omind_user")
public class OmindUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "uid")
    @TableId(type = IdType.AUTO)
    @TableField("uid")
    private Long uid;

    @Schema(description = "国家区域号码")
    @TableField("phone_code")
    private String phoneCode;

    @Schema(description = "手机号")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "昵称")
    @TableField("nick_name")
    private String nickName;

    @Schema(description = "微信名称")
    @TableField("wechat_name")
    private String wechatName;

    @Schema(description = "unionID-微信")
    @TableField("unionid_wx")
    private String unionIdWx;

    @Schema(description = "openID-微信")
    @TableField("openid_wx")
    private String openIdWx;

    @Schema(description = "unionID-阿里")
    @TableField("unionid_ali")
    private String unionIdAli;

    @Schema(description = "openID-阿里")
    @TableField("openid_ali")
    private String openIdAli;

    @Schema(description = "微信信用分授权-后支付")
    @TableField("credit_pay_wx")
    private Integer creditPayWx;

    @Schema(description = "阿里信用分授权-后支付")
    @TableField("credit_pay_ali")
    private Integer creditPayAli;

    @Schema(description = "0、未知;1、男;2、女")
    @TableField("sex")
    private Short sex;

    @Schema(description = "国家")
    @TableField("country")
    private String country;

    @Schema(description = "省")
    @TableField("province")
    private String province;

    @Schema(description = "城市")
    @TableField("city")
    private String city;

    @Schema(description = "生日")
    @TableField("birthday")
    private Long birthday;

    @Schema(description = "头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "注册来源的平台 0未知 1微信 2支付宝")
    @TableField("platform")
    private Integer platform;

    @Schema(description = "是否禁用用户:0、启用;1、禁用")
    @TableField("disable_flag")
    private Short disableFlag;

    @Schema(description = "注册时间")
    @TableField("register_time")
    private Date registerTime;

    @Schema(description = "最近一次访问时间")
    @TableField("last_visit_time")
    private Date lastVisitTime;

    @Schema(description = "最近一次访问ip")
    @TableField("last_visit_ip")
    private String lastVisitIp;

    @Schema(description = "最近一次访问ip对应区域")
    @TableField("last_visit_area")
    private String lastVisitArea;

    @Schema(description = "用户是否从属组织机构:0、不从属;1、从属")
    @TableField("org_flag")
    private Short orgFlag;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

    /**
     * 创建时间
     **/
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
