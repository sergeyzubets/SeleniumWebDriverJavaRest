package com.coherentsolutions.data;

public final class ErrorMessages {

    public static final class Common {
        public static final String RESPONSE_CODE_FAILURE = "Response code is not valid.";
        public static final String ERROR_MESSAGE_FAILURE = "Error message is not correct.";
    }

    public static final class PredefinedErrorMessages {
        public static final String USER_UNIQUENESS = "Users must be unique for complex key name+gender";
        public static final String REQUIRED_FIELDS_VALIDATION = "Some required fields are missed";
        public static final String USER_WITH_UNAVAILABLE_ZIP_CODE = "Specified zip code is not available";
        public static final String CONFLICT_PARAMETERS = "Parameters youngerThan and olderThan can't be specified together";
        public static final String USER_UPDATE_ERROR = "User to change is not found or is null or new values are null";
    }

    public static final class UserClient {
        public static final String USERNAME_FAILURE = "Actual and expected Usernames are not the same.";
        public static final String AGE_FAILURE = "Actual and expected Ages are not the same.";
        public static final String GENDER_FAILURE = "Actual and expected Genders are not the same.";
        public static final String ZIP_CODE_FAILURE = "Actual and expected Zip codes are not the same.";
        public static final String BODY_FAILURE = "Actual and expected Response bodies are not the same.";

        public static final String USERS_LIST_SIZE_FAILURE = "The size of all stored users list should be equal or more than 1.";
        public static final String PARAMETRIZED_LIST_FAILURE = "Parametrized list of users does not contain target user: ";
        public static final String PARAMETRIZED_LIST_EXTRA_USER_FAILURE = "Parametrized list of users contains extra user: ";
        public static final String UPDATE_USER_FAILURE = "User has been updated but should not.";
        public static final String USERS_LIST_FAILURE = "The list of all stored users does not contain the expected one.";
        public static final String DELETE_USER_FAILURE = "User has not been deleted from the application.";
        public static final String RETURN_ZIP_CODE_FAILURE = "Used Zip Code has not been returned to list of available codes.";
    }

    /**
     * Constant holder. Reduced visibility to avoid abuse.
     */
    private ErrorMessages() {
        // Static class. Shouldn't be any need to instantiate.
    }
}