package org.dromara.omind.userplat.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电订单信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmindBillInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单id")
    private Long billId;

    @Schema(description = "充电订单号',27字符")
    private String startChargeSeq;

    @Schema(description = "充电订单状态:1、启动中;2、充电中;3、停止中;4、已结束;5、未知")
    private Short startChargeSeqStat;

    @Schema(description = "充电设备接口编码")
    private String connectorId;

    @Schema(description = "开始充电时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTime;

    @Schema(description = "结束充电时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;

    @Schema(description = "累计充电量")
    private BigDecimal totalPower;

    @Schema(description = "总电费")
    private BigDecimal totalElecMoney;

    @Schema(description = "总服务费")
    private BigDecimal totalServiceMoney;

    @Schema(description = "累计总金额")
    private BigDecimal totalPrice;

    @Schema(description = "实际支付总金额")
    private BigDecimal payPrice;

    @Schema(description = "车辆识别码")
    private String carVin;

    @Schema(description = "创建时间",hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @Schema(description = "站点名称")
    private String stationName;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "车牌号")
    private String plateNo;

    @Schema(description = "运营商名称")
    private String operatorName;

}
