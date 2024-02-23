package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "EquipmentInfoData", description = "充电设备信息")
@Data
public class EquipmentInfoData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "EquipmentID", description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipmentID")
    private String equipmentID;

    @Schema(name = "ManufacturerID", description = "设备生产商组织机构代码")
    @JsonProperty(value = "ManufacturerID")
    private String manufacturerID;

    @Schema(name = "EquipmentModel", description = "设备型号")
    @JsonProperty(value = "EquipmentModel")
    private String equipmentModel;

    @Schema(name = "ProductionDate", description = "设备生产日期，YYYY-MM-DD")
    @JsonProperty(value = "ProductionDate")
    private String productionDate;

    @Schema(name = "EquipmentType", description = "设备类型，1直流设备，2交流设备，3交直流一体设备，4无线设备，5其他")
    @JsonProperty(value = "EquipmentType")
    private Short equipmentType;

    @Schema(name = "ConnectorInfos", description = "充电设备接口信息列表")
    @JsonProperty(value = "ConnectorInfos")
    private List<ConnectorInfoData> connectorInfos;

    @Schema(name = "Power", description = "充电设备总功率")
    @JsonProperty(value = "Power")
    private BigDecimal power;

    @Schema(name = "EquipmentName", description = "充电设备名称")
    @JsonProperty(value = "EquipmentName")
    private String equipmentName;

    public static EquipmentInfoData build(SysEquipment sysEquipment){
        EquipmentInfoData data = new EquipmentInfoData();
        if(sysEquipment == null){
            return data;
        }
        data.equipmentID = sysEquipment.getEquipmentId();
        data.manufacturerID = sysEquipment.getManufacturerId();
        data.equipmentModel = sysEquipment.getEquipmentModel();
        if(sysEquipment.getProductionDate() != null) {
            data.productionDate = sysEquipment.getProductionDate().length() > 10 ? sysEquipment.getProductionDate().substring(0, 10) : sysEquipment.getProductionDate();
        }
        data.equipmentType = sysEquipment.getEquipmentType();
        data.power = sysEquipment.getPower();
        data.equipmentName = sysEquipment.getEquipmentName();
        data.connectorInfos = new ArrayList<>();
        return data;
    }
}
