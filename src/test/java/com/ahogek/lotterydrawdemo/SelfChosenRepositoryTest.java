package com.ahogek.lotterydrawdemo;

import com.ahogek.lotterydrawdemo.repository.SelfChosenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

/**
 * @author AhogeK
 * @since 2025-01-05 23:16:38
 */
@SpringBootTest
class SelfChosenRepositoryTest {

    @Autowired
    private SelfChosenRepository selfChosenRepository;

    @Test
    void test() {
        Assertions.assertEquals(7,
                selfChosenRepository.findNumbersByDrawTimeOrderByNumberType(
                        LocalDate.of(2025, 1, 4)
                ).size()
        );
    }
}
