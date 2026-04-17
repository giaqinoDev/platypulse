package com.giosmdev.package_radar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class PackageRadarApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackageRadarApplication.class, args);
	}

}
