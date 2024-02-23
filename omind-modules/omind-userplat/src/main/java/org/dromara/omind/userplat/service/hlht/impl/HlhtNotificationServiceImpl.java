package org.dromara.omind.userplat.service.hlht.impl;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.api.user.mq.producer.UpChargeOrderInfoProducer;
import org.dromara.omind.api.user.mq.producer.UpEquipChargeStatusProducer;
import org.dromara.omind.api.user.mq.producer.UpStationStatusProducer;
import org.dromara.omind.userplat.api.constant.HlhtRet;
import org.dromara.omind.userplat.api.domain.datas.ConnectorStatusInfoData;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationChargeOrderInfoData;
import org.dromara.omind.userplat.api.domain.dto.AsynNotificationEquipChargeStatusData;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.domain.notifications.*;
import org.dromara.omind.userplat.api.domain.request.QueryEquipBusinessPolicyData;
import org.dromara.omind.userplat.api.domain.response.*;
import org.dromara.omind.userplat.constant.PlatChargeBillStatusConstant;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.service.*;
import org.dromara.omind.userplat.service.hlht.HlhtNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HlhtNotificationServiceImpl implements HlhtNotificationService {

    @Autowired
    @Lazy
    HlhtNotificationService selfService;

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    @Autowired
    @Lazy
    OmindPriceService omindPriceService;

    @Autowired
    @Lazy
    OmindStationService omindStationService;

    @Autowired
    @Lazy
    LockTemplate lockTemplate;

    @Autowired
    @Lazy
    UpStationStatusProducer upStationStatusProducer;

    @Autowired
    @Lazy
    UpEquipChargeStatusProducer upEquipChargeStatusProducer;

    @Autowired
    @Lazy
    UpChargeOrderInfoProducer upChargeOrderInfoProducer;

    @Override
    public HlhtResult stationStatus(HlhtDto hlhtDto) throws BaseException {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationStationStatusData notificationStationStatusData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationStationStatusData.class);
        //获取请求数据
        log.info("推送设备状态变化信息--notification_stationStatus--NotificationStationStatusData==" + notificationStationStatusData.toString());

        ConnectorStatusInfoData connectorStatusInfo = notificationStationStatusData.getConnectorStatusInfo();
        String connectorId = connectorStatusInfo.getConnectorID();

        //加入队列
        upStationStatusProducer.sendMsg(notificationStationStatusData);

        //返回信息
        NotificationStationStatusResponseData responseData = new NotificationStationStatusResponseData();
        responseData.setStatus((short) 0);   //接受

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult startChargeResult(HlhtDto hlhtDto) {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationStartChargeResultData notificationStartChargeResultData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationStartChargeResultData.class);
        //获取请求数据
        log.info("推送启动充电结果信息--notification_start_charge_result--NotificationStartChargeResultData==" + notificationStartChargeResultData.toString());

        String startChargeSeq = notificationStartChargeResultData.getStartChargeSeq();
        Short startChargeSeqStat = notificationStartChargeResultData.getStartChargeSeqStat().shortValue();
        String startTime = notificationStartChargeResultData.getStartTime();
        String connectorId = notificationStartChargeResultData.getConnectorID();

        //返回信息
        NotificationStartChargeResultResponseData responseData = new NotificationStartChargeResultResponseData();
        responseData.setStartChargeSeq(startChargeSeq);

        /**
         * 解析启动失败码，上报警告
         */
        try {

            //业务处理
            /**********************业务处理 开始************************/
            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
            OmindBillEntity odBillEntity = omindBillService.get(startChargeSeq);

            boolean upFlag = false;
            if (odBillEntity != null) {
                OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                updateBillInfoObjData.setBillId(odBillEntity.getBillId());
                updateBillInfoObjData.setStartChargeSeq(odBillEntity.getStartChargeSeq());

                if (startTime != null) {
                    updateBillInfoObjData.setStartTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, startTime));
                }

                if ((odBillEntity.getStartChargeSeqStat() != PlatChargeBillStatusConstant.CHARGE_BILL_FINISH)) {
                    updateBillInfoObjData.setStartChargeSeqStat(startChargeSeqStat);
                }
                upFlag = omindBillService.updateBillInfo(updateBillInfoObjData);

            }

            /**********************业务处理 结束************************/
            //组装响应数据
            if (odConnectorEntity == null) {
                responseData.setSuccStat((short) 1);   //失败
                responseData.setFailReason((short) 1);   //接收失败
            } else {
                if (odBillEntity == null) {
                    responseData.setSuccStat((short) 1);   //失败
                    responseData.setFailReason((short) 1);   //接收失败
                } else {
                    if (upFlag) {
                        responseData.setSuccStat((short) 0);   //接受
                        responseData.setFailReason((short) 0);   //无
                    } else {
                        responseData.setSuccStat((short) 1);   //失败
                        responseData.setFailReason((short) 1);   //接收失败
                    }
                }
            }

        } catch (Exception ex) {
            log.error("notification-startChargeResult-error", ex);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        }

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult equipChargeStatus(HlhtDto hlhtDto) throws BaseException {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationEquipChargeStatusData notificationEquipChargeStatusData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationEquipChargeStatusData.class);
        //获取请求数据
        log.info("推送充电设备的充电状态--notification_equip_charge_status--NotificationEquipChargeStatusData==" + notificationEquipChargeStatusData.toString());

        String startChargeSeq = notificationEquipChargeStatusData.getStartChargeSeq();

        //加入队列
        AsynNotificationEquipChargeStatusData asynNotificationEquipChargeStatusData = new AsynNotificationEquipChargeStatusData();
        asynNotificationEquipChargeStatusData.setOperatorID(operatorId);
        asynNotificationEquipChargeStatusData.setEquipChargeStatusData(notificationEquipChargeStatusData);
        upEquipChargeStatusProducer.sendMsg(asynNotificationEquipChargeStatusData);

        //返回信息
        NotificationEquipChargeStatusResponseData responseData = new NotificationEquipChargeStatusResponseData();
        responseData.setStartChargeSeq(startChargeSeq);
        responseData.setSuccStat((short) 0);   //接受

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult stopChargeResult(HlhtDto hlhtDto) {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationStopChargeResultData notificationStopChargeResultData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationStopChargeResultData.class);
        //获取请求数据
        log.info("推送停止充电结果--notification_stop_charge_result--NotificationStopChargeResultData==" + notificationStopChargeResultData.toString());

        String startChargeSeq = notificationStopChargeResultData.getStartChargeSeq();
        Short startChargeSeqStat = notificationStopChargeResultData.getStartChargeSeqStat();
        Short succStat = notificationStopChargeResultData.getSuccStat();
        String connectorId = notificationStopChargeResultData.getConnectorID();
        Short failReason = notificationStopChargeResultData.getFailReason();

        //返回信息
        NotificationStartChargeResultResponseData responseData = new NotificationStartChargeResultResponseData();
        responseData.setStartChargeSeq(startChargeSeq);

        try {
            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);

            //更新充电订单信息表
            OmindBillEntity odBillEntity = omindBillService.get(startChargeSeq);
            log.info("推送停止充电结果--notification_stop_charge_result--odBillEntity=" + odBillEntity);
            boolean upFlag = false;
            if (odBillEntity != null) {
                OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                updateBillInfoObjData.setBillId(odBillEntity.getBillId());
                updateBillInfoObjData.setStartChargeSeq(odBillEntity.getStartChargeSeq());
                updateBillInfoObjData.setSuccStat(succStat);
                updateBillInfoObjData.setStopReason(failReason.intValue());
                //防止已经结束的订单被异常推送的状态更新
                if ((odBillEntity.getStartChargeSeqStat() != PlatChargeBillStatusConstant.CHARGE_BILL_FINISH)) {
                    updateBillInfoObjData.setStartChargeSeqStat(startChargeSeqStat);
                }
                log.info("推送停止充电结果--notification_stop_charge_result--updateBillInfoObjData=" + updateBillInfoObjData);
                upFlag = omindBillService.updateBillInfo(updateBillInfoObjData);
            }

            //组装响应数据
            if (odConnectorEntity == null) {
                responseData.setSuccStat((short) 1);   //失败
                responseData.setFailReason((short) 1);   //接收失败
            } else {
                if (odBillEntity == null) {
                    responseData.setSuccStat((short) 1);   //失败
                    responseData.setFailReason((short) 1);   //接收失败
                } else {
                    if (upFlag) {
                        responseData.setSuccStat((short) 0);   //接受
                        responseData.setFailReason((short) 0);   //无
                    } else {
                        responseData.setSuccStat((short) 1);   //失败
                        responseData.setFailReason((short) 1);   //接收失败
                    }
                }
            }
        } catch (BaseException ube) {
            log.error("notification-stopChargeResult-error", ube);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        } catch (Exception e) {
            log.error("notification-stopChargeResult-error", e);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        }

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult chargeOrderInfo(HlhtDto hlhtDto) throws BaseException {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationChargeOrderInfoData notificationChargeOrderInfoData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationChargeOrderInfoData.class);
        //获取请求数据
        log.info("推送充电订单信息--notification_charge_order_info--NotificationChargeOrderInfoData==" + notificationChargeOrderInfoData.toString());

        String startChargeSeq = notificationChargeOrderInfoData.getStartChargeSeq();
        String connectorId = notificationChargeOrderInfoData.getConnectorID();

        //加入队列
        AsynNotificationChargeOrderInfoData asynNotificationChargeOrderInfoData = new AsynNotificationChargeOrderInfoData();
        asynNotificationChargeOrderInfoData.setOperatorID(operatorId);
        asynNotificationChargeOrderInfoData.setChargeOrderInfoData(notificationChargeOrderInfoData);
        upChargeOrderInfoProducer.sendMsg(asynNotificationChargeOrderInfoData);

        //返回信息
        NotificationChargeOrderInfoResponseData responseData = new NotificationChargeOrderInfoResponseData();
        responseData.setStartChargeSeq(startChargeSeq);
        responseData.setConnectorID(connectorId);
        responseData.setConfirmResult((short) 0);   //成功

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult queryEquipBusinessPolicy(HlhtDto hlhtDto) throws BaseException {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        QueryEquipBusinessPolicyData queryEquipBusinessPolicyData = hlhtDto.getDataObj(odOperatorInfo,
                QueryEquipBusinessPolicyData.class);
        //获取请求数据
        log.info("查询业务策略信息结果--query_equip_business_policy--QueryEquipBusinessPolicyData==" + queryEquipBusinessPolicyData.toString());

        String connectorId = queryEquipBusinessPolicyData.getConnectorID();

        //返回信息
        QueryEquipBusinessPolicyResponseData responseData = new QueryEquipBusinessPolicyResponseData();
        responseData.setEquipBizSeq(queryEquipBusinessPolicyData.getEquipBizSeq());
        responseData.setConnectorID(connectorId);

        /**********************业务处理 开始************************/
        try {
            //查询价格策略
            List<PolicyInfoData> policyInfoList = omindPriceService.queryEquipPrice(connectorId);

            //组装响应数据
            if (policyInfoList == null || policyInfoList.size() == 0) {
                responseData.setPolicyInfos(policyInfoList);
                responseData.setFailReason((short) 1);
                responseData.setSumPeriod((short) 0);
                responseData.setSuccStat((short) 1);
            } else {
                responseData.setPolicyInfos(policyInfoList);
                responseData.setFailReason((short) 0);
                responseData.setSumPeriod((short) policyInfoList.size());
                responseData.setSuccStat((short) 0);
            }
        } catch (BaseException ube) {
            log.error("notification-queryEquipBusinessPolicy-error", ube);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        } catch (Exception e) {
            log.error("notification-queryEquipBusinessPolicy-error", e);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        }

        /**********************业务处理 结束************************/

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public HlhtResult stationFee(HlhtDto hlhtDto) throws BaseException {
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity odOperatorInfo = omindOperatorService.selectOperatorInfoById(operatorId);
        if (odOperatorInfo == null) {
            log.error("[互联互通]--运营商[" + operatorId + "]" + "不存在");
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(odOperatorInfo);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        //获取推送数据
        NotificationStationFeeOutData notificationStationFeeOutData = hlhtDto.getDataObj(odOperatorInfo,
                NotificationStationFeeOutData.class);

        NotificationStationFeeData notificationStationFeeData = notificationStationFeeOutData.getNotificationStationFeeData();
        String stationId = notificationStationFeeData.getStationID();

        //获取请求数据
        log.info("推送站点价格信息--notification_stationFee--NotificationStationFeeData==" + JsonUtils.toJsonString(notificationStationFeeData));
        if (TextUtils.isBlank(notificationStationFeeData.getStationID())) {
            log.info(JsonUtils.toJsonString(hlhtDto));
        }

        //返回信息
        NotificationStationFeeResponseData responseData = new NotificationStationFeeResponseData();

        //处理数据业务逻辑
        /**********************业务处理 开始************************/
        String lockKey = PlatRedisKey.NOTIFICATION_STATION_FEE_LOCK + stationId;
        //推送站点价格业务处理，上分布式同步锁
        final LockInfo lockInfo = lockTemplate.lock(lockKey, 5000L, 5000L, RedissonLockExecutor.class);
        if (null == lockInfo) {
            throw new RuntimeException("业务处理中,请稍后再试");
        }
        try {
            OmindStationEntity ldStationInfoEntity = omindStationService.get(stationId);
            //组装响应数据
            if (ldStationInfoEntity == null) {
                responseData.setStatus((short) 1);   //丢弃/忽略，不需要重试
            } else {
                omindStationService.stationFeeDeal(notificationStationFeeData);
                responseData.setStatus((short) 0);   //接受
            }
        } catch (BaseException ube) {
            log.error("notification-stationFee-error", ube);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        } catch (Exception e) {
            log.error("notification-stationFee-error", e);
            return HlhtResult.error(HlhtRet.ERROR_SYS, "系统错误");
        } finally {
            //解锁
            lockTemplate.releaseLock(lockInfo);
        }

        /**********************业务处理 结束************************/

        return selfService.notifacationResponse(responseData, odOperatorInfo, operatorId);
    }

    @Override
    public <T> HlhtResult notifacationResponse(T t, OmindOperatorEntity odOperatorInfo, String operatorId) {
        String responseJson = JsonUtils.toJsonString(t);
        HlhtResult result = new HlhtResult();
        String data = AESUtils.aes128CbcPKCS5Padding(odOperatorInfo.getDataSecret(), odOperatorInfo.getDataSecretIv(), responseJson);
        result.setData(data);
        result.setRet(HlhtRet.OK);
        result.setMsg("");

        String newData = result.getRet() + result.getMsg() + result.getData();
        result.setSig(HMacMD5Util.getHMacMD5(odOperatorInfo.getSigSecret(), "", newData, "", ""));

        return result;
    }
}
