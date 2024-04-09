package com.topjava.graduation.util;

import com.topjava.graduation.model.Voice;
import com.topjava.graduation.exception.VotingRestrictionsException;
import lombok.experimental.UtilityClass;

import java.time.LocalTime;

@UtilityClass
public class VoiceUtil {

    public static boolean isAvailableUpdate(Voice voice) {
        if (voice.getTime().isBefore(LocalTime.of(11, 0))) {
            return true;
        }
        throw new VotingRestrictionsException("It is not possible to change the voice after 11:00 a.m.");
    }
}