package com.coherentsolutions.data;

public interface ErrorMessages {

    interface Common {
        String RESPONSE_CODE_FAILURE = "Response code is not valid.";
        String ERROR_MESSAGE_FAILURE = "Error message is not correct.";
    }

    interface PredefinedErrorMessages {
        String USER_UNIQUENESS = "Users must be unique for complex key name+gender";
        String REQUIRED_FIELDS_VALIDATION = "Some required fields are missed";
        String USER_WITH_UNAVAILABLE_ZIP_CODE = "Specified zip code is not available";
        String CONFLICT_PARAMETERS = "Parameters youngerThan and olderThan can't be specified together";
    }

    interface UserClient {
        String USERNAME_FAILURE = "Actual and expected Usernames are not the same.";
        String AGE_FAILURE = "Actual and expected Ages are not the same.";
        String GENDER_FAILURE = "Actual and expected Genders are not the same.";
        String ZIP_CODE_FAILURE = "Actual and expected Zip codes are not the same.";
        String BODY_FAILURE = "Actual and expected Response bodies are not the same.";

        String USERS_LIST_SIZE_FAILURE = "The size of all stored users list should be equal or more than 1.";
        String PARAMETRIZED_LIST_FAILURE = "Parametrized list of users does not contain target user: ";
        String PARAMETRIZED_LIST_EXTRA_USER_FAILURE = "Parametrized list of users contains extra user: ";
    }
}