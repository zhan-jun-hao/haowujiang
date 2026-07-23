package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillStateRespVo;
import java.util.List;

public interface SeckillApplicationService {

    SeckillStateRespVo getState(String generalCode);

    SeckillResultRespVo claim(Long userId, String generalCode);

    List<SeckillActivityRespVo> listActiveActivities();
}
