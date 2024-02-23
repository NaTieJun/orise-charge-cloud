package org.dromara.omind.mp.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "枪Vo")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GunVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "索引号")
    private Long id;

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "充电接口名")
    private String connectorName;

    @Schema(description = "充电设备接口状态:0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障")
    private Short status;

    @Schema(description = "充电设备接口类型:1、家用插座(模式2);2、交流接口插座(模式3，连接方式B);3、交流接口插头(带枪线，模式3，连接方式C);4、直流接口枪头(带枪线，模式4);5、无线充电座;6、其他")
    @TableField(value = "connector_type")
    private Short connectorType;

    @Schema(description = "车位状态:0:未知;10:空闲;50:已上锁")
    private Short parkStatus;

    @Schema(description = "充电设备总功率(单位:kW,保留小数点后1位)")
    private BigDecimal power;

    @Schema(description = "充电进度 0-1.00")
    private BigDecimal chargingPer;

    @Schema(description = "充电剩余时间（分钟）")
    private Integer remainMinute;

    @Schema(description = "开始充电时间")
    private Date startTm;

}
