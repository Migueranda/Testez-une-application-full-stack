# Testez une application full-stack

This project was generated with Angular CLI version 14.1.0.

## Start the project

```bash
git clone git@github.com:Migueranda/Testez-une-application-full-stack.git
```

## Launch Front-end
**Install dependencies**

```bash
npm install
```
**Run application**

```bash
ng serve
```
**By default the admin account is :**

* login: yoga@studio.com
* password: test!1234

## Launch Back-End

**Run** : SpringBootSecurityJwtApplication 

## MySQL
[Install MySQL](https://dev.mysql.com/downloads/installer/)

SQL script for creating the schema is available&nbsp;&nbsp;&nbsp;*ressources/sql/script.sql*

## Optional

Use [DBeaver](https://dbeaver.io/download/) to manage script end read data
 
# Launch Cypress E2E test

```bash
 ng run yoga:e2e
```
**Run** all files

```bash
  npm run cypress:run-all
```
**Coverage report**&nbsp;&nbsp;&nbsp;*front\coverage\index.htm*

# Launch Jest test

```bash
 npm run test
```
**Coverage report**&nbsp;&nbsp;&nbsp;*front\coverage\lcov-report\index.html*

# Launch Java test

* Right-click on the folder : *com\openclassrooms\starterjwt*

* Click on More Run/Debug

* Click on Run 'Test in com.openclassrooms.starterjwt with coverage'

to execute the tests and obtain the coverage report

**Coverage report**&nbsp;&nbsp;&nbsp;*back/htmlReport/index.html*


