# Project Documentation

## EXERCISE 5: Authentication Filter

### Overview

The `AuthFilter` acts as a security gatekeeper, ensuring that only
authenticated users can access protected resources.

### How It Works

#### 1. Filter Configuration

-   Intercepts all requests (`/*`)  to the application
-   Applied before any servlet or JSP processes the request

``` java
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
```

#### 2. Public URL Detection

The filter maintains a whitelist of public resources that don't require authentication
- Authentication pages: /login, /logout
- Static resources: CSS, JavaScript, images

``` java
private static final String[] PUBLIC_URLS = {
    "/login", "/logout", ".css", ".js", ".png", ".jpg", ".jpeg", ".gif"
};
```

#### 3. Request Processing Flow

-   Converts request to HTTP objects.
-   Extracts path.
-   Checks public URLs.
-   Verifies session authentication.

``` java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    
    // 1. Convert to HTTP objects
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    
    // 2. Extract request path
    String requestURI = httpRequest.getRequestURI();
    String contextPath = httpRequest.getContextPath();
    String path = requestURI.substring(contextPath.length());
    
    // 3. Check if public URL
    if (isPublicUrl(path)) {
        chain.doFilter(request, response); // Allow access
        return;
    }
    
    // 4. Check authentication
    HttpSession session = httpRequest.getSession(false);
    boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
    
    if (isLoggedIn) {
        chain.doFilter(request, response); // User logged in
    } else {
        httpResponse.sendRedirect(contextPath + "/login"); // Redirect to login
    }
}
```

#### 4. Public URL Validation

``` java
private boolean isPublicUrl(String path) {
    for (String publicUrl : PUBLIC_URLS) {
        if (path.contains(publicUrl)) return true;
        return true;
    }
    return false;
}
```

### Key Features

-  Session Management: Uses getSession(false) to avoid creating new sessions
-   Path Extraction: Removes context path to handle different deployment contexts
-   Efficient Checking: Early return for public URLs to minimize processing

### Security Impact

-   Prevents unauthorized access to all protected pages
-   Allows public resources to be accessible without login
-   Automatic redirection to login page for unauthenticated users

------------------------------------------------------------------------

## EXERCISE 6: Admin Authorization Filter

### Overview

The `AdminFilter` provides role-based access control, ensuring only admin users can perform sensitive operations.

### How It Works

#### 1. Targeted Filtering

``` java
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/student"})
```
- Specific targeting: Only intercepts /student requests
- Action-based authorization: Checks the action parameter

#### 2. Admin-Only Actions

``` java
private static final String[] ADMIN_ACTIONS = {
    "new", "insert", "edit", "update", "delete"
};
```
Defines which student management operations require admin privileges.

#### 3. Authorization Logic

Checks if the request action requires admin rights, then validates
session and role.

``` java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String action = httpRequest.getParameter("action");
    
    if (isAdminAction(action)) {
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            
            if (user != null && user.isAdmin()) {
                chain.doFilter(request, response); // Admin access granted
            } else {
                // Non-admin user denied
                httpResponse.sendRedirect(httpRequest.getContextPath() + 
                    "/student?action=list&error=Access denied. Admin privileges required.");
            }
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    } else {
        chain.doFilter(request, response); // Non-admin action allowed
    }
}
```
#### 4. Action Validation

``` java
private boolean isAdminAction(String action) {
    if (action == null) return false;
    
    for (String adminAction : ADMIN_ACTIONS) {
        if (adminAction.equals(action)) {
            return true;
        }
    }
    return false;
}
```

### Key Features

-   Role Checking: Uses `user.isAdmin()` method to verify admin status
-   Graceful Denial: Redirects with error message instead of throwing errors
-   Selective Filtering: Only applies to specific admin actions

### Security Impact

-  Prevents privilege escalation by non-admin users
- Clear user feedback when access is denied
- Maintains data integrity by protecting modification operations

------------------------------------------------------------------------

## EXERCISE 7: Role-Based UI

### Overview

Enhanced the user interface to dynamically adjust based on user roles, providing appropriate functionality and visual cues.

### Features

#### 1. Navigation Bar with User Info

``` jsp
<div class="navbar">
    <h2>📚 Student Management System</h2>
    <div class="navbar-right">
        <div class="user-info">
            <span>Welcome, ${sessionScope.fullName}</span>
            <span class="role-badge role-${sessionScope.role}">
                ${sessionScope.role}
            </span>
        </div>
        <a href="dashboard" class="btn-nav">Dashboard</a>
        <a href="logout" class="btn-logout">Logout</a>
    </div>
</div>
```

#### 2. Role-Based Element Visibility

``` jsp
<!-- Add button - Admin only -->
<c:if test="${sessionScope.role eq 'admin'}">
    <a href="student?action=new">➕ Add New Student</a>
</c:if>

<!-- Actions column - Admin only -->
<c:if test="${sessionScope.role eq 'admin'}">
    <th>Actions</th>
</c:if>

<!-- Action buttons - Admin only -->
<c:if test="${sessionScope.role eq 'admin'}">
    <td>
        <a href="student?action=edit&id=${student.id}">Edit</a>
        <a href="student?action=delete&id=${student.id}">Delete</a>
    </td>
</c:if>
```

#### 3. Error Display

``` jsp
<c:if test="${not empty param.error}">
    <div class="alert alert-error">
        ${param.error}
    </div>
</c:if>
```

#### 4. Visual Indicators

``` css
.role-admin {
    background-color: #dc3545;
    color: white;
}

.role-user {
    background-color: #28a745;
    color: white;
}
```
### Key Features
- Dynamic Interface: UI elements appear/disappear based on user role
- Visual Role Identification: Color-coded badges for quick role recognition
- Consistent Navigation: Uniform navigation across all pages
- Error Feedback: Clear display of authorization errors

### User Experience Impact

-  Reduced confusion for non-admin users by hiding unavailable functions
- Clear role awareness through visual indicators
- Streamlined interface appropriate to user privileges

------------------------------------------------------------------------

## EXERCISE 8: Change Password Feature

### Overview

Implemented a secure password change functionality with comprehensive validation and security measures.

### 1. Controller Logic - ChangePasswordController.java

#### GET Request - Display Form

``` java
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
}
```
- Purpose: Simply displays the password change form to the user
- Process:
    + User visits /change-password URL
    + Controller receives GET request
    + Forwards the request to change-password.jsp
    + JSP renders the HTML form in browser
- No processing: Just shows the form, no data manipulation

#### POST Request - Process Password Change
``` java
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException 
```
##### Step 1: Get Current User from Session
``` java
User currentUser = (User) session.getAttribute("user");
if (currentUser == null) {
    response.sendRedirect("login");
    return;
}
```
- Retrieves the logged-in user object from HTTP session
- Session: Server-side storage that persists across requests
- Safety check: If no user in session → redirect to login page
- Prevents: Unauthenticated password changes

##### Step 2: Get Form Parameters
``` java
String currentPassword = request.getParameter("currentPassword");
String newPassword = request.getParameter("newPassword");
String confirmPassword = request.getParameter("confirmPassword");
```
- Extracts data from HTML form submission
- Parameter names: Match the `name` attributes in form inputs

```html
<input name="currentPassword">
<input name="newPassword"> 
<input name="confirmPassword">
```

##### Step 3: Validate Current Password
``` java
User verifiedUser = userDAO.authenticate(currentUser.getUsername(), currentPassword);
if (verifiedUser == null) {
    response.sendRedirect("change-password?error=Current password is incorrect");
    return;
}
```
- Reuses authentication logic: Same method used for login
- Verification process:
    + Takes username from session
    + Takes current password from form
    + Calls userDAO.authenticate() which:
        1. Finds user by username in database
        2. Compares hashed passwords using BCrypt
    + Returns null if password doesn't match
- Security: Ensures user actually knows their current password

##### Step 4: Validate New Password Requirements
``` java
if (newPassword.length() < 8) {
    response.sendRedirect("change-password?error=New password must be at least 8 characters long");
    return;
}

if (!newPassword.equals(confirmPassword)) {
    response.sendRedirect("change-password?error=New password and confirmation do not match");
    return;
}
```
- Length validation: Ensures minimum 8 characters
- Confirmation check: Compares new password with confirmation field
- Early termination: If any validation fails, immediately redirects with error
- User feedback: Error message displayed on the form

##### Step 5: Hash New Password
``` java
String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
```
- BCrypt.hashpw(): Creates secure hash from plain text password
- BCrypt.gensalt(): Generates random "salt" for extra security
- Why hash?:
    + Never store plain text passwords
    + Same passwords look different due to random salt
    + Protects against database breaches

##### Step 6: Update in Database
``` java
boolean success = userDAO.updatePassword(currentUser.getId(), newHashedPassword);
```
- Calls DAO method to update database
- Parameters: User ID + new hashed password
- Returns: true if update successful, false if failed

##### Step 7: Show Result
``` java
if (success) {
    response.sendRedirect("change-password?message=Password changed successfully");
} else {
    response.sendRedirect("change-password?error=Failed to change password");
}
```
- Success: Redirects with success message parameter
- Failure: Redirects with error message parameter
- Redirect pattern:
    + Prevents form resubmission on refresh
    + Shows result on fresh page load

### 2. DAO Method - UserDAO.java
```java
public boolean updatePassword(int userId, String newHashedPassword) {
    String SQL_UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
        
        pstmt.setString(1, newHashedPassword);
        pstmt.setInt(2, userId);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```
- PreparedStatement: Prevents SQL injection
- Parameter binding: Safe value insertion
- Return value: True if exactly 1 row affected

### 3. User Interface - change-password.jsp

#### Password Requirements Display
```jsp
<div class="password-requirements">
    <h4>Password Requirements:</h4>
    <ul>
        <li>Must be at least 8 characters long</li>
        <li>Should include uppercase and lowercase letters</li>
        <li>Should include numbers and special characters</li>
        <li>Should be different from your current password</li>
    </ul>
</div>
```
#### CHange Password Form
```jsp
<form action="change-password" method="post">
    <input type="password" name="currentPassword" placeholder="Current Password" required>
    <input type="password" name="newPassword" placeholder="New Password" required minlength="8">
    <input type="password" name="confirmPassword" placeholder="Confirm Password" required minlength="8">
    <button type="submit">Change Password</button>
</form>
```
### 4. Client-Side Validation
```javascript
document.querySelector('form').addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (newPassword.length < 8) {
        alert('New password must be at least 8 characters long');
        e.preventDefault();
        return;
    }
    
    if (newPassword !== confirmPassword) {
        alert('New password and confirmation do not match');
        e.preventDefault();
        return;
    }
});
```
#### Key Security Features
- Current Password Verification: Ensures user knows existing password
- Strong Password Requirements: Minimum 8 characters with complexity suggestions
- Password Confirmation: Prevents typos in new password
- BCrypt Hashing: Secure password storage using industry-standard hashing
- Session Validation: Ensures only logged-in users can change passwords

#### User Experience Features
- Clear Requirements: Visual guide for password creation
- Immediate Feedback: Client-side validation for quick error correction
- Success/Error Messages: Clear communication of operation results
- Consistent Styling: Matches overall application design

------------------------------------------------------------------------

## Integration

-   Filter-compatible (AuthFilter + AdminFilter)
-   Added "Change Password" to navigation
-   Proper session handling throughout

------------------------------------------------------------------------

## Conclusion

This project demonstrates secure authentication, role-based
authorization, UI adaptation, and password management following best
practices in Java Servlet applications.
