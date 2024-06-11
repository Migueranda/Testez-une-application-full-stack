/// <reference types="cypress" />

describe('Spécification de Connexion', () => {

  // vérification que le formulaire de connexion affiche tous les champs requis
  it('Le formulaire de connexion doit afficher tous les champs requis', () => {
    cy.visit('/register')

    // Vérification de la présence des champs individuellement
    cy.get('input[formControlName="email"]').should('be.visible')
    cy.get('input[formControlName="password"]').should('be.visible')
  });

  // Test pour vérifier une connexion réussie
  it('Connexion réussie', () => {
    cy.visit('/login')

    // Intercepter la requête POST de connexion
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    // Interception de la requête GET pour les sessions (vide dans ce cas)
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

   // Les champs de connexion sont remplis et soumission du formulaire
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Vérification que l'URL a changé pour /sessions
    cy.url().should('include', '/sessions')
  });

  // Test pour vérifier une connexion échouée en raison d'un mot de passe incorrect
  it('Connexion échouée, mot de passe incorrect', () => {
    cy.visit('/login')

    // Interception de la requête POST de connexion avec un code d'état 401
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body:{
        error: 'An error occurred'
      },
    })

    // Interception la requête GET pour les sessions (vide dans ce cas)
    cy.intercept({
      method: 'GET',
      url: '/api/session',
    },
    []).as('session')
   
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"wrongpassword"}{enter}{enter}`)

    // Vérification que le message d'erreur est affiché
    cy.get('.error').should('contain', 'An error occurred')
    // Vérifier que l'URL contient toujours /login
    cy.url().should('include','/login')
  })

  // Test pour vérifier une connexion échouée en raison d'un email incorrect
  it('Connexion échouée, email incorrect', () => {
    cy.visit('/login')

    // Interception la requête POST de connexion avec un code d'état 401
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body:{
        error: 'An error occurred'
      },
    })

    // Interception la requête GET pour les sessions (vide dans ce cas)
    cy.intercept({
      method: 'GET',
      url: '/api/session',
    },
    []).as('session')

   
    cy.get('input[formControlName=email]').type(`${"yoga@studiocom"}{enter}{enter}`)
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
    // Vérifier que l'URL contient toujours /login
    cy.url().should('include','/login')
  })
});
