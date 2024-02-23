package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "基础平台运营商信息")
@Data
@TableName("omind_operator")
public class OmindOperatorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "运营商id")
    @TableField(value = "operator_id")
    private String operatorId;

    @Schema(description = "运营商名称")
    @TableField(value = "operator_name")
    private String operatorName;

    @Schema(description = "运营商客服电话1")
    @TableField(value = "operator_tel1")
    private String operatorTel1;

    @Schema(description = "运营商客服电话2")
    @TableField(value = "operator_tel2")
    private String operatorTel2;

    @Schema(description = "运营商注册地址")
    @TableField(value = "operator_reg_address")
    private String operatorRegAddress;

    @Schema(description = "备注信息")
    @TableField(value = "operator_note")
    private String operatorNote;

    @Schema(description = "客户归属运营商id")
    @TableField(value = "user_operator_id")
    private String userOperatorId;

    @Schema(description = "运营商密钥")
    @TableField(value = "operator_secret")
    private String operatorSecret;

    @Schema(description = "消息密钥")
    @TableField(value = "data_secret")
    private String dataSecret;

    @Schema(description = "消息密钥初始化向量")
    @TableField(value = "data_secret_iv")
    private String dataSecretIv;

    @Schema(description = "签名密钥")
    @TableField(value = "sig_secret")
    private String sigSecret;

    @Schema(description = "基础运营商-运营商密钥")
    @TableField(value = "base_operator_secret")
    private String baseOperatorSecret;

    @Schema(description = "基础运营商-消息密钥")
    @TableField(value = "base_data_secret")
    private String baseDataSecret;

    @Schema(description = "基础运营商-消息密钥初始化向量")
    @TableField(value = "base_data_secret_iv")
    private String baseDataSecretIv;

    @Schema(description = "基础运营商-签名密钥")
    @TableField(value = "base_sig_secret")
    private String baseSigSecret;

    @Schema(description = "接口地址")
    @TableField(value = "api_url")
    private String apiUrl;

    @Schema(description = "平台类型:1、奥陌;")
    @TableField(value = "plat_type")
    private Short platType;

    @Schema(description = "删除标记")
    @TableField(value = "del_flag")
    @TableLogic
    private Short isDel;

    @Schema(description = "修改时间",hidden = true)
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "创建时间",hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
