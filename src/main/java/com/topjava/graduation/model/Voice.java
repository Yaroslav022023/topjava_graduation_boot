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
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "vote_date"}, name = "user_id_vote_date_idx")},
        indexes = {@Index(columnList = "vote_date", name = "idx_vote_date")})
@Setter
@Getter
public class Voice extends BaseEntity {
    @Column(name = "vote_date", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    @Column(name = "vote_time", nullable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(groups = View.Persist.class)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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