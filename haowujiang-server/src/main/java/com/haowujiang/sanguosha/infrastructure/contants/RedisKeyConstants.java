package com.haowujiang.sanguosha.infrastructure.contants;

public interface RedisKeyConstants {

    /**
     * 秒杀武将key
     */
    String SECKILL_GENERAL_KEY_PREFIX = "seckill:general:";

    /**
     * 秒杀武将互斥锁key
     */
    String SECKILL_LOCK_KEY_PREFIX = "seckill:general:lock";

    /**
     * 逻辑过期时间
     */
    long LOGIC_EXPIRE_TIME = 60 * 60 * 1000;

    /**
     * 秒杀库存
     */
    String STOCK_KEY_PREFIX = "seckill:stock:";

    /**
     * 用户秒杀订单集合
     */
    String USER_SET_KEY_PREFIX = "seckill:order:users";

}
