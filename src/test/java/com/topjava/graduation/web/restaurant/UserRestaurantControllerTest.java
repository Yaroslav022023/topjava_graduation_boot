package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.topjava.graduation.util.RestaurantUtil.asWithDishesViewDtos;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.restaurant.UserRestaurantController.REST_URL;
import static com.topjava.graduation.web.user.UserTestData.GUEST_MAIL;
import static com.topjava.graduation.web.user.UserTestData.USER_1_MAIL;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserRestaurantControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void getAllWithTodayDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_WITH_DISHES_VIEW_DTO_MATCHER
                        .contentJson(asWithDishesViewDtos(restaurantsSort)));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());
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
}