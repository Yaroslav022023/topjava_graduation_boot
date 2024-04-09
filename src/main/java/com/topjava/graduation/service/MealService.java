package com.topjava.graduation.service;

import com.topjava.graduation.model.Meal;
import com.topjava.graduation.repository.MealRepository;
import com.topjava.graduation.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final RestaurantRepository restaurantRepository;

    public MealService(MealRepository mealRepository, RestaurantRepository restaurantRepository) {
        this.mealRepository = mealRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<Meal> getAll(int restaurantId) {
        return mealRepository.findAllByRestaurantId(restaurantId);
    }

    public Meal get(int id, int restaurantId) {
        return mealRepository.getBelonged(id, restaurantId);
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public Meal save(Meal meal, int restaurantId) {
        Assert.notNull(meal, "meal must not be null");
        if (!meal.isNew() && get(meal.id(), restaurantId) == null) {
            return null;
        }
        meal.setRestaurant(restaurantRepository.getReferenceById(restaurantId));
        return mealRepository.save(meal);
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(int id, int restaurantId) {
        mealRepository.delete(mealRepository.getBelonged(id, restaurantId));
    }
}