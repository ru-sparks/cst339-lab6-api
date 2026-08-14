# Canvas / Agent Catch-Up Notes — CST-339

Instructor working notes for Cursor (and future lab forks). **Not a student handout.**  
Do not confuse this file with the course `README.md`. Prefer updating this file over the student README when recording track-specific or instructor-only context.

---

## Tracks (critical)

The course conceptually has two tracks:

| Track | Scope | Delivery style |
| --- | --- | --- |
| **Tutorial lab track** | Often **Album-only** CRUD (narrower API surface) | Step-by-step lab writeups / tutorials |
| **Milestone track** | CRUD across Chinook-style entities | **User stories**, no tutorial narrative |

Lab 5 (full Chinook entities + Spring Security) is the parent. It was forked (or will be forked) twice:

| Fork | Keep | Why |
| --- | --- | --- |
| **Milestone 3 / CLC** | All entity endpoints | Groups keep the full API surface. CLC Milestone 3 is cloud deploy; Milestone 4 is users + Spring Security. |
| **Lab 6** | **Album only** | Labs should not ship every entity endpoint. First task after the fork: delete non-Album features, then rebuild Album infrastructure. |

**Students never clone the instructor repositories.** Each student (lab) and each CLC (milestone) creates their own GitHub repo early in the course and keeps working in that repo through later assignments. Instructor forks exist so Cursor has notes and a reference tree, not as a student starter.

This workspace is the **instructor Milestone 3** tree (full entities). Lab 6’s **instructor/Cursor workspace** should fork **this repo**, not Lab 5: the Java is the same, and this tree has the catch-up notes. After that fork, strip to Album so the instructor Lab 6 tree matches what the lab assignment asks students to do in *their* lab repo.

**Do not modify the student `README.md` to explain milestone-vs-lab scope** — that confuses students if README text is reused on the other track.

When writing shared security wording, prefer `/api/**` and “a representative API resource” so the same policy applies on both the Milestone 3 fork and the Album-only Lab 6 fork.

### Milestone / group workflow (planned pattern)

- Milestones are written as **user stories** (acceptance-focused), **without** a tutorial walkthrough.
- Work is **group work**.
- Expect a **multi-branch repository**, likely **one branch per user story** (then PR/merge), not a single shared long-lived branch for all stories.
- Instructor grades from GitHub history / PRs; groups should keep story branches small and reviewable.

---

## Lab 5 — Spring Security (complete on the parent)

Finished in this tree (form login, BCrypt, API matrix, secrets out of Git). Historical branch notes below are for the Lab 5 assignment, not remaining work.

### Source of truth for students

- `CST-339-Lab5-Spring-Security.md` — full 13-branch lab guide.
- Ignore / do not regenerate PDF from the guide unless the instructor asks (`CST-339-Lab5-Spring-Security.pdf` was removed as unused).

### Starter baseline

- Lab 5 starts from **Lab 4 level**: Thymeleaf register / **hand-rolled login** / change-password / admin, PostgreSQL `users` table, **no Spring Security**.
- Commit `fcd7ea9` restored that baseline on GitHub after an early Security push was walked back.

### Roles and authorities (decided)

- Database / domain roles: **`USER`** and **`ADMIN`** (Lab 4 convention — keep it).
- **Do not** store or require `ROLE_USER` / `ROLE_ADMIN` in the database.
- Grant Spring authorities as the **exact** stored values.
- Authorize with **`hasAuthority("ADMIN")`** / **`hasAuthority("USER")`**, not `hasRole(...)` (avoids the `ROLE_` prefix convention).

### Role Authorization Policy (finished Lab 5 target)

| Authority | `/api/**` GET | `/api/**` POST, PUT, DELETE | Swagger UI / OpenAPI | Thymeleaf `/admin/**` |
| --- | --- | --- | --- | --- |
| Anonymous | Denied | Denied | Denied | Denied |
| `USER` | Allowed | Denied (403) | Allowed (writes still 403) | Denied |
| `ADMIN` | Allowed | Allowed | Allowed | Allowed |

- Swagger requires an **authenticated** user.
- A `USER` can open Swagger and run GETs; write try-outs must fail.
- Full API/Swagger matrix is **Branch 12**; earlier branches must not jump ahead.
- Branch 4 may temporarily leave `/api/**` and Swagger open while teaching page authentication.

### Other Lab 5 design decisions

- **No in-memory users** — PostgreSQL / `UserRepository` only.
- **Keep Lab 4 Thymeleaf login page**; wire it into Spring Security `formLogin`. Do **not** use Spring’s default login page.
- Branch 1: add Security + open `permitAll` filter chain (otherwise Boot shows default login + generated password). CSRF disabled temporarily so Lab 4 forms without tokens still POST.
- BCrypt starts at **Branch 9**; seed hash migration **Branch 10**. Introducing BCrypt earlier breaks Lab 4 plaintext passwords.
- Self-registration always assigns **`USER`** (Branch 11).
- Datasource secrets stay out of Git (Branch 13): env vars or uncommitted `application-local.properties`.
- Git workflow: each branch → commit → push → PR → **merge to main** → checkout main → pull → next branch.
- Instructor is added as GitHub collaborator; grade by ordered PR history.

### Branch map (13 PRs)

1. Open Security filter chain (`permitAll`)
2. DB `UserDetailsService` (authorities = `USER`/`ADMIN`)
3. Wire Thymeleaf login into `formLogin` (remove hand-rolled POST compare)
4. Public vs authenticated pages
5. `/admin/**` → `hasAuthority("ADMIN")`
6. Logout + auth-aware navbar (Thymeleaf Security dialect)
7. Forced password-change success handler
8. Change password via authenticated `Principal`
9. BCrypt on register / change-password
10. Seed hashed admin + remove NoOp
11. Harden registration (no self-ADMIN)
12. API method rules + Swagger auth + security tests
13. Externalize datasource secrets (env vars or uncommitted local profile)

### Lab 5 implementation status

Complete through Branch 13 on the parent (BCrypt, seed hash, registration locked to `USER`, API/Swagger matrix, datasource env vars / local profile). This Milestone 3 tree includes that finished security stack **and** every Chinook feature package.

**Instructor note — change-password “first user” bug:** Some instructor trees (including this repo historically) updated passwords via `findAll().findFirst()`. **Students may not have that bug.** Fix Principal-based updates in code when doing Branch 8, but **do not enshrine that bug in the student Lab 5 assignment** as a Lab 4 defect they must discover. Frame Branch 8 as wiring change-password to the authenticated principal.

### Key paths

- Security: `src/main/java/com/sparkco/lab2_api/features/user/SecurityConfig.java`
- Users: `.../features/user/` (entity, repo, service, login/register/admin controllers, Thymeleaf templates)
- Guide: `CST-339-Lab5-Spring-Security.md`
- Package/artifact naming: **keep Java package `com.sparkco.lab2_api`** (intentional — do not rename packages). Surface labels (navbar, index, main application class) may say Lab 5; Maven `artifactId` / `spring.application.name` may still say `lab2-api` as leftover debt. Do not churn the package tree for cosmetics.

---

## CLC Milestones 3 and 4 (this workspace)

Student writeups (PR-focused): `CST-339-CLC_Milestone_3_Cloud_Deployment.md` and `CST-339-CLC_Milestone_4_User_Management_and_Security.md` (plus `.docx`). Combined security-and-deployment draft kept at `CST-339-CLC_Milestone_3_Security_and_Deployment.md`.

- **Milestone 3** (after Lab 3): two code PRs (Docker, Neon). Render URL is documented (design doc + video), not a third PR. CLC groups are 2–3.
- **Milestone 4** (after Labs 4–5): three code PRs (users, authentication, authorization + secrets). Redeploy documented the same way.
- Split for the calendar (combined version left a two-week gap). M3 is lighter than M4; M2 stays the heaviest CLC. Quizzes carry conceptual points (JAR/Docker/Render, authn vs authz).

This instructor repo already has Lab 3–5 **code** prerequisites for Render (`Dockerfile`, `server.port=${PORT:8080}`, `SPRING_DATASOURCE_*`). Live Neon/Render URLs are operational proof, not in Git.

## Lab 6 fork (instructor tree, from this repo)

Fork **this Milestone 3 tree** (`cst-339-milestone3`) for the next Cursor project. Code matches completed Lab 5; `canvas_readme.md` stays with the fork.

Students keep using **their existing lab repository** (the one they have used since Lab 1/2). The Lab 6 writeup tells them to drop non-Album Chinook CRUD in that repo (if it is still there) and add Album query endpoints. They do not clone this fork.

CLC groups keep using **their existing milestone repository**. Milestone 6 is more PRs on that repo. They do not switch to Album-only.

Album is the lab entity on purpose: it is a small CRUD surface (`title`, `artist_id`) **and** it sits in the Chinook graph (album → artist, tracks → album, then genre / media type). Lab 6 uses those relationships for paging, filtering, and joins so students can see why a DTO is a message and an entity is persistence. Today `AlbumDTO` is 1:1 with `Album` (`albumId`, `title`, `artistId`) — that mapping looks like ceremony until a query result is no longer one table.

### First tasks after the Lab 6 fork

1. **Delete non-Album Chinook features.** Remove feature packages and tests for artist, track, customer, employee, genre, invoice, invoice_line, media_type, playlist, playlist_track (controllers, services, repositories, DTOs, mappers). Keep `features/user/**`, Thymeleaf admin, Spring Security, greeting if still used, `/api/albums`.
2. **Recreate Album infrastructure.** After the strip, Album should still compile, start, and CRUD through `/api/albums` under the Lab 5 security matrix. Then add only what Album queries need: read-side types or repositories for related tables (Artist, Track, …) **behind** Album endpoints. No public CRUD packages for those related entities.
3. **Then Lab 6 content:** paging, filtering, **and** at least one join plus one aggregate in the same numbered lab (album + artist name, track count or duration). That is the DTO-vs-entity payoff. New query endpoints return Records. They do not return `Album` entities or lazy graphs. Extra API variation belongs in more Lab 6 PRs and in Milestone 6, not in a Lab 7 / Milestone 7 pair.

Keep Java package `com.sparkco.lab2_api`. Do not rewrite the student README to explain milestone-vs-lab scope.

### Catch-up checklist

1. Identify the tree: instructor **Milestone 3** (this repo) vs instructor **Lab 6** (fork of this repo, then Album-only). Students are on their own remotes.
2. Security is already complete on this parent. Fork for notes; strip to Album in the instructor Lab 6 tree.
3. Lab 6 assignment: students continue their lab repo; delete non-Album CRUD there; related tables only as query infrastructure under Album.
4. CLC: continue the group milestone repo; full entity surface; PR-focused writeups; groups of 2–3.
5. Leave the student `README.md` track-agnostic.

## CLC Milestone 6 (planned) — no coding Milestone 7

**No implementation Milestone 7.** Lab 7 was only “more API variation” on Lab 6. That variation is Milestone 6’s field of PRs (and extra Lab 6 branches if needed).

**Stop at Lab 6 as a numbered lab** if Lab 6 includes join + aggregate so the DTO insight actually lands. A second individual query lab at the end of the term stacks too many labs; the course already tends to dump labs in the last weeks.

**Milestone 7 may be a presentation** (capstone defense of the CLC product: architecture, security, deployment, query DTOs vs entities). That fills the last CLC slot without another build. It is not Lab 7 with a new number.

After Lab 6, the CLC repo (full Chinook, already secured and deployed) gets a **field of query PRs**:

- Paging
- Filter (title, artist name, …)
- Join (album + artist name)
- Aggregate (track count, duration)
- Maybe one more join (genre) if the calendar allows

That is many PRs for a group of 2–3. Bound it like Milestones 3–4: **PRs are the assignment**, not a 13-branch tutorial and not “every Chinook entity gets the same query API.” Representative endpoints. New Records for query messages; existing CRUD DTOs can stay 1:1.

Lab 6 (Album-only tutorial) teaches the pattern. Milestone 6 applies it on the group product. Do not strip the CLC API to Album. Milestone 7, if used, is presentation — not another query lab.

---

## Instructor prefs (Cursor)

- Designer role; prefers working directly in code.
- Lab guides in Markdown are preferred over PDF generation.
- Commit/push when asked; merge-then-pull-main before the next lab branch.
- Avoid in-memory security shortcuts; use the real PostgreSQL users table.
- Keep student-facing README stable; put instructor/track notes here instead.
- Milestones = user stories + group multi-branch workflow; labs = tutorial writeups.

---

## Repo remotes

- Lab 5 parent: `https://github.com/ru-sparks/cst339-lab5-api.git`
- This workspace (Milestone 3 CLC): `https://github.com/ru-sparks/cst-339-milestone3.git`
- Lab 6 instructor fork: from this repo; update this line with the new remote when it exists.
