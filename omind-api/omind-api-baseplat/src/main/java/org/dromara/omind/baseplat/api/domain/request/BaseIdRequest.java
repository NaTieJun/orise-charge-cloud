package org.dromara.omind.baseplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@Schema(name = "BaseIdRequest", description = "ID标准请求")
public class BaseIdRequest {

    @Schema(name = "id", description = "id")
    @JsonProperty(value = "id")
    private String id;

}
