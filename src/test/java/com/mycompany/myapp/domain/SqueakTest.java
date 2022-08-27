package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SqueakTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Squeak.class);
        Squeak squeak1 = new Squeak();
        squeak1.setId(1L);
        Squeak squeak2 = new Squeak();
        squeak2.setId(squeak1.getId());
        assertThat(squeak1).isEqualTo(squeak2);
        squeak2.setId(2L);
        assertThat(squeak1).isNotEqualTo(squeak2);
        squeak1.setId(null);
        assertThat(squeak1).isNotEqualTo(squeak2);
    }
}
