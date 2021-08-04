package net.shyshkin.study.redis.redisspring.geo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GeoLocation implements Serializable {

    private static final long serialVersionUID = -2810919310128589554L;

    private double longitude;
    private double latitude;

}
