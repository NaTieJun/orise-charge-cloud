package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.EquipmentInfoData;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysEquipmentDto;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.domain.service.SysEquipmentIService;
import org.dromara.omind.baseplat.service.StationOperatorLinkService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysEquipmentServiceImpl implements SysEquipmentService {


    @Autowired
    SysEquipmentIService iService;

    @Autowired
    @Lazy
    SysEquipmentService selfService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysStationService stationService;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;

    @Override
    public SysEquipment getEquipmentById(String equipmentId) {
        if(TextUtils.isBlank(equipmentId)){
            return null;
        }
        else if(equipmentId.length() <= 9){
            return null;
        }
        String key = HlhtRedisKey.SYS_EQUIPMENT + equipmentId;
        SysEquipment sysEquipment = RedisUtils.getCacheObject(key);
        if(sysEquipment != null){
            return sysEquipment;
        }
        LambdaQueryWrapper<SysEquipment> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysEquipment::getEquipmentId, equipmentId);
        queryWrapper.last("limit 1");
        List<SysEquipment> list = iService.list(queryWrapper);
        if(list != null && list.size() > 0){
            sysEquipment = list.get(0);
            sysEquipment.setGunNum(connectorService.countByEquipmentId(equipmentId));
            RedisUtils.setCacheObject(key, sysEquipment, Duration.ofDays(1));
            return sysEquipment;
        }
        return null;
    }

    @Override
    public Boolean isEquipmentIdValid(String equipmentId) {
        if(TextUtils.isBlank(equipmentId)){
            return false;
        }
        else if(equipmentId.length() <= 9){
            return false;
        }
        if(selfService.getEquipmentById(equipmentId) != null){
            return false;
        }
        return true;
    }

    @Override
    public TableDataInfo<SysEquipment> getEquipmentPageList(QuerySysEquipmentDto querySysEquipmentDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysEquipment> lambdaQuery = Wrappers.lambdaQuery();
        if(!TextUtils.isBlank(querySysEquipmentDto.getEquipmentId())){
            lambdaQuery.eq(SysEquipment::getEquipmentId, querySysEquipmentDto.getEquipmentId());
        }
        if(!TextUtils.isBlank(querySysEquipmentDto.getPileNo())){
            lambdaQuery.eq(SysEquipment::getPileNo, querySysEquipmentDto.getPileNo());
        }

        if(!TextUtils.isBlank(querySysEquipmentDto.getStationId())){
            lambdaQuery.eq(SysEquipment::getStationId, querySysEquipmentDto.getStationId());
        }

        if(querySysEquipmentDto.getEquipmentType() != null){
            lambdaQuery.eq(SysEquipment::getEquipmentType, querySysEquipmentDto.getEquipmentType());
        }

        lambdaQuery.orderByDesc(SysEquipment::getId);

        Page<SysEquipment> page = iService.page(pageQuery.build(), lambdaQuery);
        return TableDataInfo.build(page);
    }

    @Override
    public List<EquipmentInfoData> getAllByStationId(String stationId) {
        if(TextUtils.isBlank(stationId)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysEquipment> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysEquipment::getStationId, stationId);
        List<SysEquipment> list = iService.list(lambdaQuery);
        List<EquipmentInfoData> infoList = new ArrayList<>();
        if(list != null){
            for(SysEquipment sysEquipment : list){
                if(!TextUtils.isBlank(sysEquipment.getEquipmentId())){
                    RedisUtils.setCacheObject(HlhtRedisKey.SYS_EQUIPMENT + sysEquipment.getEquipmentId(), sysEquipment);
                }
                EquipmentInfoData data = EquipmentInfoData.build(sysEquipment);
                data.setConnectorInfos(connectorService.getAllByEquipmentId(data.getEquipmentID()));
                infoList.add(data);
            }
        }
        return infoList;
    }

    @Override
    public List<String> getAllEquipmentIdByStationId(String stationId) {
        if(TextUtils.isBlank(stationId)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysEquipment> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.select(SysEquipment::getEquipmentId);
        lambdaQuery.eq(SysEquipment::getStationId, stationId);
        List<SysEquipment> list = iService.list(lambdaQuery);
        List<String> idList = new ArrayList<>();
        if(list != null){
            for(SysEquipment sysEquipment : list){
                idList.add(sysEquipment.getEquipmentId());
            }
        }
        return idList;
    }

    @Override
    public Boolean update(SysEquipment sysEquipment) {
        if(sysEquipment.getId() == null || sysEquipment.getId() <= 0){
            throw new BaseException("充电设备ID错误");
        }
        SysEquipment oldData = iService.getById(sysEquipment.getId());
        if(oldData == null){
            throw new BaseException("充电设备ID错误");
        }
        if(sysEquipment.getEquipmentId() != null && !oldData.getEquipmentId().equals(sysEquipment.getEquipmentId())){
            throw new BaseException("充电设备编号不可变更");
        }

        if(sysEquipment.getStationId() != null){
            SysStation sysStation = stationService.getStationById(sysEquipment.getStationId());
            if(sysStation == null){
                throw new BaseException(HttpStatus.ERROR + "", "未找到指定充电站");
            }
        }

        if(sysEquipment != null && sysEquipment.getId() != null && sysEquipment.getId() > 0){
            if(iService.updateById(sysEquipment)){
                RedisUtils.deleteObject(HlhtRedisKey.SYS_EQUIPMENT + sysEquipment.getEquipmentId());
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean save(SysEquipment sysEquipment) {
        if(sysEquipment != null){
            stationOperatorLinkService.stationUpdate(sysEquipment.getStationId());
            return iService.save(sysEquipment);
        }
        return false;
    }

    @Override
    public Boolean remove(SysEquipment sysEquipment) {
        if(sysEquipment != null && sysEquipment.getId() != null && sysEquipment.getId() > 0) {
            RedisUtils.deleteObject(HlhtRedisKey.SYS_EQUIPMENT + sysEquipment.getEquipmentId());
            stationOperatorLinkService.stationUpdate(sysEquipment.getStationId());
            return iService.removeById(sysEquipment.getId());
        }
        return false;
    }
}
