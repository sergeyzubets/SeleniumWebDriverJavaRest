package com.coherentsolutions.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserDTO {
    private UserDTO userNewValues;
    private UserDTO userToChange;

    public UpdateUserDTO(UserDTO userNewValues, UserDTO userToChange) {
        this.userNewValues = userNewValues;
        this.userToChange = userToChange;
    }

    @Override
    public String toString() {
        return String.format("userNewValues = %s, userToChange = %s", userNewValues, userToChange);
    }
}