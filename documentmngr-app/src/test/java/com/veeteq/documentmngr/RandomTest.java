package com.veeteq.documentmngr;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator;

import java.util.ArrayList;
import java.util.HashSet;

public class RandomTest {

    @Disabled("Takes too long...")
    @DisplayName("Test random string collision")
    @Test
    public void justTest() {
        CharacterPredicate[] predicates = {t -> t >= '0' && t <= '9', t -> t >= 'A' & t <= 'Z', t -> t >= 'a' & t <= 'z'};

        var s = new HashSet<String>();
        var l = new ArrayList<String>();
        for (int i = 0; i < 40_000_000; i++) {
            var generator = new RandomStringGenerator.Builder()
                    .withinRange('0', 'z')
                    .filteredBy(predicates)
                    .build();
            s.add(generator.generate(8));
        }
        System.out.println(s.size());
    }
}
//89_999_983 / 90_000_000
//79999986 / 80