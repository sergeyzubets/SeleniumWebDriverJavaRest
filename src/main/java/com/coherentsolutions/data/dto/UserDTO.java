package com.coherentsolutions.data.dto;

import com.coherentsolutions.data.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("sex")
    private Gender gender;
    private String name;
    private Integer age;
    private String zipCode;

    public UserDTO(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public UserDTO(String name, Gender gender, int age, ZipCodeDTO zipCode) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.zipCode = zipCode.getCode();
    }

    @Override
    public String toString() {
        return String.format("name = %s, gender = %s, age = %s, zipCode = %s", name, gender, age, zipCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UserDTO)) {
            return false;
        }

        if (o == null) {
            return false;
        }

        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(name, userDTO.name) && gender == userDTO.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender);
    }
}