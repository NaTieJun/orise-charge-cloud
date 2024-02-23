package org.dromara.omind.simplat.dubbo;

import com.alibaba.fastjson.JSON;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStartChargeData;
import org.dromara.omind.baseplat.api.service.RemoteSysChargeOrderService;
import org.dromara.omind.baseplat.api.service.RemoteSysConnectorService;
import org.dromara.omind.baseplat.api.service.RemoteSysEquipmentService;
import org.dromara.omind.baseplat.api.service.RemoteSysPriceService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStartChargingService;
import org.dromara.omind.simplat.config.SimCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Log4j2
@DubboService
@Service
public class RemoteStartChargingServiceImpl implements RemoteStartChargingService {

    @DubboReference
    RemoteSysConnectorService remoteSysConnectorService;

    @DubboReference
    RemoteSysChargeOrderService remoteSysChargeOrderService;

    @Autowired
    private LockTemplate lockTemplate;

    @DubboReference
    RemoteSysPriceService priceService;

    @Autowired
    @Lazy
    SimCenter simCenter;

    @Override
    public int startCharging(SysOperator sysOperator, QueryStartChargeData queryStartChargeData) throws BaseException {
        if(!simCenter.isOnline(queryStartChargeData.getConnectorID())){
            return 2;
        }

        SysConnector sysConnector = remoteSysConnectorService.getConnectorById(queryStartChargeData.getConnectorID());
        if(sysConnector == null || sysConnector.getState() != 0 || sysConnector.getStatus() != 2){
            //getStatus状态 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障
            //state充电桩状态 0正常 1故障
            return 3;
        }
        SysChargeOrder sysChargeOrder = new SysChargeOrder();
        String lockKey = HlhtRedisKey.LOCK_KEY_CHARGE_ORDER_INFO + queryStartChargeData.getStartChargeSeq();
        LockInfo lockInfo = null;
        try{
            lockInfo = lockTemplate.lock(lockKey, 3000L, 5000L, RedissonLockExecutor.class);

            sysChargeOrder.setStartChargeSeq(queryStartChargeData.getStartChargeSeq());
            sysChargeOrder.setOperatorId(sysOperator.getOperatorId());
            sysChargeOrder.setTradeNo(queryStartChargeData.getStartChargeSeq());
            sysChargeOrder.setConnectorId(sysConnector.getConnectorId());
            sysChargeOrder.setStartChargeSeqStat((short) 2);
            sysChargeOrder.setConnectorStatus(3);
            sysChargeOrder.setStartTime(new Date());
            sysChargeOrder.setSyncFlag((short) 0);
            sysChargeOrder.setReportGov((short) 0);
            sysChargeOrder.setPhoneNum(queryStartChargeData.getPhoneNum());
            sysChargeOrder.setPlateNum(queryStartChargeData.getPlateNum());
            List<PolicyInfoData> priceList = priceService.getConnectorPriceList(sysConnector.getConnectorId());
            if(priceList != null && priceList.size() > 0){
                sysChargeOrder.setPriceInfo(JsonUtils.toJsonString(priceList));
            }
            log.info(JSON.toJSONString(sysChargeOrder));
            remoteSysChargeOrderService.save(sysChargeOrder);
            log.info("启动订单创建成功：" + JSON.toJSONString(sysChargeOrder));
        }
        finally {
            if(lockInfo != null){
                lockTemplate.releaseLock(lockInfo);
            }
        }
        simCenter.startCharge(sysChargeOrder);
        return 0;
    }
}
