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
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.request.ConnectorPageRequest;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindConnectorEntityIService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OmindConnectorServiceImpl implements OmindConnectorService {

    @Autowired
    @Lazy
    OmindConnectorEntityIService iService;

    @Autowired
    @Lazy
    OmindConnectorService selfService;

    @Override
    public List<OmindConnectorEntity> all(String equipmentId) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getEquipmentId, equipmentId);
        lambdaQueryWrapper.orderByDesc(OmindConnectorEntity::getId);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public OmindConnectorEntity selectConnectorInfo(String connectorId) {
        String key = PlatRedisKey.CONNECTOR_INFO + connectorId;
        if (PlatRedisKey.REDIS_FLAG) {
            if (RedisUtils.hasKey(key)) {
                OmindConnectorEntity odConnectorInfoData = RedisUtils.getCacheObject(key);
                if (odConnectorInfoData != null) {
                    return odConnectorInfoData;
                }
            }
        }

        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getConnectorId, connectorId).last("limit 1");
        OmindConnectorEntity odConnectorInfo = iService.getOne(lambdaQueryWrapper);

        //添加到redis
        RedisUtils.setCacheObject(key, odConnectorInfo);

        return odConnectorInfo;
    }

    @Override
    public OmindConnectorEntity get(String connectorId) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getConnectorId, connectorId);
        lambdaQueryWrapper.orderByDesc(OmindConnectorEntity::getId);
        lambdaQueryWrapper.last("limit 1");
        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public List<OmindConnectorEntity> getByStationId(String stationId) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getStationId, stationId);
        lambdaQueryWrapper.orderByDesc(OmindConnectorEntity::getId);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public int batchInsertConnector(List<OmindConnectorEntity> connectorList) throws BaseException {
        if (connectorList == null || connectorList.size() == 0) {
            return 10;
        }
        if (!iService.saveBatch(connectorList)) {
            throw new BaseException("数据保存失败");
        }
        return 0;
    }

    @Override
    public int batchUpdateConnector(List<OmindConnectorEntity> connectorList) throws BaseException {
        if (connectorList == null || connectorList.size() == 0) {
            return 10;
        }
        if (!iService.updateBatchById(connectorList)) {
            throw new BaseException("数据更新失败");
        }

        //批量更新redis
        for (OmindConnectorEntity omindConnectorEntity : connectorList) {
            String key = PlatRedisKey.CONNECTOR_INFO + omindConnectorEntity.getConnectorId();
            RedisUtils.deleteObject(key);
        }
        return 0;
    }

    @Override
    public Integer connectorCount(String stationId, String equipmentId) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getStationId, stationId);
        if (!TextUtils.isBlank(equipmentId)) {
            lambdaQueryWrapper.eq(OmindConnectorEntity::getEquipmentId, equipmentId);
        }
        return (int) iService.count(lambdaQueryWrapper);
    }

    @Override
    public OmindConnectorEntity info(Integer id) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OmindConnectorEntity::getId, id);
        lambdaQueryWrapper.orderByDesc(OmindConnectorEntity::getId);
        lambdaQueryWrapper.last("limit 1");
        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public TableDataInfo<OmindConnectorEntity> selectConnectorList(ConnectorPageRequest connectorPageRequest, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindConnectorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (connectorPageRequest.getStationId() != null && !TextUtils.isBlank(connectorPageRequest.getStationId())) {
            lambdaQueryWrapper.eq(OmindConnectorEntity::getStationId, connectorPageRequest.getStationId());
        }
        if (connectorPageRequest.getEquipmentId() != null && !TextUtils.isBlank(connectorPageRequest.getEquipmentId())) {
            lambdaQueryWrapper.eq(OmindConnectorEntity::getEquipmentId, connectorPageRequest.getEquipmentId());
        }

        lambdaQueryWrapper.orderByDesc(OmindConnectorEntity::getId);

        Page<OmindConnectorEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindConnectorEntity> iPage = iService.page(page, lambdaQueryWrapper);

        return TableDataInfo.build(iPage);
    }

    @Override
    public boolean updateConnector(OmindConnectorEntity odConnector) throws BaseException {
        OmindConnectorEntity odConnectorEntity = selfService.info(odConnector.getId().intValue());
        if (odConnectorEntity == null) {
            throw new BaseException("数据不存在");
        }
        //更新本地数据
        if (!iService.updateById(odConnector)) {
            throw new BaseException("数据更新失败");
        }

        //更新redis
        String key = PlatRedisKey.CONNECTOR_INFO + odConnector.getConnectorId();
        RedisUtils.deleteObject(key);

        return true;
    }

    @Override
    public void connectorPriceCacheDeal(String StationId) {
        List<OmindConnectorEntity> connectorList = selfService.getByStationId(StationId);
        if(connectorList != null && connectorList.size() > 0){
            for (OmindConnectorEntity odConnector : connectorList){
                String key = PlatRedisKey.CONNECTOR_PRICE_LIST + odConnector.getConnectorId();
                RedisUtils.deleteObject(key);

                //删除实时价格标签
                String realTimeKey = PlatRedisKey.STATION_EQUIPMENT_REALTIME_FLAG + StationId + ":" + odConnector.getEquipmentId();
                RedisUtils.deleteObject(realTimeKey);
            }
        }
    }
}
