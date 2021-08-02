package net.shyshkin.study.redis.redisspring.geo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class RestaurantUtil {

    private static final TypeReference<List<Restaurant>> RESTAURANT_TYPE_REFERENCE = new TypeReference<>() {
    };

    public static List<Restaurant> getRestaurants() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = RestaurantUtil.class.getClassLoader().getResourceAsStream("restaurant.json");
        try {
            return objectMapper.readValue(inputStream, RESTAURANT_TYPE_REFERENCE);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
