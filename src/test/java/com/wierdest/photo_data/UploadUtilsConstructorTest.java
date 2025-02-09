package com.wierdest.photo_data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.wierdest.photo_data.utils.UploadUtils;

class UploadUtilsConstructorTest {
    @Test
    void uploadUtilsCannotInstantiate() throws NoSuchMethodException  {
        // Using reflection to get a private constructor and change the object's accessible flag
        Constructor<UploadUtils> constructor = UploadUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof IllegalStateException, "Expected cause to be IllegalStateException!");
        assertEquals("Cannot instantiate utility class!", cause.getMessage());
    }
}
