package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.http.util.TextUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "StationInfoData", description = "充电站信息")
@Data
public class StationInfoData {

    @Schema(name = "StationID", description = "充电站ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StationID")
    private String stationID;

    @Schema(name = "OperatorID", description = "运营商ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "EquipmentOwnerID", description = "设备所属方ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipmentOwnerID")
    private String equipmentOwnerID;

    @Schema(name = "StationName", description = "充电站名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StationName")
    private String stationName;

    @Schema(name = "CountryCode", description = "充电站国家代码 2字符 比如CN", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "CountryCode")
    private String countryCode;

    @Schema(name = "AreaCode", description = "充电站省市辖区编码，填写内容参照GB/T 2260-2007，20字符", required = true)
    @JsonProperty(value = "AreaCode")
    private String areaCode;

    @Schema(name = "Address", description = "详细地址 <=50字符", required = true)
    @JsonProperty(value = "Address")
    private String address;

    @Schema(name = "StationTel", description = "站点电话 <=30字符", required = false)
    @JsonProperty(value = "StationTel")
    private String stationTel;

    @Schema(name = "ServiceTel", description = "服务电话 <=30字符", required = true)
    @JsonProperty(value = "ServiceTel")
    private String serviceTel;

    @Schema(name = "StationType", description = "站点类型 1公共 50个人 100公交（专用） 101环卫（专用） 102物流（专用）103出租车（专用） 255其他", required = true)
    @JsonProperty(value = "StationType")
    private Integer stationType;

    @Schema(name = "StationStatus", description = "站点状态，0未知，1建设中，5关闭下线，6维护中，50正常使用", required = true)
    @JsonProperty(value = "StationStatus")
    private Short stationStatus;

    @Schema(name = "ParkNums", description = "车位数量，可停放进行充电的车位总数，默认0未知", required = true)
    @JsonProperty(value = "ParkNums")
    private Integer parkNums;

    @Schema(name = "StationLng", description = "经度 GCJ-02坐标系 保留6位小数", required = true)
    @JsonProperty(value = "StationLng")
    private BigDecimal stationLng;

    @Schema(name = "StationLat", description = "纬度 GCJ-02坐标系 保留6位小数", required = true)
    @JsonProperty(value = "StationLat")
    private BigDecimal stationLat;

    @Schema(name = "SiteGuide", description = "站点引导 描述性文字，用于引导车主找到充电车位 <=100字符", required = false)
    @JsonProperty(value = "SiteGuide")
    private String siteGuide;

    @Schema(name = "Construction", description = "建设场所 1居民区 2公共机构 3企事业单位 4写字楼 5工业园区 6交通枢纽 7大型问题设施 8城市绿地 " +
        "9大型建筑配建停车位 10路边停车位 11城际高速服务区 255其他", required = true)
    @JsonProperty(value = "Construction")
    private Integer construction;

    @Schema(name = "Pictures", description = "站点照片，充电设备照片、充电车位照片、停车场入口照片", required = false)
    @JsonProperty(value = "Pictures")
    private List<String> pictures;

    @Schema(name = "MatchCars", description = "使用车型描述，描述站点接受的车辆大小及类型，如大巴、物流车、私家车、出租车等，<=100字符", required = false)
    @JsonProperty(value = "MatchCars")
    private String matchCars;

    @Schema(name = "ParkInfo", description = "车位楼层及数量描述 <=100字符", required = false)
    @JsonProperty(value = "ParkInfo")
    private String parkInfo;

    @Schema(name = "BusineHours", description = "营业时间描述 <=100字符", required = false)
    @JsonProperty(value = "BusineHours")
    private String busineHours;

    @Schema(name = "ElectricityFee", description = "充电电费率 <=256字符", required = false)
    @JsonProperty(value = "ElectricityFee")
    private String electricityFee;

    @Schema(name = "ServiceFee", description = "服务费旅 <=100字符", required = false)
    @JsonProperty(value = "ServiceFee")
    private String serviceFee;

    @Schema(name = "ParkFee", description = "停车费，<=100字符", required = false)
    @JsonProperty(value = "ParkFee")
    private String parkFee;

    @Schema(name = "Payment", description = "支付方式，支付方式：刷卡、线上、现金。  <=20字符 " +
        "其中电子钱包类卡为刷卡，身份鉴权卡、微信/支付宝、APP为线上", required = false)
    @JsonProperty(value = "Payment")
    private String payment;

    @Schema(name = "SupportOrder", description = "是否支持预约。0不支持，1支持 不填默认位0", required = false)
    @JsonProperty(value = "SupportOrder")
    private Short supportOrder;

    @Schema(name = "Remark", description = "备注 <=100字符", required = false)
    @JsonProperty(value = "Remark")
    private String remark;

    @Schema(name = "EquipmentInfos", description = "充电设备信息列表", required = true)
    @JsonProperty(value = "EquipmentInfos")
    private List<EquipmentInfoData> equipmentInfos;


    public static StationInfoData build(SysStation sysStation){
        StationInfoData data = new StationInfoData();
        if(sysStation == null){
            return data;
        }
        data.operatorID = sysStation.getOperatorId();
        data.stationID = sysStation.getStationId();
        data.stationName = sysStation.getStationName();
        data.equipmentOwnerID = sysStation.getEquipmentOwnerId();
        data.countryCode = sysStation.getCountryCode();
        data.areaCode = sysStation.getAreaCode();
        data.address = sysStation.getAddress();
        data.stationTel = sysStation.getStationTel();
        data.serviceTel = sysStation.getServiceTel();
        data.stationType = sysStation.getStationType();
        data.stationStatus = sysStation.getStationStatus();
        data.parkNums = sysStation.getParkNums();
        data.stationLng = sysStation.getStationLng();
        data.stationLat = sysStation.getStationLat();
        data.construction = sysStation.getConstruction();
        data.siteGuide = sysStation.getSiteGuide();
        data.matchCars = sysStation.getMatchCars();
        data.parkInfo = sysStation.getParkInfo();
        data.busineHours = sysStation.getBusinessHours();
        data.electricityFee = sysStation.getElectricityFee();
        data.serviceFee = sysStation.getServiceFee();
        data.parkFee = sysStation.getParkFee();
        data.payment = sysStation.getPayment();
        data.supportOrder = sysStation.getSupportOrder();
        data.remark = sysStation.getRemark();

        if(TextUtils.isBlank(sysStation.getPictures())){
            data.pictures = new ArrayList<>();
        }
        else{
            try{
                data.pictures = JsonUtils.parseArray(sysStation.getPictures(), String.class);
            }
            catch (Exception ex){
                data.pictures = new ArrayList<>();
            }
        }
        data.equipmentInfos = new ArrayList<>();
        return data;
    }
}
