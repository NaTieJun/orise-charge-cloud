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
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarCheckDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarInsertDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarListDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarUpdateDto;
import org.dromara.omind.userplat.api.domain.entity.OmindBillEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindUserCarEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindUserCarEntityIService;
import org.dromara.omind.userplat.service.OmindUserCarService;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OmindUserCarServiceImpl implements OmindUserCarService {

    @Autowired
    @Lazy
    OmindUserCarEntityIService iService;

    @Autowired
    @Lazy
    OmindUserCarService selfService;

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    @Override
    public OmindUserCarEntity get(Long uid, String plateNo) {
        if(uid == null || uid <= 0 || TextUtils.isBlank(plateNo)){
            return null;
        }
        LambdaQueryWrapper<OmindUserCarEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OmindUserCarEntity::getUserId, uid);
        queryWrapper.eq(OmindUserCarEntity::getPlateNo, plateNo);
        queryWrapper.last("limit 1");

        return iService.getOne(queryWrapper);
    }

    @Override
    public void insert(OmindUserCarInsertDto userCarInsertDto) {
        if(userCarInsertDto.getUserId() == null || userCarInsertDto.getUserId() <= 0){
            throw new BaseException("用户ID错误");
        }
        if(TextUtils.isBlank(userCarInsertDto.getPlateNo())){
            throw new BaseException("车牌号错误");
        }
        if(selfService.get(userCarInsertDto.getUserId(), userCarInsertDto.getPlateNo()) != null){
            throw new BaseException("车牌号已存在");
        }
        OmindUserCarEntity omindUserCarEntity = new OmindUserCarEntity();
        omindUserCarEntity.setUserId(userCarInsertDto.getUserId());
        omindUserCarEntity.setPlateNo(userCarInsertDto.getPlateNo());
        if(!iService.save(omindUserCarEntity)){
            throw new BaseException("保存失败");
        }
        return;
    }

    @Override
    public void del(Long id) {
        if(!iService.removeById(id)){
            throw new BaseException("移除失败");
        }
    }

    @Override
    public OmindUserCarEntity info(Long id) {
        if(id == null || id <= 0)
        {
            return null;
        }
        return iService.getById(id);
    }

    @Override
    public List<OmindUserCarEntity> list(Long uid) {
        if(uid == null || uid <= 0){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OmindUserCarEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OmindUserCarEntity::getUserId, uid);
        queryWrapper.orderByDesc(OmindUserCarEntity::getUserId);
        return iService.list(queryWrapper);
    }

    @Override
    public void upCar(OmindUserCarUpdateDto userCarUpdateDto) {
        if(userCarUpdateDto == null || userCarUpdateDto.getId() == null || userCarUpdateDto.getId() <= 0){
            throw new BaseException("无效的数据");
        }
        OmindUserCarEntity upCar = new OmindUserCarEntity();
        upCar.setId(userCarUpdateDto.getId());
        upCar.setUserId(userCarUpdateDto.getUserId());
        upCar.setPlateNo(userCarUpdateDto.getPlateNo());
        if(!iService.updateById(upCar)){
            throw new BaseException("更新失败");
        }
    }

    @Override
    public int checkUserCar(OmindUserCarCheckDto omindUserCarCheckDto) throws BaseException {
        if (omindUserCarCheckDto.getId() == null || omindUserCarCheckDto.getId() <= 0) {
            throw new BaseException("车辆id参数无效");
        }
        if (omindUserCarCheckDto.getCheckState() == null || omindUserCarCheckDto.getCheckState() < 0
                || omindUserCarCheckDto.getCheckState() > 3) {
            throw new BaseException("车辆状态参数无效");
        }
        OmindUserCarEntity odUserCarEntity = selfService.info(omindUserCarCheckDto.getId());
        if (odUserCarEntity == null) {
            throw new BaseException("车辆信息不存在");
        }
        odUserCarEntity.setCheckState(omindUserCarCheckDto.getCheckState());
        selfService.updateUserCar(odUserCarEntity);

        return 0;
    }

    @Override
    public OmindUserCarEntity updateUserCar(OmindUserCarEntity odUserCar) throws BaseException {
        OmindUserCarEntity odUserCarEntity = selfService.info(odUserCar.getId());
        if (odUserCarEntity == null) {
            throw new BaseException("数据不存在");
        }

        boolean repeatFlag = selfService.checkUserCarUnique(odUserCar);
        if (repeatFlag) {
            throw new BaseException("数据已存在");
        }

        if (!iService.updateById(odUserCarEntity)) {
            throw new BaseException("数据更新失败");
        }

        //redis处理
        String userCarListKey = PlatRedisKey.USER_CAR_LIST + odUserCar.getUserId();
        if (RedisUtils.hasKey(userCarListKey)) {
            RedisUtils.deleteObject(userCarListKey);
        }

        String userCarInfoKey = PlatRedisKey.USER_CAR_INFO + odUserCar.getId();
        if (RedisUtils.hasKey(userCarInfoKey)) {
            RedisUtils.deleteObject(userCarInfoKey);
        }

        return odUserCarEntity;
    }

    @Override
    public Boolean checkUserCarUnique(OmindUserCarEntity odUserCar) {
        LambdaQueryWrapper<OmindUserCarEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindUserCarEntity::getUserId, odUserCar.getUserId());
        lambdaQueryWrapper.eq(OmindUserCarEntity::getPlateNo, odUserCar.getPlateNo());
        OmindUserCarEntity ldUserCarCheck = iService.getOne(lambdaQueryWrapper);

        boolean repeatFlag = false;
        if (ldUserCarCheck != null) {
            if (!Objects.equals(ldUserCarCheck.getId(), odUserCar.getId())) {
                repeatFlag = true;
            }
        }
        return repeatFlag;
    }

    @Override
    public String validateUserCarListlFields(OmindUserCarListDto omindUserCarListDto) {
        String msg = "";
        if (!TextUtils.isBlank(omindUserCarListDto.getCarVin()) && omindUserCarListDto.getCarVin().length() > 18) {
            return msg = "vin码太长啦";
        }

        if (!TextUtils.isBlank(omindUserCarListDto.getPlateNo()) && omindUserCarListDto.getPlateNo().length() > 10) {
            return msg = "车牌号太长啦";
        }

        if (!TextUtils.isBlank(omindUserCarListDto.getNickName()) && omindUserCarListDto.getNickName().length() > 64) {
            return msg = "充电用户名太长啦";
        }

        if (!TextUtils.isBlank(omindUserCarListDto.getMobile()) && omindUserCarListDto.getMobile().length() != 11) {
            return msg = "手机号长度错误";
        }
        return msg;
    }

    @Override
    public TableDataInfo<OmindUserCarEntity> selectUserCarList(OmindUserCarListDto omindUserCarListDto, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindUserCarEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(omindUserCarListDto.getNickName())) {
            List<OmindUserEntity> odUserList = omindUserService.selectUserListByNickName(omindUserCarListDto.getNickName());
            List<Long> userIdList = new ArrayList<>();
            if (odUserList.size() > 0) {
                for (OmindUserEntity userInfo : odUserList) {
                    userIdList.add(userInfo.getUid());
                }
                lambdaQueryWrapper.in(OmindUserCarEntity::getUserId, userIdList);
            } else {
                //返回空结果
                lambdaQueryWrapper.isNull(OmindUserCarEntity::getId);
            }
        }
        if (!TextUtils.isBlank(omindUserCarListDto.getMobile())) {
            List<OmindUserEntity> odUserList = omindUserService.selectUserListByMobile(omindUserCarListDto.getMobile());
            List<Long> userIdList = new ArrayList<>();
            if (odUserList.size() > 0) {
                for (OmindUserEntity userInfo : odUserList) {
                    userIdList.add(userInfo.getUid());
                }
                lambdaQueryWrapper.in(OmindUserCarEntity::getUserId, userIdList);
            } else {
                //返回空结果
                lambdaQueryWrapper.isNull(OmindUserCarEntity::getId);
            }

        }
        if (!TextUtils.isBlank(omindUserCarListDto.getPlateNo())) {
            lambdaQueryWrapper.like(OmindUserCarEntity::getPlateNo,omindUserCarListDto.getPlateNo().replace("%", "\\%"));
        }
        if (!TextUtils.isBlank(omindUserCarListDto.getCarVin())) {
            lambdaQueryWrapper.like(OmindUserCarEntity::getCarVin, omindUserCarListDto.getCarVin().replace("%","\\%"));
        }
        if (omindUserCarListDto.getCheckState() != null && omindUserCarListDto.getCheckState() >= 0) {
            lambdaQueryWrapper.eq(OmindUserCarEntity::getCheckState, omindUserCarListDto.getCheckState());
        }
        if (omindUserCarListDto.getAuthState() != null && omindUserCarListDto.getAuthState() >= 0) {
            lambdaQueryWrapper.eq(OmindUserCarEntity::getAuthState, omindUserCarListDto.getAuthState());
        }
        lambdaQueryWrapper.orderByDesc(OmindUserCarEntity::getId);

        Page<OmindUserCarEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindUserCarEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            if (iPage.getRecords().size() > 0) {
                for (OmindUserCarEntity ldUserCar : iPage.getRecords()) {
                    OmindUserEntity odUser = omindUserService.getUserById(ldUserCar.getUserId());
                    String nickName = "";
                    String mobile = "";
                    if (odUser != null) {
                        nickName = odUser.getNickName();
                        mobile = odUser.getMobile();
                    }
                    ldUserCar.setNickName(nickName);
                    ldUserCar.setMobile(mobile);
                }
            }
        }
        TableDataInfo<OmindUserCarEntity> tableDataInfo = TableDataInfo.build(iPage);
        return tableDataInfo;
    }
}
