# Lab 4 API

This repository contains a Spring Boot web application built around the Chinook-style data model. It provides a REST-style API layer, server-rendered Thymeleaf pages, and user-management features such as registration, login, password changes, and admin user controls.

## What this project includes

- Spring Boot 4 web application with MVC and Thymeleaf views
- Responsive Bootstrap-based navigation for desktop and mobile layouts
- User authentication-related pages:
  - Login
  - Registration
  - Change password
  - User administration
- User management actions such as:
  - viewing users
  - changing roles
  - toggling enabled/disabled state
  - deleting users
- OpenAPI documentation through SpringDoc
- PostgreSQL database integration

## Project structure

- `src/main/java` - application code, controllers, services, repositories, and domain models
- `src/main/resources/templates` - Thymeleaf HTML templates
- `src/main/resources/static` - static assets such as CSS and the home page
- `src/main/resources/application.properties` - committed application configuration (no live database passwords)
- `src/main/resources/application-local.properties.example` - template for local datasource settings
- `src/test/java` - test classes for the application

## Prerequisites

- Java 21
- Maven
- A PostgreSQL database

## Running the application

From the project root, run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

The application will start on port `8080` by default.

## Database configuration

Do not put live database passwords or cloud connection strings in committed files.

**Application login** (Thymeleaf / Spring Security) is not the PostgreSQL server password. The seeded demo account is:

- username: `admin`
- password: `password`

**Datasource credentials** come from one of these local sources (in order of typical use):

1. Copy `src/main/resources/application-local.properties.example` to `src/main/resources/application-local.properties` and fill in your PostgreSQL URL, username, and password. That file is gitignored. The default Spring profile is `local`, so this file is loaded automatically.
2. Or set environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`

Committed `application.properties` only contains a local PostgreSQL example (`jdbc:postgresql://localhost:5432/chinook`).

## Main routes

- `/` - home page
- `/login` - login page
- `/register` - registration page
- `/change-password` - password change page
- `/admin/users` - user administration page

## API documentation

Once the app is running and you are signed in, Swagger UI is at:

- `/swagger-ui/index.html`

Swagger requires a signed-in user (Lab 5). Demo login is `admin` / `password`. That is not the PostgreSQL password.

### `GET /api/albums` query parameters

All four are optional. Omit them for page 0, size 20, unsorted. A `USER` or `ADMIN` may call this; anonymous requests are redirected to login.

| Parameter | Default | Meaning |
| --- | --- | --- |
| `page` | `0` | 0-based page index |
| `size` | `20` | Albums per page |
| `sort` | none | Album field: `title`, `albumId`, or `artistId`. Add `,desc` to reverse. Do not send Swagger’s placeholder `string`. |
| `title` | none | Case-insensitive contains filter on album title. `totalElements` is then the match count, not the full catalog. |

`page=0` with `size` and `sort` is a **top N**. Examples:

```
GET /api/albums
GET /api/albums?page=0&size=20&sort=title
GET /api/albums?page=0&size=50&sort=title,desc
GET /api/albums?page=1&size=20
GET /api/albums?title=greatest&page=0&size=20
```

The response is a Spring Data `Page`: `content` (this page’s albums) plus metadata (`totalElements`, `totalPages`, `number`, `size`, `first`, `last`). `totalElements` is the catalog count, not the page size.

## Testing

Run the test suite with:

```bash
./mvnw test
```

## Notes

If the application cannot connect to PostgreSQL, confirm `application-local.properties` exists (or the `SPRING_DATASOURCE_*` environment variables are set). Do not commit that local file.
