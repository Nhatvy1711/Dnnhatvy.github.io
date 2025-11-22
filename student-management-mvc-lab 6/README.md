# Authentication System Implementation

## 1. Overview

The authentication system provides secure user login, session
management, and role-based access control for the Student Management
System. It follows MVC architecture with security measures including
password hashing and session protection.

## 2. System Architecture

### 2.1 Component Structure

    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │    View     │ ←→ │ Controller  │ ←→ │    Model    │
    │   (JSP)     │    │  (Servlet)  │    │ (DAO/Bean)  │
    └─────────────┘    └─────────────┘    └─────────────┘
                            │
                            └───────→ Database

### 2.2 Data Flow

-   User Access → login.jsp\
-   Form Submission → LoginController\
-   Authentication → UserDAO\
-   Session Creation → Store User\
-   Role Redirect → Dashboard / Student List

## 3. Core Components
Purpose: Data representation layer for user information
### 3.1 User Model

Encapsulation: Private fields with public getters/setters

Timestamp Tracking: createdAt and lastLogin for audit trails

Role Management: String-based roles ("admin", "user")

Utility Methods: isAdmin(), isUser() for easy role checking

Security Implementation:

java
// Password is never exposed in toString() for security
@Override
public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", fullName='" + fullName + '\'' +  // Password excluded
            ", role='" + role + '\'' +
            '}';
}

### 3.2 UserDAO
Purpose: Handles all database operations with security focus

Security Features:

3.2.1 Password Hashing with BCrypt
java
// Password Storage - Hashing
String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

// Password Verification
if (BCrypt.checkpw(password, hashedPassword)) {
    // Authentication successful
}
BCrypt Advantages:

Salt Integration: Automatic salt generation and storage

Adaptive Hashing: Computationally expensive to prevent brute force

Collision Resistance: Same password produces different hashes

3.2.2 SQL Injection Prevention
java
// Using PreparedStatement instead of concatenation
PreparedStatement pstmt = conn.prepareStatement(SQL_AUTHENTICATE);
pstmt.setString(1, username);  // Parameter binding
3.2.3 Authentication Process
java
public User authenticate(String username, String password) {
    // 1. Query database for active user
    // 2. Retrieve stored hash
    // 3. Verify with BCrypt.checkpw()
    // 4. Update last_login on success
    // 5. Return User object or null
}

### 3.3 Login Controller
Purpose: Orchestrates the login process with security measures

Security Implementation:

3.3.1 Session Management
java
// Prevent session fixation attack
HttpSession oldSession = request.getSession(false);
if (oldSession != null) {
    oldSession.invalidate();  // Destroy old session
}

// Create new secure session
HttpSession session = request.getSession(true);
session.setMaxInactiveInterval(30 * 60);  // 30-minute timeout
3.3.2 Authentication Flow
Input Validation: Check for empty credentials

Database Authentication: Call UserDAO.authenticate()

Session Creation: Store user data in new session

Role-based Redirect: Admin → Dashboard, User → Student List

### 3.4 Dashboard Controller
Purpose: Protected page that requires authentication
Access Control:

java
// Session validation in every protected controller
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("user") == null) {
    response.sendRedirect("login");  // Force login
    return;
}

## 4. Security Details

### 4.1 Password Security

Hashing Algorithm: BCrypt (industry standard)

Salt Generation: Automatic with BCrypt.gensalt()

Verification: BCrypt.checkpw() for secure comparison

Storage: Only hashed passwords in database
### 4.2 Session Security

Session Regeneration: New session ID after login

Timeout Management: 30-minute inactivity limit

Session Fixation Prevention: Invalidate old sessions

Secure Storage: User object in session, not individual attributes

### 4.3 Input Validation

// Server-side validation (never trust client-side only)
if (username == null || username.trim().isEmpty() ||
    password == null || password.trim().isEmpty()) {
    request.setAttribute("error", "Username and password are required");
    // Return to login with error
}

### 4.4 SQL Injection Prevention

Prepared Statements: All database queries use parameter binding

No String Concatenation: Avoid dynamic SQL construction

## 5. User Interface

### 5.1 Login Page

Security Features:

POST Method: Credentials not visible in URL

Error Handling: Secure error message display

Input Retention: Username preserved on failed attempts

No Password Display: Always masked input

User Experience:

Professional Design: Builds trust with users

Clear Feedback: Specific error messages

Accessibility: Proper labels and focus management

### 5.2 Dashboard

-   Role-based UI control via JSTL
<c:if test="${sessionScope.role eq 'admin'}">
    <a href="student?action=new" class="action-btn">
        ➕ Add New Student  <!-- Only admins see this -->
    </a>
</c:if>

## 6. Database Schema

    CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- BCrypt hash (60 chars)
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin','user') NOT NULL DEFAULT 'user',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);
## 7. Error Handling
### 7.1 AUthentication Failures
Invalid Credentials: "Invalid username or password"

Inactive Account: Implicit rejection (user not informed specifically)

Empty Fields: "Username and password are required"

## 8. Testing and Vertification
### 8.1 Test cases

Valid Login: Correct credentials → Redirect to dashboard

Invalid Login: Wrong credentials → Error message

Empty Fields: No input → Validation error

Session Timeout: Inactive period → Redirect to login

Direct Access: Protected URLs without login → Redirect

### 8.2 Security Testing
SQL Injection Attempts: Should be blocked by prepared statements

Session Hijacking: Prevented by session regeneration

Brute Force: BCrypt slows down multiple attempts

## 9. Deployment Considerations
### 9.1 Production Security
HTTPS: Encrypt credentials in transit

Strong BCrypt Work Factor: Increase computation cost

Remove Demo Credentials: From login page

Database Security: Secure database credentials

