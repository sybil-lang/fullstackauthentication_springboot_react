# Authify API Documentation

Base URL:

```text
http://localhost:8080/api/v1.0
```

For protected APIs, send JWT using either:

```http
Authorization: Bearer <token>
```

or the `jwt` cookie returned from `/login`.

## Common Error Response

For most service errors, the API returns this structure:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "Error message"
}
```

For invalid or missing JWT token:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token"
}
```

For invalid or expired JWT token:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired JWT token"
}
```

## 1. Register

Endpoint:

```http
POST /register
```

Authentication:

```text
Not required
```

Request Body:

```json
{
  "name": "Charu Prabha",
  "email": "charuprabha051424@gmail.com",
  "password": "123456"
}
```

Success Response:

Status:

```text
201 Created
```

Body:

```json
{
  "userId": "generated-user-id",
  "name": "Charu Prabha",
  "email": "charuprabha051424@gmail.com",
  "isAccountVerified": false
}
```

Failure Responses:

Email already exists:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 409,
  "error": "409 CONFLICT",
  "message": "Email already exists"
}
```

Validation messages:

```text
Name is required
Email is required
Enter a valid email
Password is required
Password must be at least 6 characters
```

## 2. Login

Endpoint:

```http
POST /login
```

Authentication:

```text
Not required
```

Request Body:

```json
{
  "email": "charuprabha051424@gmail.com",
  "password": "123456"
}
```

Success Response:

Status:

```text
200 OK
```

Body:

```json
{
  "email": "charuprabha051424@gmail.com",
  "token": "jwt-token"
}
```

Response Header:

```http
Set-Cookie: jwt=<jwt-token>; Path=/; Max-Age=604800; HttpOnly; SameSite=Strict
```

Failure Responses:

Wrong email or password:

```json
{
  "error": true,
  "message": "Email or password is incorrect"
}
```

Disabled account:

```json
{
  "error": true,
  "message": "Account is disabled"
}
```

Other login failure:

```json
{
  "error": true,
  "message": "Failure reason"
}
```

Validation messages:

```text
Email is required
Enter valid email
Password is required
```

## 3. Send Account Verification OTP

Endpoint:

```http
POST /send-otp
```

Authentication:

```text
Required
```

Important:

```text
This API does not use email from request body. It sends OTP to the logged-in user's email from the JWT.
```

Request Body:

```json
{}
```

Success Response When OTP Is Sent:

Status:

```text
200 OK
```

Body:

```json
{
  "success": true,
  "otpSent": true,
  "email": "charuprabha051424@gmail.com",
  "message": "OTP sent successfully to your registered email"
}
```

Success Response When Account Is Already Verified:

Status:

```text
200 OK
```

Body:

```json
{
  "success": true,
  "otpSent": false,
  "email": "charuprabha051424@gmail.com",
  "message": "Account is already verified. OTP was not sent."
}
```

Frontend Usage:

```js
if (response.data.success && response.data.otpSent) {
  // OTP email was sent
}

if (response.data.success && !response.data.otpSent) {
  // Account is already verified
}
```

Failure Responses:

User not found:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 500,
  "error": "500 INTERNAL_SERVER_ERROR",
  "message": "404 NOT_FOUND \"User not found\""
}
```

Unable to send OTP email:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 500,
  "error": "500 INTERNAL_SERVER_ERROR",
  "message": "500 INTERNAL_SERVER_ERROR \"Unable to send OTP email\""
}
```

Missing or invalid JWT:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token"
}
```

## 4. Verify Account OTP

Endpoint:

```http
POST /verify-otp
```

Authentication:

```text
Required
```

Important:

```text
This API verifies OTP for the logged-in user's email from the JWT.
```

Request Body:

```json
{
  "otp": "257323"
}
```

Success Response:

Status:

```text
200 OK
```

Body:

```json
{
  "success": true,
  "verified": true,
  "email": "charuprabha051424@gmail.com",
  "message": "Email verified successfully"
}
```

Frontend Usage:

```js
if (response.data.success && response.data.verified) {
  // Email verified successfully
}
```

Failure Responses:

OTP missing:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "OTP is required"
}
```

Invalid OTP:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "Invalid OTP"
}
```

Expired OTP:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "OTP expired"
}
```

User not found:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 500,
  "error": "500 INTERNAL_SERVER_ERROR",
  "message": "404 NOT_FOUND \"User not found\""
}
```

Missing or invalid JWT:

```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token"
}
```

## 5. Send Reset Password OTP

Endpoint:

```http
POST /send-reset-otp?email=charuprabha051424@gmail.com
```

Authentication:

```text
Not required
```

Request Body:

```json
{}
```

Request Param:

```text
email=charuprabha051424@gmail.com
```

Success Response:

Status:

```text
200 OK
```

Body:

```text
No response body currently.
```

Frontend Usage:

```js
if (response.status === 200) {
  // Reset OTP email sent
}
```

Failure Responses:

User not found:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 404,
  "error": "404 NOT_FOUND",
  "message": "User not found"
}
```

Unable to send reset OTP email:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 500,
  "error": "500 INTERNAL_SERVER_ERROR",
  "message": "Unable to send reset OTP email"
}
```

Missing `email` request parameter:

```text
400 Bad Request
```

## 6. Reset Password

Endpoint:

```http
POST /reset-password
```

Authentication:

```text
Not required
```

Request Body:

```json
{
  "email": "charuprabha051424@gmail.com",
  "otp": "257323",
  "newPassword": "123456"
}
```

Success Response:

Status:

```text
200 OK
```

Body:

```text
No response body currently.
```

Frontend Usage:

```js
if (response.status === 200) {
  // Password reset successfully
}
```

Failure Responses:

User not found:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 404,
  "error": "404 NOT_FOUND",
  "message": "User not found"
}
```

Reset OTP does not match:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "Reset OTP does not match"
}
```

OTP expired:

```json
{
  "timestamp": "2026-05-29T11:30:00.000",
  "status": 400,
  "error": "400 BAD_REQUEST",
  "message": "OTP expired"
}
```

Validation messages:

```text
Email is required
Invalid email format
OTP is required
New password is required
Password must be at least 6 characters
```
