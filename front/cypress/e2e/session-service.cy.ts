/// <reference types="cypress" />

describe('Test E2E pour SessionService', () => {
  beforeEach(() => {
   
    cy.visit('/login'); 
  });

  // Test pour vérifier une connexion réussie et la redirection vers la page d'accueil
  it('devrait se connecter avec succès et rediriger vers la page d\'accueil', () => {
  
    cy.visit('/login')

    // Intercepte la requête POST de connexion et simuler une réponse
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    }).as('login')

    // Intercepte la requête GET pour les sessions (vide dans ce cas)
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    // Les champs de connexion sont remplis et soumission du formulaire
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}")

    // En attente de la réponse de la requête de connexion et vérifier le statut
    cy.wait('@login').its('response.statusCode').should('eq', 200);
    // Vérification que l'URL a changé pour /sessions après la connexion réussie
    cy.url().should('include', '/sessions')
  });

  // Test pour vérifier une déconnexion réussie
  it('devrait se déconnecter avec succès', () => {
    // Connexion de l'utilisateur d'abord
    cy.visit('/login')

    // Intercepte la requête POST de connexion et simuler une réponse
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    }).as('login')

    // Intercepte la requête GET pour les sessions (vide dans ce cas)
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    // Les champs de connexion sont remplis et soumission du formulaire
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type("test!1234{enter}{enter}")

    // En attente de la réponse de la requête de connexion et vérifier le statut
    cy.wait('@login').its('response.statusCode').should('eq', 200);
    // Vérification que l'URL a changé pour /sessions après la connexion réussie
    cy.url().should('include', '/sessions')

    // Se déconnecter
    cy.get('.mat-toolbar > .ng-star-inserted > :nth-child(3)').click();
  });
});
