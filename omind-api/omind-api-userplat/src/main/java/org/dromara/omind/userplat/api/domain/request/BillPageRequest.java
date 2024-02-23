package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "订单列表请求")
@Data
public class BillPageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

    @Schema(description = "充电设备接口编码")
    private String connectorId;

    @Schema(description = "充电站id")
    private String stationId;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "充电订单状态:1、启动中;2、充电中;3、停止中;4、已结束;5、未知;8、异常订单;20、已处理异常订单")
    private Short startChargeSeqStat;



}
