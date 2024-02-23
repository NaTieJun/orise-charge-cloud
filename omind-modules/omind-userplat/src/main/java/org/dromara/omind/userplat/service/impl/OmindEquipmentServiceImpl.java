package org.dromara.omind.userplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.userplat.api.domain.entity.OmindEquipmentEntity;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindEquipmentEntityIService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.dromara.omind.userplat.service.OmindEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OmindEquipmentServiceImpl implements OmindEquipmentService {

    @Autowired
    @Lazy
    OmindEquipmentEntityIService iService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Override
    public List<OmindEquipmentEntity> all(String stationId) {
        LambdaQueryWrapper<OmindEquipmentEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindEquipmentEntity::getStationId, stationId);
        lambdaQueryWrapper.orderByDesc(OmindEquipmentEntity::getId);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public OmindEquipmentEntity get(String equipmentId) {
        LambdaQueryWrapper<OmindEquipmentEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OmindEquipmentEntity::getEquipmentId, equipmentId);
        lambdaQueryWrapper.orderByDesc(OmindEquipmentEntity::getId);
        lambdaQueryWrapper.last("limit 1");
        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public int batchInsertEquipment(List<OmindEquipmentEntity> equipmentList) throws BaseException {
        if (equipmentList == null || equipmentList.size() == 0) {
            return 10;
        }
        if (!iService.saveBatch(equipmentList)) {
            throw new BaseException("数据保存失败");
        }
        return 0;
    }

    @Override
    public int batchUpdateEquipment(List<OmindEquipmentEntity> equipmentList) throws BaseException {
        if (equipmentList == null || equipmentList.size() == 0) {
            return 10;
        }
        if (!iService.updateBatchById(equipmentList)) {
            throw new BaseException("数据更新失败");
        }

        //批量更新redis
        for (OmindEquipmentEntity odEquipmentEntity : equipmentList) {
            String key = PlatRedisKey.EQUIPMENT_INFO + odEquipmentEntity.getEquipmentId();
            RedisUtils.deleteObject(key);
        }
        return 0;
    }

    @Override
    public Integer equipmentCount(String stationId) {
        LambdaQueryWrapper<OmindEquipmentEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindEquipmentEntity::getStationId, stationId);
        return (int) iService.count(lambdaQueryWrapper);
    }

    @Override
    public TableDataInfo<OmindEquipmentEntity> selectEquipmentList(String stationId, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindEquipmentEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(stationId)) {
            lambdaQueryWrapper.eq(OmindEquipmentEntity::getStationId, stationId);
        }
        lambdaQueryWrapper.orderByDesc(OmindEquipmentEntity::getId);

        Page<OmindEquipmentEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindEquipmentEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            for (OmindEquipmentEntity odEquipmentEntity : iPage.getRecords()) {
                odEquipmentEntity.setConnectorCount(omindConnectorService.connectorCount(odEquipmentEntity.getStationId(), odEquipmentEntity.getEquipmentId()));
            }
        }
        TableDataInfo<OmindEquipmentEntity> tableDataInfo = TableDataInfo.build(iPage);
        return tableDataInfo;
    }

    @Override
    public OmindEquipmentEntity info(Integer id) {
        LambdaQueryWrapper<OmindEquipmentEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OmindEquipmentEntity::getId, id);
        lambdaQueryWrapper.orderByDesc(OmindEquipmentEntity::getId);
        lambdaQueryWrapper.last("limit 1");
        return iService.getOne(lambdaQueryWrapper);
    }
}
