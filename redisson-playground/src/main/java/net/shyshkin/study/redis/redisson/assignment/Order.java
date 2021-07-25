package net.shyshkin.study.redis.redisson.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Order {

    private LocalDateTime date;
    private UUID id;
    private UserRank rank;
    private String product;
    private Integer amount;

}
