AI Powered Quiz Application

## QuizApp

Spring Boot application for creating, running, and participating in real‑time quizzes. Supports JWT authentication, Google OAuth2 login, REST APIs for quiz and session management, and WebSocket (SockJS/STOMP) for real‑time updates.

### Features
- **Authentication**: JWT-based login; optional Google OAuth2.
- **Quiz management**: Create quizzes with questions and options.
- **Sessions**: Start sessions, advance questions, collect answers.
- **Participants**: Join via a session code and answer questions in real time.
- **WebSocket**: Broadcast quiz events to connected clients.

### Tech stack
- **Java**: 21
- **Spring Boot**: 3.5.x (Web, Security, OAuth2 Client, Data JPA, WebSocket, Validation)
- **Database**: PostgreSQL
- **Build**: Maven Wrapper (`mvnw`/`mvnw.cmd`)
- **Auth/JWT**: `jjwt`
- **Utilities**: Lombok


### Prerequisites
- JDK 21+
- PostgreSQL 13+
- Git (optional)

### Quick start
1) Clone and open the project
```
git clone <your-repo-url>
cd BackendProd/login
```

2) Create a PostgreSQL database
```
createdb quizapp
```

3) Configure application properties
Create `src/main/resources/application.properties` (if missing) with your local settings. Example:
```
spring.application.name=quizapp

spring.datasource.url=jdbc:postgresql://localhost:5432/quizapp
spring.datasource.username=postgres
spring.datasource.password=CHANGE_ME
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
app.SECRET_KEY=CHANGE_ME_LONG_RANDOM
app.jwtExpirationMs=72000000

# OAuth2 (Google) – optional
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
spring.security.oauth2.client.provider.google.token-uri=https://oauth2.googleapis.com/token
spring.security.oauth2.client.provider.google.user-info-uri=https://openidconnect.googleapis.com/v1/userinfo
app.oauth2.redirectUri=http://localhost:5173/oauth2/success

# Server
server.port=8080

# WebSocket (SockJS)
spring.websocket.sockjs.transport-types=websocket,xhr-polling,xhr-streaming
spring.websocket.sockjs.heartbeat-time=25000
spring.websocket.sockjs.disconnect-delay=5000
```

4) Run the app
- Windows:
```
cd login
.\u006dmvnw.cmd spring-boot:run
```
- macOS/Linux:
```
cd login
./mvnw spring-boot:run
```

5) Build a runnable jar
```
cd login
./mvnw -DskipTests clean package
java -jar target/quizapp-0.0.1-SNAPSHOT.jar
```


