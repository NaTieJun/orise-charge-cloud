package org.dromara.omind.mp.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;

import java.io.Serializable;

@Schema(description = "用户登录信息")
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "登录token")
    private String token;

    @Schema(description = "用户信息")
    private OmindUserEntity userInfo;


}
