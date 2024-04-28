package com.topjava.graduation.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.topjava.graduation.View;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "dish",
        indexes = {@Index(columnList = "restaurant_id, menu_date", name = "dish_restaurant_menu_date_idx")},
        uniqueConstraints =
                {@UniqueConstraint(columnNames =
                        {"restaurant_id", "menu_date", "name"}, name = "dish_restaurant_id_menu_date_name_idx")})
@NoArgsConstructor
@Setter
@Getter
public class Dish extends NamedEntity {
    @Column(name = "menu_date", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "price", nullable = false)
    @Range(min = 1)
    private long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    @NotNull(groups = View.Persist.class)
    @Schema(hidden = true)
    private Restaurant restaurant;

    public Dish(Integer id, LocalDate date, String name, long price) {
        super(id, name);
        this.date = date;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name=" + name +
                ", date=" + date +
                ", price=" + price +
                '}';
    }
}