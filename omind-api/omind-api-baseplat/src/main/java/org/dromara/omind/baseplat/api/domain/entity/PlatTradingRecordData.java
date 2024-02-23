package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(name = "PlatTradingRecordData", description = "平台交易订单原始数据")
@Data
@TableName("plat_trade_record")
public class PlatTradingRecordData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "交易流水号")
    @TableField("trade_no")
    private String tradeNo;

    @Schema(description = "桩编号")
    @TableField("pile_no")
    private String pileNo;

    @Schema(description = "枪号")
    @TableField("gun_no")
    private String gunNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "开始充电时间")
    @TableField("start_time")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "充电结束时间")
    @TableField("end_time")
    private Date endTime;

    @Schema(description = "尖单价（元）")
    @TableField("sharp_per_price")
    private BigDecimal sharpPerPrice;

    @Schema(description = "尖电量（度）")
    @TableField("sharp_kwh")
    private BigDecimal sharpKwh;

    @Schema(description = "尖电量（度）计损")
    @TableField("sharp_all_kwh")
    private BigDecimal sharpAllKwh;

    @Schema(description = "尖电费（元）")
    @TableField("sharp_price")
    private BigDecimal sharpPrice;

    @Schema(description = "峰单价（元）")
    @TableField("peak_per_price")
    private BigDecimal peakPerPrice;

    @Schema(description = "峰电量（度）")
    @TableField("peak_kwh")
    private BigDecimal peakKwh;

    @Schema(description = "峰电量（度）计损")
    @TableField("peak_all_kwh")
    private BigDecimal peakAllKwh;

    @Schema(description = "峰电费（元）")
    @TableField("peak_price")
    private BigDecimal peakPrice;

    @Schema(description = "平单价（元）")
    @TableField("flat_per_price")
    private BigDecimal flatPerPrice;

    @Schema(description = "平电量（度）")
    @TableField("flat_kwh")
    private BigDecimal flatKwh;

    @Schema(description = "平电量（度）计损")
    @TableField("flat_all_kwh")
    private BigDecimal flatAllKwh;

    @Schema(description = "平电费（元）")
    @TableField("flat_price")
    private BigDecimal flatPrice;

    @Schema(description = "谷单价（元）")
    @TableField("valley_per_price")
    private BigDecimal valleyPerPrice;

    @Schema(description = "谷电量（度）")
    @TableField("valley_kwh")
    private BigDecimal valleyKwh;

    @Schema(description = "谷电量（度）计损")
    @TableField("valley_all_kwh")
    private BigDecimal valleyAllKwh;

    @Schema(description = "谷电费（元）")
    @TableField("valley_price")
    private BigDecimal valleyPrice;

    @Schema(description = "起始表电量（度）")
    @TableField("start_kwh")
    private BigDecimal startKwh;

    @Schema(description = "结束表电量（度）")
    @TableField("end_kwh")
    private BigDecimal endKwh;

    @Schema(description = "充电量（度）")
    @TableField("final_kwh")
    private BigDecimal finalKwh;

    @Schema(description = "计损充电量（度）")
    @TableField("final_all_kwh")
    private BigDecimal finalAllKwh;

    @Schema(description = "总消费（元）")
    @TableField("cost")
    private BigDecimal cost;

    @Schema(description = "车辆识别码Vin码（17位）")
    @TableField("vin")
    private String vin;

    @Schema(description = "交易标识")
    @TableField("trade_type")
    private Short tradeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "交易时间")
    @TableField("trade_time")
    private Date tradeTime;

    @Schema(description = "停止原因")
    @TableField("stop_type")
    private Integer stopType;

    @Schema(description = "物理卡号")
    @TableField("system_card_no")
    private String systemCardNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
