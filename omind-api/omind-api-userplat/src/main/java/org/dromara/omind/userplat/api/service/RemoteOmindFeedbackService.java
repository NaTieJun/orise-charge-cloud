package org.dromara.omind.userplat.api.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.api.domain.request.FeedbackRequest;

public interface RemoteOmindFeedbackService {

    void feedback(FeedbackRequest feedbackRequest, Long uid) throws BaseException;

    TableDataInfo page(Long uid, PageQuery pageQuery) throws BaseException;

    OmindFeedbackEntity info(Long id);

}
