import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { MeComponent } from './me.component';
import { UserService } from 'src/app/services/user.service';
import { SessionService } from 'src/app/services/session.service';
import { Location } from '@angular/common';

import{routes} from 'src/app/app-routing.module'
import { User } from 'src/app/interfaces/user.interface';
import { NgZone } from '@angular/core';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let userService: jest.Mocked<UserService>;
  let sessionService: jest.Mocked<SessionService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let router: Router;
  let location: Location;
  let ngZone: NgZone;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  beforeEach(async () => {
    const userServiceMock = {
      getById: jest.fn(),
      delete: jest.fn().mockReturnValue(of({}))
    };

    const sessionServiceMock = {
      logOut: jest.fn(),
      sessionInformation: { id: 1 },
    };

    const matSnackBarMock = {
      open: jest.fn(),
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes(routes) // Utilisation des routes importées
      ],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Location, useValue: { path: jest.fn().mockReturnValue('/'), replaceState: jest.fn() } }

      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    ngZone = TestBed.inject(NgZone);

    sessionService.sessionInformation = {
      token: 'mock-token',
      type: 'user',
      id: 1,
      username: 'mockUsername',
      firstName: 'Mock',
      lastName: 'User',
      admin: true
    };

    // Mock la méthode getById pour éviter l'erreur de undefined
    jest.spyOn(userService, 'getById').mockReturnValue(of({
      id: 1,
      firstName: 'Mock',
      lastName: 'User',
      email: 'mock@example.com',
      admin: true,
      password: '',
      createdAt: new Date(),
      updatedAt: new Date()
    }));

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user on init', () => {
    const mockDate = new Date('2023-05-29T00:00:00Z');
    const user: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      admin: true,
      password: 'password',
      createdAt: mockDate,
      updatedAt: mockDate,
    };
    userService.getById.mockReturnValue(of(user));
    component.ngOnInit();
    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(user);
  });

  it('should call window.history.back on back()', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historyBackSpy).toHaveBeenCalled();
    historyBackSpy.mockRestore();
  });


  it('should delete user account and navigate to home', async () => {
    // Espionner la méthode 'delete' du 'userService' pour vérifier qu'elle est appelée
    // et retourner un Observable vide
    const deleteSpy = jest.spyOn(userService, 'delete').mockReturnValue(of({}));
  
    // Espionner la méthode 'logOut' du 'sessionService' pour vérifier qu'elle est appelée
    const logOutSpy = jest.spyOn(sessionService, 'logOut');
  
    // Espionner la méthode 'open' du 'matSnackBar' pour vérifier qu'elle est appelée
    const snackBarSpy = jest.spyOn(matSnackBar, 'open');
  
    // Espionner la méthode 'navigate' du 'router' pour vérifier qu'elle est appelée
    const navigateSpy = jest.spyOn(router, 'navigate');
  
    // Exécuter la méthode 'delete' du composant à l'intérieur de la zone Angular
    ngZone.run(() => {
      component.delete();
    });
  
    // Vérifier que 'userService.delete' a été appelée avec l'ID '1'
    expect(deleteSpy).toHaveBeenCalledWith('1');
  
    // Vérifier que 'matSnackBar.open' a été appelée avec le message et la configuration corrects
    expect(snackBarSpy).toHaveBeenCalledWith(
      "Your account has been deleted !",
      'Close',
      { duration: 3000 }
    );
  
    // Vérifier que 'sessionService.logOut' a été appelée
    expect(logOutSpy).toHaveBeenCalled();
  
    // Vérifier que 'router.navigate' a été appelée avec ['/']
    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  
    // Attendre que tous les appels asynchrones soient terminés
    await fixture.whenStable();
  
    // Vérifier que l'URL actuelle est '/'
    expect(router.url).toBe('/');
  });
  
});


