package com.openclassrooms.starterjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.starterjwt.controllers.AuthController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;


import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;


import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;


import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.TestWebMvcConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

//import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"security.basic.enabled=false"})
//@AutoConfigureMockMvc
@AutoConfigureMockMvc(addFilters=false)
@Import({TestWebMvcConfig.class})
//@Import({ JacksonConfig.class})// Import the test configuration
public class SpringBootSecurityJwtApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Mock
	UserRepository userRepository;
	@Mock
	private SessionService sessionService;
	@Mock
	private  SessionRepository sessionRepository;
	@MockBean
	private JwtUtils jwtUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Mock
	private PasswordEncoder passwordEncoder;
	@MockBean
	private AuthenticationManager authenticationManager;
	@InjectMocks
	private AuthController authController;

	@InjectMocks
	private SessionController sessionController;

	@Mock
	private SessionMapper sessionMapper;

	private static final String TEST_USER_EMAIL = "test@example.com";
	private static final String TEST_USER_PASSWORD = "password";

	@Test
	public void contextLoads() {
		assertNotNull(mockMvc);
	}
	private static long sessionIdCounter = 1; // Initialize the counte


	@BeforeEach
	public void setUp() {
		sessionIdCounter = 1;

		// Initialisation des mocks
		MockitoAnnotations.initMocks(this);
		// Création d'un encodeur de mots de passe simulé
		passwordEncoder = new BCryptPasswordEncoder();
		// Creation d'un  mock user
		User user = new User();
		user.setEmail(TEST_USER_EMAIL);
		user.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
		user.setAdmin(false);

		Mockito.when(userRepository.findByEmail(TEST_USER_EMAIL))
				.thenReturn(Optional.of(user));
	}



	/*Test sur le register
	****************************************************************************************************************************************************
	****************************************************************************************************************************************************/
	@Test
	void registerUser() throws Exception {//Vérification que le processus d'inscription fonctionne correctement
		//Instance de SignupRequest avec des données fictives pour représenter une demande d'inscription.
		// Given
		SignupRequest signUpRequest = new SignupRequest();
		signUpRequest.setEmail("john@example.com");
		signUpRequest.setLastName("Doe");
		signUpRequest.setFirstName("John");
		signUpRequest.setPassword("password123");

		// Configuration du comportement du mock userRepository.
		// Lorsque la méthode existsByEmail est appelée avec l'email de la demande d'inscription (signUpRequest.getEmail()),
		// cette methode doit retourner false.
		// Cela simule le cas où l'email n'existe pas déjà dans la base de données,
		// ce qui signifie que l'inscription peut continuer.
		when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);

		//Création d'un constructeur de requête HTTP mock pour une requête de type POST vers l'endpoint "/api/auth/register".
		// Cela simule une demande d'inscription envoyée par un utilisateur à notre application.
		// When
		MockHttpServletRequestBuilder requestBuilder = post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(signUpRequest));

		//Execution dela requête mock créée précédemment à l'aide de MockHttpServletRequestBuilder en utilisant le MockMvc (un simulateur de servlet).
		// Then
		mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(content().json("{\"message\":\"User registered successfully!\"}"));
	}

	@Test
	void registerUserEmailAlreadyTaken() throws Exception {
		//Création d'une instance de SignupRequest
		// Given
		SignupRequest signUpRequest = new SignupRequest();
		signUpRequest.setEmail("john@example.com");
		signUpRequest.setLastName("Doe");
		signUpRequest.setFirstName("John");
		signUpRequest.setPassword("password123");


		//simulation du cas où l'e-mail est déjà pris dans la base de données.
		when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

		//Simulation de la demande d'inscription réelle envoyée par un client.
		// When
		MockHttpServletRequestBuilder requestBuilder = post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(signUpRequest));

		//Vérification que la réponse HTTP a un statut de mauvaise requête
		//le message d'erreur le message d'erreur
		// Then
		mockMvc.perform(requestBuilder)
				.andExpect(status().isBadRequest())
				.andExpect(content().json("{\"message\":\"Error: Email is already taken!\"}"));
	}
	/*
	****************************************************************************************************************************************************
	***************************************************************************************************************************************************/
	@Test
	@DisplayName("Test login avec des identifiants valides")
	public void testLoginWithValidCredentials() throws Exception {
		// instance de la classe LoginRequest qui represente les identifiants d'email et mot de passe
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(TEST_USER_EMAIL);
		loginRequest.setPassword(TEST_USER_PASSWORD);

		// requête POST vers l'endpoint /api/auth/login simulant une tentative de connexion
		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest))
						.with(csrf())) // Add CSRF token
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.isAdmin").doesNotExist());
	}

		@Test
		@DisplayName("Test login avec des identifiants incorrectes")
		public void testLoginWithInvalidCredentials() throws Exception {

			LoginRequest loginRequest = new LoginRequest();
			loginRequest.setEmail("invalid_email"); // un email incorrect
			loginRequest.setPassword("invalid_password"); // mot de passe invalide

			// requête POST vers l'endpoint de login avec les mauvaises informations d'identification
			mockMvc.perform(post("/api/auth/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(loginRequest))
							.with(csrf())) // Ajouter le jeton CSRF
					// Vérifier que le statut de la réponse est 401 Unauthorized
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.message").doesNotExist());
		}

	// Cette méthode est utilisée dans le test pour convertir l'objet SignupRequest en une chaîne JSON,
	// afin qu'il puisse être utilisé comme contenu de la requête HTTP simulée lors du test.
	// Cela permet de vérifier si le contrôleur traite correctement les demandes d'inscription avec des données JSON.
//	private String asJsonString(final Object obj)


	//**************************************************************************************************************************************
	//Test Usercontroller*******************************************************************************************************************

	/*@Test
	public void testGetUserDetailsById (){

	}*/
//	@Test
//	public void testCreateSession() throws Exception{
//
//		SessionDto sessionDto = new SessionDto();
//		sessionDto.setName("Test Session");
//		sessionDto.setDate(new Date());
//		sessionDto.setDescription("Test Description");
//		sessionDto.setUsers(new ArrayList<>());
//		sessionDto.setCreatedAt(LocalDateTime.now());
//		sessionDto.setUpdatedAt(LocalDateTime.now());
//
////		sessionDto.setCreatedAt(createdAtDTO);
////		sessionDto.setUpdatedAt(updatedAtDTO);
//
//
//		Session session = new Session();
//		session.setId(1L);
//		session.setName("Test Session");
//		session.setDate(new Date());
//		session.setDescription("Test Description");
//		session.setUsers(new ArrayList<>());
//
//		session.setCreatedAt(LocalDateTime.now());
//		session.setUpdatedAt(LocalDateTime.now());
//
////		session.setCreatedAt(createdAt);
////		session.setUpdatedAt(updatedAt);
//
//		when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
//		when(sessionService.create(any(Session.class))).thenReturn(session);
//		when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);
//
//		String sessionDtoJson = objectMapper.writeValueAsString(sessionDto);
//
//		mockMvc.perform(post("/api/session")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(sessionDtoJson))
//				.andExpect(status().isOk());
//
////		mockMvc.perform(post("/")
////						.contentType(MediaType.APPLICATION_JSON)
////						.content(new ObjectMapper().writeValueAsString(sessionDto)))
////				.andExpect(status().isOk());
//
//	}

//	@Test
//	public void testCreateSessionWithValidInput() throws Exception {
//		// Prepare data
//		SessionDto sessionDto = new SessionDto();
//		sessionDto.setName("Session 1");
//		sessionDto.setDate(new Date());
//		sessionDto.setTeacher_id(1L);
//		sessionDto.setDescription("Description for session 1");
//
//		// Perform the POST request
//		mockMvc.perform(post("/api/session")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(asJsonString(sessionDto)))
//				.andExpect(status().isOk());
//
//		// Verify that the sessionService.create() method is called with the expected session object
//		verify(sessionService, times(1)).create(any(Session.class));
//	}

	@Test
	public void testCreateSession() {
		SessionDto sessionDto = new SessionDto();
		sessionDto.setId(1L);
		sessionDto.setName("Test Session");
		sessionDto.setDate(new Date());
		sessionDto.setTeacher_id(1L); // ID du professeur fictif associé à la session
		sessionDto.setDescription("Description of the test session");
		sessionDto.setUsers(Arrays.asList(1L, 2L));
		sessionDto.setCreatedAt(LocalDateTime.now());
		sessionDto.setUpdatedAt(LocalDateTime.now());


		Session session = new Session();
		session.setId(1L);
		session.setName("Test Session");
		session.setDate(new Date());
		session.setDescription("Description of the test session");


		SessionService sessionServiceMock = mock(SessionService.class);
		SessionMapper sessionMapperMock = mock(SessionMapper.class);


		when(sessionMapperMock.toEntity(sessionDto)).thenReturn(session);


		when(sessionServiceMock.create(session)).thenReturn(session);

		SessionController sessionController = new SessionController(sessionServiceMock, sessionMapperMock);

		ResponseEntity<?> responseEntity = sessionController.create(sessionDto);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	}


	private String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
