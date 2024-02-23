package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "传递用户基础信息")
@Data
public class MpInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像")
    private String avatarUrl;

}
