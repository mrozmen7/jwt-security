package com.ozmenyavuz;

import com.ozmenyavuz.dto.CreateUserRequest;
import com.ozmenyavuz.model.UserRole;
import com.ozmenyavuz.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class BasicAutoApplication implements CommandLineRunner {

	private final UserService userService;

    public BasicAutoApplication(UserService userService) {
        this.userService = userService;
    }


    public static void main(String[] args) {
		SpringApplication.run(BasicAutoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}

	private void createDummyData() {
		CreateUserRequest request1 = CreateUserRequest.builder()
				.name("Yavuz")
				.username("yvz")
				.password("mam")
				.authorities(Set.of(UserRole.ROLE_USER))
				.build();
		userService.createUser(request1);

		CreateUserRequest request2 = CreateUserRequest.builder()
				.name("YVZ")
				.username("yvz")
				.password("pass")
				.authorities(Set.of(UserRole.ROLE_YVZ))
				.build();
		userService.createUser(request2);

		CreateUserRequest request3 = CreateUserRequest.builder()
				.name("No Name")
				.username("noname")
				.password("pass")
				.authorities(Set.of(UserRole.ROLE_ADMIN))
				.build();
		userService.createUser(request3);
	}


}
