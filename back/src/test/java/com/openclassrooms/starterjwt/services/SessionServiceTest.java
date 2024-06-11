package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {
    @InjectMocks
    private SessionService sessionService;
    @Mock
    private  SessionRepository mockSessionRepository;

    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        // ce code garantit que les mocks sont prêts à être utilisés dans chaque test.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSessionService (){
        assertEquals(mockSessionRepository, sessionService.getSessionRepository());
    }

    @Test
    public void testCreateSession(){
        //Given
        Session sessionToSave = new Session();
        // Simulation du comportement du repository
        when(mockSessionRepository.save(sessionToSave)).thenReturn(sessionToSave);

        // Appel de la méthode à tester
        //When
        Session createdSession = sessionService.create(sessionToSave);

        // Vérification si la session retournée est la même que celle passée en paramètre
        //Then
        assertEquals(sessionToSave, createdSession);

    }
    @Test
    public void testDeleteSession(){
        //Given
        Long sessionId = 1L;

        //When
        //appel de la methode delete du service avec l'id définit précédemment
        sessionService.delete(sessionId);

        //verification que la "deleteById" a été appelé qu'une seule fois avec l'id de l'utilisateur
        //Then
        verify(mockSessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    public void testFindAllSessions(){//vérification si la methode retourne la liste des sessions correctement
         //Créaction d'une liste de session
        List<Session> sessions = new ArrayList<>();

        when(mockSessionRepository.findAll()).thenReturn(sessions);
        //appel de la methode findAll() de sessionService, le résultat stocké dans la liste foundSessions
        List<Session> foundSessions = sessionService.findAll();

        verify(mockSessionRepository, times(1)).findAll();
        //vérification de que la liste retournée par la méthode findAll() de sessionService est la même que la liste initiale sessions
        assertEquals(sessions, foundSessions);
    }

    @Test
    //vérifie que lorsque la méthode getById() est appelée sur sessionService avec un identifiant de session spécifique, elle renvoie la session correspondante en utilisant le repository mock.
    public void testGetSessionById (){
        //création d'une instance de la classe Session
        Session session = new Session();
        session.setId(1L);

        //comportement du mock: lors de l'appel de la méthode findById() avec l'identifiant de la session créée précédemment
        //le mock doit retourner un objet Optional contenant cette session.
        when(mockSessionRepository.findById(session.getId())).thenReturn(Optional.of(session));

        //appelle la méthode getById() de sessionService en lui passant l'identifiant de la session
        //  le résultat est stocké dans une variable result.
        Session result = sessionService.getById(session.getId());

        //utilise l'assertion assertEquals() pour comparer la session récupérée avec la session créée précédemment
        assertEquals(session, result);
    }

    @Test
    public void testUpdateSession(){
        // définition des données nécessaires à la mise à jour d'une session.
        Long sessionId = 1L;
        Session sessionToUpdate = new Session();
        sessionToUpdate.setId(sessionId);
        sessionToUpdate.setName("Updated Session");
        sessionToUpdate.setDate(new Date(22/4/2024));
        sessionToUpdate.setDescription("Updated Description");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        sessionToUpdate.setCreatedAt(createdAt);
        sessionToUpdate.setUpdatedAt(updatedAt);

        // l'appel de la méthode findById() avec l'identifiant de la session à mettre à jour,
        // le mock doit retourner un objet Optional contenant la session à mettre à jour.
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionToUpdate));

        //configuration du mock pour spécifier que lors de l'appel de la méthode save() avec la session mise à jour,
        // le mock doit renvoyer la session mise à jour elle-même.
        when(mockSessionRepository.save(sessionToUpdate)).thenReturn(sessionToUpdate);

        //appelle de la méthode update() de sessionService en lui passant l'identifiant de la session à mettre à jour
        //et les détails mis à jour
        //le résultat est stocké dans une variable updatedSession.
        Session updatedSession = sessionService.update(sessionId, sessionToUpdate);

        //utilisation d'assertion assertEquals() pour comparer la
        //Si la session récupérée est égale à la session mise à jour
        assertEquals(sessionToUpdate, updatedSession);
    }

    @Test
    public void testParticipate() {
        // Définition de l'ID de la session et l'ID de l'utilisateur
        Long sessionId = 1L;
        Long userId = 1L;

        // Création d'une session factice
        Session session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>());

        // Création d'un utilisateur factice
        User user = new User();
        user.setId(userId);

        // Cas où la session et l'utilisateur sont trouvés
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Appel la méthode à tester
        sessionService.participate(sessionId, userId);

        // Vérification que l'utilisateur a été ajouté à la session
        assertTrue(session.getUsers().contains(user));

        // Cas où la session n'est pas trouvée
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        // Vérifier que la méthode lance une NotFoundException lorsque la session n'est pas trouvée
        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));

        // Réinitialisation de la session
        session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>());

        // Cas où l'utilisateur n'est pas trouvé
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Vérification que la méthode lance une NotFoundException lorsque l'utilisateur n'est pas trouvé
        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));

        // Réinitialiser la session et l'utilisateur
        session = new Session();
        session.setId(sessionId);
        session.setUsers(new ArrayList<>());
        user = new User();
        user.setId(userId);

        // Cas où l'utilisateur participe déjà à la session
        session.getUsers().add(user);
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Vérification que la méthode lance une BadRequestException lorsque l'utilisateur participe déjà à la session
        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
    }

    @Test//_SessionExistsUserExists_Success
    public void testNoLongerParticipate() {
        //définition des données nécessaires.
        Long sessionId = 1L;
        Long userId = 101L;

        Session session = new Session();
        session.setId(sessionId);
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(userId);
        users.add(user);
        session.setUsers(users);

        //spécification que lors de l'appel de la méthode findById() avec l'identifiant de la session,
        // le mock doit retourner un objet Optional contenant la session préparée précédemment.
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        //appelle de la méthode noLongerParticipate() de sessionService en lui passant l'identifiant de la session et l'identifiant de l'utilisateur
        //encapsulé dans une expression lambda qui ne lance pas d'exception (assertDoesNotThrow()).
        assertDoesNotThrow(() -> sessionService.noLongerParticipate(sessionId, userId));

        //vérification que l'utilisateur spécifié n'est plus présent dans la liste des utilisateurs de la session après l'exécution de la méthode.
        assertFalse(session.getUsers().stream().anyMatch(u -> u.getId().equals(userId)));
        verify(mockSessionRepository, times(1)).save(session);
    }

    @Test
    public void testNoLongerParticipateSessionDoesNotExist() {//Vérification que lorsque la méthode noLongerParticipate() est appelée sur sessionService avec l'identifiant d'une session qui n'existe pas, elle lance une exception de type NotFoundException et qu'aucune modification n'est enregistrée via le repository mock.
        //Définitions des données nécessaires.
        Long sessionId = 1L;
        Long userId = 101L;

        //spécification que lors de l'appel de la méthode findById() avec l'identifiant de la session,
        // le mock doit retourner un objet Optional vide, indiquant que la session n'existe pas.
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        //appelle de la méthode noLongerParticipate() de sessionService
        //Comme la session n'existe pas, on s'attend à ce que la méthode lance une exception de type NotFoundException.
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(sessionId, userId));

        //utilisation verify avec never() pour s'assurer que la méthode save() du mock mockSessionRepository n'est jamais appelée.
        verify(mockSessionRepository, never()).save(any());
    }

    @Test
    public void testNoLongerParticipateSessionExist() {
        //Définition les données nécessaires
        Long sessionId = 1L;
        Long userId = 101L;

        Session session = new Session();
        session.setId(sessionId);
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(102L); // user ID différent
        users.add(user);
        session.setUsers(users);

        // l'appel de la méthode findById() avec l'identifiant de la session,
        // le mock doit retourner un objet Optional contenant la session préparée précédemment.
        when(mockSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // appelle de la méthode noLongerParticipate() de sessionService
        //Comme l'utilisateur ne participe pas à la session, on s'attend à ce que la méthode lance une exception de type BadRequestException.
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));

        //utilisation de verify avec never() pour s'assurer que la méthode save() du mock mockSessionRepository n'est jamais appelée.
        verify(mockSessionRepository, never()).save(any());
    }
}

