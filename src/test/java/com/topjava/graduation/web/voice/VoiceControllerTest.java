package com.topjava.graduation.web.voice;

import com.topjava.graduation.dto.VoiceDto;
import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.service.RestaurantService;
import com.topjava.graduation.service.VoiceService;
import com.topjava.graduation.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;

import static com.topjava.graduation.exception.ErrorType.VOTING_RESTRICTIONS;
import static com.topjava.graduation.util.JsonUtil.writeValue;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.user.UserTestData.*;
import static com.topjava.graduation.web.voice.VoiceController.REST_URL;
import static com.topjava.graduation.web.voice.VoiceTestData.VOICE_MATCHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VoiceControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private VoiceService service;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void createWithLocation() throws Exception {
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValue(new VoiceDto(FRENCH_ID))))
                    .andExpect(status().isCreated())
                    .andDo(print());

            VoiceViewDto created = VOICE_MATCHER.readFromJson(action);
            assertEquals(FRENCH_ID, created.getRestaurant().getId());
            VOICE_MATCHER.assertMatch(service.get(GUEST_ID), created);

            RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                    restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated);
        }
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void update() throws Exception {
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.patch(REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValue(new VoiceDto(FRENCH_ID))))
                    .andExpect(status().isNoContent())
                    .andDo(print());

            RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                    restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated_2);
        }
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void createWithRestrictions() throws Exception {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.post(REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValue(new VoiceDto(FRENCH_ID))))
                    .andExpect(status().isUnprocessableEntity())
                    .andDo(print())
                    .andExpect(titleMessage(VOTING_RESTRICTIONS.title));
        }
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void updateWithRestrictions() throws Exception {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            perform(MockMvcRequestBuilders.patch(REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValue(new VoiceDto(FRENCH_ID))))
                    .andExpect(status().isUnprocessableEntity())
                    .andDo(print())
                    .andExpect(titleMessage(VOTING_RESTRICTIONS.title));
        }
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void getVotedByUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "voted-by-user"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(italian));
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void getNotExistVotedByUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "voted-by-user"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}