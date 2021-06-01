package com.example.demo.api.users.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.api.users.data.AlbumsServiceClient;
import com.example.demo.api.users.data.UserEntity;
import com.example.demo.api.users.data.UsersRespository;
import com.example.demo.api.users.shared.UserDto;
import com.example.demo.api.users.ui.model.AlbumResponseModel;

import feign.FeignException;

@Service
public class UserServiceImpl implements UserService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	BCryptPasswordEncoder bCryptPasswordEncoder;
//	RestTemplate restTemplate;
	UsersRespository userRepo;
	Environment env;
	AlbumsServiceClient albumsServiceClient;

	@Autowired
	public UserServiceImpl(UsersRespository userRepo, BCryptPasswordEncoder bCryptPasswordEncoder,
//			RestTemplate restTemplate, 
			AlbumsServiceClient albumsServiceClient, Environment env) {
		this.userRepo = userRepo;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//	this.restTemplate = restTemplate;
		this.albumsServiceClient = albumsServiceClient;

		this.env = env;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

		userRepo.save(userEntity);

		UserDto createdUser = modelMapper.map(userEntity, UserDto.class);
		return createdUser;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepo.findByEmail(username);
		if (userEntity == null)
			throw new UsernameNotFoundException(username);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true,
				new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepo.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepo.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException("User not found");

		String albumsUrl = String.format(env.getProperty("albums.url"), userId);
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

		/*
		 * ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
		 * restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<AlbumResponseModel>>() { });
		 * List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
		 */
		List<AlbumResponseModel> albumsList = null;
		logger.info("Before calling albums microservice");
		albumsList = albumsServiceClient.getAlbums(userId);
		logger.info("After calling albums microservice");

		userDto.setAlbums(albumsList);
		return userDto;
	}

}
