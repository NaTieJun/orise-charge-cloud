package org.dromara.omind.userplat.service.hlht;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.api.domain.dto.HlhtTokenDto;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;

public interface HlhtTokenService {

    /**
     * 生成运营商访问token
     * @param hlhtDto
     * @return
     */
    HlhtResult getToken(HlhtDto hlhtDto);

    /**
     * 查询运营商token
     * @param userOperatorId
     * @param platType
     * @return
     * @throws BaseException
     */
    HlhtTokenDto queryToken(String userOperatorId, Short platType) throws BaseException;

    /**
     * 检查token是否有效
     * @param userOperatorId
     * @param platType
     * @return
     * @throws BaseException
     */
    String checkToken(String userOperatorId, Short platType) throws BaseException;

    /**
     * 组装请求数据data
     * @param odOperatorInfoEntity
     * @return
     * @throws BaseException
     */
    String getTokenRequestData(OmindOperatorEntity odOperatorInfoEntity) throws BaseException;

    /**
     * 组装请求sig
     * @param odOperatorInfoEntity
     * @param timeStamp
     * @param seq
     * @return
     * @throws BaseException
     */
    String getRequestSig(OmindOperatorEntity odOperatorInfoEntity, String timeStamp, String seq) throws BaseException;
}
