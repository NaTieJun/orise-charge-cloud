package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.domain.mapper.SysConnectorMapper;
import org.dromara.omind.baseplat.domain.service.SysConnectorIService;
import org.springframework.stereotype.Service;

@Service
public class SysConnectorIServiceImpl extends ServiceImpl<SysConnectorMapper, SysConnector> implements SysConnectorIService {
}
