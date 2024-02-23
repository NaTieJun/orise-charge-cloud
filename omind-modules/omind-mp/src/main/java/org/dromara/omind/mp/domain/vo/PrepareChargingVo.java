package org.dromara.omind.mp.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "下单前信息")
@Data
public class PrepareChargingVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电站名称")
    private String stationName;

    @Schema(description = "充电站ID")
    private String stationId;

    @Schema(description = "设备编码")
    private String equipmentId;

    @Schema(description = "设备类型:1、直流设备;2、交流设备;3、交直流一体设备;4、无线设备;5、其他")
    private Short type;

    @Schema(description = "输出功率")
    private BigDecimal power;

    @Schema(description = "2为已插枪，可继续充电；充电设备接口状态:0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障")
    private Integer status;

    @Schema(description = "当是正在充电的用户查询时，携带订单号")
    private String startChargeSeq;

    @Schema(description = "价格列表（桩）")
    private List<PriceVo> priceList;

    @Schema(description = "充电关联的车牌号")
    private String plateNo;

}
