package org.dromara.omind.userplat.api.domain.datas;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;

import java.io.Serializable;
import java.math.BigDecimal;


@Schema(description = "充电设备接口信息")
@Data
public class ConnectorInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "ConnectorName", description = "充电设备接口名称")
    @JsonProperty(value = "ConnectorName")
    private String connectorName;

    @Schema(name = "ConnectorType", description = "充电设备接口类型，1家用插座，2交流接口插座，" +
            "3交流接口插头，4直流接口枪头，5无线充电座，6其他", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorType")
    private Short connectorType;

    @Schema(name = "VoltageUpperLimits", description = "额定电压上限", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "VoltageUpperLimits")
    private Integer voltageUpperLimits;

    @Schema(name = "VoltageLowerLimits", description = "额定电压下限", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "VoltageLowerLimits")
    private Integer voltageLowerLimits;

    @Schema(name = "Current", description = "额定电流", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "Current")
    private Integer current;

    @Schema(name = "Power", description = "额定功率", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "Power")
    private BigDecimal power;

    @Schema(name = "NationalStandard", description = "国家标准 1、2011 2、2015", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "NationalStandard")
    private Integer nationalStandard;

    public static ConnectorInfoData build(OmindConnectorEntity sysConnector) {

        ConnectorInfoData data = new ConnectorInfoData();
        if (sysConnector == null) {
            return data;
        }
        data.connectorID = sysConnector.getConnectorId();
        data.connectorType = sysConnector.getConnectorType();
        data.voltageUpperLimits = sysConnector.getVoltageUpperLimits();
        data.voltageLowerLimits = sysConnector.getVoltageLowerLimits();
        data.current = sysConnector.getCurrentValue();
        data.nationalStandard = sysConnector.getNationalStandard().intValue();
        data.power = sysConnector.getPower();

        return data;
    }

}
