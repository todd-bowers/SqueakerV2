package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MentionsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mentions.class);
        Mentions mentions1 = new Mentions();
        mentions1.setId(1L);
        Mentions mentions2 = new Mentions();
        mentions2.setId(mentions1.getId());
        assertThat(mentions1).isEqualTo(mentions2);
        mentions2.setId(2L);
        assertThat(mentions1).isNotEqualTo(mentions2);
        mentions1.setId(null);
        assertThat(mentions1).isNotEqualTo(mentions2);
    }
}
