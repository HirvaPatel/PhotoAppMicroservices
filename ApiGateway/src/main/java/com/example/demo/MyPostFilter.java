package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class MyPostFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(MyPostFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
			logger.info("My last Post-Filter executed..");
		}));
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 	0;
	}

}
