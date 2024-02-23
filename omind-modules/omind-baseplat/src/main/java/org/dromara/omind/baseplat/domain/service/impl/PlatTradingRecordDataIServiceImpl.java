package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.dromara.omind.baseplat.domain.mapper.PlatTradingRecordDataMapper;
import org.dromara.omind.baseplat.domain.service.PlatTradingRecordDataIService;
import org.springframework.stereotype.Service;

@Service
public class PlatTradingRecordDataIServiceImpl extends ServiceImpl<PlatTradingRecordDataMapper, PlatTradingRecordData> implements PlatTradingRecordDataIService {
}
