package com.realestate.realestate.dto.user;

import java.util.Set;

import com.realestate.realestate.enums.RoleName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String email;
    private String contactNumber;
    private String profilePicture;
    private boolean emailVerified;
    private Set<RoleName> roles;
}
