package com.topjava.graduation.repository;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    @Query("SELECT v FROM Voice v WHERE v.user.id=:userId AND v.date=:date")
    Optional<Voice> get(int userId, LocalDate date);

    default Voice getBelonged(int userId, LocalDate date) {
        return get(userId, date).orElseThrow(
                () -> new NotFoundException("Voice for User id=" + userId + " is not exist"));
    }
}