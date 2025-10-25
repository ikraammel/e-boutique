package com.ecommerce.demo.response;

import com.ecommerce.demo.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
