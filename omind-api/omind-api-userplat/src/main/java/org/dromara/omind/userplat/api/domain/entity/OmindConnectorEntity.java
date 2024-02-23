package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电枪信息")
@Data
@TableName("omind_connector")
public class OmindConnectorEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "充电站ID")
    @TableField("station_id")
    private String stationId;

    @Schema(description = "运营商ID")
    @TableField("operator_id")
    private String operatorId;

    @Schema(description = "用户平台运营商ID")
    @TableField("user_operator_id")
    private String userOperatorId;

    @Schema(description = "基础平台运营商ID")
    @TableField("base_operator_id")
    private String baseOperatorId;

    @Schema(description = "桩ID")
    @TableField("equipment_id")
    private String equipmentId;

    @Schema(description = "充电设备接口编码")
    @TableField("connector_id")
    private String connectorId;

    @Schema(description = "枪名")
    @TableField("connector_name")
    private String connectorName;

    @Schema(description = "充电设备接口类型:1、家用插座;2、交流接口插座;3、交流接口插头;4、直流接口枪头;5、无线充电座;6、其他")
    @TableField("connector_type")
    private Short connectorType;

    @Schema(description = "额定电压上限")
    @TableField("voltage_upper_limits")
    private Integer voltageUpperLimits;

    @Schema(description = "额定电压下限")
    @TableField("voltage_lower_limits")
    private Integer voltageLowerLimits;

    @Schema(description = "额定电流")
    @TableField("current_value")
    private Integer currentValue;

    @Schema(description = "额定功率")
    @TableField("power")
    private BigDecimal power;

    @Schema(description = "车位号")
    @TableField("park_no")
    private String parkNo;

    @Schema(description = "国家标准:1、2011;2、2015")
    @TableField("national_standard")
    private Short nationalStandard;

    @Schema(description = "充电设备接口状态:0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障")
    @TableField("status")
    private Short status;

    @Schema(description = "车位状态:0:未知;10:空闲;50:已上锁")
    @TableField("park_status")
    private Short parkStatus;

    @Schema(description = "地锁状态:0:未知;10:已解锁;50:已上锁")
    @TableField("lock_status")
    private Short lockStatus;

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
