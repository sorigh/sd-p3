package com.example.user_service.dto;

import com.example.user_service.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data // combines @Getter, @Setter, @ToString, @EqualsAndHashCode, and
      // @RequiredArgsConstructor.
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserDetailsDTO {

    private Long id;

    @NotBlank(message = "Email is required")
    @NonNull
    private String email;

    @NotBlank(message = "Name is required")
    @NonNull
    private String name;

    private String password;

    @NotNull(message = "Role is required")
    @NonNull
    private UserRole role;

    @NotNull(message = "Username is required")
    @NonNull
    private String username;
}
