package org.dromara.omind.userplat.service;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindFeedbackListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.api.domain.request.FeedbackRequest;

public interface OmindFeedbackService {

    void feedback(FeedbackRequest feedbackRequest, Long uid);

    TableDataInfo page(Long uid, PageQuery pageQuery);

    OmindFeedbackEntity info(Long id);

    String validateFeedbackFields(OmindFeedbackEntity odFeedback);

    String validateFeedbackListlFields(OmindFeedbackListDto omindFeedbackListDto);

    TableDataInfo<OmindFeedbackEntity> selectFeedbackList(OmindFeedbackListDto omindFeedbackListDto, PageQuery pageQuery);

    OmindFeedbackEntity updateFeedbackReply(OmindFeedbackEntity odFeedback) throws BaseException;
}
