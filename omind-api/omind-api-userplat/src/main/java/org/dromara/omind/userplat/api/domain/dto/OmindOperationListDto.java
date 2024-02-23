package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "基础运营商列表查询")
@Data
public class OmindOperationListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "运营商名称")
    private String operatorName;
}
