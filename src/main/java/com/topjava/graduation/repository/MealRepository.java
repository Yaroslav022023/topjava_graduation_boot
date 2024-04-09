package com.topjava.graduation.repository;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MealRepository extends JpaRepository<Meal, Integer> {

    @Query("SELECT m FROM Meal m WHERE m.id=:id AND m.restaurant.id=:restaurantId")
    Optional<Meal> get(int id, int restaurantId);

    @Query("SELECT m FROM Meal m WHERE m.restaurant.id=:restaurantId ORDER BY m.date DESC")
    List<Meal> findAllByRestaurantId(int restaurantId);

    default Meal getBelonged(int id, int restaurantId) {
        return get(id, restaurantId).orElseThrow(
                () -> new NotFoundException("Meal id=" + id + " is not exist or doesn't belong to Restaurant id=" + restaurantId));
    }
}