# Database Startup Failure Simulation

This document explains the implementation of database connectivity failure that causes Kubernetes startup probe failures.

## Implementation Overview

Three components work together to simulate realistic database connection failures during application startup:

### 1. DatabaseStartupCheck.java (NEW)
- **Type**: `@Startup` health check
- **Endpoint**: `/health/started`
- **Behavior**: 
  - Adds 15-second delay per check attempt
  - Returns DOWN status (database unreachable)
  - Checked by Kubernetes startup probe

### 2. DatabaseReadinessCheck.java (MODIFIED)
- **Type**: `@Readiness` health check
- **Endpoint**: `/health/ready`
- **Behavior**:
  - Adds 30-second delay per check attempt
  - Returns DOWN status (database unreachable)
  - Prevents pod from receiving traffic

### 3. SystemConfig.java (MODIFIED)
- **Type**: Application initialization bean
- **Behavior**:
  - Adds 10-second delay during `@PostConstruct`
  - Sets `initialized = false` when database check fails
  - Affects all health checks that depend on initialization

## How Startup Probe Failure Occurs

### Timeline Example:
```
T+0s:   Application starts
T+0s:   SystemConfig.init() begins (10s delay)
T+10s:  SystemConfig initialization fails (database unreachable)
T+15s:  First startup probe check → DatabaseStartupCheck (15s delay)
T+30s:  Returns DOWN → Probe fails (attempt 1/30)
T+35s:  Second startup probe check (5s interval)
T+50s:  Returns DOWN → Probe fails (attempt 2/30)
...
T+150s: After 30 failed attempts → Pod killed and restarted
```

### Kubernetes Configuration
```yaml
startupProbe:
  httpGet:
    path: /health/started
    port: 9080
  initialDelaySeconds: 5
  periodSeconds: 5
  failureThreshold: 30  # 30 attempts × 5s = 150s max startup time
  timeoutSeconds: 35    # Must be > 30s to allow delay to complete
```

## Total Delays Per Check Cycle

1. **SystemConfig initialization**: 10 seconds (one-time)
2. **DatabaseStartupCheck**: 15 seconds per probe attempt
3. **DatabaseReadinessCheck**: 30 seconds per probe attempt

## Health Check Endpoints

MicroProfile Health 4.0 provides three endpoints:

- `/health/started` - Startup checks (DatabaseStartupCheck)
- `/health/ready` - Readiness checks (DatabaseReadinessCheck, SystemHealth)
- `/health/live` - Liveness checks (none currently)

## Testing Locally

### Build and run:
```bash
mvn clean package
mvn liberty:run
```

### Check health endpoints:
```bash
# Startup check (will take 15+ seconds and return DOWN)
curl http://localhost:9080/health/started

# Readiness check (will take 30+ seconds and return DOWN)
curl http://localhost:9080/health/ready

# All health checks
curl http://localhost:9080/health
```

### Expected Response:
```json
{
  "status": "DOWN",
  "checks": [
    {
      "name": "DatabaseStartupCheck",
      "status": "DOWN",
      "data": {
        "database": "unreachable",
        "reason": "Cannot connect to database during startup - connection timeout",
        "status": "DOWN",
        "action": "Startup probe will fail, pod will be restarted"
      }
    }
  ]
}
```

## Reverting to Normal Operation

To restore normal operation (database reachable):

1. **DatabaseStartupCheck.java**: Change `return false;` to `return true;` (line 58)
2. **DatabaseReadinessCheck.java**: Change `return false;` to `return true;` (line 50)
3. **SystemConfig.java**: Change `return false;` to `return true;` (line 42)
4. **Optional**: Remove or reduce `Thread.sleep()` delays

## Real-World Implementation

In production, replace the simulated delays with actual database connection attempts:

```java
private boolean isDatabaseAvailable() {
    try (Connection conn = dataSource.getConnection()) {
        // Test connection with a simple query
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
        }
        return true;
    } catch (SQLException e) {
        System.err.println("Database connection failed: " + e.getMessage());
        return false;
    }
}
```

## Key Points

- **Startup probe** validates application is ready to start (one-time check)
- **Readiness probe** validates application can serve traffic (continuous)
- **Liveness probe** validates application is still running (continuous)
- Failed startup probe → Pod restart
- Failed readiness probe → Pod removed from service endpoints
- Failed liveness probe → Pod restart

## Monitoring

Watch for these log messages:
- `SystemConfig: Starting initialization...`
- `SystemConfig: Initialization failed - database not reachable`
- `DatabaseStartupCheck: Attempting database connection...`
- `DatabaseStartupCheck: Database connection attempt failed`
- `DatabaseReadinessCheck: Attempting to connect to database...`
- `DatabaseReadinessCheck: Database connection attempt timed out`