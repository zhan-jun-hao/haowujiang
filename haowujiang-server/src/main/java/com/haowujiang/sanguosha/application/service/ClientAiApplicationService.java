package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.application.vo.ai.request.ClientAiChatReqVo;
import com.haowujiang.sanguosha.application.vo.ai.response.ClientAiChatRespVo;

public interface ClientAiApplicationService {

    ClientAiChatRespVo chat(ClientAiChatReqVo reqVo);
}
