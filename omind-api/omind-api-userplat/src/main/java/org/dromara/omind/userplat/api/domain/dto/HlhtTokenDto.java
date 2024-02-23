package org.dromara.omind.userplat.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "HlhtDto",description = "互联互通标准请求对象")
public class HlhtTokenDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "AccessToken", description = "全局唯一凭证")
    @JsonProperty(value = "AccessToken")
    private String accessToken;

    @Schema(name = "TokenAvailableTime", description = "凭证有效期")
    @JsonProperty(value = "TokenAvailableTime")
    private Integer tokenAvailableTime;
}
