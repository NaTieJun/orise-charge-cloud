package org.dromara.omind.userplat.service.hlht.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.SeqUtil;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.userplat.api.constant.HlhtRedisKey;
import org.dromara.omind.userplat.api.domain.datas.*;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.api.domain.entity.*;
import org.dromara.omind.userplat.api.domain.request.*;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.response.QueryEquipAuthResponseData;
import org.dromara.omind.userplat.api.domain.response.QueryStartChargeResponseData;
import org.dromara.omind.userplat.api.domain.response.QueryStopChargeResponseData;
import org.dromara.omind.userplat.client.BPlatAuthV1Client;
import org.dromara.omind.userplat.client.BPlatChargeV1Client;
import org.dromara.omind.userplat.client.BPlatStationV1Client;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.service.*;
import org.dromara.omind.userplat.service.hlht.HlhtInfoService;
import org.dromara.omind.userplat.service.hlht.HlhtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class HlhtInfoServiceImpl implements HlhtInfoService {

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @Resource
    BPlatAuthV1Client bPlatAuthV1Client;

    @Autowired
    @Lazy
    HlhtTokenService hlhtTokenService;

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    @Autowired
    @Lazy
    OmindStationService omindStationService;

    @Autowired
    @Lazy
    OmindEquipmentService omindEquipmentService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Resource
    BPlatStationV1Client bPlatStationV1Client;

    @Resource
    BPlatChargeV1Client bPlatChargeV1Client;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int stationsInfo(QueryStationsInfoData queryStationsInfoData, OmindOperatorEntity odOperatorEntity) throws BaseException {
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JsonUtils.toJsonString(queryStationsInfoData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        //获取token
        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setOperatorID(userOperatorId);
        hlhtDtoData.setSig(sig);

        HlhtResult stationInfoResult = bPlatStationV1Client.stationsInfo(url, token, hlhtDtoData);
        //记录请求日志
        log.info("充电站信息--query_stations_info--undecrypt==" + stationInfoResult);
        int pageCount = 0;
        if (stationInfoResult == null) {
            return 0;
        }
        if (stationInfoResult.getRet() == 0) {
            try {
                String responseData = AESUtils.decrypt(stationInfoResult.getData(), odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv());
                //记录请求日志
                log.info("充电站信息--query_stations_info--decrypt==" + responseData);
                JSONObject responseDataObj = JSONObject.parseObject(responseData);
                pageCount = responseDataObj.getShort("PageCount");
                int pageNo = responseDataObj.getShort("PageNo");
                String stationInfos = responseDataObj.getString("StationInfos");

                //定义多个数组进行批量db处理
                List<OmindStationEntity> existStationList = new ArrayList<>();
                List<OmindStationEntity> unExistStationList = new ArrayList<>();
                List<OmindEquipmentEntity> existEquipmentList = new ArrayList<>();
                List<OmindEquipmentEntity> unExistEquipmentList = new ArrayList<>();
                List<OmindConnectorEntity> existConnectorList = new ArrayList<>();
                List<OmindConnectorEntity> unExistConnectorList = new ArrayList<>();

                List<StationInfoData> stationList = JSON.parseArray(stationInfos, StationInfoData.class);
                for (StationInfoData stationInfoData : stationList) {
                    try {
                        if (stationInfoData.getStationID() == null || TextUtils.isBlank(stationInfoData.getStationID())) {
                            continue;
                        }
                        OmindStationEntity odStationEntity = new OmindStationEntity();
                        odStationEntity.setStationId(stationInfoData.getStationID());
                        odStationEntity.setOperatorId(stationInfoData.getOperatorID());
                        odStationEntity.setUserOperatorId(userOperatorId);
                        odStationEntity.setBaseOperatorId(odOperatorEntity.getOperatorId());
                        odStationEntity.setEquipmentOwnerId(stationInfoData.getEquipmentOwnerID());
                        odStationEntity.setStationName(stationInfoData.getStationName());
                        odStationEntity.setCountryCode(stationInfoData.getCountryCode());
                        odStationEntity.setAreaCode(stationInfoData.getAreaCode());
                        odStationEntity.setAddress(stationInfoData.getAddress());
                        odStationEntity.setStationTel(stationInfoData.getStationTel());
                        odStationEntity.setServiceTel(stationInfoData.getServiceTel());
                        odStationEntity.setStationStatus(stationInfoData.getStationStatus());
                        odStationEntity.setParkNums(stationInfoData.getParkNums().shortValue());
                        odStationEntity.setStationLng(stationInfoData.getStationLng());
                        odStationEntity.setStationLat(stationInfoData.getStationLat());
                        odStationEntity.setSiteGuide(stationInfoData.getSiteGuide());
                        odStationEntity.setConstruction(stationInfoData.getConstruction().shortValue());
                        odStationEntity.setPictures(JSONArray.toJSONString(stationInfoData.getPictures()));
                        odStationEntity.setMatchCars(stationInfoData.getMatchCars());
                        odStationEntity.setParkInfo(stationInfoData.getParkInfo());
                        odStationEntity.setBusineHours(stationInfoData.getBusineHours());
                        odStationEntity.setElectricityFee(stationInfoData.getElectricityFee());
                        odStationEntity.setServiceFee(stationInfoData.getServiceFee());
                        odStationEntity.setParkFee(stationInfoData.getParkFee());
                        odStationEntity.setPayment(stationInfoData.getPayment());
                        odStationEntity.setSupportOrder(stationInfoData.getSupportOrder());
                        odStationEntity.setStationType(stationInfoData.getStationType().shortValue());
                        odStationEntity.setRemark(stationInfoData.getRemark());
                        odStationEntity.setCreateTime(new Date());

                        //清除站点相关缓存
                        omindStationService.stationCacheDel(stationInfoData.getStationID());

                        OmindStationEntity odStationCheck = omindStationService.get(stationInfoData.getStationID());
                        if (odStationCheck == null) {
                            odStationEntity.setPlatType(platType);
                            //加入数组
                            unExistStationList.add(odStationEntity);

                        } else {
                            //加入数组
                            odStationEntity.setId(odStationCheck.getId());
                            existStationList.add(odStationEntity);
                        }

                        List<EquipmentInfoData> equipmentList = stationInfoData.getEquipmentInfos();
                        for (EquipmentInfoData equipmentInfoData : equipmentList) {
                            if (equipmentInfoData.getEquipmentID() == null || TextUtils.isBlank(equipmentInfoData.getEquipmentID())) {
                                continue;
                            }
                            OmindEquipmentEntity odEquipmentEntity = new OmindEquipmentEntity();
                            odEquipmentEntity.setStationId(stationInfoData.getStationID());
                            odEquipmentEntity.setOperatorId(stationInfoData.getOperatorID());
                            odEquipmentEntity.setUserOperatorId(userOperatorId);
                            odEquipmentEntity.setBaseOperatorId(odOperatorEntity.getOperatorId());
                            odEquipmentEntity.setEquipmentId(equipmentInfoData.getEquipmentID());
                            odEquipmentEntity.setEquipmentName(equipmentInfoData.getEquipmentName());
                            odEquipmentEntity.setManufacturerId(equipmentInfoData.getManufacturerID());
                            odEquipmentEntity.setManufacturerName("");
                            odEquipmentEntity.setEquipmentModel(equipmentInfoData.getEquipmentModel());
                            odEquipmentEntity.setProductionDate(equipmentInfoData.getProductionDate());
                            odEquipmentEntity.setEquipmentType(equipmentInfoData.getEquipmentType());
                            odEquipmentEntity.setPower(equipmentInfoData.getPower());
                            odEquipmentEntity.setCreateTime(new Date());

                            //清除设备信息缓存
                            String key = PlatRedisKey.EQUIPMENT_INFO + equipmentInfoData.getEquipmentID();
                            RedisUtils.deleteObject(key);
                            String equipmentKey = PlatRedisKey.REALTIME_EQUIPMENT_DATA + equipmentInfoData.getEquipmentID();
                            if (RedisUtils.hasKey(equipmentKey)) {
                                RedisUtils.deleteObject(equipmentKey);
                            }

                            OmindEquipmentEntity odEquipmentCheck = omindEquipmentService.get(equipmentInfoData.getEquipmentID());
                            if (odEquipmentCheck == null) {

                                //加入数组
                                unExistEquipmentList.add(odEquipmentEntity);

                            } else {
                                //加入数组
                                odEquipmentEntity.setId(odEquipmentCheck.getId());
                                existEquipmentList.add(odEquipmentEntity);
                            }

                            List<ConnectorInfoData> conectorList = equipmentInfoData.getConnectorInfos();

                            for (ConnectorInfoData connectorInfoData : conectorList) {
                                if (connectorInfoData.getConnectorID() == null || TextUtils.isBlank(connectorInfoData.getConnectorID())) {
                                    continue;
                                }
                                OmindConnectorEntity odConnectorEntity = new OmindConnectorEntity();
                                odConnectorEntity.setStationId(stationInfoData.getStationID());
                                odConnectorEntity.setOperatorId(stationInfoData.getOperatorID());
                                odConnectorEntity.setUserOperatorId(userOperatorId);
                                odConnectorEntity.setBaseOperatorId(odOperatorEntity.getOperatorId());
                                odConnectorEntity.setEquipmentId(equipmentInfoData.getEquipmentID());
                                odConnectorEntity.setConnectorId(connectorInfoData.getConnectorID());
                                odConnectorEntity.setConnectorName(connectorInfoData.getConnectorName());
                                odConnectorEntity.setConnectorType(connectorInfoData.getConnectorType());
                                odConnectorEntity.setVoltageUpperLimits(connectorInfoData.getVoltageUpperLimits());
                                odConnectorEntity.setVoltageLowerLimits(connectorInfoData.getVoltageLowerLimits());
                                odConnectorEntity.setCurrentValue(connectorInfoData.getCurrent());
                                odConnectorEntity.setPower(connectorInfoData.getPower());
                                odConnectorEntity.setNationalStandard(connectorInfoData.getNationalStandard().shortValue());
                                odConnectorEntity.setCreateTime(new Date());

                                //清除设备编码信息缓存
                                String connectorKey = PlatRedisKey.REALTIME_CONNECTOR_DATA + connectorInfoData.getConnectorID();
                                if (RedisUtils.hasKey(connectorKey)) {
                                    RedisUtils.deleteObject(connectorKey);
                                }

                                OmindConnectorEntity odConnectorCheck = omindConnectorService.selectConnectorInfo(connectorInfoData.getConnectorID());
                                if (odConnectorCheck == null) {

                                    //加入数组
                                    unExistConnectorList.add(odConnectorEntity);

                                } else {
                                    //加入数组
                                    odConnectorEntity.setId(odConnectorCheck.getId());
                                    existConnectorList.add(odConnectorEntity);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("hlht-stationsInfo-error", ex);
                    }
                }
                //数据批量处理
                omindStationService.batchInsertStation(unExistStationList);
                omindStationService.batchUpdateStation(existStationList);
                omindEquipmentService.batchInsertEquipment(unExistEquipmentList);
                omindEquipmentService.batchUpdateEquipment(existEquipmentList);
                omindConnectorService.batchInsertConnector(unExistConnectorList);
                omindConnectorService.batchUpdateConnector(existConnectorList);
            } catch (BaseException ube) {
                log.error("hlht-stationsInfo-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-stationsInfo-error", e);
                throw new BaseException("数据解析失败");
            }
        } else if (stationInfoResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("充电站信息--query_stations_info--errInfo==" + stationInfoResult.getRet());
        }

        return pageCount;
    }

    @Override
    public int stationStatus(QueryStationStatusData queryStationStatusData, OmindOperatorEntity odOperatorEntity) throws BaseException {
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JsonUtils.toJsonString(queryStationStatusData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult stationStatusResult = bPlatStationV1Client.stationStatus(url, token, hlhtDtoData);

        //记录请求日志
        log.info("设备接口状态查询--query_station_status--undecrypt==" + stationStatusResult);

        if (stationStatusResult.getRet() == 0) {
            try {
                String stationStatusData = AESUtils.decrypt(stationStatusResult.getData(), odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv());
                //记录请求日志
                log.info("设备接口状态查询--query_station_status--decrypt==" + stationStatusData);
                JSONObject stationStatusDataObj = JSONObject.parseObject(stationStatusData);

                //解析数据
                Short total = stationStatusDataObj.getShort("Total");
                String stationStatusInfos = stationStatusDataObj.getString("StationStatusInfos");
                List<StationStatusInfoData> stationStatusInfoList = JSON.parseArray(stationStatusInfos, StationStatusInfoData.class);

                for (StationStatusInfoData stationStatusInfoData : stationStatusInfoList) {

                    List<ConnectorStatusInfoData> connectorList = stationStatusInfoData.getConnectorStatusInfos();
                    int connectorCount = connectorList.size();
                    if (connectorCount > 0) {
                        List<OmindConnectorEntity> connectorInfoList = new ArrayList<>();
                        for (ConnectorStatusInfoData connectorStatusInfoData : connectorList) {
                            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorStatusInfoData.getConnectorID());
                            if (odConnectorEntity != null) {

                                OmindConnectorEntity updateConnectorDataObj = new OmindConnectorEntity();
                                updateConnectorDataObj.setId(odConnectorEntity.getId());
                                updateConnectorDataObj.setStatus(connectorStatusInfoData.getStatus().shortValue());
                                updateConnectorDataObj.setParkStatus(connectorStatusInfoData.getParkStatus());
                                updateConnectorDataObj.setLockStatus(connectorStatusInfoData.getLockStatus());

                                connectorInfoList.add(updateConnectorDataObj);
                            }
                        }
                        if (connectorInfoList.size() > 0) {
                            omindConnectorService.batchUpdateConnector(connectorInfoList);
                        }
                    }
                }
            } catch (BaseException ube) {
                log.error("hlht-stationStatus-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-stationStatus-error", e);
                throw new BaseException("数据解析失败:" + e.toString());
            }
        } else if (stationStatusResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("设备接口状态查询--query_station_status--errInfo==" + stationStatusResult.getRet());
        }
        return 0;
    }

    @Override
    public int equipBusinessPolicyQuery(QueryEquipBusinessPolicyData queryEquipBusinessPolicyData, OmindOperatorEntity odOperatorEntity) throws BaseException {
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JSON.toJSONString(queryEquipBusinessPolicyData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult busiPolicyResult = bPlatAuthV1Client.equipBusinessPolicyQuery(url, token, hlhtDtoData);

        //记录请求日志
        log.info("查询业务策略信息结果--query_equip_business_policy--undecrypt==" + busiPolicyResult);

        if (busiPolicyResult.getRet() == 0) {
            try {
                String busiPolicyData = AESUtils.decrypt(busiPolicyResult.getData(), odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv());
                //记录请求日志
                log.info("查询业务策略信息结果--query_equip_business_policy--decrypt==" + busiPolicyData);
                JSONObject busiPolicyDataObj = JSONObject.parseObject(busiPolicyData);

                String connectorId = busiPolicyDataObj.getString("ConnectorID");
                String policyInfos = busiPolicyDataObj.getString("PolicyInfos");
                List<PolicyInfoData> policyInfoList = JSON.parseArray(policyInfos, PolicyInfoData.class);
                log.info("查询业务策略信息结果--query_equip_business_policy--policyInfoList==" + policyInfoList);

            } catch (BaseException ube) {
                log.error("hlht-equipBusinessPolicyQuery-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-equipBusinessPolicyQuery-error", e);
                throw new BaseException("数据解析失败");
            }
        } else if (busiPolicyResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("查询业务策略信息结果--query_equip_business_policy--errInfo==" + busiPolicyResult.getRet());
        }
        return 0;
    }

    @Override
    public QueryEquipAuthResponseData equipAuth(QueryEquipAuthData queryEquipAuthData) throws BaseException {
        String connectorID = queryEquipAuthData.getConnectorID();
        if (TextUtils.isBlank(connectorID)) {
            throw new BaseException("设备id为空");
        }
        OmindConnectorEntity odConnectorInfo = omindConnectorService.selectConnectorInfo(connectorID);
        if (odConnectorInfo == null) {
            throw new BaseException("设备id不存在");
        }
        OmindOperatorEntity odOperatorEntity = omindOperatorService.selectOperatorInfoById(odConnectorInfo.getBaseOperatorId());
        if (odOperatorEntity == null) {
            throw new BaseException("设备运营商不存在");
        }
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String aesStr = JsonUtils.toJsonString(queryEquipAuthData);
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), aesStr);
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult authResult = bPlatAuthV1Client.equipAuth(url, token, hlhtDtoData);
        //记录请求日志
        log.info("请求设备认证--query_equip_auth--undecrypt==" + authResult);
        QueryEquipAuthResponseData queryEquipAuthResponseData = null;

        if (authResult.getRet() == 0) {
            try {
                queryEquipAuthResponseData = authResult.getDataObj(odOperatorEntity, QueryEquipAuthResponseData.class);
                log.info("请求设备认证--query_equip_auth--decrypt==" + queryEquipAuthResponseData);
            } catch (BaseException ube) {
                log.error("hlht-equipAuth-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-equipAuth-error", e);
                throw new BaseException("数据解析失败");
            }
        } else if (authResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("请求设备认证--query_equip_auth--errInfo==" + authResult.getRet());
        }

        return queryEquipAuthResponseData;
    }

    @Override
    public QueryStartChargeResponseData startCharge(QueryStartChargeData queryStartChargeData) throws BaseException {
        String connectorID = queryStartChargeData.getConnectorID();
        if (TextUtils.isBlank(connectorID)) {
            throw new BaseException("设备id为空");
        }
        OmindConnectorEntity odConnectorInfo = omindConnectorService.selectConnectorInfo(connectorID);
        if (odConnectorInfo == null) {
            throw new BaseException("设备id不存在");
        }
        OmindOperatorEntity odOperatorEntity = omindOperatorService.selectOperatorInfoById(odConnectorInfo.getBaseOperatorId());
        if (odOperatorEntity == null) {
            throw new BaseException("设备运营商不存在");
        }
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JsonUtils.toJsonString(queryStartChargeData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult startChargeResult = bPlatAuthV1Client.startCharge(url, token, hlhtDtoData);
        //记录请求日志
        log.info("请求启动充电--query_start_charge--undecrypt==" + startChargeResult);
        QueryStartChargeResponseData queryStartChargeResponseData = null;
        if (startChargeResult.getRet() == 0) {
            try {
                queryStartChargeResponseData = startChargeResult.getDataObj(odOperatorEntity, QueryStartChargeResponseData.class);
                log.info("请求启动充电--query_start_charge--decrypt==" + queryStartChargeResponseData);
            } catch (BaseException ube) {
                log.error("hlht-startCharge-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-startCharge-error", e);
                throw new BaseException("数据解析失败");
            }
        } else if (startChargeResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("请求设备认证--query_equip_auth--errInfo==" + startChargeResult.getRet());
        }

        return queryStartChargeResponseData;
    }

    @Override
    public QueryStopChargeResponseData stopCharge(QueryStopChargeData queryStopChargeData) throws BaseException {
        String connectorID = queryStopChargeData.getConnectorID();
        if (TextUtils.isBlank(connectorID)) {
            throw new BaseException("设备id为空");
        }
        OmindConnectorEntity odConnectorInfo = omindConnectorService.selectConnectorInfo(connectorID);
        if (odConnectorInfo == null) {
            throw new BaseException("设备id不存在");
        }
        OmindOperatorEntity odOperatorEntity = omindOperatorService.selectOperatorInfoById(odConnectorInfo.getBaseOperatorId());
        if (odOperatorEntity == null) {
            throw new BaseException("设备运营商不存在");
        }
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JsonUtils.toJsonString(queryStopChargeData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult stopChargeResult = bPlatAuthV1Client.stopCharge(url, token, hlhtDtoData);

        //记录请求日志
        log.info("请求停止充电--query_stop_charge--undecrypt==" + stopChargeResult);

        QueryStopChargeResponseData queryStopChargeResponseData = null;
        if (stopChargeResult.getRet() == 0) {
            try {
                queryStopChargeResponseData = stopChargeResult.getDataObj(odOperatorEntity, QueryStopChargeResponseData.class);
                log.info("请求停止充电--query_stop_charge--decrypt==" + queryStopChargeResponseData);

                //更新充电订单信息中的是否请求充电标识
                OmindBillEntity odBillEntity = omindBillService.get(queryStopChargeData.getStartChargeSeq());
                if (odBillEntity != null) {
                    OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                    updateBillInfoObjData.setBillId(odBillEntity.getBillId());
                    updateBillInfoObjData.setStartChargeSeq(odBillEntity.getStartChargeSeq());
                    updateBillInfoObjData.setStartChargeSeqStat((short) 3);
                    omindBillService.updateBillInfo(updateBillInfoObjData);
                }

            } catch (BaseException ube) {
                log.error("hlht-stopCharge-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-stopCharge-error", e);
                throw new BaseException("数据解析失败");
            }
        } else if (stopChargeResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("请求停止充电--query_stop_charge==" + stopChargeResult.getRet());
        }
        return queryStopChargeResponseData;
    }

    @Override
    public int equipChargeStatus(QueryEquipChargeStatusData queryEquipChargeStatusData, OmindOperatorEntity odOperatorEntity) throws BaseException {
        String userOperatorId = odOperatorEntity.getUserOperatorId();
        short platType = odOperatorEntity.getPlatType();
        String url = odOperatorEntity.getApiUrl();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv(), JsonUtils.toJsonString(queryEquipChargeStatusData));
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = HMacMD5Util.getHMacMD5(odOperatorEntity.getBaseSigSecret(), userOperatorId, data, timeStamp, seq);

        String token = hlhtTokenService.checkToken(userOperatorId, platType);

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setSig(sig);
        hlhtDtoData.setOperatorID(userOperatorId);

        HlhtResult chargeStatusResult = bPlatChargeV1Client.equipChargeStatus(url, token, hlhtDtoData);

        //记录请求日志
        log.info("查询充电状态--query_equip_charge_status--undecrypt==" + chargeStatusResult);

        if (chargeStatusResult.getRet() == 0) {
            try {
                String chargeStatusData = AESUtils.decrypt(chargeStatusResult.getData(), odOperatorEntity.getBaseDataSecret(), odOperatorEntity.getBaseDataSecretIv());
                //记录请求日志
                log.info("查询充电状态--query_equip_charge_status--decrypt==" + chargeStatusData);
                JSONObject chargeStatusDataObj = JSONObject.parseObject(chargeStatusData);

                //解析数据
                OmindBillEntity omindBill = omindBillService.get(chargeStatusDataObj.getString("StartChargeSeq"));
                if(omindBill != null){
                    OmindBillEntity updateBillDataObj = new OmindBillEntity();
                    updateBillDataObj.setBillId(omindBill.getBillId());
                    updateBillDataObj.setCurrentA(chargeStatusDataObj.getBigDecimal("CurrentA"));
                    updateBillDataObj.setVoltageA(chargeStatusDataObj.getBigDecimal("VoltageA"));
                    omindBillService.updateBillInfo(updateBillDataObj);
                }

            } catch (BaseException ube) {
                log.error("hlht-equipChargeStatus-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("hlht-equipChargeStatus-error", e);
                throw new BaseException("数据解析失败");
            }

        } else if (chargeStatusResult.getRet() == 4002) {
            //处理4002  清除redis中token
            String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
            RedisUtils.deleteObject(userTokenKey);
        } else {
            log.info("查询充电状态--query_equip_charge_status==" + chargeStatusResult.getRet());
        }
        return 0;
    }
}
