package net.shyshkin.study.redis.redisson.test.util;

import net.shyshkin.study.redis.redisson.test.dto.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantUtilTest {

    @Test
    void getRestaurants() {

        //when
        List<Restaurant> restaurants = RestaurantUtil.getRestaurants();

        //then
        assertThat(restaurants)
                .isNotNull()
                .hasSizeGreaterThan(10)
                .allSatisfy(restaurant -> assertThat(restaurant).hasNoNullFieldsOrProperties());
    }
}