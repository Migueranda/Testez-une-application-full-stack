/// <reference types="cypress" />

describe('Login spec', () => {
  
  it('Login successfull', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
  })


  it('Login unsuccessfull password incorrect', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,

      body:{
        error: 'An error occurred'
      },
    })
    cy.intercept({
      method: 'GET',
      url: '/api/session',

    },
    []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"wrongpassword"}{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
    cy.url().should('include','/login')

  })

  it('Login unsuccessfull incorrect email', () => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,

      body:{
        error: 'An error occurred'
      },
    })
    cy.intercept({
      method: 'GET',
      url: '/api/session',

    },
    []).as('session')

    cy.get('input[formControlName=email]').type(`${"yoga@studiocom"}{enter}{enter}`)
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.get('.error').should('contain', 'An error occurred')
    cy.url().should('include','/login')

  })

});