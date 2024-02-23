package org.dromara.omind.userplat.api.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarInsertDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarUpdateDto;
import org.dromara.omind.userplat.api.domain.entity.OmindUserCarEntity;

import java.util.List;

public interface RemoteOmindUserCarService {

    OmindUserCarEntity get(Long uid, String plateNo);
    /**
     * 添加车牌号
     * @return
     */
    void insert(OmindUserCarInsertDto userCarInsertDto) throws BaseException;

    /**
     * 删除车牌号
     * @param id
     * @return
     */
    void del(Long id) throws BaseException;

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
    void upCar(OmindUserCarUpdateDto userCarUpdateDto) throws BaseException;

}
