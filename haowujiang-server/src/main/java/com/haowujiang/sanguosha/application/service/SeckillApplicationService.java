package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillStateRespVo;

public interface SeckillApplicationService {

    SeckillStateRespVo getState(String generalCode);

    SeckillResultRespVo claim(Long userId, String generalCode);
}
