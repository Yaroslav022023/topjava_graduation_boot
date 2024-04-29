package com.topjava.graduation.repository;

import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.dishes m WHERE m.date = :date")
    List<Restaurant> findAllWithTodayDishes(LocalDate date);

    @Query("SELECT new com.topjava.graduation.dto.RestaurantWithNumberVoicesDto(r.id, r.name, " +
            "(SELECT COUNT(v) FROM Voice v WHERE v.restaurant = r AND v.date = :date)) " +
            "FROM Restaurant r")
    List<RestaurantWithNumberVoicesDto> findAllWithTodayNumberVoices(LocalDate date);

    default Restaurant get(int id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Restaurant with id=" + id + " not found"));
    }
}