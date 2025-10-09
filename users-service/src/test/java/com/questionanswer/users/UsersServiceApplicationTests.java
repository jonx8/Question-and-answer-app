package com.questionanswer.users;

import com.questionanswer.users.config.TestingBeans;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestingBeans.class)
class UsersServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
