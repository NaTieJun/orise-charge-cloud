package org.dromara.omind.simplat.config;

import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.service.RemoteSysConnectorService;
import org.dromara.omind.baseplat.api.service.RemoteSysEquipmentService;
import org.dromara.omind.baseplat.api.service.RemoteTradeService;
import org.dromara.omind.simplat.simulation.SimPileClient;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@Configuration
public class SimCenter {

    private static Map<String, SimPileClient> pileMap = new LinkedHashMap<>();

    @DubboReference
    RemoteSysConnectorService connectorService;

    @DubboReference
    RemoteSysEquipmentService equipmentService;

    @DubboReference
    RemoteTradeService remoteTradeService;

    public Boolean isOnline(String connectorId)
    {
        if(pileMap.containsKey(connectorId)){
            return true;
        }
        else{
            return false;
        }
    }

    public void start(String connectorId) throws BaseException
    {
        log.info("启动模态充电桩：" + connectorId);
        if(pileMap.containsKey(connectorId)){
            throw new BaseException("模态桩已启动");
        }

        SysConnector sysConnector = connectorService.getConnectorById(connectorId);
        if(sysConnector == null){
            throw new BaseException("模态枪不存在");
        }
        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
        if(sysEquipment == null){
            throw new BaseException("模态桩不存在");
        }
        SimPileClient pileClient = new SimPileClient(remoteTradeService, sysEquipment, sysConnector);
        pileMap.put(connectorId, pileClient);
        try{
            new Thread(()->{
                pileClient.start();
                log.info("模态桩服务------启动成功");
            }).start();
        }
        catch (BaseException ex){
            throw ex;
        }
        catch (Exception ex){
            throw new BaseException("未知错误");
        }
    }

    public void stop(String connectorId) throws BaseException
    {
        if(!pileMap.containsKey(connectorId)){
            throw new BaseException("模态桩未启动");
        }
        try{

            SimPileClient pileClient = pileMap.get(connectorId);
            if(pileClient == null){
                throw new BaseException("枪未启动");
            }
            pileClient.stop();

        }
        catch (BaseException ex){
            throw ex;
        }
        finally {
            pileMap.remove(connectorId);
        }
    }

    public void link(String connectorId) throws BaseException
    {
        if(!pileMap.containsKey(connectorId)){
            throw new BaseException("充电桩未启动");
        }
        SimPileClient pileClient = pileMap.get(connectorId);
        if(pileClient == null){
            throw new BaseException("枪未启动");
        }
        pileClient.link();
    }

    public void unlink(String connectorId) throws BaseException
    {
        if(!pileMap.containsKey(connectorId)){
            throw new BaseException("充电桩未启动");
        }
        SimPileClient pileClient = pileMap.get(connectorId);
        if(pileClient == null){
            throw new BaseException("枪未启动");
        }
        pileClient.unlink();
    }

    public void startCharge(SysChargeOrder sysChargeOrder) throws BaseException
    {
        if(sysChargeOrder == null || TextUtils.isBlank(sysChargeOrder.getConnectorId())){
            throw new BaseException("无效的枪信息");
        }
        SimPileClient pileClient = pileMap.get(sysChargeOrder.getConnectorId());
        if(pileClient == null){
            throw new BaseException("枪未启动");
        }
        pileClient.startCharge(sysChargeOrder);
    }

    public void stopCharge(SysChargeOrder sysChargeOrder) throws BaseException
    {
        if(sysChargeOrder == null || TextUtils.isBlank(sysChargeOrder.getConnectorId())){
            throw new BaseException("无效的枪信息");
        }
        SimPileClient pileClient = pileMap.get(sysChargeOrder.getConnectorId());
        if(pileClient == null){
            throw new BaseException("枪未启动");
        }
        pileClient.stopCharge(1, sysChargeOrder);
    }
}
