package com.topjava.graduation.web.voice;

import com.topjava.graduation.MatcherFactory;
import com.topjava.graduation.dto.VoiceViewDto;

public class VoiceTestData {
    public static final MatcherFactory.Matcher<VoiceViewDto> VOICE_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(VoiceViewDto.class);
}