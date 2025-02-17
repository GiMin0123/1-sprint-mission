package com.sprint.mission.discodeit.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.global.dto.CommonResponse;
import com.sprint.mission.discodeit.user.dto.request.CreateUserRequest;
import com.sprint.mission.discodeit.user.dto.request.UpdateUserRequest;
import com.sprint.mission.discodeit.user.dto.request.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.service.UserService;
import com.sprint.mission.discodeit.user.service.UserStatusService;

@RestController
@RequestMapping("api/users")
public class UserController {
	private final UserService userService;
	private final UserStatusService userStatusService;
	private final UserMapper userMapper;

	public UserController(UserService userService, UserStatusService userStatusService, UserMapper userMapper) {
		this.userService = userService;
		this.userStatusService = userStatusService;
		this.userMapper = userMapper;
	}

	//사용자 등록
	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ResponseEntity<CommonResponse<UserResponse>> createUser(@RequestBody CreateUserRequest createUserRequest) {
		//나중에 JPA를 적용하여 UserStatus -> User로의 단방향 연관관계를 맺게 된다면 똑같이 생성을 해야겠다...
		User newUser = userService.createUser(createUserRequest);
		UserStatus userStatus = userStatusService.find(newUser.getId());

		//mapper를 통한 dto로 변환
		UserResponse response = userMapper.userToUserResponse(newUser, userStatus);
		return new ResponseEntity<>(CommonResponse.created(response), HttpStatus.CREATED);
	}

	//특정 사용자 조회
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable("id") UUID userId) {
		User existUser = userService.findUser(userId);
		UserStatus userStatus = userStatusService.find(existUser.getId());

		//mapper를 통한 dto로 변환
		UserResponse response = userMapper.userToUserResponse(existUser, userStatus);
		return new ResponseEntity<>(CommonResponse.created(response), HttpStatus.OK);
	}

	//전체 사용자 조회
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<CommonResponse<List<UserResponse>>> getAllUsers() {
		List<User> users = userService.findAllUsers();
		List<UserStatus> userStatuses = userStatusService.findAll();
		List<UserResponse> responses = userMapper.userListToUserResponseList(users, userStatuses);

		return new ResponseEntity<>(CommonResponse.success(responses), HttpStatus.OK);
	}

	//사용자 수정
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseEntity<CommonResponse<UserResponse>> updateUser(@PathVariable("id") UUID userId,
		UpdateUserRequest request) {
		User updatedUser = userService.updateUser(userId, request);
		UserStatus userStatus = userStatusService.updateByUserId(
			new UpdateUserStatusRequest(updatedUser.getId(), request.requestAt()));

		UserResponse response = userMapper.userToUserResponse(updatedUser, userStatus);
		return new ResponseEntity<>(CommonResponse.success(response), HttpStatus.OK);
	}

	//사용자 삭제
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable("id") UUID userId) {
		userService.deleteUser(userId);
		return new ResponseEntity<>(CommonResponse.success("User deleted successfully", null), HttpStatus.OK);
	}

	//사용자 온라인 상태 업데이트
	@RequestMapping(value = "/update-status", method = RequestMethod.PATCH)
	public ResponseEntity<CommonResponse<Void>> updateUserStatus(
		@RequestBody UpdateUserStatusRequest request) {
		userStatusService.update(request);
		return new ResponseEntity<>(CommonResponse.success("User status updated", null), HttpStatus.OK);
	}
}
