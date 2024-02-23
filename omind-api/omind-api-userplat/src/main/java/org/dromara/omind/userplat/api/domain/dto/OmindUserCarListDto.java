package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户车辆列表信息")
public class OmindUserCarListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名称")
    private String nickName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "车牌号")
    private String plateNo;

    @Schema(description = "车辆Vin码")
    private String carVin;

    @Schema(description = "审核状态:0、待审核;1、审核通过;2、审核不通过")
    private Short checkState;

    @Schema(description = "认证状态:0、不认证;1、待认证;2、认证通过;3、认证不通过")
    private Short authState;
}
