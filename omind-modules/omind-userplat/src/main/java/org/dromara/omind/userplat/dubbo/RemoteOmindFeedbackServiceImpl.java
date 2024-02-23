package org.dromara.omind.userplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.api.domain.request.FeedbackRequest;
import org.dromara.omind.userplat.api.service.RemoteOmindFeedbackService;
import org.dromara.omind.userplat.service.OmindFeedbackService;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteOmindFeedbackServiceImpl implements RemoteOmindFeedbackService {

    @Autowired
    @Lazy
    OmindFeedbackService omindFeedbackService;

    @Override
    public void feedback(FeedbackRequest feedbackRequest, Long uid) {
        omindFeedbackService.feedback(feedbackRequest,uid);
    }

    @Override
    public TableDataInfo page(Long uid, PageQuery pageQuery) {
        return omindFeedbackService.page(uid,pageQuery);
    }

    @Override
    public OmindFeedbackEntity info(Long id) {
        return omindFeedbackService.info(id);
    }
}
