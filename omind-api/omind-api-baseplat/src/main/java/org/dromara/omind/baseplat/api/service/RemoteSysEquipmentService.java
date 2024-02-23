package org.dromara.omind.baseplat.api.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysEquipmentDto;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;

public interface RemoteSysEquipmentService {

    SysEquipment getEquipmentById(String equipmentId);

    TableDataInfo<SysEquipment> getEquipmentPageList(QuerySysEquipmentDto querySysEquipmentDto, PageQuery pageQuery);

    Boolean update(SysEquipment sysEquipment);

}
