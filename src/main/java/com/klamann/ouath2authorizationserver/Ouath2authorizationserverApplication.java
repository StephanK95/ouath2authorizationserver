package com.klamann.ouath2authorizationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ouath2authorizationserverApplication {

	//localhost:8080/api/v1/authorize?clientId=oauth-client-1&scope=foo bar&redirectUri=http://localhost:9000/callback&state=state

	public static void main(String[] args) {
		SpringApplication.run(Ouath2authorizationserverApplication.class, args);
	}

}
