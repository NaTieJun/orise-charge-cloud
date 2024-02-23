package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QueryStationsInfoData", description = "查询充电站信息请求Data")
public class QueryStationsInfoData {

    @Schema(name = "LastQueryTime", description = "查询的起始时间。格式为“yyyy-MM-dd HH:mm:ss”，建议为空，查询所有的充电站信息。默认结束时间为接口请求时间。")
    @JsonProperty(value = "LastQueryTime")
    private String lastQueryTime;

    @Schema(name = "PageNo", description = "页号，不填写默认为1")
    @JsonProperty(value = "PageNo")
    private Integer pageNo;

    @Schema(name = "PageSize", description = "分页长度，不填写默认为10")
    @JsonProperty(value = "PageSize")
    private Integer pageSize;

}
