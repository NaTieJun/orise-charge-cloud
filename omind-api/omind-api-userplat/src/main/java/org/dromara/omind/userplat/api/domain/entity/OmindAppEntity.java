package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "应用表")
@Data
@TableName(value = "omind_app")
public class OmindAppEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "app类型 0 管理后台 1 app")
    @TableField(value = "app_type")
    private Integer appType;

    @Schema(description = "应用名称")
    @TableField(value = "app_name")
    private String appName;

    @Schema(description = "应用key")
    @TableField(value = "app_key")
    private String appKey;

    @Schema(description = "应用密钥")
    @TableField(value = "secret")
    private String secret;

    @Schema(description = "有效截止时间")
    @TableField(value = "valid_time")
    private Long validTime;

    @Schema(description = "状态 0 未启用 1 正常 2 禁用")
    @TableField(value = "state")
    private Short state;

    @Schema(description = "修改时间",hidden = true)
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "创建时间",hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
