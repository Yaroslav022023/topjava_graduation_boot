package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantVotedByUserDto;
import com.topjava.graduation.service.RestaurantService;
import com.topjava.graduation.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.topjava.graduation.exception.ErrorType.VOTING_RESTRICTIONS;
import static com.topjava.graduation.util.RestaurantUtil.convertToViewDtos;
import static com.topjava.graduation.util.RestaurantUtil.convertToVotedByUserDto;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.restaurant.UserRestaurantRestController.REST_URL;
import static com.topjava.graduation.web.user.UserTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserRestaurantRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private RestaurantService service;

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void getAllWithDishsForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_VIEW_DTO_MATCHER
                        .contentJson(convertToViewDtos(restaurantsSort)));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void getAllWithNumberVoicesForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "number-voices"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER
                        .contentJson(getWithNumberVoicesDtos()));
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
    void getNotVotedByUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "voted-by-user"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void vote() throws Exception {
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_SLASH + french.id()))
                .andExpect(status().isCreated())
                .andDo(print());

        RestaurantVotedByUserDto created = RESTAURANT_VOTED_BY_USER_DTO_MATCHER.readFromJson(action);
        RESTAURANT_VOTED_BY_USER_DTO_MATCHER.assertMatch(created, convertToVotedByUserDto(french));
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                service.getAllWithNumberVoicesForToday(), restaurantsWithNumberVoicesUpdated_2);
    }

    @Test
    @WithUserDetails(value = USER_3_MAIL)
    void voteRestrictions() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL_SLASH + asian.getId()))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(VOTING_RESTRICTIONS.title));
    }
}