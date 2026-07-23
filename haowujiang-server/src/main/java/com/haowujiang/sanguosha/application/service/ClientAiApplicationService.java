package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.interfaces.vo.ai.request.ClientAiChatReqVo;
import com.haowujiang.sanguosha.interfaces.vo.ai.response.ClientAiChatRespVo;

public interface ClientAiApplicationService {

    ClientAiChatRespVo chat(ClientAiChatReqVo reqVo);
}
