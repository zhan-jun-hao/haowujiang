package com.haowujiang.sanguosha.infrastructure.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 为redis里面的缓存数据加1个字段
 * 用来逻辑过期
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheObjectValue<T> {

    private T data;

    private Long expire;

}
