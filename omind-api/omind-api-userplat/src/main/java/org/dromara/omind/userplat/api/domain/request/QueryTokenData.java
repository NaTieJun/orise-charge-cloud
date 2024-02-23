package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "获取Token Data")
public class QueryTokenData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "OperatorID", description = "调用方的组织机构代码")
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "OperatorSecret", description = "服务方分配的唯一识别密钥")
    @JsonProperty(value = "OperatorSecret")
    private String operatorSecret;

}
