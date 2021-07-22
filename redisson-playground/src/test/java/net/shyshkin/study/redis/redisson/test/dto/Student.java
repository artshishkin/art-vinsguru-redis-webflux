package net.shyshkin.study.redis.redisson.test.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Student {

    private String name;
    private int age;
    private String city;

    @Singular
    private List<Integer> marks;

}
