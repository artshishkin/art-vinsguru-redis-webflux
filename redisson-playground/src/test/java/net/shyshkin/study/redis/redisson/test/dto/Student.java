package net.shyshkin.study.redis.redisson.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Student implements Serializable {

    private static final long serialVersionUID = -6831884057995571133L;

    private String name;
    private int age;
    private String city;

}
