package com.example.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.CheckUserDTO;
import com.example.blog.dto.ResponseDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {
	
	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Validated @RequestBody UserDTO userDTO) {
		try {
			if (userDTO == null || userDTO.getPassword() == null)
				throw new RuntimeException("invalid password");
			UserDTO resultingUserDTO = userService.createUser(userDTO);
			return ResponseEntity.ok().body(resultingUserDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> signin(@Validated @RequestBody UserDTO userDTO) {
		try {
			UserDTO resultingUserDTO = userService.getUserByCredentials(userDTO.getUserName(), userDTO.getPassword());
			if (resultingUserDTO == null)
				return ResponseEntity.notFound().build();
			else
				return ResponseEntity.ok().body(resultingUserDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	/*
		-> 이 API는 특정 user에게만 허용되어야 하는 resource에게 한 user가 접근할 때, 그 user가 정말로 
		해당하는 특정 user가 맞는지를 확인하기 위한 것이다.
				
		-> 몇 가지 사항을 전제한다:
			전제 1) 이 application에 두 user A와 B가 있다. A는 불순한 의도를 가지고 B에게만 허용된
			resource에 접근하려고 한다.
			
			전제 2) 이 backend application을 사용하는 frontend application에서는,
			어떤 user에게만 허용되어야 하는 resource들은 모두 해당하는 user의 "userName"을, domain 다음에 오는
			path name의 구간에 명시하고 있다; 즉 user "A"의 blog에서 새로운 article을 삽입하는 기능을
			수행하기 위해서는, "FRONTEND-DOMAIN/A/create-new-article" 과 같은 path에 접근해야 한다.
			
			전제 3) frontend application에 "/A/some-resouce"와 같은 path name에 map되는
			page가 존재하며, 이 page에서는 "A" user에게만 허용되어야 하는 resource를 backend에서 가져와
			사용한다.
			
			전제 4) frontend application에서는 authentication을 마쳤을 때 해당하는 user의 local storage에
			두 가지 item을 저장한다: "USERNAME"과 "ACCESS-TOKEN"이 그것들이며, 전자에는 user의 "userName"을,
			후자에는 user의 JWT를 저장한다.
		
		 이러한 전제가 있을 때, 몇 가지 case들을 상정해 볼 수 있다:
			case 1) A는 B의 token을 취득하지 못했다.
				-> case 1-1) A가 A의 userName과 A의 token을 가지고 "/B/some-resource"에 접근하려고 한다.
					-> Frontend application은 일차적으로 이 user의 "userName"과 path에 명시되어 있는 "userName"을
					비교하여 resource에 대한 접근 여부를 결정할 것이므로, frontend의 level에서 접근이 차단된다.
					
				-> case 1-2) A가 A의 userName과 임의로 위조한 token을 가지고 "/B/some-resource"에 접근하려고 한다.
					-> 상술한 이유 때문에, 이 경우도 frontend의 level에서 접근이 차단된다.
					
				-> case 1-3) A가 B의 userName과 A의 token을 가지고 "/B/some-resource"에 접근하려고 한다.
					-> 이 경우가 "checkThisUser"로 처리되어야 하는 때이다. 정확한 처리 방법에 대해서는 아래의 "흐름"에 대한
					설명을 참조하도록 한다.
					
				-> case 1-4) A가 B의 userName과 임의로 위조한 token을 가지고 "/B/some-resource"에 접근하려고 한다.
					-> 이 경우 역시 "checkThisUser"가 call될 것이다. JwtException이 throw될 것이고, UserService에서는
					false를 return할 것이다.
			
			case 2) A는 B의 token을 취득했다.
				-> 이 경우 token을 기반으로 하는 이 application의 특성상 A는 모든 B의 resource들에
				대해 접근이 가능하게 될 것이며, 이미 A는 목적을 이루었다고 봐야할 것이다. 이 경우에 대한 처치는 언급을 생략한다.
				
		-> 흐름은 다음과 같다:
			-> Frontend에서는 path에서 얻은 userName을 "pathUserName"에, 그리고 local storage에서
			얻은 userName과 token을 각각 "notCertifiedUserName"과 "notCertifiedToken"에 넣는다.
			이들은 CheckUserDTO를 나타내는 JSON 형태로 request에 load되고, 이 request가 "checkThisUser"로
			전달된다.
		
			-> UserService의 checkThisUser에서 "notCertifiedToken"을 사용해 상응하는 ID 값을 회수하여 
			"notCertifiedID"에 할당하고, "pathUserName"을 사용해 database로부터 상응하는 ID 값을 회수하여
			"pathID"에 할당한다. 이 둘을 비교하여 같으면 resource에 접근하는 것을 허용하고, 같지 않으면 frontend에
			불일치를 나타내는 값을 return하고 frontend에서는 이를 받아 user를 다른 path로 redirect한다.
	*/
	@PostMapping("/check-this-user")
	public ResponseEntity<?> checkThisUser(@RequestBody CheckUserDTO checkUserDTO) {
		try {
			boolean result = userService.checkThisUser(checkUserDTO);
			ResponseDTO responseDTO = new ResponseDTO();
			if (result) {
				responseDTO.setBoolData(result);
				responseDTO.setData("VALID");
			} else {
				responseDTO.setBoolData(result);
				responseDTO.setData("NO");
			}
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}
	
	@PostMapping("/user-deletion")
	public ResponseEntity<?> deleteUser(@Validated @RequestBody UserDTO userDTO) {
		try {
			if (userDTO == null || userDTO.getPassword() == null) 
				throw new RuntimeException("invalid password");
			String message = userService.deleteUser(userDTO);
			return ResponseEntity.ok().body(ResponseDTO.builder().data(message).build());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ResponseDTO.builder().data(e.getMessage()).build());
		}
	}

}
