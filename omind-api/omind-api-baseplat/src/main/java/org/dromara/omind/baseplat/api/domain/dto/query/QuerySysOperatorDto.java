package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QuerySysOperatorDto", description = "查询运营商请求")
public class QuerySysOperatorDto {

    @Schema(description = "用户平台运营商ID")
    private String operatorId;

    @Schema(description = "用户平台运营商名称")
    private String operatorName;

}
