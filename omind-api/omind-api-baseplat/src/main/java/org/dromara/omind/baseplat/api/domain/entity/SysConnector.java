package org.dromara.omind.baseplat.api.domain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_connector")
public class SysConnector implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("equipment_id")
    private String equipmentId;

    @TableField("connector_id")
    private String connectorId;

    @TableField("gun_no")
    private Short gunNo;

    @TableField("connector_name")
    private String connectorName;

    @TableField("connector_type")
    private Short connectorType;

    @TableField("voltage_upper_limits")
    private Integer voltageUpperLimits;

    @TableField("voltage_lower_limits")
    private Integer voltageLowerLimits;

    @TableField("current_value")
    @JSONField(name = "current")
    private Integer currentValue;

    @TableField("power")
    private BigDecimal power;

    @TableField("park_no")
    private String parkNo;

    @TableField("national_standard")
    private Short nationalStandard;

    @TableField("status")
    private Short status;

    @TableField("park_status")
    private Short parkStatus;

    @TableField("lock_status")
    private Short lockStatus;

    @TableField("price_code")
    private Integer priceCode;

    @TableField("state")
    private Short state;

    @TableField("ping_tm")
    private Date pingTm;

    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_by_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime = new Date();

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

}
