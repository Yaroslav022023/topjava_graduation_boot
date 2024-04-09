package com.topjava.graduation.model;

import com.topjava.graduation.View;
import com.topjava.graduation.util.validation.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@NoArgsConstructor
@Setter
@Getter
public class NamedEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 255)
    @NoHtml(groups = {View.Web.class})
    protected String name;

    public NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '(' + name + ')';
    }
}