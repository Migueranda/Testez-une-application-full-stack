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
import { UserService } from 'src/app/services/user.service';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of } from 'rxjs/internal/observable/of';
import { throwError } from 'rxjs/internal/observable/throwError';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  //let fb: jest.Mocked<FormBuilder>;
  let sessionService: jest.Mocked<SessionService>;
  let authService: jest.Mocked<AuthService>;
  let router: Router;

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

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should login and navigate to sessions on success', async() => {
    const loginResponse: SessionInformation = {
      token: 'mock-token',
      type: 'user',
      id: 1,
      username: 'mockUsername',
      firstName: 'Mock',
      lastName: 'User',
      admin: true
    }

    authService.login.mockReturnValue(of(loginResponse));
    component.form.setValue({ email: 'test@example.com', password: 'password' });
    component.submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password'
    } as LoginRequest);

    await fixture.whenStable();

    expect(sessionService.logIn).toHaveBeenCalledWith(loginResponse);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
 
    //expect(component).toBeTruthy();

  })

});
