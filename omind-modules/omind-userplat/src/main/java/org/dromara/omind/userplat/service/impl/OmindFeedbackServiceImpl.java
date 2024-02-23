package org.dromara.omind.userplat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindFeedbackListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.api.domain.request.FeedbackRequest;
import org.dromara.omind.userplat.domain.service.OmindFeedbackEntityIService;
import org.dromara.omind.userplat.service.OmindFeedbackService;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OmindFeedbackServiceImpl implements OmindFeedbackService {

    @Autowired
    @Lazy
    OmindFeedbackEntityIService iService;

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    @Autowired
    @Lazy
    OmindFeedbackService selfService;

    @Override
    public void feedback(FeedbackRequest feedbackRequest, Long uid) {
        if(uid == null || uid <= 0){
            throw new BaseException("用户不存在");
        }
        if(TextUtils.isBlank(feedbackRequest.getTxt())){
            throw new BaseException("反馈内容不能为空");
        }
        OmindFeedbackEntity feedback = new OmindFeedbackEntity();
        feedback.setUserId(uid);
        feedback.setConnectorId(feedbackRequest.getConnectorId());
        feedback.setFeedbackType(feedbackRequest.getType());
        feedback.setFeedbackContent(feedbackRequest.getTxt());
        feedback.setImgs(feedbackRequest.getImgArrayJson());
        if(!iService.save(feedback)){
            throw new BaseException("提交失败");
        }
    }

    @Override
    public TableDataInfo page(Long uid, PageQuery pageQuery) {
        if(uid == null || uid <= 0){
            throw new BaseException("用户id无效");
        }

        OmindUserEntity omindUser = omindUserService.getUserById(uid);
        if(omindUser == null){
            throw new BaseException("用户不存在");
        }
        LambdaQueryWrapper<OmindFeedbackEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindFeedbackEntity::getUserId, uid);
        lambdaQueryWrapper.orderByDesc(OmindFeedbackEntity::getId);

        Page<OmindFeedbackEntity> page = iService.page(pageQuery.build(), lambdaQueryWrapper);
        if(page.getRecords() != null && page.getSize() > 0){
            for(OmindFeedbackEntity omindFeedback : page.getRecords()){
                List<String> picUrlList = new ArrayList<>();
                if(!TextUtils.isBlank(omindFeedback.getImgs())){
                    String picStr = omindFeedback.getImgs();
                    try{
                        JSONArray jsonArray = JSONArray.parseArray(picStr);
                        if(jsonArray != null && jsonArray.size() > 0){
                            int size = jsonArray.size();
                            for(int i = 0; i<size;i++) {
                                String path = jsonArray.getString(i);
                                String url = "";//minioS3Utils.getAccessUrl(path);
                                //todo
                                if(!TextUtils.isBlank(url)){
                                    picUrlList.add(url);
                                }
                            }
                        }
                    }
                    catch (Exception ex){
                        log.error(ex.toString());
                    }
                }

                omindFeedback.setImgUrls(picUrlList);
                OmindUserEntity odUserEntity = omindUserService.getUserById(omindFeedback.getUserId());
                String userName = !TextUtils.isBlank(odUserEntity.getNickName()) ? odUserEntity.getNickName() : odUserEntity.getWechatName();
                omindFeedback.setUserName(userName);
                omindFeedback.setMobile(odUserEntity.getMobile());
                omindFeedback.setAvatar(odUserEntity.getAvatar());
                omindFeedback.setFeedbackTypeName(getFeedbackTypeNameByTypeId(omindFeedback.getFeedbackType()));
            }
        }
        return TableDataInfo.build(page);
    }

    @Override
    public OmindFeedbackEntity info(Long id) {
        if(id == null || id <= 0){
            return null;
        }
        OmindFeedbackEntity odUserFeedbackEntity = iService.getById(id);
        if(odUserFeedbackEntity != null){
            List<String> picUrlList = new ArrayList<>();
            if(!TextUtils.isBlank(odUserFeedbackEntity.getImgs())){
                String picStr = odUserFeedbackEntity.getImgs();
                try{
                    JSONArray jsonArray = JSONArray.parseArray(picStr);
                    if(jsonArray != null && jsonArray.size() > 0){
                        int size = jsonArray.size();
                        for(int i = 0; i<size;i++) {
                            String path = jsonArray.getString(i);
                            String url = "";//minioS3Utils.getAccessUrl(path);
                            //todo
                            if(!TextUtils.isBlank(url)){
                                picUrlList.add(url);
                            }
                        }
                    }
                }
                catch (Exception ex){
                    log.error(ex.toString());
                }
            }

            odUserFeedbackEntity.setImgUrls(picUrlList);
            OmindUserEntity odUserEntity = omindUserService.getUserById(odUserFeedbackEntity.getUserId());
            String userName = !TextUtils.isBlank(odUserEntity.getNickName()) ? odUserEntity.getNickName() : odUserEntity.getWechatName();
            odUserFeedbackEntity.setUserName(userName);
            odUserFeedbackEntity.setMobile(odUserEntity.getMobile());
            odUserFeedbackEntity.setAvatar(odUserEntity.getAvatar());
            odUserFeedbackEntity.setFeedbackTypeName(getFeedbackTypeNameByTypeId(odUserFeedbackEntity.getFeedbackType()));
        }
        return odUserFeedbackEntity;
    }

    @Override
    public String validateFeedbackFields(OmindFeedbackEntity odFeedback) {
        String msg = "";
        if(odFeedback.getFeedbackContent() != null){
            if(odFeedback.getFeedbackContent().length() > 256){
                return msg = "回复内容过长";
            }
        }
        return msg;
    }

    @Override
    public String validateFeedbackListlFields(OmindFeedbackListDto omindFeedbackListDto) {
        String msg = "";
        if (!TextUtils.isBlank(omindFeedbackListDto.getNickName()) && omindFeedbackListDto.getNickName().length() > 64) {
            return msg = "用户名太长啦";
        }
        if (!TextUtils.isBlank(omindFeedbackListDto.getMobile()) && omindFeedbackListDto.getMobile().length() > 11) {
            return msg = "手机号太长啦";
        }
        return msg;
    }

    @Override
    public TableDataInfo<OmindFeedbackEntity> selectFeedbackList(OmindFeedbackListDto omindFeedbackListDto, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindFeedbackEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(omindFeedbackListDto.getNickName())) {
            List<OmindUserEntity> odUserList = omindUserService.selectUserListByNickName(omindFeedbackListDto.getNickName());
            List<Long> userIdList = new ArrayList<>();
            if (odUserList.size() > 0) {
                for (OmindUserEntity userInfo : odUserList) {
                    userIdList.add(userInfo.getUid());
                }
                lambdaQueryWrapper.in(OmindFeedbackEntity::getUserId, userIdList);
            } else {
                //返回空结果
                lambdaQueryWrapper.isNull(OmindFeedbackEntity::getId);
            }
        }
        if (!TextUtils.isBlank(omindFeedbackListDto.getMobile())) {
            List<OmindUserEntity> odUserList = omindUserService.selectUserListByMobile(omindFeedbackListDto.getMobile());
            List<Long> userIdList = new ArrayList<>();
            if (odUserList.size() > 0) {
                for (OmindUserEntity userInfo : odUserList) {
                    userIdList.add(userInfo.getUid());
                }
                lambdaQueryWrapper.in(OmindFeedbackEntity::getUserId, userIdList);
            } else {
                //返回空结果
                lambdaQueryWrapper.isNull(OmindFeedbackEntity::getId);
            }
        }
        lambdaQueryWrapper.orderByDesc(OmindFeedbackEntity::getId);

        Page<OmindFeedbackEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindFeedbackEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            for (OmindFeedbackEntity odUserFeedbackEntity : iPage.getRecords()) {
                OmindUserEntity odUserEntity = omindUserService.getUserById(odUserFeedbackEntity.getUserId());
                String userName = !TextUtils.isBlank(odUserEntity.getNickName()) ? odUserEntity.getNickName() : odUserEntity.getWechatName();
                odUserFeedbackEntity.setUserName(userName);
                odUserFeedbackEntity.setMobile(odUserEntity.getMobile());
                String avatar = "";
                if(odUserEntity.getAvatar() != null &&  !odUserEntity.getAvatar().startsWith("http")){
                    avatar = "";//minioS3Utils.getAccessUrl(odUserEntity.getAvatar());
                }
                odUserFeedbackEntity.setAvatar(avatar);

                odUserFeedbackEntity.setFeedbackTypeName(getFeedbackTypeNameByTypeId(odUserFeedbackEntity.getFeedbackType()));
            }
        }
        return TableDataInfo.build(iPage);
    }

    @Override
    public OmindFeedbackEntity updateFeedbackReply(OmindFeedbackEntity odFeedback) throws BaseException {
        OmindFeedbackEntity ldUserFeedbackEntity = selfService.info(odFeedback.getId());
        if (ldUserFeedbackEntity == null) {
            throw new BaseException("数据不存在");
        }

        OmindFeedbackEntity updateFeedbackObjData = new OmindFeedbackEntity();
        updateFeedbackObjData.setId(ldUserFeedbackEntity.getId());
        updateFeedbackObjData.setReplyFlag(odFeedback.getReplyFlag());
        updateFeedbackObjData.setReplyContent(odFeedback.getReplyContent());

        if (!iService.updateById(updateFeedbackObjData)) {
            throw new BaseException("数据更新失败");
        }

        return selfService.info(odFeedback.getId());
    }

    private String getFeedbackTypeNameByTypeId(Short feedbackType){
        String feedbackTypeName = "其他";
        switch (feedbackType) {
            case 1:
                feedbackTypeName = "充电站";
                break;
            case 2:
                feedbackTypeName = "充电桩";
                break;
            case 3:
                feedbackTypeName = "充电枪";
                break;
            case 50:
                feedbackTypeName = "其他";
                break;
            default:
                feedbackTypeName = "其他";
        }
        return feedbackTypeName;
    }
}
