/// <reference types="cypress" />
describe('Detail Page', () => {
    beforeEach(() => {
      // Visite la page de connexion
      cy.visit('/login');
  
      // Intercepte la requête de connexion
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true
        },
      });
  
      // Intercepte la requête pour obtenir les sessions
      cy.intercept(
        {
          method: 'GET',
          url: '/api/session',
        },
        {
          body: [
            {
              id: 1,
              name: 'Session 1',
              date: '2024-05-01T00:00:00Z',
              description: 'Description for session 1',
              teacher_id: 1,
              users: [],
              createdAt: '2024-05-01T00:00:00Z',
              updatedAt: '2024-05-01T00:00:00Z',
            },
            {
              id: 2,
              name: 'Session 2',
              date: '2024-06-01T00:00:00Z',
              description: 'Description for session 2',
              teacher_id: 1,
              users: [],
              createdAt: '2024-06-01T00:00:00Z',
              updatedAt: '2024-06-01T00:00:00Z',
            }
          ]
        }
      ).as('getSessions');
  
      // Intercepte la requête pour obtenir les détails de l'enseignant
      cy.intercept('GET', '/api/teacher/1', {
        statusCode: 200,
        body: {
          id: 1,
          lastName: 'DELAHAYE',
          firstName: 'Margot',
          createdAt: '2024-01-01T00:00:00Z',
          updatedAt: '2024-01-01T00:00:00Z'
        },
      }).as('getTeacher');
  
       // Les champs de connexion sont remplis et soumission du formulaire
      cy.get('input[formControlName=email]').type("yoga@studio.com");
      cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");
  
      // Vérification que l'URL a changé pour /sessions
      cy.url().should('include', '/sessions');
      cy.wait('@getSessions');
    });
  
    it('should display session details and delete session', () => {
      // Intercepte la requête pour obtenir les détails de la session
      cy.intercept('GET', '/api/session/1', {
        statusCode: 200,
        body: {
          id: 1,
          name: 'Session 1',
          date: '2024-05-01T00:00:00Z',
          description: 'Description for session 1',
          teacher_id: 1,
          users: [],
          createdAt: '2024-05-01T00:00:00Z',
          updatedAt: '2024-05-01T00:00:00Z',
        },
      }).as('getSession');
  
      // Intercepte la requête pour supprimer la session
      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200,
        body: {},
      }).as('deleteSession');
  
      // Clique sur le bouton de détail pour naviguer vers la page de détail de la session
      cy.get('button').contains('Detail').click();
  
      // Vérification que l'URL a changé pour /sessions/detail/1
      cy.url().should('include', '/sessions/detail/1');
      cy.wait('@getSession');
      cy.wait('@getTeacher');
  
      // Vérification des informations affichées sur la page de détail
      cy.get('h1').should('contain', 'Session 1');
      cy.get('.description').should('contain', 'Description for session 1');
      cy.get('span').should('contain', 'Margot DELAHAYE');
      cy.get('span').should('contain', 'May 1, 2024');
  
      // Vérification que le bouton de suppression existe et clique dessus
      cy.get('button').contains('Delete').should('exist').click();
  
      // En attente que la requête DELETE soit complétée
      cy.wait('@deleteSession');
  
      // Vérification que le message de suppression est affiché
      cy.get('.mat-snack-bar-container').should('contain', 'Session deleted !');
  
      // Vérification que l'utilisateur est redirigé vers la page des sessions
      cy.url().should('include', '/sessions');
    });
  });
  