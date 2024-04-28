package com.topjava.graduation.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class VoiceViewDto {
    LocalDateTime dateTime;
    RestaurantVotedByUserDto restaurant;
}