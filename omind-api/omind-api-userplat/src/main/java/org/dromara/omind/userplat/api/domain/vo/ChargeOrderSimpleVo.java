package org.dromara.omind.userplat.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电订单简单数据")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ChargeOrderSimpleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

    @Schema(description = "状态 1、启动中;2、充电中;3、停止中;4、已结束;5、未知")
    private Short status;

    @Schema(description = "充电站名称")
    private String stationName;

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "充电类型，0扫码充电")
    private Short type;

    @Schema(description = "充电开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTm;

    @Schema(description = "充电结束时间，未结束不返回该字段")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTm;

    @Schema(description = "充电累计金额")
    private BigDecimal money;

    @Schema(description = "实际支付金额")
    private BigDecimal realMoney;

}
