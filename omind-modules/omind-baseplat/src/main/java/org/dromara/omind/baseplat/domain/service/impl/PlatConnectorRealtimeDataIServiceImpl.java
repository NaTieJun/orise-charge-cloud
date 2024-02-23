package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.PlatConnectorRealtimeData;
import org.dromara.omind.baseplat.domain.mapper.PlatConnectorRealtimeDataMapper;
import org.dromara.omind.baseplat.domain.service.PlatConnectorRealtimeDataIService;
import org.springframework.stereotype.Service;

@Service
public class PlatConnectorRealtimeDataIServiceImpl extends ServiceImpl<PlatConnectorRealtimeDataMapper, PlatConnectorRealtimeData> implements PlatConnectorRealtimeDataIService {
}
