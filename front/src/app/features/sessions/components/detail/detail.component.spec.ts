import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Session } from '../../interfaces/session.interface';
import { of } from 'rxjs';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';


describe('DetailComponent', () => {
  // Déclaration des variables de test
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: jest.Mocked<SessionApiService>;
  let sessionService: jest.Mocked<SessionService>;
  let teacherService: jest.Mocked<TeacherService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let router: jest.Mocked<Router>;

  // Mock pour les informations de session
  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }


  beforeEach(async () => {
     // Mocks des services avec conversion inconnue pour les types non chevauchants
     const sessionApiServiceMock = {
       all: jest.fn(),
       detail: jest.fn(),
       delete: jest.fn(),
       create: jest.fn(),
       update: jest.fn(),
       participate: jest.fn(),
       unParticipate: jest.fn()
     } as unknown as jest.Mocked<SessionApiService>;

     const sessionServiceMock = {
      sessionInformation: { id: 1, admin: true }
    } as jest.Mocked<SessionService>;

    const teacherServiceMock = {
      detail: jest.fn()
    } as unknown as jest.Mocked<TeacherService>;

    const matSnackBarMock = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatSnackBar>;

    const routerMock = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;
         
    // Configuration du TestBed pour fournir les dépendances et les modules nécessaires
    await TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [HttpClientTestingModule, ReactiveFormsModule,  MatCardModule, MatIconModule ],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: (key: string) => '1'
              }
            }
          }
        }
      ]
    }).compileComponents();

    // Création de la fixture et initialisation du composant
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService) as jest.Mocked<SessionApiService>;
    sessionService = TestBed.inject(SessionService) as jest.Mocked<SessionService>;
    teacherService = TestBed.inject(TeacherService) as jest.Mocked<TeacherService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  // Test pour vérifier que le composant est créé correctement
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Test pour vérifier le chargement des détails de la session à l'initialisation
  it('should fetch session details on init', () => {
    const mockSession: Session = {
      id: 1,
      name: 'Test Session',
      description: 'Test Description',
      date: new Date(),
      teacher_id: 1,
      users: [1, 2],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const mockTeacher: Teacher = {id: 1, firstName: 'John', lastName: 'Doe', createdAt: new Date('2023-01-01T00:00:00Z'), updatedAt: new Date('2023-01-02T00:00:00Z') };
  
    // Mock des méthodes pour retourner les valeurs simulées
    sessionApiService.detail.mockReturnValue(of(mockSession));
    teacherService.detail.mockReturnValue(of(mockTeacher));

    fixture.detectChanges(); // ngOnInit() is called here

    // Vérifications des appels et des valeurs
    expect(sessionApiService.detail).toHaveBeenCalledTimes(1);
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(component.isParticipate).toBe(true);
    expect(teacherService.detail).toHaveBeenCalledTimes(1);
    expect(component.teacher).toEqual(mockTeacher);
  });

  // Test pour vérifier la suppression de la session et la navigation
  it('should delete the session and navigate away', () => {
    sessionApiService.delete.mockReturnValue(of(null));

    component.delete();

      // Vérifications des appels et des valeurs
    expect(sessionApiService.delete).toHaveBeenCalledTimes(1);
    expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledTimes(1);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(router.navigate).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['sessions']);
  });

  // Test pour vérifier la participation à une session
  it('should participate in a session', () => {
    sessionApiService.participate.mockReturnValue(of(void 0));

    component.participate();
    // Vérifications des appels
    expect(sessionApiService.participate).toHaveBeenCalledTimes(1);
    expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
  });

  // Test pour vérifier l'annulation de la participation à une session
  it('should unParticipate from a session', () => {
    sessionApiService.unParticipate.mockReturnValue(of(void 0));

    component.unParticipate();
  // Vérifications des appels
    expect(sessionApiService.unParticipate).toHaveBeenCalledTimes(1);
    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
  });
});

