package net.shyshkin.study.redis.redisson.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Student {

    private String name;
    private int age;
    private String city;

}
