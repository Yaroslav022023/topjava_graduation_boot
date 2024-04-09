package com.topjava.graduation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.topjava.graduation.View;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "voice",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"}, name = "user_id_date_idx")},
        indexes = {@Index(columnList = "date", name = "idx_voice_date")})
@Setter
@Getter
public class Voice extends BaseEntity {
    @Column(name = "date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull(groups = View.Persist.class)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(groups = View.Persist.class)
    private User user;

    public Voice() {
        super(null);
        this.date = LocalDate.now();
        this.time = LocalTime.now();
    }

    @Override
    public String toString() {
        return "Voice{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}