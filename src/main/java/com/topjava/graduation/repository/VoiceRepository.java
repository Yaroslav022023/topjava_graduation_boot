package com.topjava.graduation.repository;

import com.topjava.graduation.model.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional(readOnly = true)
public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    @Query("SELECT v FROM Voice v WHERE v.user.id=:userId AND v.date=:date")
    Voice get(int userId, LocalDate date);
}