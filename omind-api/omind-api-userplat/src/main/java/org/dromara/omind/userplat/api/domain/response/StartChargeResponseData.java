package org.dromara.omind.userplat.api.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "小程序端请求启动充电返回数据")
@Data
public class StartChargeResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "设备认证请求操作结果 0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short equipAuthSuccStat;

    @Schema(description = "失败原因 0无 1此设备尚未插枪 2设备检测失败 3-99自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short equipAuthFailReason;

    @Schema(description = "启动充电操作结果 0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short startChargeSuccStat;

    @Schema(description = "失败原因 0无 1此设备不存在 2此设备离线 3-99自定义 90车辆未备案 91该车辆充电中", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short startChargeFailReason;

    @Schema(description = "充电订单状态，1启动中，2充电中，3停止中，4已结束，5未知", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short startChargeSeqStat;

    @Schema(description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String startChargeSeq;

    @Schema(description = "用户错误信息")
    private Short userFailReason;

    @Schema(description = "最少充值金额")
    private BigDecimal minChargeMoney;

    @Schema(description = "订单信息")
    private OmindBillEntity billInfo;
}
