package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindEquipmentEntity;

import java.util.List;

public interface OmindEquipmentService {

    List<OmindEquipmentEntity> all(String stationId);

    OmindEquipmentEntity get(String equipmentId);

    int batchInsertEquipment(List<OmindEquipmentEntity> equipmentList) throws BaseException;

    int batchUpdateEquipment(List<OmindEquipmentEntity> equipmentList) throws BaseException;

    Integer equipmentCount(String stationId);

    TableDataInfo<OmindEquipmentEntity> selectEquipmentList(String stationId, PageQuery pageQuery);

    OmindEquipmentEntity info(Integer id);

}
