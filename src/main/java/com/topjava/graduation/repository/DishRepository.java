package com.topjava.graduation.repository;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query("SELECT m FROM Dish m WHERE m.restaurant.id=:restaurantId ORDER BY m.date DESC")
    List<Dish> findAllByRestaurantId(int restaurantId);

    @Query("SELECT m FROM Dish m WHERE m.id=:id AND m.restaurant.id=:restaurantId")
    Optional<Dish> get(int id, int restaurantId);

    default Dish getBelonged(int id, int restaurantId) {
        return get(id, restaurantId).orElseThrow(
                () -> new NotFoundException(
                        "Dish id=" + id + " is not exist or doesn't belong to Restaurant id=" + restaurantId));
    }

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restaurantId")
    int delete(int id, int restaurantId);

    default void deleteExisted(int id, int restaurantId) {
        if (delete(id, restaurantId) == 0) throw new NotFoundException("Dish with id=" + id + " not found");
    }
}