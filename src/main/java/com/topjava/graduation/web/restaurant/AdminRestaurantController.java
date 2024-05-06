package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.dto.RestaurantWithNumVoicesViewDto;
import com.topjava.graduation.model.Restaurant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.topjava.graduation.util.validation.ValidationUtil.assureIdConsistent;
import static com.topjava.graduation.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantController extends AbstractRestaurantController {
    static final String REST_URL = "/api/admin/restaurants";

    @GetMapping
    public List<RestaurantViewDto> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    @Override
    @GetMapping("/menu_today")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return super.getAllWithTodayDishes();
    }

    @GetMapping("/number-voices")
    public List<RestaurantWithNumVoicesViewDto> getAllWithTodayNumberVoices() {
        log.info("getAllWithTodayNumberVoices");
        return service.getAllWithTodayNumberVoices();
    }

    @GetMapping("/{id}")
    public RestaurantViewDto get(@PathVariable int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = service.save(restaurant);

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        service.save(restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        service.delete(id);
    }
}