package com.example.demo;


import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


import reactor.core.publisher.Mono;

@Component
public class MyPreFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(MyPreFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		logger.info("My First Pre-filter is executed..");
		String path = exchange.getRequest().getPath().toString();
		logger.info("Request path: "+path);
		
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> headerNames = headers.keySet();
		
		headerNames.forEach((headerName)->{
			logger.info(headers.getFirst(headerName));
		});
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		
		return 0;
	}

}
