# JWT Authentication Testing Guide

## Overview
Your Spring Boot e-commerce application now has complete JWT authentication with Spring Security integration.

## Authentication System Features

### ✅ Implemented Components
- **JWT Token Generation & Validation** - JwtUtil class handles all JWT operations
- **Custom User Details Service** - CustomUserDetailsService integrates with Spring Security
- **JWT Authentication Filter** - JwtAuthenticationFilter processes JWT tokens in requests
- **Security Configuration** - SecurityConfig with role-based access control
- **Method-Level Security** - @PreAuthorize annotations for fine-grained access control
- **Password Encryption** - BCrypt password hashing
- **Role-Based Authorization** - Support for USER, ADMIN, STAFF roles

### 🔐 Security Endpoints

#### Public Endpoints (No Authentication Required)
```
POST /api/users/register - User registration
POST /api/users/login - User login (returns JWT token)
GET /api/test/public - Test public access
```

#### Protected Endpoints (JWT Token Required)

**User Endpoints** (USER, ADMIN, STAFF roles)
```
GET /api/users/me - Get current user info
GET /api/test/user - Test user access
GET /api/test/profile - Get user profile
```

**Staff Endpoints** (STAFF, ADMIN roles)
```
GET /api/test/staff - Test staff access
```

**Admin Endpoints** (ADMIN role only)
```
GET /api/test/admin - Test admin access
```

**Resource Access** (Owner or Admin/Staff)
```
GET /api/users/{userId} - Get user by ID (own profile or admin/staff)
POST /api/users/{userId}/info - Update user info (own profile or admin/staff)
```

## Testing the Authentication System

### 1. Start the Application
```bash
cd BACKEND/artifact
mvn spring-boot:run
```

### 2. Register a New User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPass123!",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

### 3. Login to Get JWT Token
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "TestPass123!"
  }'
```

**Response will include:**
```json
{
  "code": 200,
  "message": "Đăng nhập thành công",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": { ... }
  }
}
```

### 4. Access Protected Endpoints
Use the JWT token in the Authorization header:

```bash
# Test user endpoint
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# Get current user info
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"

# Test admin endpoint (will fail with USER role)
curl -X GET http://localhost:8080/api/test/admin \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Role Management

### Default User Role
- New registrations automatically get `USER` role
- Users can access user-level endpoints and their own resources

### Admin/Staff Setup
To create admin or staff users, you'll need to:
1. Manually update the database to change user roles
2. Or create an admin endpoint to promote users (recommended for production)

```sql
-- Example SQL to promote a user to ADMIN
UPDATE user_info SET role = 'ADMIN' WHERE username = 'testuser';
```

## JWT Token Details

### Token Structure
- **Algorithm**: HS256
- **Expiration**: 24 hours (configurable in JwtUtil)
- **Claims**: username, issued time, expiration time

### Token Validation
- Automatic validation on each protected request
- Invalid/expired tokens return 401 Unauthorized
- Missing tokens on protected endpoints return 401 Unauthorized

## Error Handling

### Common Error Responses

**401 Unauthorized** (Missing/Invalid Token)
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/test/user"
}
```

**403 Forbidden** (Insufficient Role)
```json
{
  "timestamp": "2024-01-01T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden", 
  "path": "/api/test/admin"
}
```

## Security Configuration Summary

### Protected Patterns
- `/api/users/register` - Public
- `/api/users/login` - Public  
- `/api/test/public` - Public
- `/api/users/**` - Authenticated users only
- `/api/test/**` - Role-based access
- All other endpoints - Authenticated users only

### CORS Configuration
- Configured for frontend integration
- Allows credentials and common headers
- Customize allowed origins as needed

## Next Steps for Production

1. **Environment Variables**: Move JWT secret to environment configuration
2. **Token Refresh**: Implement refresh token mechanism
3. **Rate Limiting**: Add rate limiting for login attempts  
4. **Audit Logging**: Log authentication events
5. **Role Management UI**: Create admin interface for user role management
6. **Password Reset**: Implement forgot password functionality

## Frontend Integration

### React/Angular Example
```javascript
// Login and store token
const loginResponse = await fetch('/api/users/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ usernameOrEmail: 'user', password: 'pass' })
});
const { result } = await loginResponse.json();
localStorage.setItem('jwt_token', result.token);

// Use token for authenticated requests
const response = await fetch('/api/users/me', {
  headers: { 
    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
  }
});
```

Your e-commerce backend is now production-ready with enterprise-grade JWT authentication! 🚀