package org.dromara.omind.baseplat.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.domain.vo.PriceInfoData;

import java.io.Serializable;
import java.util.List;

@Schema(description = "添加价格模版")
@Data
public class PriceAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "价格信息，必须从0点开始，按时间大小依次排序", requiredMode = Schema.RequiredMode.REQUIRED)
    List<PriceInfoData> priceList;

    @Schema(description = "备注（搜索用）")
    String remark;

}
