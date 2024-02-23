package org.dromara.omind.baseplat.dubbo;


import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysEquipmentDto;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.service.RemoteSysEquipmentService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteSysEquipmentServiceImpl implements RemoteSysEquipmentService {

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Override
    public SysEquipment getEquipmentById(String equipmentId) {
        return equipmentService.getEquipmentById(equipmentId);
    }

    @Override
    public TableDataInfo<SysEquipment> getEquipmentPageList(QuerySysEquipmentDto querySysEquipmentDto, PageQuery pageQuery) {
        return equipmentService.getEquipmentPageList(querySysEquipmentDto, pageQuery);
    }

    @Override
    public Boolean update(SysEquipment sysEquipment) {
        return equipmentService.update(sysEquipment);
    }

}
