package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "查询充电站信息请求Data")
public class QueryStationsInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "LastQueryTime", description = "查询的起始时间")
    @JsonProperty(value = "LastQueryTime")
    private String lastQueryTime;

    @Schema(name = "PageNo", description = "页号")
    @JsonProperty(value = "PageNo")
    private Integer pageNo;

    @Schema(name = "PageSize", description = "分页长度")
    @JsonProperty(value = "PageSize")
    private Integer pageSize;

}
