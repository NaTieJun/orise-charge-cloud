package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysOperatorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.domain.service.SysOperatorIService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class SysOperatorServiceImpl implements SysOperatorService {


    @Autowired
    SysOperatorIService iService;

    @Autowired
    @Lazy
    SysOperatorService selfService;

    @Override
    public SysOperator getOperatorById(String operatorId) {
        if(TextUtils.isBlank(operatorId)){
            return null;
        }
        else if(operatorId.length() != 9){
            return null;
        }
        String key = HlhtRedisKey.SYS_OPERATOR + operatorId;
        SysOperator sysOperator = RedisUtils.getCacheObject(key);
        if(sysOperator != null){
            return sysOperator;
        }
        LambdaQueryWrapper<SysOperator> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysOperator::getOperatorId, operatorId);
        queryWrapper.last("limit 1");
        List<SysOperator> list = iService.list(queryWrapper);
        if(list != null && list.size() > 0){
            sysOperator = list.get(0);
            RedisUtils.setCacheObject(HlhtRedisKey.SYS_OPERATOR + sysOperator.getOperatorId(), sysOperator);
            return sysOperator;
        }
        return null;
    }

    @Override
    public Boolean isOperatorIdValid(String operatorId) {
        if(TextUtils.isBlank(operatorId)){
            return false;
        }
        else if(operatorId.length() != 9){
            return false;
        }
        if(selfService.getOperatorById(operatorId) != null){
            return false;
        }
        return true;
    }

    @Override
    public TableDataInfo<SysOperator> getOperatorPageList(QuerySysOperatorDto querySysOperatorDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOperator> lambdaQuery = Wrappers.lambdaQuery();
        if(!TextUtils.isBlank(querySysOperatorDto.getOperatorId())){
            lambdaQuery.eq(SysOperator::getOperatorId, querySysOperatorDto.getOperatorId());
        }
        if(!TextUtils.isBlank(querySysOperatorDto.getOperatorName())){
            lambdaQuery.like(SysOperator::getOperatorName, querySysOperatorDto.getOperatorName());
        }
        lambdaQuery.orderByDesc(SysOperator::getId);

        Page<SysOperator> page = iService.page(pageQuery.build(), lambdaQuery);
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean remove(SysOperator sysOperator) {
        if(sysOperator != null && sysOperator.getId() != null && sysOperator.getId() > 0) {
            boolean success = iService.removeById(sysOperator.getId());
            RedisUtils.deleteObject(HlhtRedisKey.SYS_OPERATOR + sysOperator.getOperatorId());
            return success;
        }
        return false;
    }

    @Override
    public Boolean save(SysOperator sysOperator) {
        if(sysOperator == null){
            return false;
        }
        if(iService.save(sysOperator)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean updateById(SysOperator sysOperator) {
        if(sysOperator == null || sysOperator.getId() <= 0){
            return false;
        }
        if(sysOperator.getOperatorId() != null){
            SysOperator oldData = iService.getById(sysOperator.getId());
            if(oldData == null){
                return false;
            }
            if(!oldData.getOperatorId().equals(sysOperator.getOperatorId())){
                return false;
            }
        }
        if(iService.updateById(sysOperator)){
            RedisUtils.deleteObject(HlhtRedisKey.SYS_OPERATOR + sysOperator.getOperatorId());
            return true;
        }
        else{
            return false;
        }
    }

}
