package com.questionanswer.questions;

import java.util.UUID;

public interface TestConstants {

    // User IDs
    UUID ADMIN_USER_ID = UUID.fromString("e95f8551-8bd3-477b-85b5-a3d4a5c143a8");
    UUID USER_ID_1 = UUID.fromString("9bce5101-38d7-462d-a891-047f6c1b6129");
    UUID USER_ID_2 = UUID.fromString("9660e3c7-0d23-43ff-903d-3ca8296dc2a7");

    // Question IDs
    Long QUESTION_ID_1 = 1L;
    Long QUESTION_ID_2 = 2L;
    Long QUESTION_ID_3 = 3L;
    Long QUESTION_ID_4 = 4L;
    Long NON_EXISTENT_QUESTION_ID = 999L;

    // Answer IDs
    Long ANSWER_ID_1 = 1L;
    Long NON_EXISTENT_ANSWER_ID = 999L;

    // Roles
    String ROLE_USER = "ROLE_USER";
    String ROLE_ADMIN = "ROLE_ADMIN";

    // Test Data
    String TEST_QUESTION_TITLE = "Test Question Title";
    String TEST_QUESTION_TEXT = "Test question text content";
    String TEST_ANSWER_TEXT = "Test answer text content";

}