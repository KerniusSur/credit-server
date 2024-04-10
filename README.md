# Credit Server API

This is a simple loan management system, where users can apply for loans, extend them, and view their loan history.

A few things to note:

1. The system contains an authentication system, which means you'll have to either register or log in with the [default
   user](#default-user-credentials).
2. In order to test the endpoints you will need to add `<AUTH_TOKEN>` to requests, which you can get by logging in or
   registering a new user. The token is stored in the cookie and is valid for 2 days.
3. You might need to adjust the request body when testing the endpoints, as the given examples are just examples and
   might not work in your case.
4. Loan extensions are not limited per loan, meaning the user can extend the loan as many times as they want.
5. The system contains a few [constants](#constants) that can be modified in the `application.yml` file.
6. For testing purposes, the system does not require a valid personal code when registering a new user. This can be
   changed in the `application.yml` file.
7. The `build.sh` and `publish.sh` scripts are used to build and publish the app to docker hub. You can use them to
   publish the app to your own docker hub repository.

# Table of Contents

- [Install & run the app](#install--run-the-app)
- [REST API](#rest-api)
    - [Base URL](#base-url)
    - [API Endpoints](#api-endpoints)
    - [Default user credentials](#default-user-credentials)
    - [Documentation & Testing](#api-documentation--testing)
    - [Authentication API](#authentication-api)
        - [Register a new user](#register-a-new-user)
        - [Log in with email](#log-in-with-email)
        - [Log in with personal code](#log-in-with-personal-code)
        - [Log out](#log-out)
    - [Loan API](#loan-api)
        - [Find all user loans for client](#find-all-user-loans-for-client)
        - [Find all approved user loans for client](#find-all-approved-user-loans-for-client)
        - [Apply for a loan](#apply-for-a-loan)
    - [Loan Extension API](#loan-extension-api)
        - [Extend a loan](#extend-a-loan)
        - [Find all user loan extensions](#find-all-user-loan-extensions)
- [Constants](#constants)

<br/>

# Install & run the app

## 1. Clone the repository:

    git clone git@github.com:KerniusSur/credit-server.git

## 2. Build the app:

Build the app with maven:

    mvn clean install -DskipTests

or, if you don't have maven installed:

    ./mvnw clean install -DskipTests

## 3. Run the app:

**There are two ways to run the application:**

### 1. Run the app with `docker compose`:

If you have docker installed, you can run the application with the following command:

    docker compose up -d

If you'd like to run only the database in a container, you can run the following command:

    docker compose -f docker-compose-local.yml up -d

If you'd like to run the application using an image from docker hub, you can run the following command:

    docker compose -f docker-compose-hub.yml up -d

To stop the app, run the one of the following command:, depending on which `docker-compose` file you used:

    docker compose down
    docker compose -f docker-compose-local.yml down
    docker compose -f docker-compose-hub.yml down

### 2. Run the app with `start.sh` script:

If you're not using docker, you have to run a postgresql server locally and create a database with the following
parameters:

    host: localhost
    username: postgres
    password: postgres
    database: finance-db
    port: 5432

After creating the database, you can run the app with the `start.sh` script:

    ./start.sh

<br/>

### Note:

***Keep in mind that the system, contains an authentication system, which means you'll have to either register or log in
with the [default user](#default-user-credentials).***

<br/>

# REST API

- ## Base URL:
  ### http://localhost:8080/

- ## API Endpoints:
    - ### /api/v1/auth/**
        - /api/v1/auth/register
        - /api/v1/auth/login
        - /api/v1/auth/login/personalId
        - /api/v1/auth/logout
    - ### /api/v1/loan/**
        - /api/v1/loan
        - /api/v1/loan/client
        - /api/v1/loan/client/all
    - ### /api/v1/loan-extension/**
        - /api/v1/loan-extension
        - /api/v1/loan-extension/client

- ### Default user credentials:
    - **Email:** `john.doe@gmail.com`
    - **Personal code:** `50101011234`
    - **Password:** `password1234`

- ## Documentation & Testing:
    - ### [Swagger UI Documentation](http://localhost:8080/swagger-ui/index.html#/)
    - ### [Postman Collection](postman_collection.json)
        - [Import](https://learning.postman.com/docs/getting-started/importing-and-exporting/importing-data/) the
          `postman_collection.json` file to Postman and use it to test the endpoints.

<br/>

## Authentication API

### _Register a new user_

### Request

`POST /api/v1/auth/register`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d '{"name": "Testas","lastName": "Testauskas","email": "testas.testauskas@gmail.com","phoneNumber": "+37061234567","personalId": "40101011234","password": "password123"}'  \
    http://localhost:8080/api/v1/auth/register

### Response

    HTTP/1.1 200
    Set-Cookie: Authorization=<AUTH_TOKEN>
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Length: 0
    Date: Fri, 22 Mar 2024 12:09:27 GMT

### _Log in with email_

### Request

`POST /api/v1/auth/login`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d '{"email": "john.doe@gmail.com","password": "password1234"}' \
    http://localhost:8080/api/v1/auth/login

### Response

    HTTP/1.1 200
    Set-Cookie: Authorization=<AUTH_TOKEN>; Max-Age=172800; Expires=Fri, 29 Mar 2024 17:32:25 GMT; Path=/; HttpOnly
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Length: 0
    Date: Fri, 22 Mar 2024 13:27:36 GMT

### _Log in with personal code_

### Request

`POST /api/v1/auth/login/personalId`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -d '{"personalId": "50101011234","password": "password1234"}' \
    http://localhost:8080/api/v1/auth/login/personalId

### Response

    HTTP/1.1 200
    Set-Cookie: Authorization=<AUTH_TOKEN>
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Length: 0
    Date: Fri, 22 Mar 2024 13:29:24 GMT

### _Log out_

#### Request

`POST /api/v1/auth/logout`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    http://localhost:8080/api/v1/auth/logout

#### Response

    HTTP/1.1 200
    Set-Cookie: Authorization=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Path=/; HttpOnly
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Length: 0
    Date: Fri, 22 Mar 2024 12:30:33 GMT

<br/>

## Loan API

### _Find all user loans for client_

### Request

`GET /api/v1/loan/client/all`

    curl -i -X GET \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -H "Cookie: Authorization=<AUTH_TOKEN>" \
    http://localhost:8080/api/v1/loan/client/all

### Response

    HTTP/1.1 200
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Fri, 22 Mar 2024 07:44:04 GMT
    
    []

<br/>

### _Find all approved user loans for client_

### Request

`GET /api/v1/loan/client`

    curl -i \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -H "Cookie: Authorization=<auth_token>" \
    http://localhost:8080/api/v1/loan/client

### Response

    HTTP/1.1 200
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Fri, 22 Mar 2024 07:44:04 GMT
    
    []

<br/>

### _Apply for a loan_

### Request

`POST /api/v1/loan`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -H "Cookie: Authorization=<AUTH_TOKEN>" \
    -d '{"amount": 1000,"loanTermInMonths": 12,"applicationDate": "2024-03-22T12:14:56.228Z"}' \
    http://localhost:8080/api/v1/loan

### Response

    HTTP/1.1 200
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Fri, 22 Mar 2024 07:47:39 GMT
    
    {
        "id":4,
        "amount":1000,
        "interestRate":0.114,
        "interestAmount":114.000,
        "loanTermInDays":360,
        "status":"APPROVED",
        "dueDate":"2025-03-22,
        "createdAt":"2024-03-22T09:47:39.481501",
        "updatedAt":null,
        "ipAddress":"127.0.0.1",
        "loanExtensions":[] 
    }

<br/>

## Loan Extension API

### _Extend a loan_

### Request

`POST /api/v1/loan-extension`

    curl -i -X POST \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -H "Cookie: Authorization=<AUTH_TOKEN>" \
    -d '{"loanId": 4}' \
    http://localhost:8080/api/v1/loan-extension

### Response

    HTTP/1.1 200
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Fri, 22 Mar 2024 13:06:05 GMT

    {
        "id":2,
        "loanId":4,
        "additionalLoanTermInDays":7,
        "createdAt":"2024-03-22T10:06:05.240349"
    }

### _Find all user loan extensions_

### Request

`GET /api/v1/loan-extension/client`

    curl -i -X GET \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    -H "Cookie: Authorization=<AUTH_TOKEN>" \
    http://localhost:8080/api/v1/loan-extension/client

### Response

    HTTP/1.1 200
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 0
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Fri, 22 Mar 2024 13:07:05 GMT

    []

<br/>

# Constants

### These constants are used in the app and can be modified in the `application.yml` file.

### 1. `minLoanAmount: 100`

Minimum loan amount. The user cannot apply for a loan with an amount less than this value

### 2. `maxLoanAmount: 15000`

Maximum loan amount. The user cannot apply for a loan with an amount greater than this value

### 3. `minLoanTermInDays: 30`

Minimum loan term acceptable in days. (30 days = 1 month). The user cannot apply for a loan with a term less than this
value.

### 4. `maxLoanTermInDays: 1800`

Maximum loan term acceptable in days. (1800 days = 60 months = 5 years). The user cannot apply for a loan with a term
greater than this value.

### 5. `loanExtensionMultiplier: 1.5`

The multiplier applied to the loan interest amount if the user extends the loan. The interest amount and the interest
rate are multiplied by this value.

### 6. `loanInterestRateShortTerm: 0.129`

The interest rate applied to the loan amount for short-term loans.
Short-term loans are loans with a term of less than 360 days (12 months).

### 7. `loanInterestRateMediumTerm: 0.114`

The interest rate applied to the loan amount for medium-term loans.
Medium-term loans are loans with a term form 361 days (12 months) to 720 days (24 months).

### 8. `loanInterestRateLongTerm: 0.099`

The interest rate applied to the loan amount for long-term loans.
Long-term loans are loans with a term from 721 days (24 months) to 1800 days (60 months).

### 9. `defaultLoanExtensionTermInDays: 7`

The default amount of days added to the loan due date when extending the loan. As listed in the task description,
the user can extend the loan for 7 days.

### 10. `isValidPersonalCodeRequired: false`

If set to true, the system will require a valid personal code when registering a new user. For easier testing, it is set
to false by default.






