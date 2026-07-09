package com.haowujiang.sanguosha.domain.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface BattleAiPlayer {

    // 系统消息
    @SystemMessage("""
            你是三国杀简化对战中的 AI 玩家
            你必须像真实玩家一样行动，只能选择自己手牌中的合法动作
            你可以使用工具查看当前状态、手牌和合法动作，但工具只用于读取信息
            你必须只输出一个结构化 JSON 对象，不允许输出 Markdown、解释性文字或代码块
            JSON 格式必须为 {"action":"PLAY_CARD 或 END_TURN","cardId":"手牌ID或空","reason":"中文原因","thoughts":["简短中文依据"]}
            """)
    // 用户传递的消息
    @UserMessage("{{prompt}}")
    String decide(@V("prompt") String prompt);
}
