package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Show change password form
        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // TODO: Get current user from session
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }
        
        // TODO: Get form parameters (currentPassword, newPassword, confirmPassword)
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // TODO: Validate current password
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            response.sendRedirect("change-password?error=Current password is required");
            return;
        }
        
        // Verify current password
        User verifiedUser = userDAO.authenticate(currentUser.getUsername(), currentPassword);
        if (verifiedUser == null) {
            response.sendRedirect("change-password?error=Current password is incorrect");
            return;
        }
        
        // TODO: Validate new password (length, match)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            response.sendRedirect("change-password?error=New password is required");
            return;
        }
        
        if (newPassword.length() < 8) {
            response.sendRedirect("change-password?error=New password must be at least 8 characters long");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect("change-password?error=New password and confirmation do not match");
            return;
        }
        
        // TODO: Hash new password
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        
        // TODO: Update in database
        boolean success = userDAO.updatePassword(currentUser.getId(), newHashedPassword);
        
        // TODO: Show success/error message
        if (success) {
            response.sendRedirect("change-password?message=Password changed successfully");
        } else {
            response.sendRedirect("change-password?error=Failed to change password");
        }
    }
}