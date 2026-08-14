# CLC Milestone 3: Secure and Deploy the Chinook API

## Overview

In this milestone, your CLC group will secure and deploy the Chinook CRUD API completed in Milestone 2.

You already built application users, Spring Security, and cloud deployment as **individual** work in Labs 3, 4, and 5. This milestone applies that knowledge to the **shared CLC product**.

The finished application is a professionally secured, cloud-hosted REST API with a small Thymeleaf administrative interface. Chinook CRUD from Milestone 2 remains the primary API surface. Spring Security protects both the JSON API and the browser pages. Datasource credentials come from the environment. The running service is publicly reachable.

CLC groups are **two or three students**.

## Prerequisites

- Complete **CLC Milestone 2** on the shared group repository (CRUD for all required Chinook entities).
- Complete **Lab 3**, **Lab 4**, and **Lab 5** as individual work.
- Continue the **same CLC GitHub repository** used for Milestones 1 and 2.
- Add the instructor as a collaborator if that access is not already in place.

## Scenario

Your CLC group has been hired to take the Milestone 2 Chinook API into a form that external developers and operators can trust. Consumers still expect consistent REST endpoints and OpenAPI documentation. The client now also requires authenticated access, role-based authorization, hashed passwords, and a public cloud deployment whose database credentials are supplied at runtime.

## Learning Outcomes

- Apply Lab 4 user-management features to the CLC Chinook API.
- Apply Lab 5 Spring Security to both Thymeleaf pages and `/api/**` endpoints.
- Authenticate against the PostgreSQL `users` table through `UserRepository`.
- Enforce `USER` and `ADMIN` authorities with `hasAuthority(...)`.
- Store passwords as BCrypt hashes.
- Load datasource credentials from the environment or an uncommitted local profile.
- Deploy the secured CLC application with Docker, Render, and Neon, using the Lab 3 pipeline.
- Continue professional GitHub collaboration, design documentation, and technical defense.

## Architectural Scope

After this milestone, the CLC application supports two presentation styles, protected by one security configuration and hosted in the cloud:

| Application area | Presentation style | Security and operations |
| --- | --- | --- |
| Chinook business entities | REST controllers returning JSON | Role-based HTTP method rules on `/api/**` |
| User registration and administration | Spring MVC + Thymeleaf | Form login, sessions, roles, password change |
| API docs | Swagger UI / OpenAPI | Authenticated users only |
| Runtime | Docker container on Render | Neon PostgreSQL; credentials from the environment |

Controllers remain thin and delegate to services. Services coordinate repositories and business rules. Spring Security decides whether a request may reach those controllers. The Controller → Service → Repository architecture stays in place.

## Design Rules

These rules match the finished Lab 5 application.

- Authenticate through `UserRepository` and PostgreSQL.
- Store roles as **`USER`** and **`ADMIN`**. Grant those values as authorities. Authorize with **`hasAuthority("ADMIN")`** and **`hasAuthority("USER")`**.
- Wire `formLogin` to the CLC Thymeleaf `/login` page.
- Spring Security performs authentication.
- Encode passwords with `BCryptPasswordEncoder`. Seed data uses the provided BCrypt hash.
- Self-registration assigns **`USER`**. The server sets the role.
- Keep the Milestone 2 Chinook entity endpoints.
- Load datasource URL, username, and password from environment variables and/or an uncommitted local profile listed in `.gitignore`.

## Role Authorization Policy

The finished CLC application uses this matrix:

| Authority | `/api/**` GET | `/api/**` POST, PUT, DELETE | Swagger UI / OpenAPI | Thymeleaf `/admin/**` |
| --- | --- | --- | --- | --- |
| Anonymous | Denied | Denied | Denied | Denied |
| `USER` | Allowed | Denied (403) | Allowed (writes still 403) | Denied |
| `ADMIN` | Allowed | Allowed | Allowed | Allowed |

A `USER` may open Swagger and run GET requests. Write try-outs fail with 403. An `ADMIN` may use Swagger for both read and write operations. Self-registered accounts remain `USER`.

## GitHub Collaboration Requirements

One shared GitHub repository with instructor access. Work from the Milestone 2 `main` branch.

CLC groups have **two or three** members. Every member is the **primary author** of at least one merged pull request.

### How many pull requests

This milestone expects **four required pull requests** — one per user story:

| PR | Story | Suggested title |
| --- | --- | --- |
| 1 | Application users and account pages | `Milestone 3 PR 01: Application users and account pages` |
| 2 | Spring Security authentication | `Milestone 3 PR 02: Spring Security authentication` |
| 3 | Role-based authorization | `Milestone 3 PR 03: Role-based authorization` |
| 4 | Secrets and cloud deployment | `Milestone 3 PR 04: Secrets and cloud deployment` |

Complete them **in order**. Merge PR 1 before starting PR 2, and so on.

In a group of two or three, some members are primary author of more than one of the four PRs.

### How to split the work

Stories are sequential. Split **roles inside each story**, then rotate who is primary author.

For each PR:

- **Primary author** owns the branch, the majority of the implementation, the pull request, and the PR description (Done, Out of scope, Test plan).
- **Contributors** add focused commits on that same branch before it is opened (tests, templates, SQL, README, configuration).
- **Reviewer** is a teammate. Review before merge.

A workable rotation for a group of three:

| Member | Primary author | Also contributes / reviews |
| --- | --- | --- |
| A | PR 1 and PR 4 | Reviews PR 2 |
| B | PR 2 | Contributes tests or templates on PR 1; reviews PR 3 |
| C | PR 3 | Contributes security tests on PR 3; reviews PR 1 and PR 4 |

A workable rotation for a group of two:

| Member | Primary author | Also contributes / reviews |
| --- | --- | --- |
| A | PR 1 and PR 3 | Reviews PR 2 and PR 4 |
| B | PR 2 and PR 4 | Contributes on PR 1 and PR 3; reviews PR 1 and PR 3 |

The design document, video, and deployment proof are group deliverables. GitHub history remains the record of individual contribution.

Additional requirements:

- Meaningful feature branches and professional commit messages.
- Pull request review before merge.
- Proper `.gitignore` (Maven/Spring Boot artifacts, IDE files, and local secret files).
- Repository history should demonstrate meaningful contributions from all CLC members. Individual grades follow measurable GitHub participation.

## User Stories

Complete the stories in order. Later stories depend on earlier ones.

### Story 1: Application Users and Account Pages

**As a** visitor  
**I want** to register and use account pages in the CLC Chinook application  
**so that** the group product has PostgreSQL-backed application users and a small browser interface for registration, sign-in, password change, and administration.

#### Acceptance criteria

- A `users` table exists in PostgreSQL and is mapped by a `User` entity (`username`, `password`, `role`, `enabled`, `passwordChangeRequired`).
- A seeded administrator exists with username `admin`, role `ADMIN`, and `passwordChangeRequired` appropriate for the demo path.
- Thymeleaf pages exist for home, registration, login, change-password, and user administration.
- Registration collects username, password, and confirm password; matching passwords and unique usernames are required; the stored role is `USER`.
- Administration lists users and supports role and enabled changes. Password values stay out of the HTML.
- At least one administrator remains enabled, or the group documents a recovery path.
- Existing Milestone 2 JSON endpoints still compile and start.

### Story 2: Spring Security Authentication

**As a** registered user  
**I want** Spring Security to authenticate me against PostgreSQL with hashed passwords  
**so that** sign-in creates a real session and credentials are stored as hashes.

#### Acceptance criteria

- `spring-boot-starter-security` is on the classpath with a `SecurityFilterChain`.
- A database-backed `UserDetailsService` loads users through `UserRepository.findByUsername(...)`.
- Authorities are the stored role values `USER` or `ADMIN`.
- Disabled accounts are refused at login.
- `formLogin` uses the CLC Thymeleaf `/login` page.
- Invalid credentials return the same Thymeleaf login page with a general error.
- Logout ends the session; later visits to protected pages go through sign-in again.
- The navbar reflects anonymous versus authenticated state.
- Passwords are encoded with `BCryptPasswordEncoder` on registration and password change.
- Seed data stores a BCrypt hash.
- After a successful password change, the authenticated user’s row is updated (`Principal` or SecurityContext), and `passwordChangeRequired` is cleared.
- Users with `passwordChangeRequired=true` are redirected to `/change-password` after login.

Use this known BCrypt hash of the demo password `password` for the seeded administrator:

```text
$2a$10$WR5.cGFXDVcgneDGVNIls.S93MMoy4/SFJccInWPqYDFiixspzKHq
```

Document local demo credentials (`admin` / `password`) in the README. Supply database credentials from the environment or a local profile.

### Story 3: Role-Based Authorization

**As an** administrator  
**I want** `USER` and `ADMIN` authorities enforced on pages and API methods  
**so that** readers can use the Chinook API while administrators can change data or manage accounts.

#### Acceptance criteria

- Public routes remain reachable anonymously as needed (`/`, `/login`, `/register`, static assets, error pages).
- `/change-password` requires authentication.
- `/admin/**` requires `hasAuthority("ADMIN")`. A `USER` receives 403 (or the course-standard access-denied behavior).
- Anonymous requests to `/api/**` and to Swagger/OpenAPI follow the Role Authorization Policy.
- Under `/api/**`: `GET` is allowed for `USER` and `ADMIN`; `POST`, `PUT`, and `DELETE` are allowed for `ADMIN`.
- Swagger UI and OpenAPI docs require an authenticated user. A signed-in `USER` can open Swagger and succeed on GET; write try-outs return 403.
- Security tests verify the matrix against a representative API resource (for example albums).
- Milestone 2 CRUD behavior remains intact for authorized callers.

### Story 4: Secrets and Cloud Deployment

**As an** operations stakeholder  
**I want** the secured CLC API deployed with credentials supplied at runtime  
**so that** the public service uses the Lab 3 architecture with Lab 5 configuration practice.

#### Acceptance criteria

- Datasource URL, username, and password come from environment variables and/or an uncommitted local profile listed in `.gitignore`.
- The README explains how a classmate starts the app locally with those settings.
- The CLC application is packaged with Docker and deployed to Render.
- PostgreSQL for the deployment is hosted on Neon (Chinook data plus the `users` table).
- Render supplies datasource (and port) values as environment variables.
- The public Render URL is recorded as **deployment proof** (see below).
- On that public URL, the group demonstrates: landing or login page, authenticated Swagger, a successful authorized GET, and a write that respects the Role Authorization Policy.
- Committed properties show placeholders or local defaults only.

## Deployment Proof

Record the public Render URL in the video and in the design document.

Required proof:

1. **Technical defense video.** Open the **live Render URL** in a browser. The address bar shows the Render hostname (for example `https://your-service.onrender.com`). From that URL, demonstrate login (or a public page plus authenticated Swagger) and at least one authorized API GET against Neon.
2. **Design document.** Include a heading for the public deployment and paste the **full Render URL**. State that the service is hosted on Render, that PostgreSQL is on Neon, and that datasource credentials are environment variables. If the URL changes before submission, update the document to the URL used in the video.
3. **README (recommended).** The same Render URL may also appear in the project README. The design document still includes the URL.

The instructor should be able to open the documented URL. If the service is spun down later, the video still shows that URL working at recording time, and the design document still contains that same URL.

## Testing Requirements

- Representative service-layer tests for registration, password change, and authority mapping.
- Security tests for anonymous, `USER`, and `ADMIN` access to a representative `/api/**` resource, Swagger, and `/admin/**`.
- Evidence that the application starts locally with externalized configuration.
- Evidence that PostgreSQL persistence still functions for Chinook entities and users.
- Evidence that the Render deployment starts and connects to Neon.

## Deliverables

Individual grades may differ from the group grade based upon measurable GitHub contributions.

### 1. Shared GitHub Repository (30%)

- Repository link and instructor access.
- Four merged pull requests (one per user story). Every member is primary author of at least one PR.
- Professional commit messages and a proper `.gitignore`.
- Updated README for local startup, demo login (`admin` / `password`), and required environment variables or local profile.
- Datasource credentials supplied at runtime.
- Evidence of incremental software development and contributions from all CLC members.

### 2. Application Functionality (30%) and Technical Defense Video (10%)

Application functionality is graded from a single technical defense video. Every CLC member participates. The four user stories are visible in that recording.

Maven tests and pgAdmin may appear in the video where they help explain the product. Deployment proof is the live Render URL.

Show and explain:

- Public home, login, and register pages.
- Registration of a new user; PostgreSQL shows role `USER` and a BCrypt hash. Administration pages omit password values.
- Successful form login on the custom Thymeleaf page.
- As that `USER`: `/admin/users` returns 403; API `GET` succeeds; `POST`/`PUT`/`DELETE` return 403 (Swagger is acceptable).
- Swagger requires a signed-in user.
- As `ADMIN`: user administration succeeds; an API write succeeds.
- Logout, then a protected page that goes through sign-in again.
- Forced password-change path. After a change, that user’s database row is updated and `passwordChangeRequired` is cleared.
- Security-focused tests passing.
- **The live Render URL in the browser address bar**, then login/Swagger and an authorized GET on that host.
- Datasource credentials supplied locally and on Render from the environment (or ignored profile).
- How this milestone adds authentication, authorization, and a public host to the Milestone 2 API.
- GitHub workflow for the four required story pull requests.

### 3. Design Document (20%)

Continue the cumulative Word design document. Use Word Heading styles (Heading 1, Heading 2, and Heading 3).

Required for this milestone:

- Updated change log.
- Technical design decisions for security and deployment.
- Authentication versus authorization.
- Role Authorization Policy.
- Password encoding and seed-data consistency.
- Deployment architecture (GitHub → Render → Docker → Spring Boot → Neon).
- **Public Render URL** (full URL, same host shown in the video).
- Secrets management.
- Testing strategy.
- Architecture updates since Milestone 2.
- AI references.
- Prior instructor feedback addressed.

### 4. AI References (10%)

- Prompt or prompts used.
- AI system and model.
- Date used.
- Summary of how the response influenced the work.
- Explanation of what the student reviewed, corrected, or rejected.

AI is an approved development tool. Students are responsible for verifying and defending all submitted work.

## Grading Notes

- Every milestone requires meaningful feature branches and pull requests.
- Repository history should demonstrate incremental professional development.
- All code submitted must compile and execute successfully.
- Every group member is expected to make meaningful technical contributions.
- The instructor needs collaborator access to the CLC repository in order to grade.
- Story 4 is complete when the Render URL appears in both the technical defense video and the design document.
- All CLC members author pull requests and participate in the video. GitHub grade follows authored pull requests. Video participation is 20 percent of the individual grade.
- AI is an approved development tool. Students are responsible for verifying and defending all submitted work.
