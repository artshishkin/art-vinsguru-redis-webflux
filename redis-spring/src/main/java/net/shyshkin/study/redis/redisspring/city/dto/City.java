package net.shyshkin.study.redis.redisspring.city.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @JsonProperty("zip")
    private String zipcode;

    private String city;

    private String stateName;

    private int temperature;

/*
{
  "zip": "00603",
  "lat": 18.4544,
  "lng": -67.12201,
  "city": "Aguadilla",
  "stateId": "PR",
  "stateName": "Puerto Rico",
  "population": 47081,
  "density": 574.9,
  "temperature": 97
}
*/

}
