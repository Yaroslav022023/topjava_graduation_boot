package com.topjava.graduation.web.dish;

import com.topjava.graduation.MatcherFactory;
import com.topjava.graduation.model.Dish;

import java.time.LocalDate;
import java.util.List;

import static com.topjava.graduation.model.BaseEntity.START_SEQ;

public class DishTestData {
    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Dish.class, "restaurant");
    public static final int NOT_FOUND = 111111;
    public static final int DISH_ID = START_SEQ + 8;

    public static final Dish italian_dish_1 = new Dish(DISH_ID, LocalDate.now(), "Steak Philadelphia", 1600);
    public static final Dish italian_dish_2 = new Dish(DISH_ID + 1, LocalDate.now(), "Margherita Pizza", 300);
    public static final Dish italian_dish_3 = new Dish(DISH_ID + 2, LocalDate.now(), "Pasta", 700);

    public static final Dish asian_dish_1 = new Dish(DISH_ID + 3, LocalDate.now(), "Paella", 300);
    public static final Dish asian_dish_2 = new Dish(DISH_ID + 4, LocalDate.now(), "Shawarma", 200);
    public static final Dish asian_dish_3 = new Dish(DISH_ID + 5, LocalDate.now(), "Pad Thai", 250);
    public static final Dish asian_dish_4 = new Dish(DISH_ID + 6, LocalDate.now(), "Tandoori Chicken", 440);

    public static final Dish french_dish_1 = new Dish(DISH_ID + 7, LocalDate.now(), "Ratatouille", 680);
    public static final Dish french_dish_2 = new Dish(DISH_ID + 8, LocalDate.now(), "Beef Bourguignon", 530);

    public static final List<Dish> ITALIAN_DISHES = List.of(italian_dish_1, italian_dish_2, italian_dish_3);
    public static final List<Dish> ASIAN_DISHES = List.of(asian_dish_1, asian_dish_2, asian_dish_3, asian_dish_4);
    public static final List<Dish> FRENCH_DISHES = List.of(french_dish_1, french_dish_2);

    public static Dish getNew() {
        return new Dish(null, LocalDate.now(), "Created dish", 930);
    }

    public static Dish getUpdated() {
        return new Dish(italian_dish_1.id(), LocalDate.now(), "Updated dish", 1500);
    }
}