.PHONY: start stop restart db app clean

# Start everything
start: db app

# Start the database and wait for it
db:
	@echo "Starting database..."
	@docker compose up -d
	@echo "Waiting for MySQL to be ready..."
	@until docker exec travel-reservation-db mysqladmin ping -uroot -ppassword123 --silent 2>/dev/null; do sleep 1; done
	@echo "Database is ready."

# Build and run the Java app
app:
	@echo "Building and running Java app..."
	@./mvnw -q compile exec:java -Dexec.mainClass="org.group40.ProjectFrame"

# Stop the database
stop:
	@docker compose down

# Wipe DB volume and restart everything (re-runs init.sql)
restart:
	@docker compose down -v
	@docker compose up -d

# Clean Maven build files
clean:
	@./mvnw -q clean