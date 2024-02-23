package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_operator")
public class SysOperator implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 运营商ID（组织机构代码）
     */
    @TableField("operator_id")
    private String operatorId;

    @TableField("my_operator_id")
    private String myOperatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField("operator_tel_1")
    private String operatorTel1;

    @TableField("operator_tel_2")
    private String operatorTel2;

    @TableField("operator_reg_address")
    private String operatorRegAddress;

    @TableField("operator_note")
    private String operatorNote;

    @TableField("host")
    private String host;

    /**
     * 运营商密钥0-F字符组成,可采用32H、48H、64H
     */
    @TableField("operator_secret")
    private String operatorSecret;

    /**
     * 消息密钥
     */
    @TableField("data_secret")
    private String dataSecret;

    /**
     * 消息密钥初始化向量 固定16位 用户AES加密过程的混合加密
     */
    @TableField("data_secret_iv")
    private String dataSecretIv;

    /**
     * 签名密钥0-F字符组成,可采用32H、48H、64H 为签名的加密密钥
     */
    @TableField("sig_secret")
    private String sigSecret;

    /**
     * 用户平台-密钥0-F字符组成,可采用32H、48H、64H
     */
    @TableField("user_operator_secret")
    private String userOperatorSecret;

    /**
     * 用户平台-消息密钥
     */
    @TableField("user_data_secret")
    private String userDataSecret;

    /**
     * 用户平台-消息密钥初始化向量 固定16位 用户AES加密过程的混合加密
     */
    @TableField("user_data_secret_iv")
    private String userDataSecretIv;

    /**
     * 用户平台-签名密钥0-F字符组成,可采用32H、48H、64H 为签名的加密密钥
     */
    @TableField("user_sig_secret")
    private String userSigSecret;

    /**
     * 创建时间
     **/
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建者ID
     **/
    @TableField(fill = FieldFill.INSERT)
    private Long createById;

    /**
     * 更新时间
     **/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 更新者ID
     **/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

}
