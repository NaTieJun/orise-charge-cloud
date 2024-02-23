package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_charge_order")
public class SysChargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("start_charge_seq")
    private String startChargeSeq;

    @TableField("operator_id")
    private String operatorId;

    @TableField("trade_no")
    private String tradeNo;

    @TableField("start_charge_seq_stat")
    private Short startChargeSeqStat;

    @TableField("station_id")
    private String stationId;

    @TableField("connector_id")
    private String connectorId;

    @TableField("connector_status")
    private Integer connectorStatus;

    @TableField("current_a")
    private BigDecimal currentA;

    @TableField("current_b")
    private BigDecimal currentB;

    @TableField("current_c")
    private BigDecimal currentC;

    @TableField("voltage_a")
    private BigDecimal voltageA;

    @TableField("voltage_b")
    private BigDecimal voltageB;

    @TableField("voltage_c")
    private BigDecimal voltageC;

    @TableField("soc")
    private BigDecimal soc;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @TableField("total_power")
    private BigDecimal totalPower;

    @TableField("elec_money")
    private BigDecimal elecMoney;

    @TableField("service_money")
    private BigDecimal serviceMoney;

    @TableField("total_money")
    private BigDecimal totalMoney;

    @TableField("sum_period")
    private Short sumPeriod;

    @TableField("fail_reason")
    private Integer failReason;

    @TableField("car_vin")
    private String carVin;

    @TableField("phone_num")
    private String phoneNum;

    @TableField("plate_num")
    private String plateNum;

    @TableField("sync_flag")
    private Short syncFlag;

    @TableField("report_gov")
    private Short reportGov;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime = new Date();

    @TableField("price_info")
    private String priceInfo;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

}
