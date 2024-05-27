package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserValid() {
        User user = new User()
                .setEmail("test@example.com")
                .setFirstName("John")
                .setLastName("Doe")
                .setPassword("securepassword")
                .setAdmin(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
    @Test
    public void testEmailInvalid() {
        User user = new User()
                .setEmail("invalid-email")
                .setFirstName("John")
                .setLastName("Doe")
                .setPassword("securepassword")
                .setAdmin(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("doit être une adresse électronique syntaxiquement correcte", violations.iterator().next().getMessage());
    }


    @Test
    public void testFirstNameSize() {
        User user = new User()
                .setEmail("test@example.com")
                .setFirstName("ThisNameIsWayTooLongForTheConstraint")
                .setLastName("Doe")
                .setPassword("securepassword")
                .setAdmin(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("la taille doit être comprise entre 0 et 20", violations.iterator().next().getMessage());
    }

    @Test
    public void testLastNameSize() {
        User user = new User()
                .setEmail("test@example.com")
                .setFirstName("John")
                .setLastName("ThisNameIsWayTooLongForTheConstraint")
                .setPassword("securepassword")
                .setAdmin(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("la taille doit être comprise entre 0 et 20", violations.iterator().next().getMessage());
    }

    @Test
    public void testPasswordSize() {
        User user = new User()
                .setEmail("test@example.com")
                .setFirstName("John")
                .setLastName("Doe")
                .setPassword("short")
                .setAdmin(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();

        assertEquals("la taille doit être comprise entre 8 et 120", violations.iterator().next().getMessage());
    }

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("securepassword");
        user.setAdmin(true);

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("securepassword", user.getPassword());
        assertTrue(user.isAdmin());
    }

}