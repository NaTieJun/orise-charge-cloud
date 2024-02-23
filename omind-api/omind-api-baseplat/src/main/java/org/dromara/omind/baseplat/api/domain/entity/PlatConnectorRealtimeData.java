package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电实时通信数据")
@NoArgsConstructor
@Data
@TableName("plat_connector_realtime_data")
public class PlatConnectorRealtimeData  implements Serializable {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "ID")
    private Long id;

    @TableField("trade_no")
    @Schema(description = "桩级交易流水号")
    private String tradeNo;

    @TableField("connector_id")
    @Schema(description = "充电枪ID")
    private String connectorId;

    @TableField("pile_no")
    @Schema(description = "充电桩ID")
    private String pileNo;

    @TableField("gun_no")
    @Schema(description = "枪口号")
    private String gunNo;

    @TableField("state")
    @Schema(description = "状态码 0离线 1故障 2空闲 3充电")
    private Short state;

    @TableField("gun_state")
    @Schema(description = "枪是否归位 0否 1是 2未知")
    private Short gunState;

    @TableField("gun_link")
    @Schema(description = "是否插枪 0否 1是")
    private Short gunLink;

    @TableField("out_voltage")
    @Schema(description = "输出电压 精确到小数后一位；待机置零")
    private BigDecimal outVoltage;

    @TableField("out_current")
    @Schema(description = "输出电流 精确到小数后一位；待机置零")
    private BigDecimal outCurrent;

    @TableField("gunline_temp")
    @Schema(description = "枪线温度 偏移量-50 待机置0")
    private Integer gunlineTemp;

    @TableField("gunline_no")
    @Schema(description = "枪线编码 8位bin码")
    private Long gunlineNo;

    @TableField("soc")
    @Schema(description = "0-100 剩余电量")
    private BigDecimal soc;

    @TableField("battery_max_temp")
    @Schema(description = "电池组最高温度 偏移量-50 待机置0")
    private Integer batteryMaxTemp;

    @TableField("total_charge_durant")
    @Schema(description = "累计充电时间 单位Min，待机置零")
    private Integer totalChargeDurant;

    @TableField("remain_charge_durant")
    @Schema(description = "剩余充电时间 单位Min，待机置零")
    private Integer remainChargeDurent;

    @TableField("charge_kwh")
    @Schema(description = "充电读数，精确到小数点后4位；待机置零")
    private BigDecimal chargingKWH;

    @TableField("lose_kwh")
    @Schema(description = "计损充电读书，精确到小数点后四位;待机置零 未设置计损比例时等于充电度数")
    private BigDecimal loseKwh;

    @TableField("charge_money")
    @Schema(description = "精确到小数点后四位;待机置零 (电费+服务费)*计损充电度数")
    private BigDecimal chargeMoney;

    @TableField("hd_error")
    @Schema(description = "硬件故障，位运算解析")
    private Short hdError;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError1 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError2 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError3 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError4 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError5 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError6 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError7 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError8 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError9 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError10 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError11 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError12 = 0;

    @TableField(exist = false)
    @Schema(description = "")
    private Short hdError13 = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;
}
