import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of } from 'rxjs/internal/observable/of';

describe('LoginComponent', () => {
  // Déclaration des variables de test
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let sessionService: jest.Mocked<SessionService>;
  let authService: jest.Mocked<AuthService>;
  let router: Router;

  // Configuration avant chaque test
  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    const sessionServiceMock = {
      logIn: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn() // Mock de la méthode navigate du Router
    };

    // Configuration du TestBed pour fournir les dépendances et les modules nécessaires
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientModule,
        MatSnackBarModule,
        RouterTestingModule.withRoutes([]),
        MatCardModule, // Import du module MatCardModule
        MatFormFieldModule, // Import du module MatFormFieldModule
        MatIconModule, // Import du module MatIconModule
        MatInputModule, // Import du module MatInputModule
        BrowserAnimationsModule  // Import de BrowserAnimationsModule
      ],
      providers: [
        FormBuilder,
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
         { provide: Router, useValue: routerMock } // Utilisation du mock
      ]
    }).compileComponents();

    // Création de la fixture et initialisation du composant
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

 // Test pour vérifier que le composant est créé correctement
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test pour vérifier le comportement de la méthode de soumission du formulaire
  it('should login and navigate to sessions on success', async() => {
    // Réponse simulée du service d'authentification
    const loginResponse: SessionInformation = {
      token: 'mock-token',
      type: 'user',
      id: 1,
      username: 'mockUsername',
      firstName: 'Mock',
      lastName: 'User',
      admin: true
    }

    // Mock de la méthode login pour retourner la réponse simulée
    authService.login.mockReturnValue(of(loginResponse));
    component.form.setValue({ email: 'test@example.com', password: 'password' });
    component.submit();

     // Vérification que la méthode login a été appelée avec les bons arguments
    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password'
    } as LoginRequest);

    // Attente que toutes les opérations asynchrones soient terminées
    await fixture.whenStable();

     // Vérification que la méthode logIn du sessionService a été appelée avec la réponse simulée
    expect(sessionService.logIn).toHaveBeenCalledWith(loginResponse);
    // Vérification que la méthode navigate du router a été appelée avec le bon argument
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
     // Vérification que la propriété onError du composant est à false
    expect(component.onError).toBe(false);

  })

});
