/// <reference types="cypress" />
describe('Test E2E pour ListComponent', () => {
  
  // Test pour vérifier une connexion réussie et l'affichage des sessions
  it('Connexion réussie et affichage des sessions', () => {
   
    cy.visit('/login');

    // Intercepte la requête POST de connexion et simuler une réponse
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    });

    // Intercepte la requête GET pour les sessions et retourner des données valides
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

   // Les champs de connexion sont remplis et soumission du formulaire
    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}");

    // Vérification que l'URL a changé pour /sessions après la connexion réussie
    cy.url().should('include', '/sessions');

    // En attente que la requête API pour les sessions soit complétée
    cy.wait('@getSessions');

    // Vérification que la div avec les items existe et contient des éléments
    cy.get('div#items').should('exist').within(() => {
      // Vérification que chaque carte de session (mat-card) est affichée avec les bonnes informations
      cy.get('mat-card.item').should('have.length.greaterThan', 0).each(($el) => {
        cy.wrap($el).within(() => {
          cy.get('mat-card-title').should('contain.text', 'Session');
          cy.get('mat-card-subtitle').should('contain.text', 'Session on');
          cy.get('.picture').should('be.visible');
          cy.get('mat-card-content p').should('contain.text', 'Description for session');
        });
      });
    });
  });
});
