# CLC Milestone 3: Cloud Deployment of the Chinook API

CLC groups of **two or three** continue the Milestone 2 repository after completing Lab 3. Invite the instructor as a collaborator.

This milestone hosts the Chinook CRUD API on Render against Neon PostgreSQL. Keep the Milestone 2 entity endpoints.

## Pull requests

Two code pull requests, merged in order. Each member is primary author of at least one merged PR. Groups of three split PR 01 or PR 02 so each member authors one. A teammate reviews before merge. Each PR lists Done, Out of scope, and Test plan.

### PR 01: Containerize the CLC API

**Title:** `Milestone 3 PR 01: Containerize the CLC API`

**As an** operator, **I want** the CLC application packaged as a Docker image **so that** the same artifact can run locally and on Render.

- The project builds a Spring Boot JAR with the Maven wrapper.
- A `Dockerfile` at the repository root builds that JAR and runs it.
- `server.port` honors `PORT` and defaults to `8080`.
- The image runs locally and serves a Milestone 2 endpoint (browser or Swagger).
- Milestone 2 JSON endpoints still compile and start.

### PR 02: Connect to Neon

**Title:** `Milestone 3 PR 02: Connect to Neon`

**As an** API consumer, **I want** the CLC application to use cloud-hosted PostgreSQL **so that** Chinook data is available off a single workstation.

- A Neon PostgreSQL database is provisioned for the CLC (Vercel/Neon as in Lab 3).
- Chinook tables required by Milestone 2 are present in Neon and reachable from pgAdmin.
- The application connects to Neon (`spring.datasource.url`, username, and password).
- Locally, landing or Swagger loads and representative CRUD against Neon succeeds.
- Milestone 2 tests that run without the cloud still pass.

Datasource values may follow the Lab 3 pattern. Milestone 4 moves secrets to the environment.

## Render deployment

PRs 01 and 02 already contain the code Render needs. Standing up the Render service is documented, not a third pull request.

Record in the **design document** and **README**:

- The public Render URL (full URL).
- That Render builds from GitHub with Docker and connects to the Neon database from PR 02.
- How Chinook was loaded into Neon.

The **technical defense video** opens that same URL. The address bar shows the Render hostname (for example `https://your-service.onrender.com`). From that host, show Swagger (or equivalent), a successful `GET`, and a successful write.

## Deliverables

Individual grades may differ from the group grade based upon measurable GitHub contributions.

### 1. Shared GitHub Repository (30%)

- Repository link and instructor access.
- The two code pull requests above, reviewed and merged. Groups of three include a split so every member authors one PR.
- Professional commit messages and a proper `.gitignore`.
- README for local startup, Docker, Neon, and the Render URL.

### 2. Application Functionality (30%) and Technical Defense Video (10%)

Every CLC member participates in one video.

- Local Docker run serving a Milestone 2 endpoint.
- Chinook data in Neon (pgAdmin or API responses).
- **Live Render URL** in the address bar, then Swagger, a `GET`, and a write on that host.
- The two pull requests on GitHub.

### 3. Design Document (20%)

Continue the cumulative Word design document. Use Heading 1, Heading 2, and Heading 3 styles.

- Change log.
- Packaging and deployment decisions (GitHub → Render → Docker → Spring Boot → Neon).
- **Public Render URL** (same host as the video).
- How Chinook was loaded into Neon.
- Testing strategy.
- Architecture updates since Milestone 2.
- AI references and prior instructor feedback.

### 4. AI References (10%)

Prompt, AI system, model, date used, how the response influenced the work, and what the student reviewed or corrected.

## Grading notes

The instructor grades from GitHub history and collaborator access. Each member authors at least one merged pull request and appears in the video. GitHub grade follows authored pull requests. Video participation is 20 percent of the individual grade. The Render URL belongs in both the video and the design document.
