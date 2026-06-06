package com.ruoyi.cache;

import com.ruoyi.mqtt03.addr03.AddrHandlerFactory;
import com.ruoyi.zm.domain.DevInstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.ruoyi.cache.Key.REMOTE_KEY;
import static com.ruoyi.schedule.InstructionQueue.QUEUE_WRITE_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class WriteQueueCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, short[]> shortArrayRedisTemplate;
    private final RedisTemplate<String, int[]> intArrayRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    // --- Queue cache operations (Object) ---
    public void setQueueCache(String ip, int deviceId, String addr, Object value) {
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr, value);
    }

    public Object getQueueCache(String ip, int deviceId, String addr) {
        return redisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr);
    }

    // --- Queue cache operations (short[]) ---
    public void setQueueCacheShortArr(String ip, int deviceId, String addr, short[] value) {
        shortArrayRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr, value);
    }

    public short[] getQueueCacheShortArr(String ip, int deviceId, String addr) {
        return shortArrayRedisTemplate.opsForValue().get("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr);
    }

    // --- Select cache operations ---
    public void setGroupSelect(int deviceId, int[] groupSelectArr) {
        intArrayRedisTemplate.opsForValue().set("zm:select:group:" + deviceId, groupSelectArr);
    }

    public void setLoopSelect(int deviceId, int[] loopArr) {
        intArrayRedisTemplate.opsForValue().set("zm:select:loop:" + deviceId, loopArr);
    }

    // --- Work module hash ---
    public void setWorkModule(int deviceId, int workModule) {
        redisTemplate.opsForHash().put("zm:workModule", String.valueOf(deviceId), workModule);
    }

    // --- Delete cache keys ---
    public void deleteCreateTcp(Long deviceId) {
        redisTemplate.delete("zm:create-tcp:" + deviceId);
    }

    public void deleteDeviceList() {
        redisTemplate.delete("zm:dev_base_devices:list");
    }

    // --- Instruction queue ---
    public void pushInstruction(int deviceId, DevInstruct instruct) {
        redisTemplate.opsForList().rightPush(QUEUE_WRITE_KEY + deviceId, instruct);
    }

    // --- Scene global select ---
    public void setGlobalSceneSelect(String value) {
        redisTemplate.opsForValue().set("zm:global:scene:select", value);
    }

    // --- Scene name cache ---
    public void setSceneName(String ip, int deviceId, String addr, String name) {
        stringRedisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr, name);
    }

    // --- Remote key cache ---
    public void setRemoteKey(String ip, int deviceId, String addr, Object remote) {
        redisTemplate.opsForValue().set(REMOTE_KEY + ip + ":" + deviceId + ":" + addr, remote);
    }

    // --- Addr handler factory key ---
    public Object getAddrHandlerKey(String ip, String addr, int deviceId) {
        return redisTemplate.opsForValue().get(AddrHandlerFactory.getKey(ip, addr, deviceId));
    }

    public void setAddrHandlerKey(String ip, String addr, int deviceId, Object value) {
        redisTemplate.opsForValue().set(AddrHandlerFactory.getKey(ip, addr, deviceId), value);
    }

    // --- Enabled cache ---
    public void setEnabledCache(String ip, int deviceId, String addr, Object value) {
        redisTemplate.opsForValue().set("zm:queue:zm:cache:63:" + ip + ":" + deviceId + ":" + addr, value);
    }

    // --- Queue cache operations (cache:3 prefix) ---
    public void setQueueCache3(String ip, int deviceId, String addr, Object value) {
        redisTemplate.opsForValue().set("zm:queue:zm:cache:3:" + ip + ":" + deviceId + ":" + addr, value);
    }

    // --- Generic hash put ---
    public void putHash(String key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
}
