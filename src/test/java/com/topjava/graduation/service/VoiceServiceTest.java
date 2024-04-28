package com.topjava.graduation.service;

import com.topjava.graduation.dto.VoiceDto;
import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.exception.VotingRestrictionsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

import static com.topjava.graduation.util.RestaurantUtil.asViewDto;
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
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            assertNull(service.get(ADMIN_ID));
            service.save(ADMIN_ID, new VoiceDto(FRENCH_ID));

            RESTAURANT_VIEW_DTO_MATCHER.assertMatch(
                    restaurantService.getVotedByUserForToday(ADMIN_ID), asViewDto(french));
            RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                    restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated);

            VoiceViewDto voice = service.get(ADMIN_ID);
            assertNotNull(voice);
            assertEquals(FRENCH_ID, voice.getRestaurant().getId());
        }
    }

    @Test
    void update() {
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            service.save(USER_1_ID, new VoiceDto(FRENCH_ID));

            RESTAURANT_VIEW_DTO_MATCHER.assertMatch(
                    restaurantService.getVotedByUserForToday(USER_1_ID), asViewDto(french));
            RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                    restaurantService.getAllWithTodayNumberVoices(), restaurantsWithNumberVoicesUpdated_2);

            VoiceViewDto voice = service.get(USER_1_ID);
            assertNotNull(voice);
            assertEquals(FRENCH_ID, voice.getRestaurant().getId());
        }
    }

    @Test
    void createWithRestrictions() {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            assertThrows(VotingRestrictionsException.class, () -> service.save(GUEST_ID, new VoiceDto(FRENCH_ID)));
        }
    }

    @Test
    void updateWithRestrictions() {
        if (LocalTime.now().isAfter(LocalTime.of(11, 0))) {
            assertThrows(VotingRestrictionsException.class, () -> service.save(USER_3_ID, new VoiceDto(FRENCH_ID)));
        }
    }
}