package net.shyshkin.study.redis.performance.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ProductVisitServiceTest {


    @Test
    void collectMapTest_toMap() {
        //given
        List<Integer> idList = List.of(2, 1, 2, 3, 1, 1, 1, 2, 1, 2);

        //when
        Map<Integer, Integer> idCount = idList.stream()
                .collect(Collectors.toMap(Function.identity(), i -> 1, Integer::sum));

        //then
        log.debug("{}", idCount);
        assertThat(idCount.get(1)).isEqualTo(5);
        assertThat(idCount.get(2)).isEqualTo(4);
        assertThat(idCount.get(3)).isEqualTo(1);
    }

    @Test
    void collectMapTest_grouping() {
        //given
        List<Integer> idList = List.of(2, 1, 2, 3, 1, 1, 1, 2, 1, 2);

        //when
        Map<Integer, Long> idCount = idList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //then
        log.debug("{}", idCount);
        assertThat(idCount.get(1)).isEqualTo(5);
        assertThat(idCount.get(2)).isEqualTo(4);
        assertThat(idCount.get(3)).isEqualTo(1);
    }
}