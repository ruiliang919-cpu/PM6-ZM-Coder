# Security Review Report

**Project:** PM6-ZM-Coder (DC Lighting Monitoring System)
**Framework:** RuoYi-Vue-Plus v4.8.0 / Spring Boot 2.7.13 / Vue 2.6.12 / Electron 13
**Review Date:** 2026-05-23
**Risk Level:** HIGH

---

## Summary

| Severity | Count |
|----------|-------|
| CRITICAL | 4     |
| HIGH     | 12    |
| MEDIUM   | 14    |
| LOW      | 4     |

**Overall:** This project has several critical and high-severity vulnerabilities that should be addressed immediately. The most pressing issues are the weak JWT secret key, unauthenticated IoT control endpoints (SaIgnore on WriteController), non-expiring authentication tokens, and hardcoded credentials in configuration files.

## CRITICAL Issues (Fix Immediately)

### 1. Weak JWT Secret Key -- Trivially Crackable

**Severity:** CRITICAL
**Category:** A02: Cryptographic Failures
**Location:** `server/ruoyi-admin/src/main/resources/application.yml:126` and `server/ruoyi-admin/src/main/resources/application.copy:131`
**Exploitability:** Remote, unauthenticated. Attacker needs only one valid JWT token to extract the signing key and forge tokens for any user, including admin.
**Blast Radius:** Full application compromise -- attacker can forge valid JWTs for any user (including admin), gaining complete access to the management system and IoT device control.

**Issue:** The JWT signing key is set to `abcdefghijklmnopqrstuvwxyz`, a trivially guessable string. This key is used to sign all JWT tokens for the application. Anyone who obtains a single valid JWT can brute-force the key and forge tokens for arbitrary users.

**Remediation:**
```yaml
# BAD (application.yml:126)
sa-token:
  jwt-secret-key: abcdefghijklmnopqrstuvwxyz

# GOOD
sa-token:
  jwt-secret-key: ${JWT_SECRET_KEY}  # Set via environment variable, min 256 bits
```
Generate a strong key: `openssl rand -base64 64` and store it as an environment variable `JWT_SECRET_KEY`. Never commit the secret to source control. Immediately rotate this key and invalidate all existing tokens.

---

### 2. Entire IoT Write Controller is Unauthenticated (SaIgnore)

**Severity:** CRITICAL
**Category:** A01: Broken Access Control
**Location:** `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/WriteController.java:54`
**Exploitability:** Remote, unauthenticated. Any network-accessible client can call all endpoints on this controller.
**Blast Radius:** Unauthenticated control of all lighting devices, DC cabinets, scene controls, time controls, and power modules. An attacker could turn lights on/off, change voltage values, reconfigure entire lighting infrastructure, or cause physical damage to hardware.

**Issue:** The entire `WriteController` class is annotated with `@SaIgnore`, which bypasses Sa-Token authentication. This means every endpoint under `/zm/write/**` is accessible without any authentication token. All 60+ endpoints in this controller accept requests from anyone with network access.

The controller exposes:
- Group/zone lighting control (switch, brightness)
- Scene control (enter/modify scenes)
- Time-based control configuration
- Infrared/illuminance sensor configuration
- AC/DC output voltage control
- Module mode selection
- Firmware time synchronization

**Remediation:**
```java
// BAD (WriteController.java:54)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write")
@SaIgnore   // <-- THIS MUST BE REMOVED
public class WriteController {
    // ...
}

// GOOD
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/zm/write")
// Remove @SaIgnore entirely. Authentication is enforced by Sa-Token interceptor.
// Add @SaCheckPermission on individual methods if fine-grained control is needed.
public class WriteController {
    // ...
}
```
If certain endpoints genuinely need public access (doubtful for write operations), mark only those specific methods with @SaIgnore, not the entire class.

---

### 3. Authentication Tokens Never Expire

**Severity:** CRITICAL
**Category:** A07: Identification and Authentication Failures
**Location:** `server/ruoyi-admin/src/main/resources/application.yml:111-114`
**Exploitability:** Remote. A leaked/stolen token remains valid indefinitely.
**Blast Radius:** Once an attacker obtains a token (via XSS, network sniffing, or log leakage), they have permanent access with no forced re-authentication.

**Issue:** Both `timeout` and `active-timeout` are set to `-1`, meaning tokens never expire and have no idle timeout. A stolen token grants permanent access.

**Remediation:**
```yaml
# BAD (application.yml:111-114)
sa-token:
  timeout: -1          # Never expires
  active-timeout: -1   # Never expires from inactivity

# GOOD
sa-token:
  timeout: 86400       # 24 hours maximum lifetime
  active-timeout: 1800 # 30 minutes idle timeout
```

---

### 4. Multiple SaIgnore Endpoints Across ZM/IoT Controllers

**Severity:** CRITICAL
**Category:** A01: Broken Access Control
**Location:**
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/WriteController.java:54` (class-level, ~60 endpoints)
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/WriteController.java:211` (updateZoneLightSwitch1)
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/SensorModuleController.java:20` (class-level)
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/RecoverController.java:24` (class-level, including password check endpoint)
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/PointController.java:64,77,91`
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/MethodTimeController.java:19`
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/LeakageController.java:16`
- `server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/BasicInformationController.java:108`

**Exploitability:** Remote, unauthenticated. Combined hundreds of endpoints across ~8 controllers are publicly accessible.
**Blast Radius:** Unauthenticated access to read sensor data, device configuration information, fault records, power data, leakage detection, and infrastructure metadata. Combined with the WriteController, this provides both read and write access to the entire IoT infrastructure without authentication.

**Remediation:** Remove @SaIgnore from all IoT controller classes unless there is a documented, legitimate reason for public access. For any endpoint that must remain public, document the business justification, limit to GET-only operations, and add rate limiting.
## HIGH Issues (Fix Within 1 Week)

### 5. Hardcoded Database Credentials in Configuration Files

**Severity:** HIGH
**Category:** A02: Cryptographic Failures / A05: Security Misconfiguration
**Location:**
- application-prod.yml:24-25 -- DB user zm_root, password Huiyeda98315
- application-create.yml:59-60 -- DB user root, password 123456
- xxl-job-admin application-prod.yml:18-19 -- DB user root, password root
- application-prod.yml:69-70 -- recover password 123456

**Exploitability:** Local file access or source code repository access reveals database credentials.
**Blast Radius:** Full database access for an attacker who obtains these credentials. The application-create.yml uses root/123456 which is trivially guessable even without file access.

**Remediation:** Remove all hardcoded default password values from Spring property placeholders. Use environment variables with no defaults. Example: username: ${DB_USERNAME} (no default value after colon).

---

### 6. Hardcoded MQTT Broker Credentials

**Severity:** HIGH
**Category:** A02: Cryptographic Failures
**Location:** application-prod.yml:3-4 and application-create.yml:3-4 -- MQTT username admin, password Hyd@qwe
**Exploitability:** Local file access or source code repository access.
**Blast Radius:** Unauthorized access to the MQTT broker, enabling an attacker to publish malicious messages to IoT devices or subscribe to all telemetry data.
**Remediation:** Use environment variables MQTT_USERNAME and MQTT_PASSWORD with no hardcoded defaults.

---

### 7. Hardcoded Redis Password in Configuration

**Severity:** HIGH
**Category:** A02: Cryptographic Failures
**Location:** application-create.yml:102 -- Redis password ciH4N2
**Blast Radius:** Unauthorized access to Redis cache storing device state, fault records, sensor data, and IoT command queues.
**Remediation:** Use ${REDIS_PASSWORD} environment variable with no hardcoded default.

---

### 8. Hardcoded Default Login Credentials in Client Code

**Severity:** HIGH
**Category:** A07: Identification and Authentication Failures
**Location:** web/src/views/login.vue:79-80
**Issue:** Login form pre-populated with admin/admin123. These are baked into the distributed application binary/web assets.
**Remediation:** Set loginForm defaults to empty strings. Force password change on first login.

---

### 9. Electron App: nodeIntegration Enabled, contextIsolation Disabled

**Severity:** HIGH
**Category:** A05: Security Misconfiguration
**Location:** web/src/background.js:50-52, web/.env.development:22, web/.env.production:20
**Blast Radius:** Remote code execution on users machine. Any XSS in the Vue app becomes full RCE.
**Issue:** nodeIntegration: true, contextIsolation: false, enableRemoteModule: true. This is the most dangerous Electron configuration possible.
**Remediation:** Set nodeIntegration: false, contextIsolation: true, enableRemoteModule: false, sandbox: true. Move Node.js API calls to preload script using contextBridge.exposeInMainWorld().

---

### 10. MySQL Connections Use SSL=false and allowPublicKeyRetrieval=true

**Severity:** HIGH
**Category:** A02: Cryptographic Failures
**Location:** application-prod.yml:23,30 and application-create.yml:58,66
**Issue:** All JDBC URLs use useSSL=false&allowPublicKeyRetrieval=true. DB traffic in cleartext, weaker auth mode susceptible to MITM.
**Remediation:** Enable useSSL=true, requireSSL=true, verifyServerCertificate=true. Remove allowPublicKeyRetrieval=true.

---

### 11. MQTT Communication Over Plain TCP (No TLS)

**Severity:** HIGH
**Category:** A02: Cryptographic Failures
**Location:** application-prod.yml:2 (tcp://127.0.0.1:1883), application-create.yml:2 (tcp://39.108.173.227:1883 -- PUBLIC IP!)
**Blast Radius:** All MQTT messages in cleartext. application-create.yml exposes broker on public internet.
**Remediation:** Use ssl:// protocol with TLS. Move the public-IP MQTT broker behind a firewall immediately.

---

### 12. Weak Password Recovery Mechanism

**Severity:** HIGH
**Category:** A07: Identification and Authentication Failures
**Location:** application-prod.yml:69-70 (recover.password: 123456), RecoverController.java:24-27 (SaIgnore password endpoint)
**Issue:** Recovery password is 123456, stored in plaintext, checked via unauthenticated endpoint.
**Blast Radius:** Brute-force attack (6 digits) can trigger device data migration/reading commands.
**Remediation:** Require authentication, use bcrypt hash for recovery password, add rate limiting, use constant-time comparison.

---

### 13. Database Seed Contains Weak Default Passwords

**Severity:** HIGH
**Category:** A07: Identification and Authentication Failures
**Location:** database/zm.sql:12763 (default password 123456), database/zm.sql:14362 (test user with password 666666 in comment)
**Remediation:** Remove default password config. Force password change on first login. Remove test accounts from prod seed. Do not store passwords in comments.

---

### 14. Demo Encryption Code Contains Hardcoded Keys

**Severity:** HIGH
**Category:** A02: Cryptographic Failures
**Location:** server/ruoyi-demo/src/main/java/com/ruoyi/demo/domain/TestDemoEncrypt.java:17-26
**Issue:** Hardcoded AES password 10rfylhtccpuyke5 and RSA/SM2 key pairs in demo source. Risk of copy-paste into production code.
**Remediation:** Replace with placeholder values and add warning comments.

---

### 15. Spring Boot Actuator Endpoints Fully Exposed

**Severity:** HIGH
**Category:** A05: Security Misconfiguration
**Location:** application.yml:236-239 and SecurityConfig.java:35-37
**Issue:** All actuator endpoints exposed (include: *) and excluded from auth. Sensitive endpoints like /heapdump, /env, /configprops leak internal state.
**Remediation:** Restrict to health,info,metrics. Require authentication on actuator endpoints.

---

### 16. Sa-Token Using Release Candidate Version

**Severity:** HIGH
**Category:** A06: Vulnerable and Outdated Components
**Location:** server/pom.xml:27 -- satoken.version: 1.35.0.RC
**Issue:** RC version in production. May contain unresolved security bugs.
**Remediation:** Upgrade to latest stable GA release of Sa-Token.


## MEDIUM Issues (Fix Within 1 Month)

### 17. application.copy Leftover Config File with Same Weak JWT Secret
**Severity:** MEDIUM | **Category:** A05: Security Misconfiguration
**Location:** server/ruoyi-admin/src/main/resources/application.copy:131
**Issue:** A backup/copy configuration file exists with the same weak JWT secret. Redundant config files increase the attack surface.
**Remediation:** Delete this file immediately.

### 18. XXL-JOB Admin Has Hardcoded Default Credentials
**Severity:** MEDIUM | **Category:** A07: Identification and Authentication Failures
**Location:** server/ruoyi-extend/ruoyi-xxl-job-admin/src/main/resources/application-prod.yml:9-10 -- username ruoyi, password 123456
**Remediation:** Use environment variables for XXL-JOB admin credentials.

### 19. CSRF Protection Disabled in Monitor Admin
**Severity:** MEDIUM | **Category:** A01: Broken Access Control
**Location:** server/ruoyi-extend/ruoyi-monitor-admin/src/main/java/com/ruoyi/monitor/admin/config/SecurityConfig.java:45
**Issue:** .csrf().disable() is called. CSRF should be enabled for the form-login-based admin panel even if API endpoints are stateless.
**Remediation:** Enable CSRF protection or explicitly document and justify why it is disabled.

### 20. Password Policy is Ineffective
**Severity:** MEDIUM | **Category:** A07: Identification and Authentication Failures
**Location:** server/ruoyi-admin/src/main/resources/application.yml:60-63
**Issue:** maxRetryCount: 999 with lockTime: 1 minute. Attacker can attempt 999 passwords before a 1-minute lockout -- effectively no brute-force protection.
**Remediation:** Set maxRetryCount: 5, lockTime: 15 (minutes).

### 21. Redisson 3.20.1 Has Known Vulnerabilities
**Severity:** MEDIUM | **Category:** A06: Vulnerable and Outdated Components
**Location:** server/pom.xml:33 -- redisson.version: 3.20.1
**Issue:** CVE-2023-42809 (Deserialization of Untrusted Data) can lead to RCE.
**Remediation:** Upgrade redisson to latest stable (3.23.x or newer).

### 22. Spring Boot 2.7.x is End of Life
**Severity:** MEDIUM | **Category:** A06: Vulnerable and Outdated Components
**Location:** server/pom.xml:17 -- spring-boot.version: 2.7.13
**Issue:** EOL since November 2023. No further security patches.
**Remediation:** Plan migration to Spring Boot 3.x (requires javax-to-jakarta migration, Java 17+).

### 23. Vue 2.6.12 is End of Life
**Severity:** MEDIUM | **Category:** A06: Vulnerable and Outdated Components
**Location:** web/package.json:34 -- vue: 2.6.12
**Issue:** Vue 2 EOL December 31, 2023. No further security patches.
**Remediation:** Plan migration to Vue 3.

### 24. Electron 13 is End of Life with Known Critical CVEs
**Severity:** MEDIUM | **Category:** A06: Vulnerable and Outdated Components
**Location:** web/package.json:51 -- electron: ^13.0.0
**Issue:** EOL since November 2021. Multiple critical Chromium CVEs (CVE-2021-37973, CVE-2021-30632, etc.).
**Remediation:** Upgrade to Electron 28+ (latest stable). Update electron-devtools-installer and vue-cli-plugin-electron-builder to compatible versions.

### 25. RecoverController Endpoints Exposed Without Authentication
**Severity:** MEDIUM | **Category:** A01: Broken Access Control
**Location:** server/ruoyi-admin/src/main/java/com/ruoyi/web/controller/zm/RecoverController.java:24-40
**Issue:** Entire RecoverController is @SaIgnore. password() endpoint reveals whether guessed password is correct. issued() and read() trigger device operations.
**Remediation:** Require authentication on all recover endpoints.

### 26. Passwords Stored in Browser Cookies
**Severity:** MEDIUM | **Category:** A04: Insecure Design
**Location:** web/src/views/login.vue:144-146
**Issue:** Remember me feature stores encrypted password in browser cookie. Even encrypted, cookies are included in every request and can be stolen via XSS.
**Remediation:** Use a server-issued remember-me token instead of storing the password in cookies.

### 27. Cookie Removal Bug in Login Logic
**Severity:** MEDIUM | **Category:** A04: Insecure Design
**Location:** web/src/views/login.vue:148-150
**Issue:** When remember me is unchecked, the Electron branch removes username for all three cookies instead of username, password, and rememberMe respectively. Passwords persist in Electron app cookies even when user opts out.
**Remediation:** Fix the cookie names in the remove calls to match the set calls.

### 28. No CORS Configuration Found
**Severity:** MEDIUM | **Category:** A01: Broken Access Control
**Issue:** No CORS configuration found. Default Spring Boot behavior may allow cross-origin requests, enabling CSRF attacks.
**Remediation:** Add explicit CORS configuration with a restrictive allowlist of origins.

### 29. Inconsistent Password Storage (Plaintext Recovery Password)
**Severity:** MEDIUM | **Category:** A02: Cryptographic Failures
**Location:** application-prod.yml:70 and RecoverController.java:22
**Issue:** User passwords use bcrypt but recovery password is plaintext in config and compared with String.equals() (non-constant-time).
**Remediation:** Hash recovery password with bcrypt. Use constant-time comparison.

### 30. app Protocol Registered with secure: true
**Severity:** MEDIUM | **Category:** A05: Security Misconfiguration
**Location:** web/src/background.js:15
**Issue:** The app protocol is registered with secure: true, telling Chromium to treat it like HTTPS, but content loads from local filesystem. Provides false sense of security.
**Remediation:** Standard for Electron but ensure preload script properly sanitizes all IPC channels.


## LOW Issues (Backlog)

### 31. Logger Level Set to DEBUG in Production
**Severity:** LOW | **Category:** A09: Security Logging and Monitoring Failures
**Location:** server/ruoyi-admin/src/main/resources/application.yml:53
**Issue:** com.ruoyi: debug logging may expose sensitive info (tokens, device data, request params).
**Remediation:** Set to info or warn in production profiles.

### 32. ELECTRON_NODE_INTEGRATION = true in Both .env Files
**Severity:** LOW | **Category:** A05: Security Misconfiguration
**Location:** web/.env.development:22 and web/.env.production:20
**Issue:** Confirms the insecure Electron config is intentional for production.
**Remediation:** Remove these lines after fixing background.js configuration.

### 33. No Content-Security-Policy Header
**Severity:** LOW | **Category:** A05: Security Misconfiguration
**Issue:** No CSP header configuration found. CSP helps mitigate XSS by restricting resource loading.
**Remediation:** Add Content-Security-Policy header, especially important for the Electron app.

### 34. Potential ArrayIndexOutOfBounds in WriteController
**Severity:** LOW | **Category:** A04: Insecure Design (Defense in Depth)
**Location:** WriteController.java (multiple array accesses without bounds checking)
**Issue:** Array access without bounds checking could throw exceptions and leak internal state in error responses.
**Remediation:** Add bounds checking before array access. Ensure error responses do not leak stack traces.

---

## Dependency Vulnerability Summary

| Component | Current Version | Risk | Notes |
|-----------|----------------|------|-------|
| Spring Boot | 2.7.13 | MEDIUM | EOL since Nov 2023 |
| Electron | 13.0.0 | MEDIUM | EOL, multiple critical Chromium CVEs |
| Redisson | 3.20.1 | MEDIUM | CVE-2023-42809 (RCE via deserialization) |
| Sa-Token | 1.35.0.RC | HIGH | Release candidate, not stable GA |
| Vue | 2.6.12 | MEDIUM | EOL since Dec 2023 |
| SnakeYAML | 1.33 | LOW | Explicitly pinned for CVE fix; verify if newer needed |
| OkHttp | 4.10.0 | LOW | Several versions behind latest (4.12.x) |
| jsencrypt | 3.0.0-rc.1 | LOW | Release candidate version |
| Axios | 0.24.0 | LOW | Multiple major versions behind (latest 1.x) |
| Element UI | 2.15.13 | LOW | Vue 2 only; no migration path to Vue 3 |
| echarts | 5.4.0 | LOW | Current is 5.5.x |

Note: No package-lock.json or yarn.lock file was found in the web/ directory. Run npm audit to check for known vulnerabilities in the full transitive dependency tree.

---

## Security Checklist

### Immediate (CRITICAL)
- [ ] Rotate JWT secret key -- generate 256+ bit random key, store as env var
- [ ] Remove @SaIgnore from WriteController class (entire /zm/write/**)
- [ ] Remove @SaIgnore from SensorModuleController, RecoverController, other IoT controllers
- [ ] Set sa-token timeout to 86400 (24h) and active-timeout to 1800 (30min)

### This Week (HIGH)
- [ ] Remove all hardcoded password defaults from Spring config placeholders
- [ ] Move MQTT, DB, Redis credentials to environment variables only
- [ ] Remove default admin/admin123 from login.vue
- [ ] Set nodeIntegration: false, contextIsolation: true in Electron
- [ ] Enable SSL for MySQL connections, remove allowPublicKeyRetrieval
- [ ] Enable TLS for MQTT broker (ssl:// protocol)
- [ ] Move public-IP MQTT broker (39.108.173.227) behind firewall
- [ ] Upgrade Sa-Token from 1.35.0.RC to latest stable GA
- [ ] Restrict actuator endpoints to health, info, metrics
- [ ] Add authentication on actuator endpoints in monitor-admin
- [ ] Hash recovery password with bcrypt

### This Month (MEDIUM)
- [ ] Delete application.copy file
- [ ] Strengthen password policy (maxRetryCount: 5, lockTime: 15)
- [ ] Upgrade Redisson to 3.23.x+
- [ ] Fix cookie removal bug in login.vue
- [ ] Add CORS configuration
- [ ] Add rate limiting on login and recover endpoints
- [ ] Run npm audit on web/ directory
- [ ] Plan Spring Boot 2.7 -> 3.x migration
- [ ] Plan Vue 2 -> Vue 3 migration
- [ ] Plan Electron 13 -> 28+ upgrade
- [ ] Use remember-me tokens instead of storing password in cookies

### Backlog (LOW)
- [ ] Set production logging to INFO/WARN
- [ ] Add Content-Security-Policy header
- [ ] Add bounds checking in WriteController array access
- [ ] Consider Modbus TCP encryption (IPsec/VPN tunnel)

---

## Files With Security Issues Found

| File | Issues |
|------|--------|
| application.yml | Weak JWT key, token never expires, weak password policy, actuator exposure, debug logging |
| application.copy | Same weak JWT key (delete this file) |
| application-prod.yml | Hardcoded DB/MQTT/Recover credentials, useSSL=false, plain TCP MQTT |
| application-create.yml | Hardcoded DB(root/123456)/MQTT/Redis credentials, public IP MQTT |
| WriteController.java | @SaIgnore on entire class, @SaIgnore on individual methods |
| SensorModuleController.java | @SaIgnore on entire class |
| RecoverController.java | @SaIgnore, plaintext password, no rate limiting |
| PointController.java | @SaIgnore on 3 methods |
| MethodTimeController.java | @SaIgnore on class |
| LeakageController.java | @SaIgnore on class |
| BasicInformationController.java | @SaIgnore on 1 method |
| login.vue | Hardcoded admin credentials, password in cookies, cookie removal bug |
| background.js | nodeIntegration:true, contextIsolation:false, enableRemoteModule:true |
| .env.development | ELECTRON_NODE_INTEGRATION=true |
| .env.production | ELECTRON_NODE_INTEGRATION=true |
| SecurityConfig.java (monitor) | CSRF disabled, actuator paths unauthenticated |
| TestDemoEncrypt.java | Hardcoded encryption keys (AES/RSA/SM2) in demo code |
| zm.sql | Default password 123456, test accounts with known passwords in comments |
| application-prod.yml (xxl-job) | Hardcoded DB root/root, hardcoded admin ruoyi/123456 |
| pom.xml | Sa-Token RC, Redisson 3.20.1, Spring Boot 2.7 EOL |
| package.json | Vue 2.6 EOL, Electron 13 EOL, outdated dependencies |
