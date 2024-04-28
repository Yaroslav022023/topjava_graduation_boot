package com.topjava.graduation.util;

import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.exception.VotingRestrictionsException;
import com.topjava.graduation.model.Voice;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.topjava.graduation.util.RestaurantUtil.asViewDto;

@UtilityClass
public class VoiceUtil {

    public static boolean isAvailableToSave() {
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            return true;
        }
        throw new VotingRestrictionsException("Voting is closed for today after 11 a.m.");
    }

    public static VoiceViewDto asDto(Voice voice) {
        return new VoiceViewDto(
                LocalDateTime.of(voice.getDate(), voice.getTime()),
                asViewDto(voice.getRestaurant()));
    }
}