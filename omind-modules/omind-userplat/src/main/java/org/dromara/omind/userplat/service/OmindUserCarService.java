package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarCheckDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarInsertDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarListDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarUpdateDto;
import org.dromara.omind.userplat.api.domain.entity.OmindUserCarEntity;

import java.util.List;

public interface OmindUserCarService {

    OmindUserCarEntity get(Long uid, String plateNo);
    /**
     * 添加车牌号
     * @return
     */
    void insert(OmindUserCarInsertDto userCarInsertDto);

    /**
     * 删除车牌号
     * @param id
     * @return
     */
    void del(Long id);

    /**
     * 车辆详情
     * @param id
     * @return
     */
    OmindUserCarEntity info(Long id);

    /**
     * 车辆列表
     * @return
     */
    List<OmindUserCarEntity> list(Long uid);

    /**
     * 修改车牌号
     * @return
     */
    void upCar(OmindUserCarUpdateDto userCarUpdateDto);

    int checkUserCar(OmindUserCarCheckDto omindUserCarCheckDto) throws BaseException;

    OmindUserCarEntity updateUserCar(OmindUserCarEntity odUserCar) throws BaseException;

    Boolean checkUserCarUnique(OmindUserCarEntity odUserCar);

    String validateUserCarListlFields(OmindUserCarListDto omindUserCarListDto);

    TableDataInfo<OmindUserCarEntity> selectUserCarList(OmindUserCarListDto omindUserCarListDto, PageQuery pageQuery);
}
