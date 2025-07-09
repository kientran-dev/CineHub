package com.kientran.cinehub.entity;

import com.kientran.cinehub.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity{
    @Field(name = "email")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be blank")
    String email;

    @Field(name = "password")
    @NotBlank(message = "Password should not be blank")
    String password;

    @Field(name = "first_name")
    String firstName;

    @Field(name = "last_name")
    String lastName;

    @Field(name = "roles")
    Set<UserRole> roles = new HashSet<>();
}
