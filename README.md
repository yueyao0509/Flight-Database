# CS336 Travel Reservation System 

## Due May 5th

A Java Swing application with a MySQL database for user authentication, built for CS336 Group 40.

## Prerequisites

Make sure you have the following installed:

- [Java 21](https://adoptium.net/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Getting Started

1. **Clone the repo**
   ```bash
   git clone <repo-url>
   cd CS336-Project
   ```

2. **Start everything** (database + app)
   ```bash
   make start
   ```

   That's it! The Makefile starts the MySQL container, waits for it to be ready, then builds and launches the Java app.

## Makefile Commands

| Command | Mac/Linux | Windows |
|---|---|---|
| Start database + app | `make start` | `./run.bat start` |
| Start only the database | `make db` | `./run.bat db` |
| Build and run the app | `make app` | `./run.bat app` |
| Stop the database | `make stop` | `./run.bat stop` |
| Wipe DB and start fresh | `make restart` | `./run.bat restart` |
| Clean Maven build files | `make clean` | `./run.bat clean` |

## Database Info

The MySQL database runs in Docker on **port 3307**. Default credentials:

- **Host:** `localhost:3307`
- **Database:** `project-db`
- **User:** `root`
- **Password:** `password123`

A default admin account is created on first run via `init.sql`:
- **Username:** `admin`
- **Password:** `admin123`

## Troubleshooting

- **"Unable to create a connection to the database"** — Make sure Docker Desktop is running and run `make db` first.
- **Tables not loading** — Run `make restart` to wipe the volume and re-run `init.sql`.
- **Port 3307 in use** — Stop any other MySQL instances or change the port in `docker-compose.yml` and `ProjectFrame.java`.