package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户列表")
public class OmindUserListDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String nickName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "是否禁用用户:0、启用;1、禁用")
    private Short disableFlag;
}
