package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.MatcherFactory;
import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantVotedByUserDto;
import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.web.dish.DishTestData;

import java.util.HashSet;
import java.util.List;

import static com.topjava.graduation.model.BaseEntity.START_SEQ;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "dishes");
    public static final MatcherFactory.Matcher<RestaurantViewDto> RESTAURANT_VIEW_DTO_MATCHER =
            MatcherFactory.usingEqualsComparator(RestaurantViewDto.class);
    public static final MatcherFactory.Matcher<RestaurantWithNumberVoicesDto> RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER =
            MatcherFactory.usingEqualsComparator(RestaurantWithNumberVoicesDto.class);
    public static final MatcherFactory.Matcher<RestaurantVotedByUserDto> RESTAURANT_VOTED_BY_USER_DTO_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(RestaurantVotedByUserDto.class, "dishes");

    public static final int ITALIAN_ID = START_SEQ + 5;
    public static final int ASIAN_ID = START_SEQ + 6;
    public static final int FRENCH_ID = START_SEQ + 7;

    public static final Restaurant italian = new Restaurant(ITALIAN_ID, "Italian");
    public static final Restaurant asian = new Restaurant(ASIAN_ID, "Asian");
    public static final Restaurant french = new Restaurant(FRENCH_ID, "French");

    public static final List<Restaurant> restaurants = List.of(asian, french, italian);
    public static final List<Restaurant> restaurantsSort = List.of(italian, asian, french);

    static {
        italian.setDishes(new HashSet<>(DishTestData.ITALIAN_DISHES));
        asian.setDishes(new HashSet<>(DishTestData.ASIAN_DISHES));
        french.setDishes(new HashSet<>(DishTestData.FRENCH_DISHES));
    }

    public static final List<RestaurantWithNumberVoicesDto> restaurantsWithNumberVoices = List.of(
            new RestaurantWithNumberVoicesDto(ITALIAN_ID, "Italian", 2),
            new RestaurantWithNumberVoicesDto(ASIAN_ID, "Asian", 1),
            new RestaurantWithNumberVoicesDto(FRENCH_ID, "French", 0)
    );
    public static final List<RestaurantWithNumberVoicesDto> restaurantsWithNumberVoicesUpdated = List.of(
            new RestaurantWithNumberVoicesDto(ITALIAN_ID, "Italian", 2),
            new RestaurantWithNumberVoicesDto(ASIAN_ID, "Asian", 1),
            new RestaurantWithNumberVoicesDto(FRENCH_ID, "French", 1)
    );

    public static final List<RestaurantWithNumberVoicesDto> restaurantsWithNumberVoicesUpdated_2 = List.of(
            new RestaurantWithNumberVoicesDto(ITALIAN_ID, "Italian", 1),
            new RestaurantWithNumberVoicesDto(ASIAN_ID, "Asian", 1),
            new RestaurantWithNumberVoicesDto(FRENCH_ID, "French", 1)
    );

    public static Restaurant getNew() {
        return new Restaurant(null, "Created Restaurant");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(ITALIAN_ID, "Updated Restaurant");
    }

    public static List<RestaurantWithNumberVoicesDto> getWithNumberVoicesDtos() {
        return List.of(new RestaurantWithNumberVoicesDto(ITALIAN_ID, "Italian", 2),
                new RestaurantWithNumberVoicesDto(ASIAN_ID, "Asian", 1),
                new RestaurantWithNumberVoicesDto(FRENCH_ID, "French", 0));
    }
}