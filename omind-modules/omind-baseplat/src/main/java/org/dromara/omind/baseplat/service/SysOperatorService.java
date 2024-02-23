package org.dromara.omind.baseplat.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysOperatorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;

public interface SysOperatorService {

    SysOperator getOperatorById(String operatorId);

    /**
     * 判断运营商ID是否已存在
     * @param operatorId
     * @return
     */
    Boolean isOperatorIdValid(String operatorId);

    TableDataInfo<SysOperator> getOperatorPageList(QuerySysOperatorDto querySysOperatorDto, PageQuery pageQuery);

    Boolean remove(SysOperator sysOperator);

    Boolean save(SysOperator sysOperator);

    Boolean updateById(SysOperator sysOperator);

}
