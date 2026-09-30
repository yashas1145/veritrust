package in.ybuilds.veritrust.backend.dto.response;

import in.ybuilds.veritrust.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long userId;
    private String email;
    private Role role;
}