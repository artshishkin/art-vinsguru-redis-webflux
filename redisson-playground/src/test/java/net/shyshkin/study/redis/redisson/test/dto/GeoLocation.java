package net.shyshkin.study.redis.redisson.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GeoLocation {

    private double longitude;
    private double latitude;

}
