package com.topjava.graduation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class VoiceDto {
    int restaurantId;

    @JsonCreator
    public VoiceDto(@JsonProperty("restaurantId") int restaurantId) {
        this.restaurantId = restaurantId;
    }
}