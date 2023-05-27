package com.woowacourse.momo.support.distributionlock;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class DistributionLockAop {
    private static final String LOCK_PREFIX = "LOCK: ";

    private final RedissonClient redissonClient;

    @Around("@annotation(com.woowacourse.momo.support.distributionlock.DistributionLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributionLock distributionLock = method.getAnnotation(DistributionLock.class);

        String key = LOCK_PREFIX + CustomSpringELParser.getDynamicValue(methodSignature.getName(),
                methodSignature.getParameterNames(), joinPoint.getArgs(), distributionLock.key());

        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(distributionLock.waitTime(), distributionLock.leaseTime(), distributionLock.timeUnit())) {
                // TODO: MomoException 으로 변경할 것
                throw new RuntimeException("Lock 획득을 실패했습니다");
            }
            log.info("lock - " + key);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            log.info("unlock - " + key);
            lock.unlock();
        }
    }
}
