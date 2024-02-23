package org.dromara.omind.baseplat.scheduler.task;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.dromara.omind.baseplat.service.notify.NotifyConnectorStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UnliveRemoveTask {

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Autowired
    @Lazy
    NotifyConnectorStatusService notifyConnectorStatusService;

    @XxlJob("UnliveRemoveTask")
    public void excute(){
        List<SysConnector> connectorList = connectorService.getAllUnaliveConnector();

        List<String> donwList = new ArrayList<>();
        for(SysConnector sysConnector : connectorList){

            SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
            if(sysEquipment == null || sysEquipment.getOnlineTm() == null || (System.currentTimeMillis() - sysEquipment.getOnlineTm().getTime()) > (60 * 1000))
            {
                //下线、推送
                donwList.add(sysConnector.getConnectorId());
                sysConnector.setStatus((short)0);
                notifyConnectorStatusService.heartDisConnect(sysConnector);
            }
        }
        if(donwList != null && donwList.size() > 0){
            connectorService.offlineWithConnectorIds(donwList);
        }
    }

}
