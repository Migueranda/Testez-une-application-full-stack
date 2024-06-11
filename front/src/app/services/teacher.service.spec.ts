import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  // Déclaration des variables pour le service et le contrôleur HTTP de test
  let service: TeacherService;
  let httpMock: HttpTestingController;

  // Définition de la base path pour les requêtes API
  const basePath = 'api/teacher';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    // Injection du service et du contrôleur HTTP de test
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

   // Vérification qu'il n'y a pas de requêtes HTTP en suspens après chaque test
  afterEach(() => {
    httpMock.verify();
  });

  // Test pour vérifier que le service est créé correctement
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

// Définition de la suite de tests pour la méthode 'all'
  describe('all', () => {
     // Test pour vérifier la récupération de tous les teachers
    it('should retrieve all teachers', () => {
        // Données simulées pour les teachers
      const mockTeachers: Teacher[] = [
        { id: 1, firstName: 'John', lastName: 'Doe', createdAt: new Date('2023-01-01T00:00:00Z'), updatedAt: new Date('2023-01-02T00:00:00Z') },
        { id: 2, firstName: 'Jane', lastName: 'Doe', createdAt: new Date('2023-01-03T00:00:00Z'), updatedAt: new Date('2023-01-04T00:00:00Z') }
      ];

      // Appel de la méthode 'all' du service et vérification des résultats
      service.all().subscribe(teachers => {
        expect(teachers).toEqual(mockTeachers);
      });

       // Vérification de la requête HTTP effectuée par le service
      const req = httpMock.expectOne(basePath);
      expect(req.request.method).toBe('GET');
      // Réponse simulée avec les données des teachers
      req.flush(mockTeachers);
    });
  });

  // Définition de la suite de tests pour la méthode 'detail'
  describe('detail', () => {
    // Test pour vérifier la récupération d'un enseignant par ID
    it('should retrieve a teacher by ID', () => {
      const mockTeacher: Teacher = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date('2023-01-01T00:00:00Z'),
        updatedAt: new Date('2023-01-02T00:00:00Z')
      };
      // Appel de la méthode 'detail' du service avec un ID et vérification du résultat
      service.detail('1').subscribe(teacher => {
        expect(teacher).toEqual(mockTeacher);
      });

      // Vérification de la requête HTTP effectuée par le service
      const req = httpMock.expectOne(`${basePath}/1`);
      expect(req.request.method).toBe('GET');
      // Réponse simulée avec les données du teacher
      req.flush(mockTeacher);
    });
  });
});
