import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('logIn', () => {
    it('should set sessionInformation and isLogged, and call next', () => {
      const sessionInfo: SessionInformation = {
        token: 'mock-token',
        type: 'user',
        id: 1,
        username: 'mockUsername',
        firstName: 'Mock',
        lastName: 'User',
        admin: true
      };

      // Espionner la méthode next pour vérifier qu'elle est appelée
      const nextSpy = jest.spyOn(service as any, 'next' as any);


      // Appeler la méthode logIn
      service.logIn(sessionInfo);

      // Vérifier que sessionInformation est mis à jour
      expect(service.sessionInformation).toEqual(sessionInfo);

      // Vérifier que isLogged est défini à true
      expect(service.isLogged).toBe(true);

      // Vérifier que next est appelé
      expect(nextSpy).toHaveBeenCalled();
    });
  });
});
