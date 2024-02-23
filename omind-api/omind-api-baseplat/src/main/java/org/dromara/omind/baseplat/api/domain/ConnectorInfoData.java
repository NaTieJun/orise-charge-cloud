package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(name = "ConnectorInfoData", description = "充电设备接口信息")
@Data
public class ConnectorInfoData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "ConnectorID", description = "充电设备接口ID",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "ConnectorName", description = "充电设备接口名称",  requiredMode = Schema.RequiredMode.AUTO)
    @JsonProperty(value = "ConnectorName")
    private String connectorName;

    @Schema(name = "ConnectorType", description = "充电设备接口类型，1家用插座（模式2），2交流接口插座（模式3，链接方式B），" +
        "3交流接口插头（带枪线，模式3，连接方式C），4直流接口枪头（带枪线，模式4），5无线充电座，6其他",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorType")
    private Short connectorType;

    @Schema(name = "VoltageUpperLimits", description = "额定电压上限，单位V",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "VoltageUpperLimits")
    private Integer voltageUpperLimits;

    @Schema(name = "VoltageLowerLimits", description = "额定电压下限，单位V",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "VoltageLowerLimits")
    private Integer voltageLowerLimits;

    @Schema(name = "Current", description = "额定电流，单位A",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "Current")
    private Integer current;

    @Schema(name = "Power", description = "额定功率，单位kW 保留小数点后1位",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "Power")
    private BigDecimal power;

    @Schema(name = "NationalStandard", description = "国家标准 1、2011 2、2015",  requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "NationalStandard")
    private Integer nationalStandard;

    public static ConnectorInfoData build(SysConnector sysConnector){

        ConnectorInfoData data = new ConnectorInfoData();

        if(sysConnector == null){
            return data;
        }
        data.connectorID = sysConnector.getConnectorId();
        data.connectorName = sysConnector.getConnectorName();
        data.connectorType = sysConnector.getConnectorType();
        data.voltageUpperLimits = sysConnector.getVoltageUpperLimits();
        data.voltageLowerLimits = sysConnector.getVoltageLowerLimits();
        data.current = sysConnector.getCurrentValue();
        data.nationalStandard = sysConnector.getNationalStandard().intValue();
        data.power = sysConnector.getPower();

        return data;
    }
}
