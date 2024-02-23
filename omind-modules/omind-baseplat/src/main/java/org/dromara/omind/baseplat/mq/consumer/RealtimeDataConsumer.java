package org.dromara.omind.baseplat.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.entity.PlatConnectorRealtimeData;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyChargingStatusService;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyConnectorStatusService;
import org.dromara.omind.baseplat.service.pile.PlatConnectorRealtimeDataService;
import org.dromara.omind.baseplat.service.SysChargeOrderService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.mq.api.producer.PriceSendProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

@Log4j2
@Component
public class RealtimeDataConsumer {

    /**
     * 充电订单每xx处理一次
     */
    private long CHARGING_ORDER_NOTIFY_DURANT = 15;

    @Autowired
    @Lazy
    RealtimeDataConsumer selfService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Autowired
    PriceSendProducer priceSendProducer;

    @Autowired
    LockTemplate lockTemplate;

    @Autowired
    RemoteNotifyConnectorStatusService remoteNotifyConnectorStatusService;

    @Autowired
    RemoteNotifyChargingStatusService remoteNotifyChargingStatusService;

    @Autowired
    PlatConnectorRealtimeDataService platConnectorRealtimeDataService;

    @Bean
    Consumer<List<PlatConnectorRealtimeData>> realtimeData()
    {
        log.info("RealtimeDataConsumer-初始化订阅");
        return realtimeDataList -> {
            log.info("RealtimeDataConsumer 消息接收成功=>" + JSON.toJSONString(realtimeDataList));
            selfService.dealWithData(realtimeDataList);
        };
    }

    void dealWithData(List<PlatConnectorRealtimeData> platConnectorRealtimeDataList)
    {
        long durantTm = System.currentTimeMillis();
        log.info("[MQ_AIO_GUN_REALTIME_DATA]batch task start");
        List<PlatConnectorRealtimeData> realtimeDataSaveList = new ArrayList<>();
        Map<String, SysConnector> connectorUpMap = new HashMap<>();
        Map<String, SysChargeOrder> chargingOrderMap = new HashMap<>();
        for(PlatConnectorRealtimeData platConnectorRealtimeData : platConnectorRealtimeDataList) {
            try {
                /**
                 * 更新数据库
                 * 推送设备状态改变
                 * 推送充电状态
                 */
                if (platConnectorRealtimeData == null) {
                    return;
                }
                //是否向用户平台发送通知，即是否需要更新SysConnector
                boolean isNotify = false;
                //是否需要更新充电订单信息
                boolean isCharging = false;

                //先存入数据库，会自动接入缓存，实时数据不用刷缓存
                SysChargeOrder sysChargeOrder = null;
                SysConnector sysConnector = null;
                SysConnector updateConnector = new SysConnector();
                sysConnector = connectorService.getConnectorById(platConnectorRealtimeData.getConnectorId());
                if (sysConnector == null) {
                    return;
                }
                updateConnector.setId(sysConnector.getId());
                updateConnector.setConnectorId(sysConnector.getConnectorId());
                updateConnector.setPingTm(sysConnector.getPingTm());
                //如果不全是0
                if (!TextUtils.isBlank(platConnectorRealtimeData.getTradeNo())
                    && !platConnectorRealtimeData.getTradeNo().matches("^[0]+$")
                    && platConnectorRealtimeData.getState() == 3) {
                    log.info("[MQ_AIO_GUN_REALTIME_DATA][rabbitmq]=》chargingMsg，tradeNo：" + platConnectorRealtimeData.getTradeNo());
                    //含充电数据
                    isCharging = true;
                    sysChargeOrder = chargeOrderService.getChargeOrderByTradeNo(platConnectorRealtimeData.getTradeNo());
                    if (sysChargeOrder != null && sysChargeOrder.getStartChargeSeqStat() != 4) {
                        sysChargeOrder.setConnectorStatus(3);
                        sysChargeOrder.setCurrentA(platConnectorRealtimeData.getOutCurrent());
                        sysChargeOrder.setVoltageA(platConnectorRealtimeData.getOutVoltage());
                        sysChargeOrder.setSoc(platConnectorRealtimeData.getSoc());
                        sysChargeOrder.setEndTime(new Date());
                        sysChargeOrder.setElecMoney(platConnectorRealtimeData.getChargeMoney());
                        sysChargeOrder.setTotalPower(platConnectorRealtimeData.getChargingKWH());
                        sysChargeOrder.setTotalMoney(platConnectorRealtimeData.getChargeMoney());
                    }
                    if (sysChargeOrder.getStartChargeSeqStat() >= 3) {
                        //订单其实已经结束了，这个是老数据
                        isCharging = false;
                    } else if (sysConnector.getStatus() != 3 || sysConnector.getState() != 0) {
                        updateConnector.setStatus((short) 3);
                        updateConnector.setState((short) 0);
                        isNotify = true;
                    }
                } else {
                    //普通状态
                    if (sysConnector != null) {

                        //platConnetorRealtimeData.getState() 设备状态 0离线 1故障 2空闲 3充电
                        //sysConnector.getStatus() 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障
                        //sysConnector.getState() 0正常 1故障
                        if (platConnectorRealtimeData.getState() == 0 && sysConnector.getStatus() != 0) {
                            updateConnector.setStatus((short) 0);
                            updateConnector.setState((short) 0);
                            //其实没有收到过0的，所以这里不冗余处理
                            isNotify = true;
                        } else if (platConnectorRealtimeData.getState() == 1 && sysConnector.getStatus() != 255) {
                            updateConnector.setStatus((short) 255);
                            updateConnector.setState((short) 1);
                            isNotify = true;
                        } else if (platConnectorRealtimeData.getState() == 2) {
                            updateConnector.setState((short) 0);
                            //桩报空闲
                            if (sysConnector.getStatus() != 2 && platConnectorRealtimeData.getGunLink() == 1) {
                                //原来没插枪，现在插枪了
                                isNotify = true;
                                updateConnector.setStatus((short) 2);
                                //更新价格策略
                                //1-获取价格策略
                                ///24小时内没更新的 更新下
                                //rabbitTemplate.convertAndSend(rabbitMQSettingConfig.getAioCallbackEx(), rabbitMQSettingConfig.getAioGunPricePullRk(), sysConnector.getConnectorId());
                                priceSendProducer.sendMsg(sysConnector.getConnectorId());

                            } else if (sysConnector.getStatus() != 1 && platConnectorRealtimeData.getGunLink() == 0) {
                                isNotify = true;
                                updateConnector.setStatus((short) 1);
                            } else if (sysConnector.getStatus() != 1 && sysConnector.getStatus() != 2) {
                                isNotify = true;
                                if (platConnectorRealtimeData.getGunLink() == 1) {
                                    updateConnector.setStatus((short) 2);
                                } else {
                                    updateConnector.setStatus((short) 1);
                                }
                            }
                        } else if (platConnectorRealtimeData.getState() == 3) {
                            updateConnector.setState((short) 0);
                            if (sysConnector.getStatus() != 3) {
                                isNotify = true;
                                updateConnector.setStatus((short) 3);
                            }
                        }
                    }
                }
                if (isCharging) {
                    log.info("[MQ_AIO_GUN_REALTIME_DATA]notify chargeOrder=" + sysChargeOrder.getStartChargeSeq() + " connectorId=" + platConnectorRealtimeData.getConnectorId());
                    if (sysChargeOrder != null) {
                        //超过时间段再计算一次数据
                        long lastTm = 0;
                        String key = HlhtRedisKey.PLAT_CHARGE_ORDER_DETAIL_LAST_CUL_TM + sysChargeOrder.getStartChargeSeq();
                        if (RedisUtils.hasKey(key)) {
                            lastTm = Long.valueOf(RedisUtils.getCacheObject(key).toString());
                        }
                        long now = System.currentTimeMillis() / 1000;
                        if (now - lastTm > CHARGING_ORDER_NOTIFY_DURANT) {
                            RedisUtils.setCacheObject(key, now);
                            //计算缓存
                            String lockKey = HlhtRedisKey.LOCK_KEY_CHARGE_ORDER_INFO + sysChargeOrder.getStartChargeSeq();
                            LockInfo lockInfo = null;
                            try {
                                lockInfo = lockTemplate.lock(lockKey,2000L, 10000L, RedissonLockExecutor.class);
                                //取最新的订单信息
                                SysChargeOrder newestOrder = chargeOrderService.getChargeOrderByStartChargeSeq(sysChargeOrder.getStartChargeSeq());
                                if (newestOrder.getStartChargeSeqStat() < 3) {
                                    chargeOrderService.update(sysChargeOrder);
                                }
                            } finally {
                                if (lockInfo != null) {
                                    lockTemplate.releaseLock(lockInfo);
                                }
                            }
                            chargingOrderMap.put(sysChargeOrder.getStartChargeSeq(), sysChargeOrder);
                        }
                    }
                }
                if (isNotify) {
                    if (sysConnector != null) {
                        connectorUpMap.put(updateConnector.getConnectorId(), updateConnector);
                    }
                }
                if (isCharging || isNotify) {
                    realtimeDataSaveList.add(platConnectorRealtimeData);
                }

            } catch (Exception e) {
                log.warn("message consume failed:", e);
            }
        }
        if(realtimeDataSaveList != null && realtimeDataSaveList.size() > 0){
            platConnectorRealtimeDataService.saveBatch(realtimeDataSaveList);
        }
        if(connectorUpMap != null && connectorUpMap.size() > 0){
            List<SysConnector> upList = new ArrayList<>();
            for(String key : connectorUpMap.keySet()){
                upList.add(connectorUpMap.get(key));
            }
            connectorService.updateBatchById(upList, true);
            for(SysConnector sysConnector : upList){
                remoteNotifyConnectorStatusService.realtimeData(sysConnector.getConnectorId());
            }
        }
        if(chargingOrderMap != null && chargingOrderMap.size() > 0){
            List<SysChargeOrder> orderList = new ArrayList<>();
            for(String key : chargingOrderMap.keySet()){
                orderList.add(chargingOrderMap.get(key));
            }
            for(SysChargeOrder sysChargeOrder : orderList){
                remoteNotifyChargingStatusService.realtimeData(sysChargeOrder);
            }
        }
        log.info("[MQ_AIO_GUN_REALTIME_DATA]finish========>" + (System.currentTimeMillis() - durantTm) + "ms " +
            "msgCount=" + platConnectorRealtimeDataList.size() + " chargeMsgCount=" + chargingOrderMap.size());
    }
}
