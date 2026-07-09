-- KEYS[1] stockKey: seckill:stock:{generalCode}
-- KEYS[2] userSetKey: seckill:order:users:{generalCode}
-- ARGV[1] userId
-- 0 成功   1 库存不足   2 重复下单

local stockKey = KEYS[1]
local userSetKey = KEYS[2]
local userId = ARGV[1]

-- 一人一单
if redis.call('SISMEMBER', userSetKey, userId) == 1 then
    -- 返回json
    return 2
end

-- 库存校验 防止超卖
-- string num = GET stockKey -> int num = tonumber(num)
local stock = tonumber(redis.call('GET', stockKey))
if(stock == nil) or (stock <= 0) then
    return 1
end

-- 扣库存 记录用户
-- DECR stockKey 让stockKey --
redis.call('DECR', stockKey)
-- SADD userSetKey userId 在userSet里面添加userId
redis.call('SADD', userSetKey, userId)

return 0
