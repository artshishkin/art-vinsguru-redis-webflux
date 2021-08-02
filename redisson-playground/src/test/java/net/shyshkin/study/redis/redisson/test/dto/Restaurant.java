package net.shyshkin.study.redis.redisson.test.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    private String id;
    private String city;
    private double latitude;
    private double longitude;
    private String name;
    private String zip;


    /*
    {
        "id": "AVwdhkOGByjofQCxp9qV",
        "city": "Monahans",
        "latitude": 31.580721,
        "longitude": -102.891455,
        "name": "Dairy Queen",
        "zip": 79756
    }
    */

}
