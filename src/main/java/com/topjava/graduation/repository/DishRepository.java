package com.topjava.graduation.repository;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query("SELECT m FROM Dish m WHERE m.id=:id AND m.restaurant.id=:restaurantId")
    Optional<Dish> get(int id, int restaurantId);

    @Query("SELECT m FROM Dish m WHERE m.restaurant.id=:restaurantId ORDER BY m.date DESC")
    List<Dish> findAllByRestaurantId(int restaurantId);

    default Dish getBelonged(int id, int restaurantId) {
        return get(id, restaurantId).orElseThrow(
                () -> new NotFoundException("Dish id=" + id + " is not exist or doesn't belong to Restaurant id=" + restaurantId));
    }
}