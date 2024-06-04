import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const basePath = 'api/teacher';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all', () => {
    it('should retrieve all teachers', () => {
      const mockTeachers: Teacher[] = [
        { id: 1, firstName: 'John', lastName: 'Doe', createdAt: new Date('2023-01-01T00:00:00Z'), updatedAt: new Date('2023-01-02T00:00:00Z') },
        { id: 2, firstName: 'Jane', lastName: 'Doe', createdAt: new Date('2023-01-03T00:00:00Z'), updatedAt: new Date('2023-01-04T00:00:00Z') }
      ];

      service.all().subscribe(teachers => {
        expect(teachers).toEqual(mockTeachers);
      });

      const req = httpMock.expectOne(basePath);
      expect(req.request.method).toBe('GET');
      req.flush(mockTeachers);
    });
  });

  describe('detail', () => {
    it('should retrieve a teacher by ID', () => {
      const mockTeacher: Teacher = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date('2023-01-01T00:00:00Z'),
        updatedAt: new Date('2023-01-02T00:00:00Z')
      };

      service.detail('1').subscribe(teacher => {
        expect(teacher).toEqual(mockTeacher);
      });

      const req = httpMock.expectOne(`${basePath}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTeacher);
    });
  });
});
