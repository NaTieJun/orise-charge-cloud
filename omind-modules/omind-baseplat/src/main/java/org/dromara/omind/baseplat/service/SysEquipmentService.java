package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.EquipmentInfoData;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysEquipmentDto;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;

import java.util.List;

public interface SysEquipmentService {

    SysEquipment getEquipmentById(String equipmentId);

    /**
     * 判断充电设备ID是否已存在
     * @param equipmentId
     * @return
     */
    Boolean isEquipmentIdValid(String equipmentId);

    TableDataInfo<SysEquipment> getEquipmentPageList(QuerySysEquipmentDto querySysEquipmentDto, PageQuery pageQuery);

    List<EquipmentInfoData> getAllByStationId(String stationId);

    List<String> getAllEquipmentIdByStationId(String stationId);

    Boolean update(SysEquipment sysEquipment);

    Boolean save(SysEquipment sysEquipment);

    Boolean remove(SysEquipment sysEquipment);

}
