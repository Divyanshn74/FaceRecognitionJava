package com.example.facerecog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FacerecogApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacerecogApplication.class, args);
	}

}
