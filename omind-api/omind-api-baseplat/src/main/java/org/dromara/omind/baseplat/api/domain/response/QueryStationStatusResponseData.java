package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.StationStatusInfoData;

import java.io.Serializable;
import java.util.List;

@Schema(name = "QueryStationStatusResponseData",description = "设备接口状态查询 返回数据")
@Data
public class QueryStationStatusResponseData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StationStatusInfos", description = "充电站信息")
    @JsonProperty(value = "StationStatusInfos")
    private List<StationStatusInfoData> stationStatusInfos;

    @Schema(name = "Total", description = "充电站数量")
    @JsonProperty(value = "Total")
    private Integer total;
}
