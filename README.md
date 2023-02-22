# Publisher Platform Backend

This project contains a simple backend for an article management platform. There are two entity types defined in the platform: `Users` and `Articles`.

The current implementation is very basic and lacking a lot of security features. The following sections explain the desired behavior of the backend:

## Access Controls
The system supports the following distinct user types:
1. `Administrators` - they manage the user accounts.
2. `Authors` - they can post and update their articles on the platform.
3. `Publishers` - they decide which articles get published on the platform.

### Access rules:
- `Administrators` can add/delete/list users and update any user information (except the user key).
  - When an `Administrator` creates a new user, the system should generate a 6 digit user key and gives it back to the `Administrator`.
- `Authors` can only get/update their own user information:
  - Note: `Authors` can only update their `username`, `firstname`, `lastname`.
- `Publishers` can list authors, but they only can see `username`, `firstname` and `lastname`.
- `Publishers` can get/update their own user information:
    - Note: `Publishers` can only update their `username`, `firstname`, `lastname`.

### Bonus points
- `Users` should be able to use their `username` and the initial `userkey` to request a new `userkey`, which gets stored in their user record.
  - Requesting new `userkey` should be handled by a new API endpoint just for that purpose.
- `Users` cannot perform any functions on the platform until their initial user key is changed.


## Workflow Permissions
- `Articles` can only be created by `Authors`
- A new `Article` should automatically be assigned the `Draft` state.
- Only the `Author` can list/get/update an `Article` in the `Draft` state.
- When the `Author` is happy with the article, they can change the `Article` status to `ReadyForReview`.
- `Publishers` can list/get `Articles` with status `ReadyForReview`.
- `Publishers` can update `Article` status to `Published`.
- Any user can list/get `Articles` in the `Published` state.
- Only the `Author` of an `Article` can change the status of their article back to `Draft`.

### Bonus points
- introduce a new role for  `Reviewer`
  - A `Reviewer` can list/get `Articles` in the `ReadyForReview` state.
  - Only the `Reviewer` can update the states from `ReadyForReview` to either `Approved` or `Rejected`.
  - A `Publisher` can only change the status from `Approved` to `Published`.
  - Only the `Author` of the `Article` can change the status from `Rejected` to `Draft`.


## Preventing Cross Site Scripting (XSS) attacks
Cross-Site Scripting (XSS) attacks are a type of injection, in which malicious scripts are injected into otherwise benign and trusted websites. XSS attacks occur when an attacker uses a web application to send malicious code, generally in the form of a browser side script, to a different end user. Flaws that allow these attacks to succeed are quite widespread and occur anywhere a web application uses input from a user within the output it generates without validating or encoding it.

Since articles `fulltext` can be stored as html, we need to validate that html to ensure that it only contains safe html tags. No scripts should be allowed. We need to clean the html before saving in the backend.


# Implementation details
- This project is built using the Quarkus framework, gradle, and Java 17.
- An in-memory database (H2) is being used. `db.sql` file under `/resources` is used to seed the database with some users and articles.
- Hibernate-ORM with Panache is used to define the Object Relational Mapper layer.

## Implementation hints
- Feel free to change/refactor any of the existing code.
- Since there are no proper tests developed, It is not guaranteed that the code is fully functioning.
- Every user is assigned a `UserKey`. It is stored in the database with `"KEY-"` prefix, followed by 6 digits.
- The APIs should use `username` along with the 6 digit `userkey` to verify users.
- For simplicity, you can use the url or query params to send `username`/`userkey`. But in reality, this is considered unsecure.

### Bonus points
- Sending `username` and `uaerkey` in the url or query parameters is unsecure. Find another way to send this information.
- Change the project to serve the APIs using https instead of http.
- Improve test coverage for the endpoints.