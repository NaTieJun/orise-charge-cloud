package org.dromara.omind.userplat.service.hlht;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.request.*;
import org.dromara.omind.userplat.api.domain.response.QueryEquipAuthResponseData;
import org.dromara.omind.userplat.api.domain.response.QueryStartChargeResponseData;
import org.dromara.omind.userplat.api.domain.response.QueryStopChargeResponseData;


public interface HlhtInfoService {

    /**
     * 查询运营商的充电站的信息
     * 接口名称：query_stations_info
     * @return
     */
    int stationsInfo(QueryStationsInfoData queryStationsInfoData, OmindOperatorEntity odOperatorEntity) throws BaseException;

    /**
     * 设备接口状态查询
     * 接口名称：query_station_status
     * @return
     */
    int stationStatus(QueryStationStatusData queryStationStatusData, OmindOperatorEntity odOperatorEntity) throws BaseException;

    /**
     * 查询业务策略信息结果
     * 接口名称：query_equip_business_policy
     * @return
     */
    int equipBusinessPolicyQuery(QueryEquipBusinessPolicyData queryEquipBusinessPolicyData, OmindOperatorEntity odOperatorEntity) throws BaseException;

    /**
     * 请求设备认证
     * 接口名称：query_equip_auth
     * @return
     */
    QueryEquipAuthResponseData equipAuth(QueryEquipAuthData queryEquipAuthData) throws BaseException;

    /**
     * 请求启动充电
     * 接口名称：query_start_charge
     * @return
     */
    QueryStartChargeResponseData startCharge(QueryStartChargeData queryStartChargeData) throws BaseException;

    /**
     * 请求停止充电
     * 接口名称：query_stop_charge
     * @return
     */
    QueryStopChargeResponseData stopCharge(QueryStopChargeData queryStopChargeData) throws BaseException;

    /**
     * 查询充电状态
     * 接口名称：query_equip_charge_status
     * @return
     */
    int equipChargeStatus(QueryEquipChargeStatusData queryEquipChargeStatusData, OmindOperatorEntity odOperatorEntity) throws BaseException;

}
