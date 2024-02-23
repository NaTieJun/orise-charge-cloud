package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.ConnectorInfoData;
import org.dromara.omind.baseplat.api.domain.ConnectorStatusInfoData;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysConnectorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.api.service.RemoteSysEquipmentService;
import org.dromara.omind.baseplat.domain.service.SysConnectorIService;
import org.dromara.omind.baseplat.service.StationOperatorLinkService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
public class SysConnectorServiceImpl implements SysConnectorService {

    @Autowired
    SysConnectorIService iService;

    @Autowired
    @Lazy
    SysConnectorService selfService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @Autowired
    @Lazy
    RemoteSysEquipmentService equipmentService;

    @Autowired
    @Lazy
    SysStationService stationService;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;

    @Override
    public SysConnector getConnectorById(String connectorId) {
        if(TextUtils.isBlank(connectorId)){
            return null;
        }
        else if(connectorId.length() <= 9){
            return null;
        }
        String key = HlhtRedisKey.SYS_CONNECTOR + connectorId;
        SysConnector sysConnector = RedisUtils.getCacheObject(key);
        if(sysConnector != null){
            return sysConnector;
        }

        LambdaQueryWrapper<SysConnector> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysConnector::getConnectorId, connectorId);
        queryWrapper.last("limit 1");
        List<SysConnector> list = iService.list(queryWrapper);
        if(list != null && list.size() > 0){
            sysConnector = list.get(0);
            RedisUtils.setCacheObject(key, sysConnector, Duration.ofMinutes(5));
            return sysConnector;
        }
        return null;
    }

    @Override
    public Boolean isConnectorIdValid(String connectorId) {
        if(TextUtils.isBlank(connectorId)){
            return false;
        }
        else if(connectorId.length() <= 9){
            return false;
        }

        if(selfService.getConnectorById(connectorId) != null){
            return false;
        }

        return true;
    }

    @Override
    public TableDataInfo<SysConnector> getConnectorPageList(QuerySysConnectorDto querySysConnectorDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        if(!TextUtils.isBlank(querySysConnectorDto.getConnectorId())){
            lambdaQuery.eq(SysConnector::getConnectorId, querySysConnectorDto.getConnectorId());
        }
        if(!TextUtils.isBlank(querySysConnectorDto.getEquipmentId())){
            lambdaQuery.eq(SysConnector::getEquipmentId, querySysConnectorDto.getEquipmentId());
        }

        if(!TextUtils.isBlank(querySysConnectorDto.getConnectorName())){
            lambdaQuery.like(SysConnector::getConnectorName, querySysConnectorDto.getConnectorName());
        }

        if(querySysConnectorDto.getState() != null){
            lambdaQuery.eq(SysConnector::getState, querySysConnectorDto.getState());
        }

        if(querySysConnectorDto.getStatus() != null){
            lambdaQuery.eq(SysConnector::getStatus, querySysConnectorDto.getStatus());
        }

        lambdaQuery.orderByDesc(SysConnector::getId);

        Page<SysConnector> page = iService.page(pageQuery.build(), lambdaQuery);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ConnectorInfoData> getAllByEquipmentId(String equipmentId) {
        if(TextUtils.isBlank(equipmentId)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysConnector::getEquipmentId, equipmentId);
        List<SysConnector> list = iService.list(lambdaQuery);
        List<ConnectorInfoData> infoList = new ArrayList<>();
        List<String> connectorIdList = new ArrayList<>();
        if(list != null){
            for(SysConnector sysConnector : list){
                if(!TextUtils.isBlank(sysConnector.getConnectorId())) {
                    RedisUtils.setCacheObject(HlhtRedisKey.SYS_CONNECTOR + sysConnector.getConnectorId(), sysConnector, Duration.ofMinutes(5));
                }
                connectorIdList.add(sysConnector.getConnectorId());
                ConnectorInfoData data = ConnectorInfoData.build(sysConnector);
                infoList.add(data);
            }
        }
        if(connectorIdList.size() > 0){
            RedisUtils.setCacheObject(HlhtRedisKey.SYS_CONNECTOR_ID_LIST_4_EQUIPMENT + equipmentId, connectorIdList, Duration.ofMinutes(5));
        }
        return infoList;
    }

    @Override
    public int countByEquipmentId(String equipmentId) {
        if(TextUtils.isBlank(equipmentId)){
            return 0;
        }
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysConnector::getEquipmentId, equipmentId);
        long count = iService.count(lambdaQuery);
        return (int)count;
    }

    @Override
    public List<String> getAllIdByEquipmentId(String equipmentId) {
        String key = HlhtRedisKey.SYS_CONNECTOR_ID_LIST_4_EQUIPMENT + equipmentId;
        List<String> list = RedisUtils.getCacheObject(key);
        if(list != null){
            return list;
        }
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.select(SysConnector::getConnectorId);
        lambdaQuery.eq(SysConnector::getEquipmentId, equipmentId);
        lambdaQuery.orderByAsc(SysConnector::getId);
        List<SysConnector> connectorList = iService.list(lambdaQuery);
        List<String> idList = new ArrayList<>();
        if(connectorList != null && connectorList.size() > 0){
            for(SysConnector sysConnector : connectorList){
                idList.add(sysConnector.getConnectorId());
            }
        }
        if(idList != null && idList.size() > 0){
            RedisUtils.setCacheObject(key, idList, Duration.ofMinutes(5));
        }
        return idList;
    }

    @Override
    public List<SysConnector> getAllConnectorByEquipmentId(String equipmentId, Boolean skipCache) {
        String key = HlhtRedisKey.SYS_CONNECTOR_ALL_LIST_4_EQUIPMENT + equipmentId;
        if(!skipCache) {
            List<SysConnector> list = RedisUtils.getCacheObject(key);
            if (list != null) {
                return list;
            }
        }
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysConnector::getEquipmentId, equipmentId);
        List<SysConnector> list = iService.list(lambdaQuery);
        if(list != null && list.size() > 0){
            RedisUtils.setCacheObject(key, list, Duration.ofMinutes(5));
        }
        return list;
    }

    @Override
    public List<ConnectorStatusInfoData> getAllStatusByEquipmentId(String equipmentId) {
        if(TextUtils.isBlank(equipmentId)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysConnector> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.select(SysConnector::getConnectorId, SysConnector::getStatus, SysConnector::getLockStatus, SysConnector::getParkStatus);
        lambdaQuery.eq(SysConnector::getEquipmentId, equipmentId);
        List<SysConnector> list = iService.list(lambdaQuery);
        List<ConnectorStatusInfoData> statusInfoData = new ArrayList<>();
        if(list != null){
            for(SysConnector sysConnector : list){
                ConnectorStatusInfoData data = new ConnectorStatusInfoData();
                data.setConnectorID(sysConnector.getConnectorId());
                data.setStatus(sysConnector.getStatus().intValue());
                data.setLockStatus(sysConnector.getLockStatus());
                data.setParkStatus(sysConnector.getParkStatus());
                statusInfoData.add(data);
            }
        }
        return statusInfoData;
    }

    @Override
    public Boolean add(SysConnector sysConnector) {
        if(sysConnector != null) {
            RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR_ID_LIST_4_EQUIPMENT + sysConnector.getEquipmentId());
            RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR_ALL_LIST_4_EQUIPMENT + sysConnector.getEquipmentId());
            if(sysConnector.getPingTm() == null){
                sysConnector.setPingTm(new Date());
            }
            SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
            stationOperatorLinkService.stationUpdate(sysEquipment.getStationId());
            return iService.save(sysConnector);
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean updateById(SysConnector sysConnector) {
        if(sysConnector == null || sysConnector.getId() == null || sysConnector.getId() <= 0){
            log.error("【枪状态更新失败】ID错误");
            throw new BaseException("ID错误");
        }
        if(sysConnector.getEquipmentId() != null){
            SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
            if(sysEquipment == null){
                log.error("【枪状态更新失败】未找到指定充电设备");
                throw new BaseException("未找到指定充电设备");
            }
        }
        if(sysConnector.getConnectorId() != null){
            SysConnector oldData = iService.getById(sysConnector.getId());
            if(!oldData.getConnectorId().equals(sysConnector.getConnectorId())){
                log.error("【枪状态更新失败】充电接口编号不可修改");
                throw new BaseException("充电接口编号不可修改");
            }
        }


        RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR + sysConnector.getConnectorId());
        if(iService.updateById(sysConnector)){
            return true;
        }
        else{
            log.error("【枪状态更新失败】数据库更新失败");
            return false;
        }
    }

    @Override
    public Boolean updateBatchById(List<SysConnector> sysConnectorList, boolean refreshCache) {
        if(sysConnectorList == null || sysConnectorList.size() == 0){
            return false;
        }
        Boolean success = iService.updateBatchById(sysConnectorList);
        if(refreshCache && success) {
            List<String> keyList = new ArrayList<>();
            for (SysConnector sysConnector : sysConnectorList) {
                if (!TextUtils.isBlank(sysConnector.getConnectorId())) {
                    RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR + sysConnector.getConnectorId());
                }
            }
        }
        return success;
    }

    @Override
    public Boolean remove(SysConnector sysConnector) {
        if(sysConnector == null || sysConnector.getId() == null || sysConnector.getId() <= 0){
            return false;
        }
        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
        if(sysEquipment != null) {
            stationOperatorLinkService.stationUpdate(sysEquipment.getStationId());
        }
        boolean success = iService.removeById(sysConnector.getId());
        RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR_ID_LIST_4_EQUIPMENT + sysConnector.getEquipmentId());
        RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR_ALL_LIST_4_EQUIPMENT + sysConnector.getEquipmentId());
        RedisUtils.deleteObject(HlhtRedisKey.SYS_CONNECTOR + sysConnector.getConnectorId());
        return success;
    }

    @Override
    public Boolean updatePingTm2Cache(String connectorId, Date tm) {
        SysConnector sysConnector = selfService.getConnectorById(connectorId);
        if(sysConnector == null){
            return false;
        }
        else{
            sysConnector.setPingTm(tm);
            RedisUtils.setCacheObject(HlhtRedisKey.SYS_CONNECTOR + connectorId, sysConnector, Duration.ofMinutes(5));
            return true;
        }
    }

    @Override
    public SysOperator getDefaultOperatorByConnectorId(String connectorId) {
        SysConnector sysConnector = selfService.getConnectorById(connectorId);
        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
        if(sysEquipment == null){
            return null;
        }
        SysStation sysStation = stationService.getStationById(sysEquipment.getStationId());
        if(sysStation == null){
            return null;
        }
        SysOperator sysOperator = operatorService.getOperatorById(sysStation.getOperatorId());
        if(sysOperator == null) {
            return null;
        }
        return sysOperator;
    }

    @Override
    public SysStation getStationInfoByConnectorId(String connectorId) {
        String key = HlhtRedisKey.SYS_STATION_INFO_FOR_CONNECTOR_ID + connectorId;

        SysStation sysStation = RedisUtils.getCacheObject(key);
        if(sysStation != null){
            return sysStation;
        }

        SysConnector sysConnector = selfService.getConnectorById(connectorId);
        if (sysConnector != null) {
            SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
            if (sysEquipment != null) {
                sysStation = stationService.getStationById(sysEquipment.getStationId());
                if(sysStation != null){
                    RedisUtils.setCacheObject(key, sysStation, Duration.ofHours(24));
                }
                return sysStation;
            }
        }
        return null;
    }

    @Override
    public List<SysConnector> getAllUnaliveConnector() {
        LambdaQueryWrapper<SysConnector> lambdaQueryWrapper = Wrappers.lambdaQuery();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -60);
        Date timeoutDate = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        calendar2.add(Calendar.HOUR_OF_DAY, -12);
        Date timeoutDate2 = calendar2.getTime();

        //因为增加了联合基础平台，所以查询无心跳1分钟以上，60分钟以下的失联桩
        //联合基础平台桩无心跳
        lambdaQueryWrapper.gt(SysConnector::getStatus, 0);
        log.info("【离线检查】时间范围" + timeoutDate2.toString() + " to " + timeoutDate.toString());
        lambdaQueryWrapper.gt(SysConnector::getPingTm, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, timeoutDate2));
        lambdaQueryWrapper.lt(SysConnector::getPingTm, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, timeoutDate));
        List<SysConnector> list = iService.list(lambdaQueryWrapper);

        return list;
    }

    @Override
    public boolean offlineWithConnectorIds(List<String> connectorIdList) {
        if(connectorIdList == null || connectorIdList.size() <= 0){
            return true;
        }
        UpdateWrapper<SysConnector> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", (short)0);
        updateWrapper.in("connector_id", connectorIdList);
        return iService.update(updateWrapper);
    }
}
