package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "意见反馈请求")
@Data
public class FeedbackRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "反馈类型:1、充电站;2、充电桩;3、充电枪;50、其他")
    private Short type;

    @Schema(description = "枪编号")
    private String connectorId;

    @Schema(description = "反馈内容")
    private String txt;

    @Schema(description = "图片路径列表")
    private String imgArrayJson;

    @Schema(description = "小程序版本号")
    private String appVersion;

}
