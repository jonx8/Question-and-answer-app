configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

