# CLC Milestone 4: User Management and Spring Security

CLC groups of **two or three** continue the Milestone 3 repository after completing Labs 4 and 5. Invite the instructor as a collaborator.

This milestone adds application users and Spring Security to the CLC Chinook API, then records the secured public URL. Keep the Milestone 2 entity endpoints.

## Pull requests

Three code pull requests, merged in order. Each member is primary author of at least one merged PR. Groups of two: one member authors two PRs. A teammate reviews before merge. Each PR lists Done, Out of scope, and Test plan.

Authenticate through `UserRepository` and PostgreSQL. Store roles as `USER` and `ADMIN`. Authorize with `hasAuthority("ADMIN")` and `hasAuthority("USER")`. Wire `formLogin` to the CLC Thymeleaf `/login` page. Encode passwords with `BCryptPasswordEncoder`. Self-registration assigns `USER`.

### Role authorization policy

| Authority | `/api/**` GET | `/api/**` POST, PUT, DELETE | Swagger UI / OpenAPI | Thymeleaf `/admin/**` |
| --- | --- | --- | --- | --- |
| Anonymous | Denied | Denied | Denied | Denied |
| `USER` | Allowed | Denied (403) | Allowed (writes still 403) | Denied |
| `ADMIN` | Allowed | Allowed | Allowed | Allowed |

### PR 01: Application users and account pages

**Title:** `Milestone 4 PR 01: Application users and account pages`

**As a** visitor, **I want** to register and use account pages **so that** the CLC product has PostgreSQL-backed application users.

- A `users` table exists in PostgreSQL (local and Neon) and is mapped by a `User` entity (`username`, `password`, `role`, `enabled`, `passwordChangeRequired`).
- Seeded administrator: username `admin`, role `ADMIN`, `passwordChangeRequired` set for the demo path.
- Thymeleaf pages for home, registration, login, change-password, and user administration.
- Registration collects username, password, and confirm password; unique usernames; stored role `USER`.
- Administration lists users and supports role and enabled changes. Password values stay out of the HTML.
- At least one administrator remains enabled, or the group documents a recovery path.
- Chinook JSON endpoints still compile and start.

### PR 02: Spring Security authentication

**Title:** `Milestone 4 PR 02: Spring Security authentication`

**As a** registered user, **I want** Spring Security to authenticate me against PostgreSQL with hashed passwords **so that** sign-in creates a real session.

- `spring-boot-starter-security` and a `SecurityFilterChain`.
- `UserDetailsService` loads users through `UserRepository.findByUsername(...)`.
- Authorities are the stored values `USER` or `ADMIN`. Disabled accounts are refused at login.
- `formLogin` uses the CLC Thymeleaf `/login` page. Invalid credentials return that page with a general error.
- Logout ends the session. The navbar reflects anonymous versus authenticated state.
- `BCryptPasswordEncoder` on registration and password change. Seed data stores a BCrypt hash.
- Password change updates the authenticated user (`Principal` or SecurityContext) and clears `passwordChangeRequired`.
- Users with `passwordChangeRequired=true` go to `/change-password` after login.

Known BCrypt hash of `password` for the seeded administrator:

```text
$2a$10$WR5.cGFXDVcgneDGVNIls.S93MMoy4/SFJccInWPqYDFiixspzKHq
```

README documents demo login `admin` / `password`.

### PR 03: Authorization and secrets

**Title:** `Milestone 4 PR 03: Authorization and secrets`

**As an** administrator, **I want** `USER` and `ADMIN` authorities enforced and datasource credentials supplied at runtime **so that** the API follows the policy above and Git stays free of live database passwords.

- Public routes: `/`, `/login`, `/register`, static assets, error pages.
- `/change-password` requires authentication. `/admin/**` requires `hasAuthority("ADMIN")`.
- `/api/**` GET for `USER` and `ADMIN`; POST, PUT, DELETE for `ADMIN`. Swagger and OpenAPI require a signed-in user.
- Security tests cover a representative API resource (for example albums), Swagger, and `/admin/**`.
- Datasource URL, username, and password come from environment variables and/or an uncommitted local profile listed in `.gitignore`.
- README explains local startup with those settings. Committed properties use placeholders or local defaults.
- Neon includes Chinook data and the `users` table.

## Render deployment

PR 03 already contains the security rules and environment configuration Render needs. Updating the live service is documented, not a fourth pull request.

Record in the **design document** and **README**:

- The public Render URL for this milestone (update it if it changed since Milestone 3).
- That Render supplies datasource and port values as environment variables.
- That Neon holds Chinook data and the `users` table.

The **technical defense video** opens that same URL. The address bar shows the Render hostname. From that host, show login (or a public page plus authenticated Swagger) and an authorized API GET.

## Deliverables

Individual grades may differ from the group grade based upon measurable GitHub contributions.

### 1. Shared GitHub Repository (30%)

- Repository link and instructor access.
- The three code pull requests above, reviewed and merged.
- Professional commit messages and a proper `.gitignore`.
- README for local startup, demo login, environment variables or local profile, and the Render URL.

### 2. Application Functionality (30%) and Technical Defense Video (10%)

Every CLC member participates in one video.

- Public home, login, and register pages.
- New user in PostgreSQL with role `USER` and a BCrypt hash. Administration pages omit password values.
- Form login on the CLC Thymeleaf page.
- As `USER`: `/admin/users` returns 403; API `GET` succeeds; writes return 403.
- Swagger requires a signed-in user.
- As `ADMIN`: administration succeeds; an API write succeeds.
- Logout, forced password-change path, and security tests passing.
- **Live Render URL** in the address bar, then login/Swagger and an authorized GET on that host.

### 3. Design Document (20%)

Continue the cumulative Word design document. Use Heading 1, Heading 2, and Heading 3 styles.

- Change log.
- Authentication versus authorization and the Role Authorization Policy.
- Password encoding and seed data.
- Secrets management.
- Deployment architecture and the **public Render URL** (same host as the video).
- Testing strategy.
- Architecture updates since Milestone 3.
- AI references and prior instructor feedback.

### 4. AI References (10%)

Prompt, AI system, model, date used, how the response influenced the work, and what the student reviewed or corrected.

## Grading notes

The instructor grades from GitHub history and collaborator access. Each member authors at least one merged pull request and appears in the video. GitHub grade follows authored pull requests. Video participation is 20 percent of the individual grade. The Render URL belongs in both the video and the design document.
