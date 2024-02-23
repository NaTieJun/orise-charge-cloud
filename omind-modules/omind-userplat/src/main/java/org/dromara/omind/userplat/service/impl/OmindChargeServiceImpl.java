package org.dromara.omind.userplat.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.api.common.utils.IDWorker;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.request.QueryEquipAuthData;
import org.dromara.omind.userplat.api.domain.request.QueryStartChargeData;
import org.dromara.omind.userplat.api.domain.request.StartChargeRequest;
import org.dromara.omind.userplat.api.domain.response.*;
import org.dromara.omind.userplat.constant.PlatChargeBillStatusConstant;
import org.dromara.omind.userplat.service.*;
import org.dromara.omind.userplat.service.hlht.HlhtInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OmindChargeServiceImpl implements OmindChargeService {

    @Autowired
    @Lazy
    HlhtInfoService hlhtInfoService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    @Autowired
    @Lazy
    OmindPriceService omindPriceService;

    @Override
    public R auth(String operatorId, String connectorId) {
        if (TextUtils.isBlank(connectorId)) {
            throw new BaseException("设备号为空");
        }
        OmindConnectorEntity omindConnector = omindConnectorService.selectConnectorInfo(connectorId);
        if (omindConnector == null) {
            throw new BaseException("设备不存在");
        }
        QueryEquipAuthData queryEquipAuthData = new QueryEquipAuthData();
        IDWorker idWorker = new IDWorker(2, 3);
        queryEquipAuthData.setEquipAuthSeq(omindConnector.getBaseOperatorId() + idWorker.nextId());
        queryEquipAuthData.setConnectorID(connectorId);
        QueryEquipAuthResponseData queryEquipAuthResponseData = hlhtInfoService.equipAuth(queryEquipAuthData);
        if(queryEquipAuthResponseData != null){
            return R.ok(queryEquipAuthResponseData);
        }
        return R.fail("设备授权失败");
    }

    @Override
    public StartChargeResponseData startCharge(StartChargeRequest startChargeRequest, Long userId) {
        try {
            String connectorId = startChargeRequest.getConnectorId();
            String qrCode = connectorId;
            Short chargeType = startChargeRequest.getType();
            BigDecimal chargeMoney = startChargeRequest.getMoney() == null ? new BigDecimal("0.00") : startChargeRequest.getMoney();

            String plateNo = startChargeRequest.getPlateNo() == null ? "" : startChargeRequest.getPlateNo();
            log.info("mp-start----------mpStartChargeData=" + startChargeRequest);

            //获取充电设备编码信息
            OmindConnectorEntity odConnectorInfoEntity = omindConnectorService.selectConnectorInfo(connectorId);
            if (odConnectorInfoEntity == null) {
                throw new BaseException("该设备不存在");
            }

            //获取用户信息
            OmindUserEntity odUserEntity = omindUserService.getUserById(userId);
            if (odUserEntity == null) {
                throw new BaseException("该用户不存在");
            }

            //获取platType
            OmindOperatorEntity odOperatorInfoEntity = omindOperatorService.getOperatorInfo(odConnectorInfoEntity.getBaseOperatorId(), odConnectorInfoEntity.getUserOperatorId());
            if (odOperatorInfoEntity == null) {
                throw new BaseException("运营商:" + odConnectorInfoEntity.getBaseOperatorId() + "不存在");
            }

            //请求响应初始化
            StartChargeResponseData startChargeResponseData = new StartChargeResponseData();

            //封装设备认证data
            QueryEquipAuthData queryEquipAuthData = new QueryEquipAuthData();
            IDWorker idWorker = new IDWorker(2, 3);
            queryEquipAuthData.setEquipAuthSeq(odConnectorInfoEntity.getBaseOperatorId() + idWorker.nextId());
            queryEquipAuthData.setConnectorID(connectorId);

            //设备认证请求
            QueryEquipAuthResponseData queryEquipAuthResponseData = hlhtInfoService.equipAuth(queryEquipAuthData);
            log.info("mp-start----------authdata==" + queryEquipAuthResponseData);
            startChargeResponseData.setEquipAuthSuccStat(queryEquipAuthResponseData.getSuccStat());
            startChargeResponseData.setEquipAuthFailReason(queryEquipAuthResponseData.getFailReason());
            if (queryEquipAuthResponseData.getSuccStat() == 0) {//设备认证成功

                //判断充值金额是否合法---首先按停止充电阈值判断，然后按固定值1来判断
                if (chargeType != 1) {//当自定义充值金额时判断
                    if (chargeMoney.compareTo(new BigDecimal("1.00")) < 0) {
                        startChargeResponseData.setUserFailReason((short) 6);
                        startChargeResponseData.setMinChargeMoney(new BigDecimal("1.00"));
                        return startChargeResponseData;
                    }
                }

                //判断站点是否设置价格
                List<PolicyInfoData> policyInfoList = omindPriceService.queryEquipPrice(connectorId);
                log.info("mp-start----------policyInfoList=" + policyInfoList);
                if (policyInfoList == null || policyInfoList.size() == 0) {//站点未设置价格
                    startChargeResponseData.setUserFailReason((short) 5);
                    return startChargeResponseData;
                }

                //启动充电请求data封装
                QueryStartChargeData queryStartChargeData = new QueryStartChargeData();
                String startChargeSeq = odConnectorInfoEntity.getUserOperatorId() + idWorker.nextId();
                queryStartChargeData.setStartChargeSeq(startChargeSeq);
                queryStartChargeData.setConnectorID(connectorId);
                queryStartChargeData.setQrCode(qrCode);
                queryStartChargeData.setBalance(chargeMoney);
                queryStartChargeData.setPhoneNum(odUserEntity.getMobile());
                //添加车牌号
                queryStartChargeData.setPlateNum(plateNo);
                log.info("mp-start----------queryStartChargeData=" + queryStartChargeData);

                try {
                    //启动充电请求StartChargeSuccStat
                    QueryStartChargeResponseData queryStartChargeResponseData = hlhtInfoService.startCharge(queryStartChargeData);
                    log.info("mp-start----------startchargedata=" + queryStartChargeResponseData);
                    startChargeResponseData.setStartChargeSuccStat(queryStartChargeResponseData.getSuccStat());
                    startChargeResponseData.setStartChargeFailReason(queryStartChargeResponseData.getFailReason());
                    if (queryStartChargeResponseData.getSuccStat() == 0) {//请求启动充电成功

                        startChargeResponseData.setStartChargeSeq(queryStartChargeResponseData.getStartChargeSeq());

                        //充电订单信息
                        OmindBillEntity odBillInfoEntity = new OmindBillEntity();
                        odBillInfoEntity.setStationId(odConnectorInfoEntity.getStationId());
                        odBillInfoEntity.setBaseOperatorId(odConnectorInfoEntity.getBaseOperatorId());
                        odBillInfoEntity.setStartChargeSeq(startChargeSeq);
                        odBillInfoEntity.setConnectorId(connectorId);
                        odBillInfoEntity.setStartChargeSeqStat(queryStartChargeResponseData.getStartChargeSeqStat());
                        odBillInfoEntity.setUserId(odUserEntity.getUid());
                        //新增创建订单时赋予订单开始、结束时间--2023-08-04
                        Date curDateTime = new Date();
                        odBillInfoEntity.setStartTime(curDateTime);
                        odBillInfoEntity.setEndTime(curDateTime);
                        //添加车牌号
                        odBillInfoEntity.setPlateNo(plateNo);
                        odBillInfoEntity.setPayState((short) 0);
                        odBillInfoEntity.setPriceInfo(JsonUtils.toJsonString(policyInfoList));
                        odBillInfoEntity.setChargeType(chargeType);
                        odBillInfoEntity.setChargeMoney(chargeMoney);
                        odBillInfoEntity.setCreateTime(curDateTime);
                        log.info("mp-start----------odBillInfoEntity=" + odBillInfoEntity);
                        OmindBillEntity odBillInfo = omindBillService.insertBillInfo(odBillInfoEntity);

                        startChargeResponseData.setBillInfo(odBillInfo);
                    }
                } catch (BaseException ube) {
                    log.error("charge-mpStartChargeDeal-error", ube);
                    throw ube;
                } catch (Exception e) {
                    log.error("charge-mpStartChargeDeal-error", e);
                    throw e;
                }
            }
            return startChargeResponseData;
        } catch (BaseException ube) {
            log.error("charge-mpStartChargeDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("charge-mpStartChargeDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public StopChargeResponseData stopCharge(String startChargeSeq, Long userId) {
        try {

            StopChargeResponseData stopChargeResponseData = new StopChargeResponseData();

            //更新充电订单信息
            OmindBillEntity odBillInfoEntity = omindBillService.get(startChargeSeq);
            if(odBillInfoEntity == null){
                throw new BaseException("订单不存在");
            }else if(odBillInfoEntity.getStartChargeSeqStat() == PlatChargeBillStatusConstant.CHARGE_BILL_FINISH){
                throw new BaseException("订单已结束");
            }
            String connectorId = odBillInfoEntity.getConnectorId();

            //获取充电设备编码信息
            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
            if (odConnectorEntity == null) {
                throw new BaseException("该设备不存在");
            }

            //获取用户信息
            OmindUserEntity odUserEntity = omindUserService.getUserById(userId);
            if (odUserEntity == null) {
                throw new BaseException("该用户不存在");
            }

            //停止充电请求处理
            QueryStopChargeResponseData queryStopChargeResponseData = omindBillService.billStopChargeDeal(startChargeSeq, connectorId, odBillInfoEntity.getBillId());

            stopChargeResponseData.setSuccStat(queryStopChargeResponseData.getSuccStat());
            stopChargeResponseData.setFailReason(queryStopChargeResponseData.getFailReason());

            return stopChargeResponseData;
        } catch (BaseException ube) {
            log.error("charge-mpStopChargeDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("charge-mpStopChargeDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public TableDataInfo chargeOrderList(ChargeOrderListRequest request, Long uid, PageQuery pageQuery) {
        return null;
    }

    @Override
    public OmindBillEntity chargeOrderInfo(String startChargeSeq) {
        return null;
    }
}
