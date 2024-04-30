package com.topjava.graduation.dto;

import com.topjava.graduation.util.validation.NoHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class NamedDto extends BaseDto {
    @NotBlank
    @Size(min = 2, max = 255)
    @NoHtml
    protected final String name;

    public NamedDto(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }
}