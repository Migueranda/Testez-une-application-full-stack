package com.openclassrooms.starterjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.controllers.AuthController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.controllers.UserController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;

import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringBootSecurityJwtApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TeacherService teacherService;

	@MockBean
	private TeacherMapper teacherMapper;

	@InjectMocks
	private TeacherController teacherController;

	@MockBean
	UserRepository userRepository;

	@MockBean
	private UserService userService;

	@MockBean
	private UserMapper userMapper;

	@InjectMocks
	private UserController userController;

	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private PasswordEncoder passwordEncoder;
	@MockBean
	private AuthenticationManager authenticationManager;
	@MockBean
	private AuthController authController;

	@InjectMocks
	private SessionController sessionController;
	@MockBean
	private SessionMapper sessionMapper;
	@MockBean
	private SessionService sessionService;
	@MockBean
	private SessionRepository sessionRepository;

	@Autowired
	private JwtUtils jwtUtils;

	@Test
	public void contextLoads() {
		assertNotNull(mockMvc);
	}
	private static long sessionIdCounter = 1; // Initialize the counte

	public String Email = "john@example.com";
	public String LastName = "Doe";
	public String FirstName = "John";
	public String Password = "password";
	private Session session;
	@Mock
	private SessionDto sessionDto;
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		List<User> mockUsers = new ArrayList<>();
		mockUsers.add(new User(1L, "user1@mail.com", "User", "USER", "password", false, LocalDateTime.now(), LocalDateTime.now()));
		mockUsers.add(new User(2L, "user2@mail.com", "Test", "TEST", "password", true, LocalDateTime.now(), LocalDateTime.now()));

		passwordEncoder = Mockito.mock(PasswordEncoder.class);
		authenticationManager = Mockito.mock(AuthenticationManager.class);
		jwtUtils = Mockito.mock(JwtUtils.class);

		authController = new AuthController(authenticationManager,
				passwordEncoder,
				jwtUtils,
				userRepository);

		sessionIdCounter = 1;
	}

	/**
	 * Ce test vérifie le bon fonctionnement de la méthode authenticateUser dans le AuthController.
	 * Il initialise un utilisateur, le sauvegarde, crée une requête de connexion, simule
	 * l'authentification et vérifie que la réponse est correcte.
	 */
	@DisplayName("Authentification d'un utilisateur")
	@Test
	void authenticateUserTest(){

		// Initialisation d'un Utilisateur avec des détails fictifs
		User user = new User();
		user.setEmail(Email);  // Email utilisateur
		user.setFirstName(FirstName);  // Prénom utilisateur
		user.setLastName(LastName);  // Nom utilisateur
		user.setPassword(Password);  // Mot de passe utilisateur
		user.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);  // Génération d'un ID unique
		userRepository.save(user);  // Sauvegarde de l'utilisateur dans le dépôt


		// Création d'une instance UserDetailsImpl avec les détails utilisateur
		UserDetailsImpl userDetail = new UserDetailsImpl(user.getId(), Email,FirstName,LastName,true,Password);

		// Configuration de la requête de connexion
		var loginRequest = new LoginRequest();
		loginRequest.setEmail(Email);
		loginRequest.setPassword(Password);

		// Simulation de l'authentification et de la génération du jeton JWT
		var authentication = Mockito.mock(Authentication.class);
		Mockito.when(authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()))).thenReturn(authentication);
		Mockito.when(authentication.getPrincipal()).thenReturn(userDetail);
		Mockito.when(jwtUtils.generateJwtToken(authentication)).thenReturn("token");

		// Exécution de la méthode authenticateUser du contrôleur d'authentification
		final ResponseEntity response = authController.authenticateUser(loginRequest);

		// Vérification des résultats de la réponse
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(Email, ((JwtResponse)response.getBody()).getUsername());
		assertEquals(FirstName, ((JwtResponse)response.getBody()).getFirstName());
		assertEquals(LastName, ((JwtResponse)response.getBody()).getLastName());
		assertEquals(false, ((JwtResponse)response.getBody()).getAdmin());
		assertEquals("token", ((JwtResponse)response.getBody()).getToken());
	}

	/**
	 * Test pour l'inscription d'un utilisateur.
	 * Vérifie que la méthode registerUser retourne une réponse HTTP OK et le message approprié
	 * lorsque l'utilisateur est inscrit avec succès.
	 */
	@Test
	@DisplayName("Enregistrement d'un utilisateur")
	public void registerUserTest() {
		// Given
		SignupRequest signUpRequest = new SignupRequest();
		signUpRequest.setEmail(Email);  		// Email utilisateur
		signUpRequest.setFirstName(FirstName); 	// Prénom utilisateur
		signUpRequest.setLastName(LastName); 	// Nom utilisateur
		signUpRequest.setPassword(Password);	//Mot de passe utilisateur

		// Simulation du comportement du dépôt utilisateur et de l'encodeur de mot de passe
		when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

		// Appel de la méthode registerUser du contrôleur d'authentification
		ResponseEntity<?> responseEntity = authController.registerUser(signUpRequest);

		//Vérification de la réponse
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("User registered successfully!", ((MessageResponse) responseEntity.getBody()).getMessage());

	}

	/**
	 * Teste l'inscription d'un utilisateur avec un e-mail déjà existant.
	 * Vérifie que la méthode registerUser retourne une réponse HTTP BAD_REQUEST
	 * et le message approprié lorsque l'e-mail est déjà pris.
	 */
	@Test
	@DisplayName("Enregistrement d'un utilisateur avec un email existant")
	public void registerUserExistingEmail() {
		// Définition des détails de l'utilisateur avec un e-mail existant
		String existingEmail = "existing@example.com";
		SignupRequest signUpRequest = new SignupRequest();
		signUpRequest.setEmail(existingEmail);
		signUpRequest.setFirstName(FirstName);
		signUpRequest.setLastName(LastName);
		signUpRequest.setPassword(Password);

		// Simulation du comportement du dépôt utilisateur
		when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

		// Appel de la méthode registerUser du contrôleur d'authentification
		ResponseEntity<?> responseEntity = authController.registerUser(signUpRequest);

		// Vérification de la réponse
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals("Error: Email is already taken!", ((MessageResponse) responseEntity.getBody()).getMessage());

	}

	/**
	 * Test pour vérifier que l'API permet de récupérer une session par son ID.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 * Vérifie que la réponse contient les informations correctes de la session.
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Trouver la session en function de l'id correspondant")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdSession() throws Exception {
		// Création d'une session et d'un DTO de session avec des valeurs fictives
		String id = "1";
		Session session = new Session();
		session.setId(1L);
		session.setName("Test Session");

		SessionDto sessionDto = new SessionDto();
		sessionDto.setId(1L);
		sessionDto.setName("Test Session");

		// Simulation du comportement du service et du mapper
		when(sessionService.getById(anyLong())).thenReturn(session);
		when(sessionMapper.toDto(session)).thenReturn(sessionDto);

		//Exécution de la requête GET et vérification des résultats
		mockMvc.perform(get("/api/session/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Session"));
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 404 Not Found
	 * lorsque la session avec l'ID donné n'est pas trouvée.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Lorsque la session avec l'ID donné n'est pas trouvée")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdNotFound() throws Exception {
		// Définition de l'ID de la session à rechercher
		String id = "1";

		// Simulation du comportement du service pour retourner null
		when(sessionService.getById(anyLong())).thenReturn(null);

		// Exécution de la requête GET et vérification du statut 404 Not Found
		mockMvc.perform(get("/api/session/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque la session est recherchée avec un ID invalide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Lorsque la session est recherchée avec un ID invalide")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdBadRequest() throws Exception {
		// Définition d'un ID de session invalide
		String invalidId = "abc";

		//  Exécution de la requête GET et vérification du statut 400 Bad Request
		mockMvc.perform(get("/api/session/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API retourne toutes les sessions avec un statut HTTP 200 OK.
	 * Vérifie que les services et les mappers sont appelés correctement.
	 */
	@Test
	@DisplayName("Récupérer toutes les sessions")
	void findAllSessions() {
		// Création des listes de sessions et de leurs DTOs
		List<Session> sessions = Collections.singletonList(session);
		List<SessionDto> sessionDtos = Collections.singletonList(sessionDto);

		// Simulation des comportements du service et du mapper
		when(sessionService.findAll()).thenReturn(sessions);
		when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

		// Appel de la méthode findAll du contrôleur
		SessionController sessionController = new SessionController(sessionService, sessionMapper);
		ResponseEntity<?> response = sessionController.findAll();

		// Vérification du statut et du corps de la réponse
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(sessionDtos, response.getBody());

		// Vérification des appels aux services et aux mappers
		verify(sessionService).findAll();
		verify(sessionMapper).toDto(sessions);
	}

	/**
	 * Test pour vérifier que l'API permet de créer une nouvelle session et retourne les bonnes informations.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("création d'une nouvelle session")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testCreateSession() throws Exception {
		// Création des objets SessionDto, Session et responseDto avec des valeurs fictives
		SessionDto sessionDto = new SessionDto();
		sessionDto.setName("Test Session");
		sessionDto.setDate(new Date());
		sessionDto.setTeacher_id(1L); // Assurez-vous que cet ID correspond à un Teacher existant
		sessionDto.setDescription("This is a test session");

		Session session = new Session();
		session.setId(1L);
		session.setName("Test Session");
		session.setDate(new Date());
		session.setDescription("This is a test session");

		SessionDto responseDto = new SessionDto();
		responseDto.setId(1L);
		responseDto.setName("Test Session");
		responseDto.setDate(sessionDto.getDate());
		responseDto.setTeacher_id(1L);
		responseDto.setDescription("This is a test session");

		// Simulation des comportements du mapper et du service
		when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
		when(sessionService.create(any(Session.class))).thenReturn(session);
		when(sessionMapper.toDto(any(Session.class))).thenReturn(responseDto);

		// Exécution de la requête POST et vérification des résultats
		mockMvc.perform(post("/api/session")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(sessionDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Session"))
				.andExpect(jsonPath("$.description").value("This is a test session"));
	}

	/**
	 * Test pour vérifier que l'API permet de mettre à jour une session avec des informations valides
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */

	@Test
	@DisplayName("Mise à jour réussie d'une session avec des informations valides. ")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testUpdateSession() throws Exception {
		//  Définition des identifiants et des objets SessionDto, Teacher, User, et Session
		String id = "1";
		SessionDto sessionDto = new SessionDto(1L, "Test Session", new Date(), 1L, "This is a test session.", Arrays.asList(1L, 2L), null, null);

		Teacher teacher = new Teacher();
		teacher.setId(1L);

		User user1 = new User();
		user1.setId(1L);

		User user2 = new User();
		user2.setId(2L);

		List<User> users = Arrays.asList(user1, user2);

		Session session = new Session();
		session.setId(1L);
		session.setName("Test Session");
		session.setDate(new Date());
		session.setTeacher(teacher);
		session.setDescription("This is a test session.");
		session.setUsers(users);

		// Simulation des comportements du mapper et du service
		when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
		when(sessionService.update(eq(Long.parseLong(id)), any(Session.class))).thenReturn(session);
		when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

		// Exécution de la requête PUT et vérification du statut 200 OK
		mockMvc.perform(put("/api/session/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(sessionDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Session"))
				.andExpect(jsonPath("$.description").value("This is a test session."));
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque l'identifiant de session pour la mise à jour est invalide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Mise à jour échoué d'une session avec des informations invalides. ")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testUpdateBadRequest() throws Exception {
		// Définition d'un ID de session invalide et création de l'objet SessionDto
		String invalidId = "abc";
		SessionDto sessionDto = new SessionDto(1L, "Test Session", new Date(), 1L, "This is a test session.", Arrays.asList(1L, 2L), null, null);

		// Exécution de la requête PUT et vérification du statut 400 Bad Request
		mockMvc.perform(put("/api/session/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(sessionDto)))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API permet de supprimer une session et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("supprimer une session")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testDeleteSession() throws Exception {
		// Définition de l'ID de la session à supprimer et création de l'objet Session
		String id = "1";
		Session session = new Session();
		session.setId(1L);
		session.setName("Test Session");

		// Simulation des comportements du service
		when(sessionService.getById(anyLong())).thenReturn(session);
		doNothing().when(sessionService).delete(anyLong());

		//  Exécution de la requête DELETE et vérification du statut 200 OK
		mockMvc.perform(delete("/api/session/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 404 Not Found
	 * lorsque la session à supprimer n'est pas trouvée.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("La session à supprimer n'est pas trouvé")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testDeleteSessionNotFound() throws Exception {

		// Définition de l'ID de la session à supprimer
		String id = "1";

		// Simulation du comportement du service pour retourner null lorsque la session n'est pas trouvée
		when(sessionService.getById(anyLong())).thenReturn(null);

		// Exécution de la requête DELETE et vérification du statut 404 Not Found
		mockMvc.perform(delete("/api/session/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque l'ID de la session à supprimer est invalide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("La session à supprimer avec un ID invalide")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testDeleteSessionBadRequest() throws Exception {
		//  Définition d'un ID de session invalide
		String invalidId = "abc";

		//  Exécution de la requête DELETE et vérification du statut 400 Bad Request
		mockMvc.perform(delete("/api/session/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API permet de participer à une session avec des identifiants valides
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" participer à une session avec des identifiants de session et d'utilisateur valides")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testParticipate() throws Exception {
		// Définition des identifiants de session et d'utilisateur valides
		String sessionId = "1";
		String userId = "2";

		// Simulation du comportement du service
		doNothing().when(sessionService).participate(anyLong(), anyLong());

		//Exécution de la requête POST et vérification du statut 200 OK
		mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque les identifiants de session et d'utilisateur pour participer à une session sont invalides.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Participer à une session avec des identifiants de session et d'utilisateur invalides")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testParticipateBadRequest() throws Exception {
		// Définition des identifiants de session et d'utilisateur invalides
		String invalidSessionId = "abc";
		String invalidUserId = "xyz";

		//  Exécution de la requête POST et vérification du statut 400 Bad Request
		mockMvc.perform(post("/api/session/{id}/participate/{userId}", invalidSessionId, invalidUserId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API permet à un utilisateur de cesser de participer à une session
	 * avec des identifiants valides et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Utilisateur cesse de participer à une session avec des identifiants de session et d'utilisateur valides")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testNoLongerParticipateSuccess() throws Exception {
		//Définition des identifiants de session et d'utilisateur valides
		String sessionId = "1";
		String userId = "2";

		// Simulation du comportement du service
		doNothing().when(sessionService).noLongerParticipate(anyLong(), anyLong());

		// Exécution de la requête DELETE et vérification du statut 200 OK
		mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque les identifiants de session et d'utilisateur pour cesser de participer à une session sont invalides.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Utilisateur cesse de participer à une session avec des identifiants de session et d'utilisateur invalides")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testNoLongerParticipateBadRequest() throws Exception {
		// Définition des identifiants de session et d'utilisateur invalides
		String invalidSessionId = "abc";
		String invalidUserId = "xyz";

		// Exécution de la requête DELETE et vérification du statut 400 Bad Request
		mockMvc.perform(delete("/api/session/{id}/participate/{userId}", invalidSessionId, invalidUserId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	//***************************************************************************************************************
	//test user******************************************************************************************************

	/**
	 * Test pour vérifier que l'API permet de récupérer les détails d'un utilisateur avec un identifiant valide
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Récupération des détails d'un utilisateur à partir de son identifiant.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdUser() throws Exception {
		// Définition de l'ID de l'utilisateur et création des objets User et UserDto

		String id = "1";
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setFirstName("John");
		user.setLastName("Doe");

		UserDto userDto = new UserDto();
		userDto.setId(1L);
		userDto.setEmail("test@example.com");
		userDto.setFirstName("John");
		userDto.setLastName("Doe");

		// Simulation des comportements du service et du mapper
		when(userService.findById(anyLong())).thenReturn(user);
		when(userMapper.toDto(user)).thenReturn(userDto);

		// Exécution de la requête GET et vérification du statut 200 OK
		mockMvc.perform(get("/api/user/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("test@example.com"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"));
	}
	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 404 Not Found
	 * lorsque l'utilisateur avec l'identifiant fourni n'est pas trouvé.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Récupération des détails d'un utilisateur à partir de son identifiant, mais l'utilisateur n'est pas trouvé.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdUserNotFound() throws Exception {
		// Définition de l'ID de l'utilisateur
		String id = "1";

		when(userService.findById(anyLong())).thenReturn(null);

		// Exécution de la requête GET et vérification du statut 404 Not Found
		mockMvc.perform(get("/api/user/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test d'intégration pour le contrôleur d'utilisateur.
	 * Ce test vérifie le comportement de l'API lorsqu'une requête de récupération des détails d'un utilisateur est faite
	 * avec un identifiant invalide.
	 */
	@Test
	@DisplayName(" Récupération des détails d'un utilisateur à partir d'un identifiant de session invalide.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByIdUserBadRequest() throws Exception {
		// Définition d'un ID de session invalide
		String invalidId = "abc";

		//  Exécution de la requête GET et vérification du statut 400 Bad Request
		mockMvc.perform(get("/api/user/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API permet de supprimer un utilisateur avec un identifiant valide
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" supprimer un utilisateur avec un identifiant valide.")
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	public void testDeleteUser() throws Exception {
		// Définition de l'ID de l'utilisateur et création de l'objet User
		String id = "1";
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setFirstName("John");
		user.setLastName("Doe");

		//Simulation des comportements du service
		when(userService.findById(anyLong())).thenReturn(user);
		doNothing().when(userService).delete(anyLong());

		// Configuration du contexte de sécurité
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password("password")
				.roles("USER")
				.build();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

		// Exécution de la requête DELETE et vérification du statut 200 OK
		mockMvc.perform(delete("/api/user/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 404 Not Found
	 * lorsque l'utilisateur avec l'identifiant fourni n'est pas trouvé.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Supprimer un utilisateur avec un identifiant valide, mais  l'utilisateur n'est pas trouvé dans la base de données.")
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	public void testDeleteUserNotFound() throws Exception {
		// Définition de l'ID de l'utilisateur
		String id = "1";

		// Simulation du comportement du service pour retourner null lorsque l'utilisateur n'est pas trouvé
		when(userService.findById(anyLong())).thenReturn(null);

		//  Exécution de la requête DELETE et vérification du statut 404 Not Found
		mockMvc.perform(delete("/api/user/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}


	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 401 Unauthorized
	 * lorsque l'utilisateur authentifié essaie de supprimer un autre utilisateur.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName(" Supprimer un utilisateur avec un identifiant valide, mais l'utilisateur authentifié n'est pas autorisé à effectuer cette action")
	@WithMockUser(username = "test@example.com", roles = {"USER"})
	public void testDeleteUserUnauthorized() throws Exception {
		// Given
		String id = "1";
		User user = new User();
		user.setId(1L);
		user.setEmail("other@example.com"); //  Email différent

		// Simulation du comportement du service
		when(userService.findById(anyLong())).thenReturn(user);

		// Configuration du contexte de sécurité
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("test@example.com") // Utilisateur authentifié avec un email différent
				.password("password")
				.roles("USER")
				.build();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

		// Exécution de la requête DELETE et vérification du statut 401 Unauthorized
		mockMvc.perform(delete("/api/user/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque l'identifiant de l'utilisateur fourni est invalide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Supprimer un utilisateur avec un identifiant invalide. ")
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void testDeleteUserBadRequest() throws Exception {
		//  Définition d'un ID invalide
		String invalidId = "abc";

		// Exécution de la requête DELETE et vérification du statut 400 Bad Request
		mockMvc.perform(delete("/api/user/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	//********************************************************************************
	//************************Teacher************************************************************


	/**
	 * Test pour vérifier que l'API permet de récupérer les détails d'un enseignant avec un identifiant valide
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Récupérer des détails d'un enseignant à partir de son identifiant.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindByITeacher() throws Exception {
		// Définition de l'ID de l'enseignant et création des objets Teacher et TeacherDto
		String id = "1";
		Teacher teacher = new Teacher();
		teacher.setId(1L);
		teacher.setFirstName("Jane");
		teacher.setLastName("Doe");

		TeacherDto teacherDto = new TeacherDto();
		teacherDto.setId(1L);
		teacherDto.setFirstName("Jane");
		teacherDto.setLastName("Doe");

		// Simulation des comportements du service et du mapper
		when(teacherService.findById(anyLong())).thenReturn(teacher);
		when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

		//Exécution de la requête GET et vérification du statut 200 OK
		mockMvc.perform(get("/api/teacher/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				//.andExpect(jsonPath("$.email").value("teacher@example.com"))
				.andExpect(jsonPath("$.firstName").value("Jane"))
				.andExpect(jsonPath("$.lastName").value("Doe"));
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 404 Not Found
	 * lorsque l'enseignant avec l'identifiant fourni n'est pas trouvé.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Récupérer des détails  d'un enseignant à partir de son identifiant, mais l'enseignant n'est pas trouvé dans la base de données.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testTeacherNotFound() throws Exception {
		//Définition de l'ID de l'enseignant
		String id = "1";

		// Simulation du comportement du service pour retourner null lorsque l'enseignant n'est pas trouvé
		when(teacherService.findById(anyLong())).thenReturn(null);

		//Exécution de la requête GET et vérification du statut 404 Not Found
		mockMvc.perform(get("/api/teacher/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 400 Bad Request
	 * lorsque l'identifiant de l'enseignant fourni est invalide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Récupérer des détails  d'un enseignant à partir d'un identifiant invalide.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testTeacherBadRequest() throws Exception {
		// Définition d'un ID invalide
		String invalidId = "abc";

		// Exécution de la requête GET et vérification du statut 400 Bad Request
		mockMvc.perform(get("/api/teacher/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test pour vérifier que l'API permet de récupérer tous les enseignants
	 * et retourne le statut HTTP 200 OK.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Récupérer tous les enseignants.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindAllTeacher() throws Exception {
		// Création des objets Teacher et TeacherDto
		Teacher teacher1 = new Teacher();
		teacher1.setId(1L);
		teacher1.setFirstName("John");
		teacher1.setLastName("Doe");

		Teacher teacher2 = new Teacher();
		teacher2.setId(2L);
		teacher2.setFirstName("Jane");
		teacher2.setLastName("Smith");

		List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

		TeacherDto teacherDto1 = new TeacherDto();
		teacherDto1.setId(1L);
		teacherDto1.setFirstName("John");
		teacherDto1.setLastName("Doe");

		TeacherDto teacherDto2 = new TeacherDto();
		teacherDto2.setId(2L);
		teacherDto2.setFirstName("Jane");
		teacherDto2.setLastName("Smith");

		List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

		// Simulation des comportements du service et du mapper
		when(teacherService.findAll()).thenReturn(teachers);
		when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

		// Exécution de la requête GET et vérification du statut 200 OK
		mockMvc.perform(get("/api/teacher")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				//.andExpect(jsonPath("$[0].email").value("teacher1@example.com"))
				.andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[1].id").value(2))
				//.andExpect(jsonPath("$[1].email").value("teacher2@example.com"))
				.andExpect(jsonPath("$[1].firstName").value("Jane"))
				.andExpect(jsonPath("$[1].lastName").value("Smith"));
	}

	/**
	 * Test pour vérifier que l'API retourne une réponse HTTP 200 OK
	 * lorsque la liste des enseignants est vide.
	 * Utilise un utilisateur simulé avec le rôle "USER".
	 *
	 * @throws Exception en cas d'erreur lors de la requête HTTP.
	 */
	@Test
	@DisplayName("Récupérer tous les enseignants mais la liste des enseignants est vide.")
	@WithMockUser(username = "user", roles = {"USER"})
	public void testFindAllEmpty() throws Exception {
		// Création de listes vides pour Teacher et TeacherDto
		List<Teacher> teachers = Collections.emptyList();
		List<TeacherDto> teacherDtos = Collections.emptyList();

		// Simulation des comportements du service et du mapper
		when(teacherService.findAll()).thenReturn(teachers);
		when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

		//Exécution de la requête GET et vérification du statut 200 OK
		mockMvc.perform(get("/api/teacher")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isEmpty());
	}
}
