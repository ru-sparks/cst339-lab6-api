# CST-339 Lab 6: Paging, Filtering, and Query DTOs

## Entities, Messages, and the Chinook Album Graph

### Overview

In Labs 1–5 you built an Album REST API, persisted it to PostgreSQL, secured it with Spring Security, and documented it with OpenAPI. Lab 5’s Role Authorization Policy still applies: a `USER` may `GET` `/api/**`; an `ADMIN` may also `POST`, `PUT`, and `DELETE`; Swagger requires a signed-in user.

Lab 6 does not add another entity’s CRUD API. You already have Album. Chinook’s `album` table has **372** rows. An unpaged `GET /api/albums` is a dump, not a professional collection resource. This lab teaches paging, filtering, and query messages that are no longer a 1:1 copy of the `Album` entity.

Today `AlbumDTO` is `(albumId, title, artistId)` — the same shape as the `album` table. That mapping looks like ceremony until a result needs an artist **name** or a **track count**. Those values do not live on `album`. They come from a join and an aggregate. The query result is a different message than the CRUD DTO.

**Important design rule:** Do not add `artistName` or `trackCount` onto `AlbumDTO`. Create and update still need `artistId`. Query endpoints return **new Java Records**. They do not return `Album` entities, lazy-loaded graphs, or Jackson-serialized JPA proxies.

Complete each branch in order. Each branch is one coherent query feature. Merge the pull request before beginning the next branch. Do not jump ahead to joins or aggregates before paging is in place — otherwise you cannot tell whether a slow or huge response is a paging problem or a query problem.

### Purpose

This lab introduces the difference between a **persistence entity** and a **query message**. Album stays the only public CRUD resource. Related Chinook tables (`artist`, `track`) are read-side infrastructure **behind** Album endpoints. They are not new public APIs.

Paging is required because 372 albums is enough for page number and page size to matter. Filtering, a join (album + artist name), and an aggregate (track count) belong in this same numbered lab so the DTO insight actually lands.

### Lab Outcomes

- Replace an unpaged album collection with a paged response that reports total rows (about 372).
- Filter albums in the database (not in Java after `findAll()`).
- Map `artist` and `track` only as query support — no `/api/artists` or `/api/tracks`.
- Return a query Record that includes artist name from a join.
- Include an aggregate (`trackCount`) on that same query Record.
- Keep Lab 5 security: query `GET`s are allowed for `USER` and `ADMIN`; writes stay `ADMIN`-only.
- Keep existing Album create/update/delete using `AlbumDTO` as a 1:1 CRUD message.



### Prerequisites

- Complete Labs 1–5 in **your existing lab repository**. Continue that repo. Do not start a new GitHub project for Lab 6.
- Begin with Lab 5 complete: Spring Security, BCrypt, paged-unaware `GET /api/albums` returning a list of `AlbumDTO`, Thymeleaf login/register/admin, PostgreSQL Chinook plus `users`.
- Confirm the application starts, you can sign in, and `GET /api/albums` as a `USER` returns albums (a large JSON array is expected before Branch 1).
- Confirm Chinook’s `album`, `artist`, and `track` tables are present. You need them for joins and aggregates. You do not expose them as REST CRUD.
- Use the existing feature-branch and pull-request workflow.
- Add your instructor as a GitHub repository collaborator before Branch 1 if that access is not already in place.



### Architectural Scope

After finishing this lab, Album still supports CRUD, and it also supports a read-side query:


| Application area                           | Message type                                | Persistence                            |
| ------------------------------------------ | ------------------------------------------- | -------------------------------------- |
| Album create / update / delete / get-by-id | `AlbumDTO` (`albumId`, `title`, `artistId`) | `Album` entity, `album` table          |
| Album collection list                      | Paged `AlbumDTO`                            | `album` table only                     |
| Album query (name, counts)                 | New Record (not `Album`, not `AlbumDTO`)    | `album` + `artist` + `track` via query |
| User registration and administration       | Thymeleaf + Lab 5 security                  | `users` table                          |
| API docs                                   | Swagger UI / OpenAPI                        | Same Lab 5 rules                       |


Controllers stay thin. Services coordinate queries. Repositories execute paging, filters, joins, and aggregates. Spring Security still decides whether a request may reach those controllers. Lab 6 does not reopen Lab 5’s filter chain design.

### Entity versus query message

Keep these distinct:

- An **entity** (`Album`) is how the application persists a row. It matches a table. It is not an API contract.
- A **CRUD DTO** (`AlbumDTO`) is the message for create, update, and simple reads. In this course it happens to match `album` 1:1. That is acceptable for CRUD.
- A **query Record** is the message for a read that spans tables or includes an aggregate. It is shaped for the caller, not for Hibernate.

Do **not**:

- Put `@ManyToOne` / `@OneToMany` on `Album` and return the entity graph as JSON.
- Add `artistName` onto `AlbumDTO` and keep using it for POST/PUT.
- Call `findAll()`, then loop 372 albums to look up each artist in Java.

Do:

- Keep `AlbumDTO` for CRUD.
- Introduce a new Record for the join/aggregate query.
- Page and filter in the repository query (JPQL, Spring Data query methods, or equivalent) so PostgreSQL does the work.



### Role Authorization Policy (unchanged from Lab 5)


| Authority | `/api/**` GET | `/api/**` POST, PUT, DELETE | Swagger UI / OpenAPI       | Thymeleaf `/admin/**` |
| --------- | ------------- | --------------------------- | -------------------------- | --------------------- |
| Anonymous | Denied        | Denied                      | Denied                     | Denied                |
| `USER`    | Allowed       | Denied (403)                | Allowed (writes still 403) | Denied                |
| `ADMIN`   | Allowed       | Allowed                     | Allowed                    | Allowed               |


New query endpoints are `GET`s under `/api/**`. A `USER` must be able to call them. Do not invent a new security matrix.

### GitHub Workflow

Complete each branch in order. Each branch should contain one coherent feature and should be merged before the next branch begins.

1. Create the named feature branch.
2. Complete and test the required work for that branch only.
3. Commit with a meaningful message that explains why the change exists.
4. Push the branch to GitHub.
5. Open a pull request titled `Lab 6 PR XX: <short title>`.
6. In the pull request body, list **Done**, **Out of scope**, and **Test plan**.
7. Merge the pull request into the working branch used for the lab.
8. Pull the updated main/working branch before creating the next feature branch.

**Branch naming example:** `feature/lab6-pr01-page-albums`

**Grading note:** The instructor grades the ordered pull-request history, not only the final state of `main`. A pull request that sneaks the join and aggregate into the paging branch does not meet the lab requirements.

---



## Branch 1: Page the Album Collection

Chinook has **372** albums. Before you change anything, call `GET /api/albums` as a signed-in `USER` and **look at the payload**. That list is the problem this branch solves.

Page the existing collection resource. Keep `AlbumDTO` 1:1 with `Album`. Do not join artist yet.

Spring’s `Pageable` is not only page number and page size. It also carries `Sort`. You do not add a second sorting API. Once `GET /api/albums` takes `Pageable` and the repository calls `findAll(pageable)`, `?sort=title` is already wired. That parameter is less flexible than it looks: the value must be a Java property on `Album` (`title`, `albumId`, `artistId`), not a column alias, not artist name, and not Swagger’s type placeholder `string`. `sort=title` works; `sort=greatest` or `sort=string` does not.

That pairing is what makes a **top N**. `page=0`, `size=20`, `sort=title` is the first 20 albums in title order. Change `size` to 50 and you have a top 50 — same endpoint, no extra code. Without `sort`, page 0 is only “the first 20 rows in database order,” which is not a ranking.

Keep the names straight: `Pageable` **is the request** (`page`, `size`, `sort`). `Page` **is the response**: the slice (`content`) plus **metadata** (`totalElements`, `totalPages`, `number`, `size`, `first`, `last`, and a nested echo of the `Pageable`). Lab 5 returned a bare array, which has no metadata. Returning `Page` is what gives the client a way to navigate.

### Learning Expectations

- Why an unpaged collection is unacceptable once the table is hundreds of rows.
- How Spring `Pageable` binds `page` and `size` query parameters — the controller does not parse them.
- That `Sort` comes with `Pageable`. There is no separate “make it sortable” step for Album fields.
- That `page=0` plus `size` plus `sort` is a top N (top 20, top 50) — not a second API.
- That `JpaRepository.findAll(Pageable)` is enough: Hibernate emits `LIMIT`/`OFFSET`, an `ORDER BY` when `sort` is present, and a count query.
- That a `Page` is `content` plus **metadata**. `Pageable` is what the client sends; the metadata is how the client knows the catalog size and where this slice sits.
- That `GET /api/albums` with no query string still pages: default page 0, size 20. Swagger may mark the `Pageable` object required; the server does not.
- Why paging belongs in the repository, not in a Java `subList` after `findAll()`.
- That the `Pageable` abstraction hides SQL, not the contract: pages are 0-based, returning `Page` is what produces `content` plus `totalElements`, and `sort` must be an `Album` field (`title`, `albumId`, `artistId`), not Swagger’s `string`.



### Required Work

- Change `GET /api/albums` so it accepts paging (for example `Pageable` or explicit `page` and `size` parameters).
- Return a paged result of `AlbumDTO`. Spring Data’s `Page` JSON is acceptable.
- Default page size must be smaller than 372 (for example 20). A client may request another size.
- `totalElements` (or equivalent) must reflect the full album count, not only the current page.
- Do not implement a separate sort endpoint or parse `sort` by hand. If you used `Pageable`, try `sort=title` and confirm the order changes.
- Get-by-id, POST, PUT, and DELETE stay as they are, still using `AlbumDTO`.
- Do not add artist name, track count, or a new query Record in this branch.
- Do not load all albums and slice the list in memory.
- Update any test that assumed `GET /api/albums` returned a bare JSON array.
- Document the collection query parameters (`page`, `size`, `sort`) in Swagger and the README so a classmate can call them without reading the controller. Title filter still waits.



### Checkpoint

- Unpaged `findAll()` is no longer the collection implementation.
- `GET /api/albums` with no query string returns at most 20 albums (`@PageableDefault`). `totalElements` is still the catalog count.
- `GET /api/albums?page=0&size=20` returns at most 20 albums.
- The response includes a total count near **347–372** (your Chinook copy may differ; it must not equal the page size).
- `GET /api/albums?page=1&size=20` returns a different slice than page 0.
- `GET /api/albums?page=0&size=20&sort=title` is the first 20 by title (a top 20). `size=50` is a top 50. `sort=title,desc` reverses the ranking.
- Get-by-id and writes still work for `ADMIN`.
- Students can explain the Swagger `Pageable` object (`page`, `size`, `sort: ["string"]`), why they must replace `"string"`, that Swagger may mark it required even though a bare `GET /api/albums` is valid, and how to read `content` vs `totalElements` in the `Page` JSON.



### What to expect now

Run the application, sign in as `USER`, and **examine** Swagger or the browser/HTTP client.

Start with a bare `GET /api/albums` — no query string. `@PageableDefault(size = 20)` fills in page 0 and size 20. You should see about **20** albums in `content` and `totalElements` near 347. Swagger often draws the `Pageable` object as **required**. That is the OpenAPI schema, not the server. The HTTP API does not require `page`, `size`, or `sort`. If Swagger will not Execute without the object, you can still paste `/api/albums` in the browser after sign-in, or fill `page` 0, `size` 20, and `"sort": []`.

When you do use Swagger’s object, it looks like this **before** you Execute:

```json
{
  "page": 0,
  "size": 1,
  "sort": ["string"]
}
```

That `sort` value is the OpenAPI type placeholder. `Album` has no property named `string`, so Execute throws `PropertyReferenceException` (HTTP 500). **Fix the throw before you judge paging:** replace `"string"` with a real field (`"title"` or `"title,desc"`), or use `"sort": []` for database order. Then Execute.

You should get Spring Data `Page` JSON, not Lab 5’s bare array. The body has two parts: `content` (the albums on this page) and **metadata** (everything else — totals, page index, flags, and an echo of the `Pageable` you sent). With `page` 0, `size` 1, and `sort` `title`, it looks like this (your titles and totals will differ; this copy has 347 albums):

```json
{
  "content": [
    {
      "albumId": 156,
      "title": "...And Justice For All",
      "artistId": 50
    }
  ],
  "empty": false,
  "first": true,
  "last": false,
  "number": 0,
  "numberOfElements": 1,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 1,
    "paged": true,
    "sort": { "empty": false, "sorted": true, "unsorted": false },
    "unpaged": false
  },
  "size": 1,
  "sort": { "empty": false, "sorted": true, "unsorted": false },
  "totalElements": 347,
  "totalPages": 347
}
```

Read the metadata in this order (after `content`):

- `content` — this page’s `AlbumDTO`s. Still `albumId`, `title`, `artistId`. One row because `size` was 1. `"...And Justice For All"` is first under `sort=title` because punctuation sorts before letters.
- `totalElements` — the catalog count (here 347). Chinook is often cited near 347–372; your copy may differ. It must **not** equal `size` unless you really have only one album.
- `totalPages` — `ceil(totalElements / size)`. With `size` 1 that equals `totalElements`.
- `number` / `size` — 0-based page index and requested page size.
- `numberOfElements` — how many rows actually came back on this page (can be smaller than `size` on the last page).
- `first`, `last`, `empty` — where this page sits in the collection.
- `pageable` — echo of the request (`offset` is `page * size`). Nested `sort` tells you whether a sort was applied (`sorted: true`), not the property name. The field you typed in Swagger does not always reappear in this JSON.

Call a second page (`page` 1, same `size`) and confirm `content` changes, `number` is 1, and `totalElements` stays the same.

Then treat page 0 as a ranking: `size=20` and `sort=title` is a top 20; change `size` to 50 for a top 50. Same `Pageable`, no new endpoint.

In your PR notes, contrast this payload with the Lab 5 dump of every album. Mention that sort arrived with `Pageable`, not as extra code, that page 0 plus size plus sort is a top N, and that you had to replace Swagger’s `"string"` before the call succeeded.

Explain why slicing in Java after `findAll()` would still pull every album row from PostgreSQL.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 2: Filter Paged Albums by Title

Add an optional title filter to the paged collection. Stay on the `album` table and `AlbumDTO`. The join still waits.

On this resource, **title is the only field that is logical to filter on**. `albumId` is get-by-id, not a collection search. `artistId` is an integer FK — callers do not search “50”. Artist **name** is the filter they want, and that column is not on `album`. So Branch 2 adds one query parameter, `title`, and leaves name search for the join.

### Learning Expectations

- Why filtering must happen in the query when the table has hundreds of rows.
- How an optional request parameter combines with `Pageable`.
- Why `totalElements` after a filter is the filtered total, not 372.
- That **title filter** is the custom query (`findByTitleContainingIgnoreCase`). **Sort** is not. `Pageable` already carries `Sort` into both `findAll(pageable)` and that derived method, so you do not write `findBy…OrderByTitle`.
- Why `title` is the only collection filter on `GET /api/albums`: it is the only human-searchable column on `album`.



### Required Work

- Accept an optional query parameter such as `title`.
- When `title` is present, return only albums whose title matches (case-insensitive contains is appropriate).
- When `title` is absent, behavior matches Branch 1 (all albums, paged).
- Keep paging. A filtered result that still has many rows must not dump the rest.
- Implement the filter in the repository (query method, JPQL, or Criteria) — not `findAll()` plus a Java stream. A derived method such as `findByTitleContainingIgnoreCase(String title, Pageable pageable)` is enough. Do not add a second method just to sort.
- Do not add artist-name filtering yet. That requires the join in a later branch.
- Do not change `AlbumDTO`.



### Checkpoint

- `GET /api/albums?title=greatest&page=0&size=20` returns only matching titles, paged.
- The total in that response is the number of matches, not the full 372.
- `GET /api/albums?page=0&size=20` with no `title` still pages the full catalog.
- Students can explain why filtering after `findAll()` is not acceptable here.
- Students can explain why `sort=title` works on both the unfiltered and filtered calls even though the only custom repository method is the title contains query.



### What to expect now

- Try a title fragment you know exists in Chinook (for example `greatest` or `love`) and read both `content` and the total.
- Try a title that matches nothing and explain the empty page vs an error (empty page is the usual collection behavior).
- Explain that `artistId` in `AlbumDTO` is still just a foreign key, not a name.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 3: Join Artist Name into a Query Record

`AlbumDTO` cannot honestly carry an artist name: POST/PUT still send `artistId`. This branch introduces a **new** read model.

Map Chinook `artist` only as query infrastructure. Do **not** add `/api/artists`.

### Learning Expectations

- Why a CRUD DTO and a query message are different types.
- How a join produces a column that does not exist on `album`.
- Why related tables do not automatically become public REST resources.
- Why returning an `Album` entity with a lazy `Artist` association is not the solution.



### Required Work

- Add a persistence mapping for Chinook `artist` (`artist_id`, `name`) sufficient to join in a query. No `ArtistController`. No public artist CRUD service used as an API.
- Introduce a new Java **Record** for the query message. Required fields at this branch: album id, album title, **artist name**. Do not reuse `AlbumDTO`.
- Add a new `GET` endpoint under Album (for example `GET /api/albums/summaries`).
- Populate artist name with a **join** (JPQL, SQL, or Spring Data join). Do not N+1: one query per page, not one query per album.
- Page this endpoint from the start. Do not dump 372 summaries.
- Declare the path so Spring does not treat `summaries` as an `{albumId}` (put the specific mapping where the framework will match it, or use a path that cannot collide).
- Keep `GET /api/albums` and CRUD on `AlbumDTO` as Branches 1–2 left them.
- Do not add track count yet.
- Lab 5 security: this `GET` is allowed for `USER` and `ADMIN`.



### Checkpoint

- There is no `/api/artists` (or equivalent artist CRUD controller).
- The new endpoint’s JSON includes artist **names**, not only `artistId`.
- The type returned is a Record (or equivalent query DTO), not `Album` and not `AlbumDTO`.
- A page of summaries is smaller than 372, and the total is still about 372 when unfiltered.
- Students can explain why stuffing `artistName` onto `AlbumDTO` would break the CRUD contract.



### What to expect now

- Sign in as `USER` and open the new endpoint in Swagger. Read one page. Confirm names such as well-known Chinook artists appear.
- Compare one `AlbumDTO` (has `artistId`) with one query Record (has `artistName`).
- Explain that `artist` is in the database for this join, not because the lab added a second CRUD API.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 4: Filter the Query by Artist Name

The join now earns its keep: callers can filter on artist **name**, which is not a column on `album`.

### Learning Expectations

- Why artist-name filter cannot be a simple `Album` field match.
- How optional filters compose with a join and with `Pageable`.
- Why the filtered total is a count of matching joined rows (album grain), not a dump of tracks.



### Required Work

- Add an optional query parameter such as `artistName` on the **query** endpoint from Branch 3.
- When present, keep rows whose artist name matches (case-insensitive contains is appropriate).
- Combine with paging. Optional title filter on the same query endpoint is allowed if it stays in the database query.
- Still no track aggregate.
- Still no artist CRUD API.



### Checkpoint

- `GET /api/albums/summaries?artistName=ac/dc` (or another known Chinook name) returns only that artist’s albums, paged, each row showing the artist name.
- With no `artistName`, summaries still page the catalog.
- Filtering is not implemented by loading all summaries and dropping rows in Java.



### What to expect now

- Filter by a distinctive artist name and inspect the page total.
- Filter by a name that should miss and explain the empty page.
- Point at the query (repository method or JPQL) as the place the join and filter run together.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 5: Aggregate Track Count on the Same Query Record

Tracks belong to albums. The query message should include how many tracks each album has. Map Chinook `track` only as query infrastructure. Do **not** add `/api/tracks`.

### Learning Expectations

- What an aggregate is (`COUNT`, or `SUM` of duration) versus a join of a single parent row.
- Why the query stays at **album grain**: one Record per album, not one per track.
- Why `GROUP BY` (or an equivalent counted join) belongs in the database.



### Required Work

- Add a persistence mapping for Chinook `track` sufficient to count tracks per album (`album_id` is the link). No `TrackController`.
- Add `trackCount` (integer) to the **same** query Record from Branch 3 — not a third DTO unless you are replacing the Branch 3 Record in place.
- Albums with zero tracks still appear, with `trackCount` 0 (`LEFT JOIN` / `COUNT` behavior).
- Keep paging and the artist-name (and title, if present) filters. Totals stay album counts, not track counts.
- Do not return a list of track entities on the album query.
- Optional: you may also expose total duration (`SUM(milliseconds)`) **instead of or in addition to** `trackCount`. Track count is the required default.



### Checkpoint

- Query JSON includes `trackCount` (or documented duration) plus artist name.
- A known album’s count matches pgAdmin (or a SQL `COUNT`) for that `album_id`.
- The endpoint still returns one row per album, not one row per track.
- There is no `/api/tracks` CRUD controller.



### What to expect now

- Pick one album from the query page, count its tracks in pgAdmin, and match the JSON.
- Explain why adding `@OneToMany List<Track>` to `Album` and returning the entity would be a different (and worse) API for this lab.
- Confirm a `USER` can `GET` the query and still receives 403 on `POST /api/albums`.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 6: Prove Queries and Keep Lab 5 Security

Automated tests should lock the query contract so a later change cannot silently return entities or drop paging.

### Learning Expectations

- How to test paging metadata without depending on all 372 titles.
- How `@WithMockUser(authorities = "USER")` still applies to new `GET`s.
- Why tests should assert artist name and track count on the query Record, not only HTTP 200.



### Required Work

- Tests for the collection endpoint: a page size smaller than the total; total elements greater than that page size.
- Tests for title filter (at least one match case and one empty-result case, with a stubbed repository or a focused slice — do not require a live 372-row database if the rest of your suite is `@WebMvcTest` / mocked services).
- Tests for the query endpoint: JSON (or DTO) includes artist name and track count; paging still applies.
- Security tests: anonymous query `GET` is rejected; `USER` can `GET` the query endpoint; `USER` still cannot `POST /api/albums`; `ADMIN` can still write.
- You may extend `ApiAuthorizationTest` or add a focused test class. Keep using `authorities`, not `roles`, because the filter chain uses `hasAuthority`.



### Checkpoint

- `mvn test` passes locally.
- A reader can see tests for paging, join fields, aggregate, and the Lab 5 matrix.
- Students can explain which tests would fail if someone returned `List<Album>` again.



### What to expect now

- Run the tests and map each failure you could cause on purpose (temporarily) to a requirement above — then revert. Do not commit broken tests.
- In the PR, list which test method covers paging, which covers artist name, which covers `USER` GET vs POST.

Commit and push the branch. Open a pull request, review the change, and merge it before beginning the next branch.

---



## Branch 7: Document the Query API

Classmates (and your future self) must be able to call paging and query parameters without reading your controller source.

### Learning Expectations

- Why query parameters are part of the API contract.
- Why README demo login (`admin` / `password`) is still not the PostgreSQL password (Lab 5 Branch 13).



### Required Work

- Update the README with:
  - How to call paged `GET /api/albums` (`page`, `size`, optional `title` and `sort` — `page` / `size` / `sort` should already be there from Branch 1; add `title`).
  - How to call the query endpoint (path, `page`, `size`, `artistName`, and `trackCount` in the body).
  - That Swagger still requires login under the Lab 5 policy.
- Confirm Swagger shows the new parameters and the query Record schema.
- Do not put live datasource passwords in the README. Do not rewrite the README into a lecture on lab-track vs CLC-track.



### Checkpoint

- A classmate could page and filter from the README alone.
- Swagger lists the query endpoint after sign-in.
- Committed properties still have no live cloud database password.



### What to expect now

- Sign in, open Swagger, and walk the query endpoint from the documented parameters.
- Skim `git` for `application.properties` and confirm secrets stayed out.

Commit and push the branch. Open a pull request, review the change, and merge it.

---



## Verification

1. Start the Lab 6 application with Lab 5 security active.
2. As a `USER`, show that unauthenticated API access is still denied.
3. As a `USER`, call paged `GET /api/albums` and show a page size smaller than the total (~372).
4. Filter by title and show the total drop to the match count.
5. Call the query endpoint and show **artist name** (not only `artistId`) on a Record that is not `AlbumDTO`.
6. Filter that query by artist name.
7. Show `trackCount` (or documented duration) and match one album against pgAdmin or SQL.
8. As a `USER`, show `POST /api/albums` still returns 403.
9. As an `ADMIN`, show an Album write still succeeds.
10. Show there is no public artist or track CRUD API in Swagger.
11. Run the tests and show they pass.
12. In the video or written explanation, contrast `AlbumDTO` (CRUD, 1:1 with `album`) with the query Record (join + aggregate).

---



## Deliverables



### 1. GitHub Repository (40%)

- All seven feature branches, or an instructor-approved equivalent that preserves the same incremental history (paging before join before aggregate).
- Pull requests and merge history in order.
- Meaningful commit messages.
- Instructor added as a collaborator.
- README documents paging and query parameters.
- No committed real production passwords, API keys, or cloud database secrets.



### 2. Video Demonstration (40%)

- The 372-row problem: a paged collection with a total much larger than the page.
- Title filter on the collection.
- Query Record with artist name from a join.
- Artist-name filter on the query.
- Track count (or duration) matched to the database for one album.
- No artist/track CRUD in Swagger.
- `USER` can GET queries; `USER` cannot POST albums; `ADMIN` can write.
- An explicit explanation of entity vs CRUD DTO vs query Record.



### 3. AI References (20%)

- Prompt or prompts used.
- AI system and model.
- Date used.
- Summary of how the response influenced the work.
- Explanation of what the student reviewed, corrected, or rejected.

---



## Honors Branch

This is required for honors students, optional for everyone else.

### Total duration

Add `totalMilliseconds` (or a formatted duration) using `SUM` on `track.milliseconds`, still one row per album.


Be prepared to present this to the class.
---



## Study Guide — Lab 6

This study guide emphasizes why Lab 5’s 1:1 `AlbumDTO` was not the end of the DTO story.

### Paging

Spring’s `Pageable` is the paging API — and it already includes **sort**. The controller does not parse `page`, `size`, or `sort`. Spring binds `?page=1&size=20&sort=title` into one `Pageable`. `JpaRepository` already has `findAll(Pageable)`, so Hibernate emits `LIMIT`/`OFFSET`, an `ORDER BY` when sort is present, plus a count query. That count becomes `totalElements` (~372), not the page size.

You do not add a second “sortable collection” feature for Album fields. `Sort` arrived with paging. Together they give you a **top N**: `page=0`, `size=20`, `sort=title` is the first 20 in that order; `size=50` is a top 50. Without `sort`, the first page is only database order, not a ranking.

`sort=value` is less flexible than it looks. The value is an `Album` property path (`title`, `albumId`, `artistId`), not free text and not a SQL column alias. `sort=title` works. `sort=greatest` does not. Artist name and `trackCount` are not `Album` fields, so they cannot ride this parameter.

The abstraction hides SQL. It does not hide the contract. Students still have to know that pages are **0-based**, that returning `Page` is what produces `content` plus `totalElements`, and that `sort` must name a real `Album` field (`title`, `albumId`, `artistId`), optionally with a direction (`title,desc`).

Swagger UI documents `Pageable` as an object: `{ "page": 0, "size": 1, "sort": ["string"] }`. It often marks that object **required**. The server does not: `GET /api/albums` with no query string is page 0, size 20, unsorted (`@PageableDefault`). `content` then has at most 20 albums; `totalElements` is still the catalog. The `sort` entry is the OpenAPI type placeholder. Students must fix that before Execute: use `"title"` (or `"title,desc"`), or `"sort": []`. Leaving `"string"` throws `PropertyReferenceException`.

The HTTP body is a Spring Data `Page`, not a JSON array. A `Page` is **content plus metadata**. `Pageable` is the request (`page`, `size`, `sort`). The metadata is the rest of the JSON: `totalElements` (catalog count — often about 347 or 372, never “whatever `size` was”), `number` (0-based page), `size` (requested page size), plus orientation fields (`first` / `last` / `empty`, `numberOfElements`, `totalPages`) and a nested `pageable` that echoes the request. Nested `sort` reports `sorted: true` or `false`; it usually does **not** repeat the property name you typed. Lab 5’s array had none of this.

Students should be able to explain:

1. Why 372 albums makes an unpaged `GET` a dump.
2. How `Pageable` binds `page`, `size`, and `sort` so the controller does not parse those parameters itself.
3. Why `Sort` is not extra work on `GET /api/albums` once the method takes `Pageable`.
4. Why `page=0` plus `size` plus `sort` is a top N (top 20 or top 50), and why that is not a new endpoint.
5. Why Swagger’s `sort: ["string"]` must be edited before Execute, and why a required-looking `Pageable` object does not mean `GET /api/albums` needs query parameters.
6. That a `Page` is content plus metadata, and that `Pageable` is the request, not the metadata block.
7. What `content`, `totalElements`, `number`, and `size` mean, and why `totalElements` is not `content.length`.
8. What `first`, `last`, `totalPages`, and nested `pageable` add, and why nested `sort` may omit the field name.
9. Why paging must be executed by PostgreSQL, not by `findAll()` plus `subList`.
10. Why Spring Data pages are often zero-based.
11. Why returning `Page` (not `List`) is what gives the client that JSON shape.
12. Why sorting by `trackCount` on the query Record is a different problem than sorting by `title` on `Album`.



### Filtering

Students should be able to explain:

1. Why optional query parameters must not require a new endpoint for every combination.
2. Why the filtered total is the match count.
3. Why title is the only logical collection filter on `GET /api/albums` (`albumId` is get-by-id; `artistId` is a FK integer). Why title filter can stay on `album`, but artist-name filter cannot.
4. Why the only custom repository method is the title contains query, and why `sort=title` still works: `Pageable` applies `ORDER BY` to `findAll` and to `findByTitleContainingIgnoreCase`. Sort is not a second derived method.
5. Why a PostgreSQL index on `album_id` or `artist_id` is not why `sort=albumId` works. An index can make `ORDER BY` cheaper. It does not create a Spring Data method. `sort=title` also needs no custom method, indexed or not. The derived method exists because “contains, ignore case” is a `WHERE` shape `JpaRepository` does not already have.



### Entity, CRUD DTO, and query Record

Students should be able to explain:

1. `Album` is persistence. `AlbumDTO` is the CRUD message. The summary Record is a query message.
2. Why `artistId` belongs on `AlbumDTO` for POST/PUT, while `artistName` belongs on the query Record.
3. Why adding fields onto `AlbumDTO` until it matches every screen is how APIs rot.
4. Why this lab forbids returning JPA entity graphs as JSON.



### Joins and aggregates

Students should be able to explain:

1. Artist name requires a join to `artist`.
2. Track count requires an aggregate over `track`, grouped by album.
3. Album grain means one JSON object per album, even after joining tracks.
4. Related tables can exist in JPA without becoming `/api/artists` and `/api/tracks`.



### Security

Students should be able to explain:

1. Lab 6 query endpoints are still `/api/**` GETs under the Lab 5 matrix.
2. A new GET does not justify weakening POST/PUT/DELETE rules.
3. Swagger try-outs for writes still fail for `USER`.



### Common Failure Points

Students should be prepared to diagnose:

1. `GET /api/albums/summaries` handled by `/{albumId}` because the path variable mapping wins.
2. `totalElements` equal to page size because the query was limited without a separate count.
3. Returning `List<AlbumDTO>` after a paged query, so the JSON is a bare array again and has no `totalElements`.
4. Parsing `page`, `size`, or `sort` as raw parameters instead of taking Spring’s `Pageable`.
5. `No property 'string' found for type 'Album'` because Swagger’s `Pageable` object shipped `sort: ["string"]`. Replace `"string"` with `title` (or `title,desc`) or use `"sort": []`. Do not treat sort as a later feature you still have to code for `Album`.
6. Adding a separate “sort albums” endpoint after paging, instead of noticing `Sort` already arrived with `Pageable`.
7. Claiming `sort=albumId` needs no custom method *because* `album_id` is indexed. Indexes are storage. `Pageable` is why `ORDER BY` appears. The title contains method exists for the `WHERE`, not because `title` lacks an index.
8. Artist names loaded in a loop (N+1) after paging albums.
9. `LazyInitializationException` or huge JSON from serializing entity associations.
10. `AlbumDTO` modified to carry `artistName`, then POST failed or ignored the extra field.
11. `findAll()` plus Java `filter`/`skip`/`limit` presented as paging.
12. A new `ArtistController` “for convenience.”
13. Query `GET` accidentally requiring `ADMIN` because it was not under `GET /api/**`.
14. Tests using `@WithMockUser(roles = "USER")` while the app checks `hasAuthority("USER")`.

---

