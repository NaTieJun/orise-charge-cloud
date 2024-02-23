package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.system.api.domain.vo.RemoteAreaVo;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "树-区域节点")
@Data
public class TreeNodeVo {

    @Schema(description = "区域码")
    Integer areaCode;

    @Schema(description = "区域Vo")
    RemoteAreaVo areaVo;

    @Schema(description = "子区域Vo")
    List<TreeNodeVo> subNodes;

    @Schema(description = "充电站列表")
    List<StationVo> stationList;

    public TreeNodeVo(){
        stationList = new ArrayList<>();
        subNodes = new ArrayList<>();
    }

}
