package com.example.demo.api.users.data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;

@FeignClient(name = "albums-ws", fallbackFactory = AlbumsFallbackFactory.class)
public interface AlbumsServiceClient {

	@GetMapping(path = "/users/{id}/albums")
	public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}

@Component
class AlbumsFallbackFactory implements FallbackFactory<AlbumsServiceClient>{

	@Override
	public AlbumsServiceClient create(Throwable cause) {
		// TODO Auto-generated method stub
		return new AlbumsServiceClientFallback(cause);
	}	
}

class AlbumsServiceClientFallback implements AlbumsServiceClient{

	Logger logger = LoggerFactory.getLogger(this.getClass()) ;
	
	private final Throwable cause;
	
	@Autowired
	public AlbumsServiceClientFallback(Throwable cause) {
		this.cause = cause;
	}
	
	@Override
	public List<AlbumResponseModel> getAlbums(String id) {
		if(cause instanceof FeignException && ((FeignException) cause).status() == 404) {
			logger.error("404 error took place when getAlbums was called with userId: "+id+ ". Error Message: "
					+cause.getLocalizedMessage());
		}
		else
			logger.error("Other error took place: "+cause.getLocalizedMessage());
		
		return new ArrayList<>();
	}

}