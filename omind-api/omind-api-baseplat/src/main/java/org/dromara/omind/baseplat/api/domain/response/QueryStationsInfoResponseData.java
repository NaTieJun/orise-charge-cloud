package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(name = "QueryStationsInfoResponseData", description = "查询充电站信息 返回数据")
@Data
public class QueryStationsInfoResponseData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "PageNo", description = "当前页数，如果查询页码大于页码总数，返回查询页码数")
    @JsonProperty(value = "PageNo")
    private Integer pageNo;

    @Schema(name = "PageCount", description = "页码总数，总页数")
    @JsonProperty(value = "PageCount")
    private Integer pageCount;

    @Schema(name = "ItemSize", description = "总记录条数，符合条件的电站总数")
    @JsonProperty(value = "ItemSize")
    private Integer itemSize;

    @Schema(name = "StationInfos", description = "充电站信息列表")
    @JsonProperty(value = "StationInfos")
    private List stationInfos;

}
