package com.topjava.graduation.util;

import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.exception.VotingRestrictionsException;
import com.topjava.graduation.model.Voice;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.LocalTime;

@UtilityClass
public class VoiceUtil {

    public static final LocalTime VOTING_CHANGE_DEADLINE = LocalTime.of(11, 0);

    public static boolean isAvailableUpdate() {
        if (LocalTime.now().isBefore(VOTING_CHANGE_DEADLINE)) {
            return true;
        }
        throw new VotingRestrictionsException("It is not possible to change the vote for today after " +
                VOTING_CHANGE_DEADLINE + " a.m.");
    }

    public static VoiceViewDto asDto(Voice voice) {
        return new VoiceViewDto(LocalDateTime.of(voice.getDate(), voice.getTime()), voice.getRestaurant().id());
    }
}