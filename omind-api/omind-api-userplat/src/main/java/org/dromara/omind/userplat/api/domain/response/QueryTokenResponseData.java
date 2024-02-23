package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "获取token 返回数据")
@Data
public class QueryTokenResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "OperatorID", description = "运营商标识")
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "SuccStat", description = "成功状态，0成功1失败")
    @JsonProperty(value = "SuccStat")
    private Short succStat;

    @Schema(name = "AccessToken", description = "获取的凭证，全局唯一")
    @JsonProperty(value = "AccessToken")
    private String accessToken;

    @Schema(name = "TokenAvailableTime", description = "凭证有效期")
    @JsonProperty(value = "TokenAvailableTime")
    private Long tokenAvailableTime;

    @Schema(name = "FailReason", description = "失败原因 0无 1无此运营商 2密钥错误 3-99自定义")
    @JsonProperty(value = "FailReason")
    private Short failReason;

}
