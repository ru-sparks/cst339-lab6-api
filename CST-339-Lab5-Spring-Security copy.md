# CST-339 Lab 5: Spring Security

## Authentication, Authorization, Sessions, and Password Encoding

### Overview

In Labs 1–3, you built, persisted, tested, containerized, and deployed a professional Spring Boot REST API. Lab 4 added application users with Thymeleaf forms for registration, manual login, password change, and administration. Lab 4 was intentionally insecure: passwords were stored and compared as plain text, roles were data rather than enforced authorities, and routes were not protected by a security filter chain.

Lab 5 replaces those temporary mechanisms with Spring Security. You will authenticate against the existing PostgreSQL `users` table, establish authenticated sessions, enforce role-based authorization, hash passwords with BCrypt, and protect both Thymeleaf pages and mutating REST endpoints.

**Important design rule:** Do not introduce an in-memory user store. Lab 4 already provides a working database of application users. Every authentication step in this lab uses `UserRepository` and PostgreSQL.

Complete each branch in order. Each branch is one coherent security feature. Merge the pull request before beginning the next branch. Do not jump ahead—especially do not introduce BCrypt before Branch 9, or login will fail against existing Lab 4 plain-text passwords.

### Purpose

This lab introduces Spring Security as the application’s authentication and authorization framework. The Chinook REST API remains the primary application surface. Thymeleaf continues to serve the small administrative interface created in Lab 4. Spring Security sits in front of both, enforcing who may access which routes.

### Lab Outcomes

- Add Spring Security without immediately breaking Lab 4 behavior.
- Implement a database-backed `UserDetailsService` using the existing `users` table.
- Keep the Lab 4 Thymeleaf login page and wire it into Spring Security form login (do not use Spring’s default login page).
- Protect routes so anonymous users cannot reach authenticated pages.
- Enforce `ADMIN` access to user-administration pages.
- Configure logout and auth-aware navigation.
- Enforce the forced password-change workflow after authentication.
- Update passwords for the authenticated user only.
- Encode passwords with BCrypt and update seed data accordingly.
- Prevent self-registration as `ADMIN`.
- Protect the REST API so `USER` can only GET, `ADMIN` can GET/POST/PUT/DELETE, and Swagger requires an authenticated user.
- Keep datasource credentials out of Git by using environment variables or an uncommitted local profile.



### Prerequisites

- Complete Labs 1–4.
- Begin with the Lab 4 application: Thymeleaf registration, manual login, change-password, user administration, and a PostgreSQL `users` table.
- Confirm the application starts and existing REST endpoints work before adding Spring Security.
- Confirm seeded and registered users exist in PostgreSQL and can complete the Lab 4 login workflow.
- Use the existing feature-branch and pull-request workflow.
- Add your instructor as a GitHub repository collaborator before Branch 1 so the instructor can grade by pull-request history.



### Architectural Scope

After finishing this lab, the application still supports two presentation styles, now protected by one security configuration:


| Application Area                     | Presentation Style              | Security Responsibility                      |
| ------------------------------------ | ------------------------------- | -------------------------------------------- |
| Album and reporting features         | REST controllers returning JSON | Role-based HTTP method rules on `/api/**`    |
| User registration and administration | Spring MVC + Thymeleaf          | Form login, sessions, roles, password change |
| API docs                             | Swagger UI / OpenAPI            | Require an authenticated user                |


Spring Security does not replace your service layer. Controllers still call services. Services still use repositories. Security decides whether a request is allowed to reach those controllers.

### Role Authorization Policy

Lab 4 stores roles as `USER` and `ADMIN`. Lab 5 grants those values as authorities with `hasAuthority(...)` (no `ROLE_` prefix). The finished application uses this matrix:


| Authority | `/api/**` GET | `/api/**` POST, PUT, DELETE | Swagger UI / OpenAPI | Thymeleaf `/admin/**` |
| --------- | ------------- | --------------------------- | -------------------- | --------------------- |
| Anonymous | Denied        | Denied                      | Denied               | Denied                |
| `USER`    | Allowed       | Denied                      | Allowed              | Denied                |
| `ADMIN`   | Allowed       | Allowed                     | Allowed              | Allowed               |


Notes for students and graders:

- A `USER` may open Swagger and try requests, but write operations (`POST`, `PUT`, `DELETE`) must fail with 403.
- An `ADMIN` may use Swagger for both read and write operations.
- Self-registered accounts remain `USER` and therefore cannot mutate API data or open user administration.
- This full API/Swagger matrix is completed in Branch 12. Earlier branches must not jump ahead to it.



### Authentication versus Authorization

Students must keep these terms distinct:

- **Authentication** answers: Who are you? Form login, `UserDetailsService`, password encoding, and sessions belong here.
- **Authorization** answers: What may you do? `permitAll`, `authenticated`, and `hasAuthority("ADMIN")` belong here.

Lab 4 performed credential lookup inside a controller. That is not authentication in the Spring Security sense. Lab 5 moves authentication into the security filter chain so every request can be checked consistently.

### Password Encoding Timing

Until Branch 9, continue using plain-text passwords with `NoOpPasswordEncoder` (or an equivalent temporary approach documented by the instructor). Existing Lab 4 rows store plain text. Introducing BCrypt too early will make login fail for every existing user and will obscure whether the failure is in form login, `UserDetailsService`, or encoding.

Branch 9 switches to `BCryptPasswordEncoder`, encodes new passwords on write, and migrates existing rows in pgAdmin’s table editor using the instructor-provided BCrypt hash of `password`. Branch 10 updates the seed SQL script to the same hash.

### GitHub Workflow

Complete each branch in order. Each branch should contain one coherent feature and should be merged before the next branch begins.

1. Create the named feature branch.
2. Complete and test the required work for that branch only.
3. Commit with a meaningful message that explains why the change exists.
4. Push the branch to GitHub.
5. Open a pull request titled `Lab 5 PR XX: <short title>`.
6. In the pull request body, list **Done**, **Out of scope**, and **Test plan**.
7. Merge the pull request into the working branch used for the lab.
8. Pull the updated main/working branch before creating the next feature branch.

**Branch naming example:** `feature/lab5-pr04-protect-pages`

**Grading note:** The instructor grades the ordered pull-request history, not only the final state of `main`. A pull request that sneaks later features into an earlier branch does not meet the lab requirements.

---



## Branch 1: Add Spring Security with an Open Filter Chain

Add Spring Security to the classpath and configure a filter chain that permits every request. The goal is to prove that the dependency can be introduced without changing Lab 4 behavior.

**Important note — default Spring Security behavior:** As soon as `spring-boot-starter-security` is on the classpath, Spring Security steps in automatically. If you start the application *before* an open `SecurityFilterChain` is in place—or if that chain is missing or incorrect—Spring Security presents its own default login UI and expects a default user with a **generated password** printed in the console at startup. That is not the Lab 4 Thymeleaf login page, and it is not the end state of this branch. The open `permitAll` filter chain exists specifically so Lab 4 routes and the existing Thymeleaf login remain usable. After Branch 1 is correctly completed, you should again use your Lab 4 login page—not Spring’s default login screen or the generated password.

### Learning Expectations

- What `spring-boot-starter-security` adds to a Spring Boot application.
- Why an unconfigured or default security setup can unexpectedly lock the application and show Spring’s default login UI with a generated password.
- Why the first security change should be deliberate and minimal.



### Required Work

- Add the `spring-boot-starter-security` dependency.
- Create a `SecurityConfig` class with a `SecurityFilterChain` bean.
- Permit all requests (`anyRequest().permitAll()` or equivalent explicit public matchers covering the whole application).
- Do not configure form login yet.
- Do not create a `UserDetailsService` yet.
- Do not add BCrypt yet.
- Leave the Lab 4 login controller and manual credential check in place.



### Checkpoint

- The application starts successfully.
- Spring’s default login page and generated console password are **not** required for normal use.
- Lab 4 registration, manual login, change-password, and administration still work through the Thymeleaf pages.
- Existing JSON endpoints continue to work.
- Students can explain that Spring Security is present but not yet enforcing authentication.



### What to expect now

Run the application and **examine** the behavior. Be ready to **explain** what you observe—not only that it “works.”

- Confirm Lab 4 login, register, admin, and a sample API call still behave as before.
- Optionally remove or comment out the open filter chain briefly (locally, do not commit) and restart: notice Spring’s default login and the generated password in the console, then restore `permitAll` and explain the difference.
- In your PR notes, explain why Spring Security is on the classpath yet the app does not force a secured login experience yet.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 2: Create a Database-Backed UserDetailsService

Teach Spring Security how to load application users from PostgreSQL. Do not replace the Lab 4 login form in this branch.

**Important note — the bean is registered, not called by the browser login yet:** After this branch, Spring Security has a `UserDetailsService` bean in the application context (you may see startup logs mentioning that the `AuthenticationManager` is configured with it). That does **not** mean `loadUserByUsername(...)` runs when a student uses the Lab 4 Thymeleaf login page. Requests are still `permitAll`, and `LoginController` still compares passwords itself. Prove the mapping with unit tests in this branch. Spring Security will call `UserDetailsService` at runtime starting in **Branch 3**, when `formLogin` owns the `/login` POST.

### Learning Expectations

- The role of `UserDetails` and `UserDetailsService`.
- How application roles become granted authorities.
- Why `enabled` must be mapped into Spring Security’s account status.
- Why this lab uses the existing database rather than an in-memory user store.
- Why registering a `UserDetailsService` bean is not the same as Spring Security authenticating browser logins yet.



### Required Work

- Implement a `UserDetailsService` bean that loads users through `UserRepository.findByUsername(...)`.
- Keep database role values as Lab 4 defined them: `USER` and `ADMIN`.
- When building `UserDetails`, grant the authority using the stored role value exactly (for example, stored `ADMIN` becomes authority `ADMIN`). Do **not** prepend `ROLE_`. Later authorization will use `hasAuthority("ADMIN")`, which matches the Lab 4 values directly.
- Map the entity `enabled` flag so disabled accounts cannot authenticate later.
- Add focused unit tests for authority mapping (`USER` / `ADMIN`) and disabled-user mapping.
- Do not remove the Lab 4 login controller yet.
- Do not configure form login yet.
- Do not introduce BCrypt yet.



### Checkpoint

- Unit tests show the `UserDetailsService` can load a PostgreSQL-backed user by username (or map a repository result correctly).
- A missing username results in `UsernameNotFoundException`.
- Role mapping produces authorities `USER` or `ADMIN` with no `ROLE_` prefix.
- Disabled users are represented as disabled `UserDetails`.
- The Lab 4 browser login workflow still uses the old controller and does **not** require `UserDetailsService` to run for a successful page login.



### What to expect now

Run the application and **examine** the behavior. Be ready to **explain** what you observe.

- Sign in with the Lab 4 Thymeleaf form and confirm it still uses the hand-rolled controller.
- Check startup logs for evidence that a `UserDetailsService` was registered with the `AuthenticationManager`.
- Explain why that log line does **not** mean `loadUserByUsername` ran during your browser login.
- Point to your unit tests as the place this branch proves role and enabled mapping.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 3: Wire the Thymeleaf Login Page into Spring Security

Keep the Lab 4 Thymeleaf login page and Bootstrap styling. Do **not** use Spring Security’s default generated login page. Change what happens when the form is submitted: Spring Security owns authentication, while your MVC controller continues to serve the custom HTML page.

### Learning Expectations

- Difference between a login **page** (Thymeleaf view) and login **processing** (Spring Security filter).
- How `formLogin().loginPage("/login")` points Spring Security at your existing page.
- How the Thymeleaf form posts `username` and `password` into the Spring Security flow.
- Why the Lab 4 controller’s manual password comparison must be removed, while the GET that renders `login.html` remains.
- Why password encoding remains temporary in this branch.



### Required Work

- Configure `formLogin` with `.loginPage("/login")` so Spring Security uses your Thymeleaf page, not the framework default.
- Keep the existing `login.html` template and overall page design. Adjust only what is required for Spring Security integration (typically `name="username"` and `name="password"`, and posting to the configured login-processing URL).
- Keep a GET `/login` controller mapping (or equivalent) that returns the Thymeleaf `login` view.
- Remove the Lab 4 hand-rolled POST login logic that compared passwords in application code. Spring Security’s filter now performs authentication using the Branch 2 `UserDetailsService`.
- Display login errors through the existing page (for example `/login?error` handled in the template), not through Spring’s default error page.
- Configure a temporary `PasswordEncoder` such as `NoOpPasswordEncoder` so existing plain-text Lab 4 passwords continue to work.
- Do not add `hasAuthority("ADMIN")` yet; leave authorization tightening for Branch 4 and Branch 5.
- Do not introduce BCrypt yet.



### Checkpoint

- The browser still shows the course Thymeleaf/Bootstrap login page—not Spring’s default login screen.
- A valid PostgreSQL user can sign in through that page.
- Invalid credentials return the user to the same Thymeleaf login page with a general error.
- The controller no longer compares passwords manually.
- Students can explain that the view is still theirs, while authentication is now Spring Security’s responsibility.



### What to expect now

Run the application and **examine** the login flow. Be ready to **explain** what changed.

- Successful login with a known database user; failed login with a wrong password (`/login?error` on your Thymeleaf page).
- Confirm you still see *your* Bootstrap login page—not Spring’s default form.
- Explain that POST `/login` is now handled by Spring Security, which calls `UserDetailsService` and the temporary `PasswordEncoder`.
- Explain why routes such as `/admin/users` may still be reachable without a meaningful security session policy until later branches tighten authorization.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 4: Protect Authenticated Pages

Configure which routes are public and which require an authenticated session.

### Learning Expectations

- Request matchers and authorization rules.
- The difference between “the page exists” and “the page is reachable anonymously.”
- Why public assets and login/register endpoints must remain reachable.



### Required Work

- Permit public routes needed by the application, such as `/`, `/login`, `/register`, error pages, and static assets.
- Leave final API and Swagger authorization for Branch 12. In this branch it is acceptable for `/api/**` and Swagger/OpenAPI paths to remain reachable so earlier security lessons stay focused on page authentication.
- Require authentication for all other requests (`anyRequest().authenticated()`).
- Keep `/admin/**` reachable by any authenticated user in this branch; Branch 5 tightens it to `ADMIN` only.
- Verify anonymous users are redirected to login when requesting a protected page.



### Checkpoint

- Anonymous users can open public pages.
- Anonymous users cannot open protected pages without signing in.
- Authenticated users can open protected non-admin pages.
- Students can explain which matchers are public and why.



### What to expect now

Run the application and **examine** anonymous versus authenticated access. Be ready to **explain** your matcher choices.

- As an anonymous user, open `/`, `/login`, and `/register` successfully.
- As an anonymous user, attempt a protected page (for example `/change-password` or `/admin/users`) and observe the redirect to login.
- Sign in, then reopen that protected page and describe what changed.
- In your PR notes, list which paths are public and explain why each must stay public.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 5: Enforce Administrator Authorization

Protect user administration so only users with the `ADMIN` role can access it.

### Learning Expectations

- Authentication versus authorization.
- How `hasAuthority("ADMIN")` matches the `ADMIN` value stored in Lab 4 and granted on `UserDetails`.
- Why this lab uses `hasAuthority` rather than `hasRole` (so no `ROLE_` prefix is required).
- Why hiding a navbar link is not authorization.



### Required Work

- Restrict `/admin/**` with `hasAuthority("ADMIN")`.
- Confirm authority mapping from Branch 2 grants `ADMIN` exactly as stored in PostgreSQL.
- Keep ordinary authenticated `USER` accounts able to use non-admin authenticated features.
- Do not introduce BCrypt yet.
- Do not yet focus on API authorization; that is Branch 12.



### Checkpoint

- An `ADMIN` user can open `/admin/users` and perform administration actions.
- A `USER` account receives 403 Forbidden (or the course-standard access-denied behavior) when requesting `/admin/users`.
- Students can explain that Lab 4’s open admin URL is now closed by the filter chain.



### What to expect now

Run the application and **examine** role-based access. Be ready to **explain** authentication versus authorization.

- Sign in as `USER` and request `/admin/users`; observe denial.
- Sign in as `ADMIN` and complete the same request successfully.
- Explain why being authenticated is not enough for administration, and why `hasAuthority("ADMIN")` matches the database role value prefix.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 6: Configure Logout and Auth-Aware Navigation

Complete the session lifecycle in the browser UI.

### Learning Expectations

- Logout as a security concern, not only a link.
- How Thymeleaf can adapt navigation based on authentication state.
- Why the Security dialect is required for `sec:authorize` expressions.



### Required Work

- Configure logout (logout URL and logout success URL).
- Update the shared navigation bar so authenticated users see Logout and anonymous users see Login and Register.
- Add the Thymeleaf Spring Security dialect dependency/configuration if `sec:authorize` is used.
- Optionally show User Administration only to administrators (for example `sec:authorize="hasAuthority('ADMIN')"`).
- Do not introduce BCrypt yet.



### Checkpoint

- Logout ends the authenticated session.
- After logout, protected pages again require sign-in.
- The navbar reflects authenticated versus anonymous state.
- Direct visits to admin URLs remain blocked for non-admin users.



### What to expect now

Run the application and **examine** the session lifecycle in the UI. Be ready to **explain** what logout actually does.

- Sign in and confirm the navbar shows Logout (and hides Login/Register as designed).
- Log out, then retry a protected URL and explain why access is denied again.
- If User Administration is shown only to admins, verify that with both roles and explain that hiding a link is not the same as authorization.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 7: Forced Password Change After Login

Use an authentication success handler to honor `passwordChangeRequired` from the database.

### Learning Expectations

- What happens after successful authentication.
- Why password-change enforcement belongs in the security flow, not only in a cooperative redirect.
- How Lab 4’s temporary redirect becomes a real post-authentication rule.



### Required Work

- Implement an `AuthenticationSuccessHandler` (or equivalent success handling) that:
  - Sends users with `passwordChangeRequired=true` to `/change-password`.
  - Sends other users to a sensible default such as `/`.
- Keep the change-password page and service available for the next branch’s Principal-based update if not already complete.
- Do not introduce BCrypt yet.



### Checkpoint

- The seeded or flagged administrator with `passwordChangeRequired=true` lands on the change-password page after login.
- A user with `passwordChangeRequired=false` lands on the normal success destination.
- Students can explain that this rule runs only after successful authentication.



### What to expect now

Run the application and **examine** post-login redirects. Be ready to **explain** where the decision is made.

- Log in as a user with `passwordChangeRequired=true` and confirm you land on `/change-password`.
- Log in as a user with the flag cleared and confirm the normal success destination.
- Explain why this belongs in the authentication success path rather than only in a cooperative controller redirect.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 8: Secure Change Password for the Authenticated User

Wire password changes to the authenticated principal so the signed-in user’s row is updated and `passwordChangeRequired` is cleared.

### Learning Expectations

- How to identify the current user through `Principal` or the SecurityContext.
- Why a password-change operation must target the authenticated account once Spring Security owns the session.
- Why `/change-password` must require authentication.



### Required Work

- Require authentication for `/change-password` (already true if Branch 4’s `anyRequest().authenticated()` covers it; confirm it stays that way).
- Update `UserService` so password changes are performed by username for the authenticated user (for example `changePassword(String username, String newPassword)`).
- Update the change-password controller to pass `Principal.getName()` (or equivalent) into the service.
- Clear `passwordChangeRequired` after a successful change.
- Redirect to login with a success message after password change, or follow the course-standard post-change flow.
- Do not introduce BCrypt yet; store the new password in the temporary plain-text form so Branches 3–8 remain testable against existing data conventions.



### Checkpoint

- Changing a password updates only the authenticated user’s row in PostgreSQL.
- `passwordChangeRequired` becomes false for that user.
- Another user’s password is not modified.
- Anonymous access to change-password is blocked.



### What to expect now

Run the application and **examine** whose password changes. Be ready to **explain** how the authenticated principal is used.

- Sign in as user A, change the password, and verify only A’s row changed in PostgreSQL.
- Confirm `passwordChangeRequired` is cleared for A and that anonymous access to `/change-password` is blocked.
- Explain how `Principal` (or the SecurityContext) identifies which account to update after Branch 7’s forced redirect.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 9: Introduce BCrypt Password Encoding

Replace temporary plain-text password handling with BCrypt. Switch the application encoder, encode passwords on write, and migrate existing Lab 4 plain-text rows in PostgreSQL using a known hash provided by the instructor.

### Learning Expectations

- Why passwords must be stored as irreversible hashes.
- How `PasswordEncoder` participates in authentication and password updates.
- Why encoding on write and matching on authentication must use the same encoder.
- How to migrate existing plain-text database rows so they match the new encoder.



### Known demo hash

Use this BCrypt hash. It encodes the plain password 'password':

```text
$2a$10$WR5.cGFXDVcgneDGVNIls.S93MMoy4/SFJccInWPqYDFiixspzKHq
```

Do not invent a different hash for the seeded/admin migration in this branch. Using the shared value keeps everyone’s database and login checks aligned.

### Required Work

- Replace `NoOpPasswordEncoder` with `BCryptPasswordEncoder`.
- Encode passwords when registering new users.
- Encode passwords when changing passwords.
- Migrate existing plain-text users in PostgreSQL using **pgAdmin’s table editor** (not a migration script):
  1. In pgAdmin, open the `users` table and view/edit the data (Browse/Edit Data, or equivalent).
  2. For the `admin` row (and any other plain-text accounts you need for demo), replace the plain-text `password` value with:

```text
$2a$10$WR5.cGFXDVcgneDGVNIls.S93MMoy4/SFJccInWPqYDFiixspzKHq
```

1. Set `password_change_required` as needed for your test path (for example `TRUE` to force a change on next login).
2. Save the row changes in pgAdmin, then re-open the table and confirm the password column shows the BCrypt value (not plain text).

- Update service tests to expect encoded passwords where appropriate.



### Checkpoint

- `SecurityConfig` uses `BCryptPasswordEncoder` (no NoOp).
- A newly registered user’s PostgreSQL password value is a BCrypt hash, not plain text.
- That new user can log in successfully.
- Password change stores a BCrypt hash.
- Existing admin (or migrated users) can sign in with password `password` after the pgAdmin table edit.
- Students can explain why BCrypt hashes should never be displayed in administration pages.



### What to expect now

Run the application and **examine** stored passwords. Be ready to **explain** encoding versus authentication.

- After editing the `users` table in pgAdmin, confirm the admin row stores the provided BCrypt hash and the password-change flag is set as you intended.
- Sign in as `admin` with password `password` and explain why that works only after both the encoder bean and the database value use BCrypt.
- Register a new user and inspect the `password` column—expect a BCrypt hash (it will not match the instructor hash exactly; each encode produces a different salt).
- Explain why plain-text Lab 4 rows would fail login once NoOp is removed if you skip the pgAdmin table edit.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 10: Update Seed Data and Complete Encoding Migration

Keep the source-tree SQL seed script aligned with BCrypt so a fresh database matches the encoder from Branch 9.

### Learning Expectations

- How seed data must match the application’s password encoder.
- Why SQL scripts belong with the source tree.
- Why plain-text credentials must not remain in committed seed data after encoding is required.



### Required Work

- Update `users.sql` (or the course-standard seed script) so the administrator password is the known BCrypt hash of `password`:

```text
$2a$10$WR5.cGFXDVcgneDGVNIls.S93MMoy4/SFJccInWPqYDFiixspzKHq
```

- Set `password_change_required` in the seed as appropriate for your demo (typically `TRUE` for the temporary admin).
- Ensure a database rebuilt from the script can log in with username `admin` and password `password`.
- Confirm no remaining temporary NoOp usage in the application.
- Document local demo credentials (`admin` / `password`) in the README without committing unrelated secrets.
- Verify disabled-user and role behavior still work with hashed passwords.



### Checkpoint

- Seed script contains the provided BCrypt hash, not plain-text `password`.
- Seeded admin login succeeds with password `password`.
- Registration and password change continue to store hashes.
- README documents local demo credentials without exposing unrelated secrets.



### What to expect now

Run the application and **examine** seed data against the encoder. Be ready to **explain** consistency between SQL and code.

- Confirm the seed script stores the instructor-provided BCrypt hash and that `admin` / `password` signs in successfully on a fresh or re-seeded database.
- Re-check registration/password-change still write hashes.
- Explain why the seed value must be a BCrypt hash of the documented password, and why real production secrets must not be committed.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 11: Harden Self-Registration

Close privilege-escalation gaps in the registration workflow.

### Learning Expectations

- Least privilege for self-registration.
- Why client-side controls and hidden fields are not security controls.
- Difference between assigning a default role and accepting a requested role from the browser.



### Required Work

- Ensure self-registration always assigns `USER`.
- Remove any role picker or role input from the registration form if present.
- Reject or ignore manipulated requests that attempt to register as `ADMIN`.
- Keep username uniqueness and password confirmation validation in place.
- Newly registered users should still receive encoded passwords from Branch 9.



### Checkpoint

- A normal registration creates a `USER` in PostgreSQL.
- A crafted request cannot create an `ADMIN` through the public registration form.
- Duplicate usernames are still rejected.
- The new user can log in and cannot open `/admin/users`.



### What to expect now

Run the application and **examine** self-registration privileges. Be ready to **explain** least privilege.

- Register normally and verify the stored role is `USER`.
- Attempt to force `ADMIN` through the form or a crafted request; explain why the server must ignore or reject it.
- Sign in as that new user and confirm `/admin/users` remains forbidden.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 12: Protect APIs, Swagger, and Add Security Tests

Apply the Role Authorization Policy to the REST API and Swagger, then prove the rules with automated tests.

### Learning Expectations

- Why a secured browser UI does not automatically secure JSON endpoints.
- How HTTP methods map to read versus write authorization.
- Why Swagger must require authentication, while write try-outs still fail for `USER`.
- How MockMvc and `@WithMockUser` verify security configuration.



### Required Work

- Require an authenticated user for Swagger UI and OpenAPI docs (for example `/swagger-ui/**` and `/v3/api-docs/**`).
- Under `/api/**`:
  - Allow `GET` for authorities `USER` and `ADMIN`.
  - Allow `POST`, `PUT`, and `DELETE` only for authority `ADMIN`.
  - Reject anonymous access to `/api/**`.
- Keep Thymeleaf `/admin/**` restricted to `ADMIN` as established in Branch 5.
- Add security tests that verify:
  - Anonymous API and Swagger requests are rejected.
  - A `USER` can `GET` a representative API resource and is forbidden on `POST`/`PUT`/`DELETE`.
  - An `ADMIN` can `GET` and write (`POST`/`PUT`/`DELETE`) a representative API resource.
  - A `USER` cannot access `/admin/**`; an `ADMIN` can.
- Keep changes focused on security; do not add unrelated API features.



### Checkpoint

- Opening Swagger while anonymous redirects to login (or otherwise denies access).
- After signing in as `USER`, Swagger opens, API `GET` succeeds, and a write operation fails with 403.
- After signing in as `ADMIN`, Swagger write operations and `/admin/**` succeed.
- Security tests pass locally with Maven.
- Students can explain the full Role Authorization Policy matrix.



### What to expect now

Run the application and **examine** the full Role Authorization Policy. Be ready to **explain** each cell of the matrix with evidence.

- Anonymous: Swagger and `/api/**` denied.
- As `USER`: Swagger opens, GET succeeds, POST/PUT/DELETE fail with 403.
- As `ADMIN`: writes succeed; `/admin/**` succeeds.
- Relate your automated security tests to the same behaviors you demonstrated manually.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 13: Externalize Secrets

Keep database credentials and other secrets out of Git. Branch 12 secured routes; this branch secures how the application is configured.

### Learning Expectations

- Why committed datasource passwords are a security defect even after Spring Security is in place.
- Difference between demo login credentials (`admin` / `password`) documented in the README and real database secrets.
- How environment variables or an uncommitted local Spring profile supply configuration at runtime.
- Why `.gitignore` is part of the security control, not only a convenience.

### Required Work

- Remove real datasource usernames, passwords, and cloud database URLs from committed `application.properties` (or equivalent committed config).
- Load those values from environment variables and/or an `application-local.properties` (or `application-local.yml`) file that is not committed.
- Add the local secrets file to `.gitignore` if you use that approach.
- Leave committed config with placeholders or local-default examples only (for example `jdbc:postgresql://localhost:5432/chinook`) — never a live cloud password.
- Update the README so a classmate can set the required variables or local file and start the application.
- Confirm the application still starts and the Branch 12 authorization matrix still works after the move.

### Checkpoint

- `git grep` (or GitHub file view) of `application.properties` shows no live database password or cloud connection string.
- `.gitignore` excludes the local secrets file if one is used.
- README documents how to supply credentials locally.
- Login, administration, and a representative API call still succeed with the externalized configuration.

### What to expect now

Run the application from a clean checkout mindset: credentials come from the environment or ignored local file, not from Git.

- Show the committed properties file and explain what was removed and why.
- Start the app with the local/env configuration and sign in as `admin`.
- Explain why README demo users (`admin` / `password`) are not the same as the PostgreSQL server password.

Commit and push the branch. Open a pull request, review the change, and merge it.

---



## Verification

1. Start the completed Lab 5 application with Spring Security active.
2. Open the public home/login/register pages without authenticating.
3. Register a new user and verify the PostgreSQL password value is a BCrypt hash and the role is `USER`.
4. Log in as the new user and show that `/admin/users` is forbidden.
5. Log in as the seeded administrator and open user administration successfully.
6. Demonstrate logout and show that protected pages again require authentication.
7. Demonstrate forced password change for an account with `passwordChangeRequired=true`.
8. Change a password and verify only that user’s database row changes.
9. As a `USER`, show a successful API `GET` and a failed `POST`/`PUT`/`DELETE` (including from Swagger if demonstrated).
10. As an `ADMIN`, show a successful API write and successful access to `/admin/**`.
11. Show that anonymous access to Swagger is denied.
12. Run the security-focused tests and show that they pass.
13. Show that datasource credentials are not committed (environment variables or an ignored local profile).
14. In the video or written explanation, contrast Lab 4’s intentional insecurity with Lab 5’s enforced authentication and authorization.

---



## Deliverables



### 1. GitHub Repository

- All thirteen feature branches, or an instructor-approved equivalent branch structure that preserves the same incremental history.
- Pull requests and merge history in order.
- Meaningful commit messages.
- Instructor added as a collaborator before Branch 1.
- Updated README instructions for local startup and documented demo credentials.
- No committed real production passwords, API keys, or cloud database secrets. Use environment variables or a local profile for sensitive configuration.



### 2. Video Demonstration

- Successful form login against PostgreSQL users.
- Authorization failure for a non-admin visiting administration.
- Successful admin access.
- Logout and session end.
- Forced password-change path.
- BCrypt hash visible in PostgreSQL for a registered or updated user.
- Swagger requires login; `USER` can GET via API/Swagger but writes fail; `ADMIN` can write.
- Datasource credentials are supplied locally (environment or ignored profile), not committed in Git.
- An explicit explanation of what Lab 4 left insecure and what Lab 5 now enforces.



### 3. AI References

- Prompt or prompts used.
- AI system and model.
- Date used.
- Summary of how the response influenced the work.
- Explanation of what the student reviewed, corrected, or rejected.

---



## Optional Stretch Branches

These are not required unless announced by the instructor.

### Stretch A: Re-enable CSRF for Browser Forms

Re-enable CSRF protection for Thymeleaf form posts and add CSRF hidden fields to login, registration, password-change, logout, and administration forms. Document the API CSRF strategy separately.

### Stretch B: Method Security

Add `@EnableMethodSecurity` and protect selected service methods with `@PreAuthorize`.

---



## Study Guide — Lab 5

This study guide emphasizes Spring Security concepts that build directly on Lab 4’s user-management work.

### Authentication

Students should be able to explain:

1. Why Lab 4 credential lookup was not secure authentication.
2. What a security filter chain does on each request.
3. How a custom Thymeleaf login page is wired into Spring Security form login without using the default login page.
4. How form login submits credentials to Spring Security.
5. How `UserDetailsService` loads a user from PostgreSQL.
6. How `PasswordEncoder` verifies a password without storing or comparing plain text after Branch 9.
7. What an authenticated session means for later requests.



### Authorization

Students should be able to explain:

1. The difference between authentication and authorization.
2. The meaning of `permitAll`, `authenticated`, and `hasAuthority("ADMIN")`.
3. Why removing a link from a navbar is not authorization.
4. Why `/admin/**` must be protected by the filter chain.
5. Why REST endpoints need their own authorization rules.
6. The Role Authorization Policy: `USER` = GET only; `ADMIN` = GET/POST/PUT/DELETE; Swagger requires an authenticated user.



### UserDetails and Roles

Students should be able to explain:

1. The relationship between the `User` entity and Spring Security `UserDetails`.
2. Why this lab grants authorities as `USER` / `ADMIN` exactly as stored in the database.
3. Why `hasAuthority("ADMIN")` is used instead of `hasRole("ADMIN")`, avoiding the `ROLE_` prefix convention.
4. How the `enabled` flag integrates with authentication.
5. Why this lab uses the database rather than an in-memory user list.



### Password Encoding

Students should be able to explain:

1. Why plain-text password storage is unacceptable in real systems.
2. Why Lab 5 delays BCrypt until after form login and route protection work.
3. What a BCrypt hash looks like at a high level and why it is not reversible.
4. Why seed SQL must be updated when the encoder changes.
5. Why administration pages must never display password hashes as if they were useful UI data.



### Forced Password Change

Students should be able to explain:

1. The purpose of `passwordChangeRequired`.
2. Why enforcement belongs in the authentication success path.
3. Why password changes must target the authenticated principal.



### MVC, REST, and Security Together

Students should understand that:

1. Thymeleaf pages and REST controllers can coexist in one Spring Boot application.
2. Spring Security can protect both styles of endpoint.
3. Services remain responsible for business rules; security remains responsible for access control.
4. Lab 5 does not abandon the API-centered architecture of Labs 1–4; it secures it.



### Common Failure Points

Students should be prepared to diagnose:

1. Login failures caused by introducing BCrypt before migrating existing passwords.
2. `403` errors caused by using `hasRole("ADMIN")` while authorities are granted as `ADMIN` (use `hasAuthority("ADMIN")` instead).
3. Navbar `sec:authorize` expressions that do nothing because the Security dialect is missing.
4. Change-password flows that do not use the authenticated principal (wrong account updated).
5. A secure browser UI with wide-open mutating API endpoints.
6. Database passwords or cloud connection strings committed in `application.properties`.

