package com.topjava.graduation.web.dish;

import com.topjava.graduation.model.Dish;
import com.topjava.graduation.service.DishService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.topjava.graduation.util.validation.ValidationUtil.assureIdConsistent;
import static com.topjava.graduation.util.validation.ValidationUtil.checkNew;
import static com.topjava.graduation.web.dish.AdminDishController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminDishController {
    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DishService service;

    public AdminDishController(DishService service) {
        this.service = service;
    }

    @GetMapping()
    public List<Dish> getAll(@PathVariable int restaurantId) {
        log.info("getAll");
        return service.getAll(restaurantId);
    }

    @GetMapping("/{id}")
    public Dish get(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("get dish={} for restaurant={}", id, restaurantId);
        return service.get(id, restaurantId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createWithLocation(@Valid @RequestBody Dish dish, @PathVariable int restaurantId) {
        log.info("create {} for restaurant={}", dish, restaurantId);
        checkNew(dish);
        Dish created = service.save(dish, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Dish dish, @PathVariable int id, @PathVariable int restaurantId) {
        log.info("update {} for restaurant={}", dish, restaurantId);
        assureIdConsistent(dish, id);
        service.save(dish, restaurantId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("delete={} from restaurant={}", id, restaurantId);
        service.delete(id, restaurantId);
    }
}