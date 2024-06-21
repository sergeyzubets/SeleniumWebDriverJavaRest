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
        public static final String UPLOAD_INVALID_FILE = "Can't parse file with users";
        public static final String USED_NOT_FOUND = "User is not found";
    }

    public static final class ZipCodeClient {
        public static final String ADD_ZIP_CODE_FAILURE = "Zip code %s has not been added.";
        public static final String ZIP_CODE_LIST_FAILURE = "List of available zip codes does not contain added one.";
        public static final String ADD_DUPLICATE_FAILURE = "Duplicate for used code %s was added to the application.";
    }

    public static final class UserClient {
        public static final String USERNAME_FAILURE = "Actual and expected Usernames are not the same.";
        public static final String AGE_FAILURE = "Actual and expected Ages are not the same.";
        public static final String GENDER_FAILURE = "Actual and expected Genders are not the same.";
        public static final String ZIP_CODE_FAILURE = "Actual and expected zip codes are not the same.";
        public static final String BODY_FAILURE = "Actual and expected Response bodies are not the same.";
        public static final String USED_ZIP_CODE_FAILURE = "Used zip code has not been removed from available zip codes of application.";
        public static final String ADD_USER_FAILURE = "User has been added to the application but should not.";
        public static final String USERS_LIST_SIZE_FAILURE = "The size of all stored users list should be equal or more than 1.";
        public static final String PARAMETRIZED_LIST_FAILURE = "Parametrized list of users does not contain target user: ";
        public static final String PARAMETRIZED_LIST_EXTRA_USER_FAILURE = "Parametrized list of users contains extra user: ";
        public static final String UPDATE_USER_FAILURE = "User has been updated but should not.";
        public static final String USERS_LIST_FAILURE = "The list of all stored users does not contain the expected one.";
        public static final String DELETE_USER_FAILURE = "User has not been deleted from the application.";
        public static final String RETURN_ZIP_CODE_FAILURE = "Used zip code has not been returned to list of available codes.";
        public static final String UPLOAD_USERS_RESPONSE_FAILURE = "Response does not contain correct number of uploaded users.";
        public static final String UPLOAD_USER_FAILURE = "The users has not been uploaded.";
        public static final String UPLOAD_INVALID_USER_FAILURE = "The list of all stored users is invalid.";
    }

    /**
     * Constant holder. Reduced visibility to avoid abuse.
     */
    private ErrorMessages() {
        // Static class. Shouldn't be any need to instantiate.
    }
}