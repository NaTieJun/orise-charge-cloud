package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindOperationListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;

import java.util.List;

public interface OmindOperatorService {

    OmindOperatorEntity selectOperatorInfoById(String operatorId);

    OmindOperatorEntity selectOperatorInfo(String userOperatorId, Short platType);

    OmindOperatorEntity getOperatorInfo(String baseOperatorId, String userOperatorId);

    TableDataInfo<OmindOperatorEntity> selectOperatorInfoList(OmindOperationListDto omindOperationListDto, PageQuery pageQuery);

    boolean insertOperatorInfo(OmindOperatorEntity odOperatorInfo) throws BaseException;

    boolean updateOperatorInfo(OmindOperatorEntity odOperatorInfo) throws BaseException;

    Boolean checkOperatorInfoUnique(OmindOperatorEntity odOperatorInfo);

    String validateOperatorFields(OmindOperatorEntity odOperatorInfo);

    OmindOperatorEntity getOperatorInfoById(Integer id);

    OmindOperatorEntity resetSecret(String operatorId) throws BaseException;

    int deleteOperatorInfoById(String operatorId) throws BaseException;

    List<OmindOperatorEntity> selectOperatorList();
}
