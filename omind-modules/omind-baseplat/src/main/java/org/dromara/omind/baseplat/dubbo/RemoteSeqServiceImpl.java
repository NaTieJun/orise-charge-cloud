package org.dromara.omind.baseplat.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class RemoteSeqServiceImpl implements RemoteSeqService {

    @Override
    public String getHlhtRequestSeq() {
        return RedisUtils.incrAtomicValue(HlhtRedisKey.HLHT_API_SEQ_KEY + (System.currentTimeMillis()/1000)) + "";
    }

}
