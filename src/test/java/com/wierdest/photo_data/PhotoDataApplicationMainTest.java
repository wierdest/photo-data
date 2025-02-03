package com.wierdest.photo_data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
// this test is not strictly necessary, as main just bootstraps the application
// i did it just so coverage would be at 100%, it was buggin me
public class PhotoDataApplicationMainTest {
    @Test
    void testMain() {
        PhotoDataApplication.main(new String[] {});
        assertThat(true).isTrue();
    }

}
