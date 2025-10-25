package com.ecommerce.demo.mappers;
import com.ecommerce.demo.dtos.UserDto;
import com.ecommerce.demo.models.Role;
import com.ecommerce.demo.models.User;
import com.ecommerce.demo.response.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDto dto, PasswordEncoder passwordEncoder){
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        return user;
    }

    public UserResponse toResponseDto(User user){
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
