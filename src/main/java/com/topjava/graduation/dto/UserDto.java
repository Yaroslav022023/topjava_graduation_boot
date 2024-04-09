package com.topjava.graduation.dto;

import com.topjava.graduation.HasIdAndEmail;
import com.topjava.graduation.util.validation.NoHtml;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "password")
public class UserDto extends NamedDto implements HasIdAndEmail {
    @Email
    @NotBlank
    @Size(max = 128)
    @NoHtml
    String email;

    @NotBlank
    @Size(min = 5, max = 128)
    String password;

    public UserDto(Integer id, String name, String email, String password) {
        super(id, name);
        this.email = email;
        this.password = password;
    }
}