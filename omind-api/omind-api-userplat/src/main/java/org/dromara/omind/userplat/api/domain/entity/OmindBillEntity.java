package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "订单信息")
@Data
@TableName("omind_bill")
public class OmindBillEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long billId;

    @Schema(description = "充电站ID")
    @TableField("station_id")
    private String stationId;

    @Schema(description = "基础平台运营商ID")
    @TableField("base_operator_id")
    private String baseOperatorId;

    @Schema(description = "充电订单号")
    @TableField("start_charge_seq")
    private String startChargeSeq;

    @Schema(description = "充电设备接口编码")
    @TableField("connector_id")
    private String connectorId;

    @Schema(description = "充电订单状态:1、启动中;2、充电中;3、停止中;4、已结束;5、未知;8、异常订单;20、已处理异常订单")
    @TableField("start_charge_seq_stat")
    private Short startChargeSeqStat;

    @Schema(description = "充电者用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "开始充电时间")
    @TableField("start_time")
    private Date startTime;

    @Schema(description = "结束充电时间")
    @TableField("end_time")
    private Date endTime;

    @Schema(description = "累计充电量")
    @TableField("total_power")
    private BigDecimal totalPower;

    @Schema(description = "总电费")
    @TableField("total_elec_money")
    private BigDecimal totalElecMoney;

    @Schema(description = "总服务费")
    @TableField("total_service_money")
    private BigDecimal totalServiceMoney;

    @Schema(description = "累计总金额")
    @TableField("total_money")
    private BigDecimal totalMoney;

    @Schema(description = "实际支付总金额")
    @TableField("real_pay_money")
    private BigDecimal realPayMoney;

    @Schema(description = "充电结束原因:0、用户手动停止充电;1、客户归属地运营商平台停止充电;2、BMS停止充电;3、充电机设备故障;4、连接器断开;5-99、自定义")
    @TableField("stop_reason")
    private Integer stopReason;

    @Schema(description = "停止失败原因:0、无;1、此设备不存在;2、此设备离线;3、设备已停止充电;4~99、自定义")
    @TableField("stop_fail_reason")
    private Short stopFailReason;

    @Schema(description = "时段数N，范围：0～32")
    @TableField("sum_period")
    private Short sumPeriod;

    @Schema(description = "交易信息 json")
    @TableField("charge_detail")
    private String chargeDetail;

    @Schema(description = "车辆识别码")
    @TableField("car_vin")
    private String carVin;

    @Schema(description = "车牌号")
    @TableField("plate_no")
    private String plateNo;

    @Schema(description = "成功标识:0、成功;1、失败;")
    @TableField("succ_stat")
    private Short succStat;

    @Schema(description = "订单类型 0扫码充电 1刷卡充电")
    @TableField("bill_type")
    private Short billType;

    @Schema(description = "电池剩余电量(默认:0)")
    @TableField("soc")
    private BigDecimal soc;

    @Schema(description = "是否支付:0、未支付;1、已支付;")
    @TableField("pay_state")
    private Short payState;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "充电价格信息")
    @TableField("price_info")
    private String priceInfo;

    @Schema(description = "付款平台编码 如：wxpay、alipay")
    @TableField("pay_plat")
    private String payPlat;

    @Schema(description = "付款流水号")
    @TableField("transaction_id")
    private String transactionId;

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

    @Schema(description = "A相电流")
    @TableField("currentA")
    private BigDecimal currentA;

    @Schema(description = "A相电压")
    @TableField("voltageA")
    private BigDecimal voltageA;

    @Schema(description = "充电类型 1、充满;2、按金额充电")
    @TableField("charge_type")
    private Short chargeType;

    @Schema(description = "用户预计充电金")
    @TableField("charge_money")
    private BigDecimal chargeMoney;

    @Schema(description = "站点名称")
    @TableField(exist = false)
    private String stationName;

    @Schema(description = "用户昵称")
    @TableField(exist = false)
    private String nickName;

    @Schema(description = "手机号")
    @TableField(exist = false)
    private String mobile;

    @Schema(description = "运营商名称")
    @TableField(exist = false)
    private String operatorName;

    @Schema(description = "充电时长")
    @TableField(exist = false)
    private Long chargeDur;

    @Schema(description = "当前功率")
    @TableField(exist = false)
    private BigDecimal currentP;

}
