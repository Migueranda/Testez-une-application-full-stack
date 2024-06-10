/// <reference types="cypress" />

describe('Me Page', () => {
    beforeEach(() => {
      // Visite la page de connexion
      cy.visit('/login');
  
      // Intercepte de la requête de connexion
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true
        },
      });  
      // Intercepte de la requête pour obtenir les sessions
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
              date: '2024-06-06',
              description: 'Description for session 1',
              imageUrl: 'https://example.com/image1.jpg'
            },
            {
              id: 2,
              name: 'Session 2',
              date: '2024-06-07',
              description: 'Description for session 2',
              imageUrl: 'https://example.com/image2.jpg'
            }
          ]
        }
      ).as('getSessions');
  
     // Les champs de connexion sont rempli et soumission du formulaire
      cy.get('input[formControlName=email]').type("yoga@studio.com");
      cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");
  
      // Vérification que l'URL a changé pour /sessions
      cy.url().should('include', '/sessions');
      cy.wait('@getSessions');

      cy.intercept('GET', '/api/user/1', {
        statusCode: 200,
        body: {
          id: 1,
          email: 'user@studio.com',
          lastName: 'User',
          firstName: 'Normal',
          admin: false,
          createdAt: '2024-04-08T00:00:00Z',
          updatedAt: '2024-04-08T00:00:00Z',
        },
      }).as('getUser');
    });
  
    it('should display Admin information correctly', () => {
      // Intercepte la requête pour obtenir les informations utilisateur
      cy.intercept('GET', '/api/user/1', {
        statusCode: 200,
        body: {
          id: 1,
          email: 'yoga@studio.com',
          lastName: 'ADMIN',
          firstName: 'Admin',
          admin: true,
          createdAt: '2024-04-08T00:00:00Z',
          updatedAt: '2024-04-08T00:00:00Z',
        },
      }).as('getUser');
  
      // Clique sur le bouton Account pour naviguer vers la page /me
      cy.get('[routerlink="me"]').should('exist').click();
  
      // Vérification que l'URL a changé pour /me
      cy.url().should('include', '/me');
      cy.wait('@getUser');
  
      // Vérification des informations affichées sur la page /me
      cy.get('#me-name').should('contain', 'Name: Admin ADMIN');
      cy.get('#me-email').should('contain', 'Email: yoga@studio.com');
      cy.get('p').contains('You are admin').should('exist');
      cy.get('p').contains('Create at:').should('exist');
      cy.get('p').contains('Last update:').should('exist');
    });

    it('should display user information correctly', () => {
        // Intercepte la requête pour obtenir les informations utilisateur
        cy.intercept('GET', '/api/user/1', {
          statusCode: 200,
          body: {
            id: 1,
            email: 'user@studio.com',
            lastName: 'User',
            firstName: 'Normal',
            admin: false,
            createdAt: '2024-04-08T00:00:00Z',
            updatedAt: '2024-04-08T00:00:00Z',
          },
        }).as('getUser');
    
        // Clique sur le bouton Account pour naviguer vers la page /me
        cy.get('[routerlink="me"]').should('exist').click();
    
        // Vérification que l'URL a changé pour /me
        cy.url().should('include', '/me');
        cy.wait('@getUser');
    
        // Vérification les informations affichées sur la page /me
        cy.get('#me-name').should('contain', 'Name: Normal USER');
        cy.get('#me-email').should('contain', 'Email: user@studio.com');
        cy.get('p').contains('You are admin').should('not.exist');
        cy.get('p').contains('Create at:').should('exist');
        cy.get('p').contains('Last update:').should('exist');
      });
  
  
  
    it('should navigate back when clicking back button', () => {
      
          // Clique sur le bouton Account pour naviguer vers la page /me
          cy.get('[routerlink="me"]').should('exist').click();
      
          // Vérification que l'URL a changé pour /me
          cy.url().should('include', '/me');
          cy.wait('@getUser');
            cy.get('.mat-card-title > div > .mat-focus-indicator').click
   
    });
  
    it('should delete the account if not admin', () => {
      // Simulater un utilisateur que n'est pas un admin
      cy.intercept('GET', '/api/user/1', {
        statusCode: 200,
        body: {
          id: 1,
          email: 'user@studio.com',
          lastName: 'User',
          firstName: 'Normal',
          admin: false,
          createdAt: '2024-04-08T00:00:00Z',
          updatedAt: '2024-04-08T00:00:00Z',
        },   
      }).as('getUser');

       // Intercepte la requête DELETE
    cy.intercept('DELETE', '/api/user/1', {
        statusCode: 200,
        body: {},
    }).as('deleteUser');


      cy.get('[routerlink="me"]').should('exist').click();
  
      cy.url().should('include', '/me');
  
      cy.wait('@getUser');

      cy.get('button').contains('delete').should('exist');
      cy.get('button').contains('delete').click();

      cy.wait('@deleteUser');
      
      cy.get('.mat-snack-bar-container').should('contain', 'Your account has been deleted !');
      cy.url().should('eq', 'http://localhost:4200/');
    });
  });
  