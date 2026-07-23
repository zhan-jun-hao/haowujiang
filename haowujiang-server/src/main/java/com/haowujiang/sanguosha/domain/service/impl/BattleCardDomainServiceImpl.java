package com.haowujiang.sanguosha.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.haowujiang.sanguosha.infrastructure.enums.BattleLogType;
import com.haowujiang.sanguosha.infrastructure.enums.BattleTiming;
import com.haowujiang.sanguosha.infrastructure.enums.CardKind;
import com.haowujiang.sanguosha.infrastructure.enums.Side;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.model.*;
import com.haowujiang.sanguosha.domain.service.BattleCardDomainService;
import com.haowujiang.sanguosha.domain.skill.BattleEventBus;
import com.haowujiang.sanguosha.domain.skill.SkillRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 卡牌操作领域服务 —— 处理杀 / 闪 / 桃 / 酒的使用、结算和伤害。
 * <p>
 * 这是战斗系统的「物理引擎」：负责牌的合法性检查、效果结算、
 * 在关键节点发布 {@link BattleTiming 时机窗口} 事件给技能系统。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BattleCardDomainServiceImpl implements BattleCardDomainService {

    private final SkillRegistry skillRegistry;
    private final BattleEventBus battleEventBus;

    // ========================
    // 找牌
    // ========================

    @Override
    public BattleCard findFirstCard(BattleActor actor, CardKind kind) {
        return actor.getHand().stream()
                .filter(card -> card.getKind() == kind)
                .findFirst()
                .orElse(null);
    }

    @Override
    public BattleCard findAttackCard(BattleState state, BattleActor actor) {
        // 先找真正的杀
        BattleCard sha = findFirstCard(actor, CardKind.SHA);
        if (sha != null) {
            return sha;
        }

        // 再找可以通过技能当杀用的牌（如龙胆：闪当杀）
        return actor.getHand().stream()
                .filter(card -> canUseAs(state, actor, card, CardKind.SHA))
                .findFirst()
                .orElse(null);
    }

    // ========================
    // 规则判断（委托给 SkillRegistry）
    // ========================

    @Override
    public boolean canUseSha(BattleState state, BattleActor actor, BattleCard card) {
        // shaUsed == 0 表示本回合还没出过杀，或者有咆哮等技能无视次数限制
        return actor.getShaUsed() == 0
                || skillRegistry.ignoreShaLimit(
                new BattleContext(state),
                actor,
                card,
                CardKind.SHA
        );
    }

    @Override
    public boolean canUseAs(BattleState state,
                            BattleActor actor,
                            BattleCard card,
                            CardKind targetKind) {
        return skillRegistry.canUseAs(actor, card, targetKind);
    }

    // ========================
    // 出牌主入口
    // ========================

    @Override
    public void useCard(BattleState state, BattleActor actor, BattleActor target,
                        BattleCard card, boolean aiControlled) {

        // 能当杀用 → 按杀结算
        if (canUseAs(state, actor, card, CardKind.SHA)) {
            useSha(state, actor, target, card);
            return;
        }

        // 桃
        if (card.getKind() == CardKind.TAO) {
            useTao(state, actor, card);
            return;
        }

        // 酒
        if (card.getKind() == CardKind.JIU) {
            useJiu(state, actor, card);
            return;
        }

        // 无法使用的牌（AI 不出提示，避免刷屏）
        if (!aiControlled) {
            addLog(state, BattleLogType.SYSTEM, "无法使用该牌");
        }
    }

    /**
     * 使用「杀」—— 检查次数限制，出牌，决定是否需要等待响应。
     */
    private void useSha(BattleState state,
                        BattleActor actor,
                        BattleActor target,
                        BattleCard card) {

        // 检查出杀次数（没有咆哮技能且本回合已出过杀 → 不能出）
        if (!canUseSha(state, actor, card)) {
            addLog(state, logTypeForSide(actor.getSide()),
                    actor.getName() + " 本回合杀已用完");
            return;
        }

        // 从手牌移除，放入弃牌堆
        removeCard(actor, card);
        state.getDiscard().add(card);
        actor.setShaUsed(actor.getShaUsed() + 1);

        addLog(state, logTypeForSide(actor.getSide()),
                actor.getName() + " 对 " + target.getName() + " 使用杀");

        // 发布 CARD_USED 事件
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.CARD_USED, actor, target, card)
        );

        // 目标为玩家 → 设置 pendingResponse，等待玩家选择出闪或不出
        if (target.getSide() == Side.PLAYER) {
            int damage = actor.isDrunk() ? 2 : 1;
            state.setPendingResponse(
                    BattlePendingResponse.sha(actor, target, card, damage)
            );
            return;
        }

        // 目标为 AI → 直接结算（AI 自动决定出闪还是吃伤害）
        resolveAttack(state, actor, target, card);
    }

    // ========================
    // 杀 → 结算
    // ========================

    /**
     * 结算攻击：AI 找闪防御，有闪抵消，无闪扣血。
     */
    private void resolveAttack(BattleState state,
                               BattleActor attacker,
                               BattleActor defender,
                               BattleCard attackCard) {

        // 找闪（包括技能转化的，如龙胆：杀当闪）
        BattleCard defense = findDefenseCard(state, defender);

        if (defense != null) {
            // 有闪 → 抵消攻击
            removeCard(defender, defense);
            state.getDiscard().add(defense);

            addLog(state, logTypeForSide(defender.getSide()),
                    defender.getName() + " 使用闪");

            attacker.setDrunk(false);

            battleEventBus.publish(
                    state,
                    new BattleEvent(BattleTiming.CARD_USED, defender, attacker, defense)
            );
            return;
        }

        // 没闪 → 受到伤害
        int damage = attacker.isDrunk() ? 2 : 1;
        attacker.setDrunk(false);

        applyDamage(state, attacker, defender, attackCard, damage);
    }

    // ========================
    // 玩家响应机制
    // ========================

    @Override
    public void respondToPendingAttack(BattleState state,
                                       BattleActor defender,
                                       BattleCard card) {

        BattlePendingResponse pending = requirePendingResponse(state);

        if (defender.getSide() != pending.getDefenderSide()) {
            throw new BusinessException("不是响应方");
        }

        // 检查能否当闪（直接是闪 或 通过技能转化）
        if (!canUseAs(state, defender, card, CardKind.SHAN)) {
            throw new BusinessException("不能用该牌当闪");
        }

        BattleActor attacker = findActorBySide(state, pending.getAttackerSide());

        // 出闪 → 从手牌移除
        removeCard(defender, card);
        state.getDiscard().add(card);

        addLog(state, logTypeForSide(defender.getSide()),
                defender.getName() + " 使用闪");

        attacker.setDrunk(false);
        state.setPendingResponse(null);

        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.CARD_USED, defender, attacker, card)
        );
    }

    @Override
    public void passPendingAttack(BattleState state) {
        BattlePendingResponse pending = requirePendingResponse(state);

        BattleActor attacker = findActorBySide(state, pending.getAttackerSide());
        BattleActor defender = findActorBySide(state, pending.getDefenderSide());

        attacker.setDrunk(false);
        state.setPendingResponse(null);

        // 不出闪 → 直接吃伤害
        applyDamage(state, attacker, defender, pending.getAttackCard(), pending.getDamage());
    }

    // ========================
    // 桃
    // ========================

    /**
     * 使用「桃」—— 体力未满时回复 1 点体力。
     */
    private void useTao(BattleState state,
                        BattleActor actor,
                        BattleCard card) {

        if (actor.getHp() >= actor.getMaxHp()) {
            addLog(state, logTypeForSide(actor.getSide()),
                    actor.getName() + " 体力已满");
            return;
        }

        removeCard(actor, card);
        state.getDiscard().add(card);

        actor.setHp(Math.min(actor.getMaxHp(), actor.getHp() + 1));

        addLog(state, BattleLogType.HEAL,
                actor.getName() + " 使用桃回复1点体力");

        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.CARD_USED, actor, null, card)
        );
    }

    // ========================
    // 酒
    // ========================

    /**
     * 使用「酒」—— 进入酒状态，下一张杀的伤害 +1。
     * <p>一回合只能喝一次酒，且不能叠加。</p>
     */
    private void useJiu(BattleState state,
                        BattleActor actor,
                        BattleCard card) {

        if (actor.isDrunk()) {
            addLog(state, logTypeForSide(actor.getSide()),
                    actor.getName() + " 已经喝过酒");
            return;
        }

        removeCard(actor, card);
        state.getDiscard().add(card);

        actor.setDrunk(true);

        addLog(state, logTypeForSide(actor.getSide()),
                actor.getName() + " 使用酒");

        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.CARD_USED, actor, null, card)
        );
    }

    // ========================
    // 伤害结算（关键的 Timing 发布点）
    // ========================

    /**
     * 造成伤害 —— 先后发布 DAMAGE_BEFORE 和 DAMAGE_AFTER 事件。
     * <p>
     * 这是技能系统最重要的触发点：
     * <ul>
     *   <li>DAMAGE_BEFORE —— 适合「防止伤害」（如「谦逊」）、「伤害+1」（如「裸衣」）、「转移伤害」</li>
     *   <li>DAMAGE_AFTER —— 适合「受伤后获得牌」（如「奸雄」）、「受伤摸牌」（如「遗计」）、「受伤判定」（如「刚烈」）</li>
     * </ul>
     * </p>
     */
    private void applyDamage(BattleState state,
                             BattleActor source,
                             BattleActor target,
                             BattleCard card,
                             int damage) {

        // 伤害结算前 —— 可以在这里防止伤害或修改伤害值
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.DAMAGE_BEFORE, source, target, card)
        );

        // 扣血
        target.setHp(Math.max(0, target.getHp() - damage));

        addLog(state, BattleLogType.DAMAGE,
                target.getName() + " 受到 " + damage + " 点伤害");

        // 伤害结算后 —— 触发受伤技能（如奸雄）
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.DAMAGE_AFTER, source, target, card)
        );

        // 检查是否被击败
        if (target.getHp() <= 0) {
            target.setDefeated(true);
            state.setWinner(source.getSide());

            addLog(state, BattleLogType.SYSTEM,
                    source.getName() + " 获胜");
        }
    }

    // ========================
    // 辅助方法
    // ========================

    /**
     * 查找防御牌（闪），包括可以通过技能转化的（如龙胆：杀当闪）。
     */
    private BattleCard findDefenseCard(BattleState state, BattleActor actor) {
        BattleCard shan = findFirstCard(actor, CardKind.SHAN);
        if (shan != null) {
            return shan;
        }

        return actor.getHand().stream()
                .filter(card -> canUseAs(state, actor, card, CardKind.SHAN))
                .findFirst()
                .orElse(null);
    }

    private BattlePendingResponse requirePendingResponse(BattleState state) {
        if (state.getPendingResponse() == null) {
            throw new BusinessException("没有待响应");
        }
        return state.getPendingResponse();
    }

    private BattleActor findActorBySide(BattleState state, Side side) {
        if (state.getPlayer().getSide() == side) {
            return state.getPlayer();
        }
        if (state.getEnemy().getSide() == side) {
            return state.getEnemy();
        }
        throw new BusinessException("角色不存在");
    }

    private void removeCard(BattleActor actor, BattleCard card) {
        actor.getHand().removeIf(c -> c.getId().equals(card.getId()));
    }

    private void addLog(BattleState state, BattleLogType type, String msg) {
        state.getLogs().add(0,
                new BattleLog(
                        "log-" + IdUtil.randomUUID(),
                        type,
                        msg
                )
        );
    }

    private static BattleLogType logTypeForSide(Side side) {
        return side == Side.PLAYER ? BattleLogType.PLAYER : BattleLogType.ENEMY;
    }
}
