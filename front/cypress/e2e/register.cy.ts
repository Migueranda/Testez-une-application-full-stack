// /// <reference types="cypress" />

// describe('Register spec', () => {

//   beforeEach(() => {
//       cy.visit('/register');
//   });


//   it('should display the registration form', () => {
//     cy.get('form').should('exist');
//     cy.get('input[formControlName="firstName"]').should('exist');
//     cy.get('input[formControlName="lastName"]').should('exist');
//     cy.get('input[formControlName="email"]').should('exist');
//     cy.get('input[formControlName="password"]').should('exist');
//   });

  

//   it('should fill out the form and enable submit button', () => {
      
//       cy.get('input[formControlName="firstName"]').type('John');
//       cy.get('input[formControlName="lastName"]').type('Doe');
//       cy.get('input[formControlName="email"]').type('john.doe@example.com');
//       cy.get('input[formControlName="password"]').type('password123');

//       // En attente que le bouton de soumission soit activé
//       cy.get('button[mat-raised-button][type="submit"]').should('not.be.disabled');
//   });


//   it('Register successful', () => {
//       // On intercepte la requête API et retourner une réponse simulée
//       cy.intercept('POST', '/api/auth/register', {
//           statusCode: 200,
//           body: {}
//       }).as('registerRequest');
      
//       cy.get('input[formControlName="firstName"]').type('John');
//       cy.get('input[formControlName="lastName"]').type('Doe');
//       cy.get('input[formControlName="email"]').type('john.doe@example.com');
//       cy.get('input[formControlName="password"]').type('password123');
  
//       //cy.get('.register-form > .mat-focus-indicator').click
//       cy.get('button[mat-raised-button][type="submit"]').click();

//       // en attente que la requête API soit faite
//       cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);;

//       // Vérification du résultat attendu après la soumission
//       cy.url().should('include', '/login');
//   });

//   it('Register unsuccessful user exists', () => {
//       cy.intercept('POST', '/api/auth/register', {
//           statusCode: 409,
//           body: {
//               error: 'User already exists'
//           }
//       }).as('registerRequest');

     
//       cy.get('input[formControlName="firstName"]').type('Admin');
//       cy.get('input[formControlName="lastName"]').type('Admin');
//       cy.get('input[formControlName="email"]').type('admin@example.com');
//       cy.get('input[formControlName="password"]').type('password123');

//       cy.get('button[mat-raised-button][type="submit"]').click();

      
//       cy.wait('@registerRequest');

//       // Vérification du message d'erreur et l'URL
//       cy.get('.error').should('contain', 'An error occurred');
//       cy.url().should('include', '/register');
//   });


// });


/// <reference types="cypress" />

describe('Spécification d\'inscription', () => {

    beforeEach(() => {
      cy.visit('/register'); // Visiter la page d'inscription avant chaque test
    });
  
   // Les champs de connexion sont rempli et soumission du formulaire
    it('devrait afficher le formulaire d\'inscription', () => {
      cy.get('form').should('exist');
      cy.get('input[formControlName="firstName"]').should('exist');
      cy.get('input[formControlName="lastName"]').should('exist');
      cy.get('input[formControlName="email"]').should('exist');
      cy.get('input[formControlName="password"]').should('exist');
    });
  
    //  Le formulaire est rempli et vérifie que le bouton de soumission est activé
    it('devrait remplir le formulaire et activer le bouton de soumission', () => {
      cy.get('input[formControlName="firstName"]').type('John');
      cy.get('input[formControlName="lastName"]').type('Doe');
      cy.get('input[formControlName="email"]').type('john.doe@example.com');
      cy.get('input[formControlName="password"]').type('password123');
      cy.get('button[mat-raised-button][type="submit"]').should('not.be.disabled');
    });
  
    // Inscription réussie
    it('Inscription réussie', () => {
      cy.intercept('POST', '/api/auth/register', {
        statusCode: 200,
        body: {}
      }).as('registerRequest');
  
      cy.get('input[formControlName="firstName"]').type('John');
      cy.get('input[formControlName="lastName"]').type('Doe');
      cy.get('input[formControlName="email"]').type('john.doe@example.com');
      cy.get('input[formControlName="password"]').type('password123');
      cy.get('button[mat-raised-button][type="submit"]').click();
  
      cy.wait('@registerRequest').its('response.statusCode').should('eq', 200);
      cy.url().should('include', '/login'); // Vérifie la redirection vers la page de connexion
    });
  
    // Inscription échouée car l'utilisateur existe déjà
    it('Inscription échouée, l\'utilisateur existe déjà', () => {
      cy.intercept('POST', '/api/auth/register', {
        statusCode: 409,
        body: {
          error: 'User already exists'
        }
      }).as('registerRequest');
  
      cy.get('input[formControlName="firstName"]').type('Admin');
      cy.get('input[formControlName="lastName"]').type('Admin');
      cy.get('input[formControlName="email"]').type('admin@example.com');
      cy.get('input[formControlName="password"]').type('password123');
      cy.get('button[mat-raised-button][type="submit"]').click();
  
      cy.wait('@registerRequest');
      cy.get('.error').should('contain', 'An error occurred');
      cy.url().should('include', '/register'); // Vérifie que l'URL reste /register
    });
  });
  