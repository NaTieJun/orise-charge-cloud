package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.ChargeOrderData;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "NotificationCheckChargeOrdersData", description = "推送订单对账结果信息")
@Data
public class NotificationCheckChargeOrdersData {
    @Schema(name = "CheckOrderSeq", description = "订单对账流水号", required = true)
    @JsonProperty(value = "CheckOrderSeq")
    private String checkOrderSeq;

    @Schema(name = "StartTime", description = "账单开始时间，格式“yyyy-MM-dd HH:mm:ss”", required = true)
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "EndTime", description = "账单结束时间，格式“yyyy-MM-dd HH:mm:ss”", required = true)
    @JsonProperty(value = "EndTime")
    private String endTime;

    @Schema(name = "OrderCount", description = "订单数量N", required = true)
    @JsonProperty(value = "OrderCount")
    private Integer orderCount;

    @Schema(name = "TotalOrderPower", description = "总电量", required = true)
    @JsonProperty(value = "TotalOrderPower")
    private BigDecimal totalOrderPower;

    @Schema(name = "TotalOrderMoney", description = "总金额", required = true)
    @JsonProperty(value = "TotalOrderMoney")
    private BigDecimal totalOrderMoney;

    @Schema(name = "ChargeOrders", description = "订单列表", required = true)
    @JsonProperty(value = "ChargeOrders")
    private List<ChargeOrderData> chargeOrders;
}
