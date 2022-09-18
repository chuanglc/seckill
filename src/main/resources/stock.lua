local sucStock = tonumber(redis.call('get',KEYS[1]));
if sucStock > 0 then
    redis.call("incrby", KEYS[1], -1);
    return sucStock-1;
else
    return -1;
end;