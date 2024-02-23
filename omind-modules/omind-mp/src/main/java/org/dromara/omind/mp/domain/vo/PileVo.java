package org.dromara.omind.mp.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "桩Vo")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PileVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "索引号")
    private Long id;

    @Schema(description = "桩ID")
    private String equipmentId;

    @Schema(description = "桩名")
    private String name;

    @Schema(description = "设备类型:1、直流设备;2、交流设备;3、交直流一体设备;4、无线设备;5、其他")
    private Short equipmentType;

    @Schema(description = "枪列表")
    private List<GunVo> gunList;

}
