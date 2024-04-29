package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.View;
import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.model.Restaurant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = AdminRestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantRestController extends AbstractRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";

    @Override
    @GetMapping
    public List<RestaurantViewDto> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/with-today-dishes")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return super.getAllWithTodayDishes();
    }

    @Override
    @GetMapping("/number-voices")
    public List<RestaurantWithNumberVoicesDto> getAllWithTodayNumberVoices() {
        return super.getAllWithTodayNumberVoices();
    }

    @Override
    @GetMapping("/{id}")
    public RestaurantViewDto get(@PathVariable int id) {
        return super.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createWithLocation(@Validated(View.Web.class) @RequestBody Restaurant restaurant) {
        Restaurant created = super.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Restaurant restaurant, @PathVariable int id) {
        super.update(restaurant, id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }
}