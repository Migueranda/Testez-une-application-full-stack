
/// <reference types="cypress" />
describe('Modifier Session E2E Test', () => {
    beforeEach(() => {

      // Interception des détails de la session pour le formulaire de mise à jour
      cy.intercept('GET', '/api/session/1', {
        body: {
          id: 1,
          name: 'Session 1',
          date: '2024-06-06',
          teacher_id: 1,
          description: 'Description for session 1',
        }
      }).as('getSession');

     // Interception de la liste des enseignants
      cy.intercept('GET', '/api/teacher', {
        body: [
          {
            id: 1,
            firstName: 'Margot',
            lastName: 'DELAHAYE'
          },
          {
            id: 2,
            firstName: 'John',
            lastName: 'DOE'
          }
        ]
      }).as('getTeachers');
    });

    it('Connexion réussie et affichage des sessions', () => {
      // Visite de la page de connexion
      cy.visit('/login');
      
      // Interception de la requête de connexion
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true
        },
      });
    
      // Interception de la requête API pour les sessions avec des données valides
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
    
      // En attente que la requête API soit complétée
      cy.wait('@getSessions');
    
      // Vérification que les sessions sont affichées
      cy.get('div#items').should('exist').within(() => {
        cy.get('mat-card.item').should('have.length.greaterThan', 0).each(($el) => {
          cy.wrap($el).within(() => {
            cy.get('mat-card-title').should('contain.text', 'Session');
            cy.get('mat-card-subtitle').should('contain.text', 'Session on');
            cy.get('.picture').should('be.visible');
            cy.get('mat-card-content p').should('contain.text', 'Description for session');
          });
        });
      });

      // Vérification que le bouton de détail existe 
      cy.get(':nth-child(1) > .mat-card-actions > .ng-star-inserted').should('exist');

      // Clique sur le bouton de détail
      cy.get(':nth-child(1) > .mat-card-actions > .ng-star-inserted').click();

      // Vérification que l'URL a changé pour /sessions/update/1
      cy.url().should('include', '/sessions/update/1');

      cy.wait('@getTeachers');
            
      // Vérification que le formulaire soit pré-rempli avec les détails de la session existante
      cy.get('input[formControlName=name]').should('have.value', 'Session 1');
      cy.get('input[formControlName=date]').should('have.value', '2024-06-06');
      cy.get('mat-select[formControlName=teacher_id]').should('contain.text', 'Margot DELAHAYE');
      cy.get('textarea[formControlName=description]').should('have.value', 'Description for session 1');

      //Le formulaire est rempli avec de nouvelles données
      cy.get('input[formControlName=name]').clear().type('Updated Session 1');
      cy.get('input[formControlName=date]').clear().type('2024-07-07');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').contains('John DOE').click();
      cy.get('textarea[formControlName=description]').clear().type('Updated description for session 1');
    
      // Interception de la requête de mise à jour de la session
      cy.intercept('PUT', '/api/session/1', {
        statusCode: 200,
        body: {
          id: 1,
          name: 'Updated Session 1',
          date: '2024-07-07',
          teacher_id: 2,
          description: 'Updated description for session 1',
        }
      }).as('updateSession');
    
      // Soumettre le formulaire
      cy.get('button[type=submit]').click();

      // Vérification que la session a été mise à jour avec succès
      cy.wait('@updateSession');
      cy.get('simple-snack-bar').should('contain.text', 'Session updated !');
    
      // Vérification que la redirection vers la liste des sessions
      cy.url().should('include', '/sessions');

    });
  
});
  