package com.example.aspect;

import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.example.annotation.RateLimited;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitAspect {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private HttpServletRequest request; // To get the request info.

	// The "JoinPoint" where intercept all the methods (with the annotation)
	@Around("@annotation(rateLimited)")
	public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {

		// 1. Get IP address
		String ip = request.getRemoteAddr();
		String key = "rate:limit:" + ip;

		// 2. Increase the value in Redis
		Long currentRequests = redisTemplate.opsForValue().increment(key);

		// 3. In case is the first request, it's necessary to add an expiration date
		if (currentRequests != null && currentRequests == 1) {
			redisTemplate.expire(key, Duration.ofSeconds(rateLimited.period()));
		}

		// 4. Verify the limit
		if (currentRequests != null && currentRequests > rateLimited.requests()) {
			System.err.println(
					">>> [DENIED] IP: " + ip + " tried to request more than " + rateLimited.requests() + " times.");

			throw new RuntimeException("Denied - Requests exceeded");
		}

		System.out.println(">>> IP: " + ip + " - [ " + currentRequests + "]/" + rateLimited.requests());

		return joinPoint.proceed();
	}

}
