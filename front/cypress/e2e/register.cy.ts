/// <reference types="cypress" />

describe('Register spec', () => {

    it('Form Register should display all required fields ', () => {
        cy.visit('/register')

      // Vérifier la présence des champs individuellement
      cy.get('input[formControlName="firstName"]').should('be.visible')
      cy.get('input[formControlName="lastName"]').should('be.visible')
      cy.get('input[formControlName="email"]').should('be.visible')
      cy.get('input[formControlName="password"]').should('be.visible')
  
      // Vérifier les placeholders si nécessaire
      cy.get('#mat-input-0').should('be.visible')
      cy.get('#mat-input-1').should('be.visible')
      cy.get('#mat-input-2').should('be.visible')
      cy.get('#mat-input-3').should('be.visible')

    })


    it('Register successfull', () => {

        cy.visit('/register')

        cy.intercept('POST', '/api/auth/register', {
            body: {
              email: 'email',
              firstName: 'firstName',
              lastName: 'lastName',
              password: 'password'
            },
          }).as('register')


    cy.get('input[formControlName=firstName]').type("Admin")
    cy.get('input[formControlName=lastName]').type(`${"Admin"}`)
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}`)

    cy.get('form').submit()

    cy.wait('@register').its('response.statusCode').should('eq', 200)
    cy.url().should('include', '/login')    

    })

    it('Register unsuccessful user exist', () => {

        cy.visit('/register')

        cy.intercept('POST', '/api/auth/register', {
            statusCode: 409,
            body: {
                error: 'An error occurred'
            },
          }).as('register')


    cy.get('input[formControlName=firstName]').type("Admin")
    cy.get('input[formControlName=lastName]').type(`${"Admin"}`)
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}`)

    cy.get('form').submit()

    cy.wait('@register').its('response.statusCode').should('eq', 409)
    cy.get('.error').should('contain', 'An error occurred')
    cy.url().should('include', '/register')    

    })
})