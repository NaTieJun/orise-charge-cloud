package org.dromara.omind.baseplat.controller.hlht;

import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatsData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatusData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationsInfoData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatsResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatusResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationsInfoResponseData;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name = "EvcsInfomationV1Controller", description = "[V1]互联互通——公共信息对象接口V1")
@RestController
@RequestMapping("/evcs/v1/")
public class EvcsInfomationV1Controller {

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    SysStationService stationService;

    @Operation(summary = "[V1]基础设施充电站点信息")
    @PostMapping("query_stations_info")
    public HlhtResult queryStationsInfo(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryStationsInfoData queryStationsInfoData = hlhtDto.getDataObj(sysOperator, QueryStationsInfoData.class);
        log.info("【hlht-基础设施充电站点信息】" + JSON.toJSONString(queryStationsInfoData));
        if(queryStationsInfoData == null){
            queryStationsInfoData = new QueryStationsInfoData();
        }
        if(queryStationsInfoData.getPageNo() == null || queryStationsInfoData.getPageNo() <= 0){
            queryStationsInfoData.setPageNo(1);
        }
        if(queryStationsInfoData.getPageSize() == null || queryStationsInfoData.getPageSize() <= 0){
            queryStationsInfoData.setPageSize(20);
        }

        QueryStationsInfoResponseData responseData = stationService.getStationPageList(sysOperator, queryStationsInfoData);

        HlhtResult hlhtResult = new HlhtResult();
        hlhtResult.setRet(HlhtRet.OK);
        hlhtResult.setMsg("");
        String jsonStr = JsonUtils.toJsonString(responseData);
        hlhtResult.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), jsonStr));
        hlhtResult.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), hlhtResult.getRet() + hlhtResult.getMsg() + hlhtResult.getData()));
        return hlhtResult;
    }

    @Operation(summary = "[V1]设备接口状态查询")
    @PostMapping("query_station_status")
    public HlhtResult queryStationStatus(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryStationStatusData queryStationStatusData = hlhtDto.getDataObj(sysOperator, QueryStationStatusData.class);
        if(queryStationStatusData.getStationIDs() == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-无有效充电站ID");
        }
        QueryStationStatusResponseData responseData = stationService.getStationStatusInfos(queryStationStatusData);
        HlhtResult hlhtResult = new HlhtResult();
        hlhtResult.setRet(HlhtRet.OK);
        hlhtResult.setMsg("");
        String jsonStr = JsonUtils.toJsonString(responseData);
        hlhtResult.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), jsonStr));
        hlhtResult.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), hlhtResult.getRet() + hlhtResult.getMsg() + hlhtResult.getData()));
        return hlhtResult;
    }

    @Operation(summary = "[V1]查询统计信息（考虑性能，时间跨度不超过7天）")
    @PostMapping("query_station_stats")
    public HlhtResult queryStationStats(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryStationStatsData queryStationStatsData = hlhtDto.getDataObj(sysOperator, QueryStationStatsData.class);
        log.info("【查询统计信息】operatorID =" + operatorId + " REQ:" + JSON.toJSONString(queryStationStatsData));
        if(TextUtils.isBlank(queryStationStatsData.getStationID())){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-充电站ID");
        }
        if (TextUtils.isBlank(queryStationStatsData.getStartTime()) || TextUtils.isBlank(queryStationStatsData.getEndTime())) {
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-时间范围无效");
        }
        try{
            long start = DateUtils.getMillionSceondsBydate(queryStationStatsData.getStartTime());
            long end = DateUtils.getMillionSceondsBydate(queryStationStatsData.getEndTime());
            if(start > end){
                return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-开始时间大于结束时间");
            }
            if((end - start) > (7*24*3600*1000L)){
                return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-时间范围不能超过7天");
            }
        }
        catch (Exception ex){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-时间范围无效");
        }
        QueryStationStatsResponseData responseData = stationService.getStationStatsInfos(queryStationStatsData);
        log.info("【查询统计信息】operatorID =" + operatorId + " RESPONSE:" + JSON.toJSONString(responseData));
        HlhtResult hlhtResult = new HlhtResult();
        hlhtResult.setRet(HlhtRet.OK);
        hlhtResult.setMsg("");
        String jsonStr = JsonUtils.toJsonString(responseData);
        hlhtResult.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), jsonStr));
        hlhtResult.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), hlhtResult.getRet() + hlhtResult.getMsg() + hlhtResult.getData()));
        return hlhtResult;
    }
}
