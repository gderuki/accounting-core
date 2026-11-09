## Requirements

JDK 25 (`JAVA_HOME` and `PATH` configured), Docker Desktop with Linux containers.
Maven 3.10.0 is provided by the wrapper. Spring Boot 4.1.2 manages dependency versions,
including JUnit 6 required by Spring Framework 7.

## Run (PowerShell)

```powershell
docker compose up -d --wait
./mvnw.cmd spring-boot:run
```

Check health in another terminal:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```