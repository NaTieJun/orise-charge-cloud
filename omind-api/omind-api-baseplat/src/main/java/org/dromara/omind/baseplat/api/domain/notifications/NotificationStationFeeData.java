package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.baseplat.api.domain.StationFeeDetailData;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "推送站点价格信息")
@Data
public class NotificationStationFeeData {

    @Schema(name = "OperatorID", description = "运营商id")
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "StationID", description = "充电站ID")
    @JsonProperty(value = "StationID")
    private String stationID;

    @Schema(name = "ChargeFeeDetail", description = "费率详情")
    @JsonProperty(value = "ChargeFeeDetail")
    private List<StationFeeDetailData> chargeFeeDetail;

    @Schema(name = "DiscountFeeDetail", description = "折扣后费率详情")
    @JsonProperty(value = "DiscountFeeDetail")
    private List<StationFeeDetailData> discountFeeDetail;

    @Schema(name = "OriginalFeeDetail", description = "原价费率详情")
    @JsonProperty(value = "OriginalFeeDetail")
    private List<StationFeeDetailData> originalFeeDetail;

}
