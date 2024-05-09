package com.topjava.graduation.service;

import com.topjava.graduation.dto.VoiceDto;
import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.exception.VotingRestrictionsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

import static com.topjava.graduation.util.RestaurantUtil.asViewDto;
import static com.topjava.graduation.util.VoiceUtil.VOTING_CHANGE_DEADLINE;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.user.UserTestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class VoiceServiceTest extends AbstractServiceTest {

    @Autowired
    private VoiceService service;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    void create() {
        assertNull(service.getViewDto(ADMIN_ID));
        service.save(ADMIN_ID, new VoiceDto(FRENCH_ID), null);

        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(
                restaurantService.getVotedByUserForToday(ADMIN_ID), asViewDto(french));
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated);

        VoiceViewDto voice = service.getViewDto(ADMIN_ID);
        assertNotNull(voice);
        assertEquals(FRENCH_ID, voice.getRestaurantId());
    }

    @Test
    void update() {
        if (LocalTime.now().isBefore(VOTING_CHANGE_DEADLINE)) {
            service.save(USER_1_ID, new VoiceDto(FRENCH_ID), service.get(USER_1_ID));

            RESTAURANT_VIEW_DTO_MATCHER.assertMatch(
                    restaurantService.getVotedByUserForToday(USER_1_ID), asViewDto(french));
            RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                    restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated_2);

            VoiceViewDto voice = service.getViewDto(USER_1_ID);
            assertNotNull(voice);
            assertEquals(FRENCH_ID, voice.getRestaurantId());
        }
    }

    @Test
    void updateWithRestrictions() {
        if (LocalTime.now().isAfter(VOTING_CHANGE_DEADLINE)) {
            assertThrows(VotingRestrictionsException.class, () ->
                    service.save(USER_3_ID, new VoiceDto(FRENCH_ID), service.get(USER_3_ID)));
        }
    }
}