package net.shyshkin.study.redis.redisspring.city.repository;

import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, String> {
}
