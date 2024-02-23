package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.domain.mapper.SysOperatorMapper;
import org.dromara.omind.baseplat.domain.service.SysOperatorIService;
import org.springframework.stereotype.Service;

@Service
public class SysOperatorIServiceImpl extends ServiceImpl<SysOperatorMapper, SysOperator> implements SysOperatorIService {
}
