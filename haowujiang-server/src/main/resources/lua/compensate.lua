-- KEYS[1] stockKey
-- KEYS[2] userSetKey
-- ARGV[1] userId
local stockKey = KEYS[1]
local userSetKey = KEYS[2]
local userId = ARGV[1]

redis.call('INCR', stockKey)
redis.call('SREM', userSetKey, userId)

return 0