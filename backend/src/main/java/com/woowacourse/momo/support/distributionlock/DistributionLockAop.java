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

import com.woowacourse.momo.global.exception.exception.GlobalErrorCode;
import com.woowacourse.momo.global.exception.exception.MomoException;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class DistributionLockAop {
    private static final String LOCK_PREFIX = "LOCK: ";

    private final RedissonClient redissonClient;
    private final TransactionGeneratorAop transactionGeneratorAop;

    @Around("@annotation(com.woowacourse.momo.support.distributionlock.DistributionLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributionLock distributionLock = method.getAnnotation(DistributionLock.class);

        String key = LOCK_PREFIX + DistributionLockKeyGenerator.generate(methodSignature.getName(),
                methodSignature.getParameterNames(), joinPoint.getArgs(), distributionLock.key());

        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(distributionLock.waitTime(), distributionLock.leaseTime(), distributionLock.timeUnit())) {
                throw new MomoException(GlobalErrorCode.LOCK_ACQUISITION_FAILED_ERROR);
            }
            log.info("lock - " + key);
            return transactionGeneratorAop.proceed(joinPoint);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new MomoException(GlobalErrorCode.LOCK_INTERRUPTED_ERROR);
        } finally {
            log.info("unlock - " + key);
            lock.unlock();
        }
    }
}
