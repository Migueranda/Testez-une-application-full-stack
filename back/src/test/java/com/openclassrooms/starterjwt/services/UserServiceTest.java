package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class UserServiceTest {
    @InjectMocks//injection des mocks dans le service
    private UserService userService;

    // Cretion d'un faux UserRepository pour simuler le comportement d'un vrai repository
    @Mock
    private UserRepository mockUserRepository;
    // la méthode setUp() annotée avec @BeforeEach pour initialiser les annotations Mockito avant chaque test.
    // Cela garantit que les mocks sont injectés correctement dans le UserService avant d'exécuter le test.
    @BeforeEach
    void setUp() {
        // ce code garantit que les mocks sont prêts à être utilisés dans chaque test.
        MockitoAnnotations.openMocks(this);
    }
    //ce test vérifie que le UserServeice initialisé correctement avec le bon UserRepository
    // en utilisant des mocks Mockito pour simuler le comportement d'UserRepository
    @Test
    public void testUserService(){
        assertEquals(mockUserRepository, userService.getUserRepository());
    }

    @Test
    public void testDeleteById(){
        //given
        Long userId = 123L;

        //when
        //appel de la methode delete du service avec l'id définit précédemment
        userService.delete(userId);

        //then
        //verification que la "deleteById" à été appelé q'une seule fois avec l'id de l'utilisateur
        verify(mockUserRepository, times(1)).deleteById(userId);

    }
    @Test
    public void testFindUserById(){
        //given
        Long userId = 123L;
        User user = new User();

        //simulation du comportement de la methode findById
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        //appel de la methode findById du service avec l'id définit précédemment
        User result = userService.findById(userId);

        //then
        //verification que la "findById" a été appelé qu'une seule fois avec l'id de l'utilisateur
        verify(mockUserRepository, times(1)).findById(userId);
        // vériication que l'utilisateur retourné est bien celui qui est défini dans la section "given
        assertEquals(user, result);
    }
}