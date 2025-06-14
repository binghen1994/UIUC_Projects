package edu.uiuc.cs427app;


/**
 * The UserBO (User Business Object) class represents a user's data including user ID, username, password, and selected theme.
 * It provides getter and setter methods to access and modify these attributes.
 */
public class UserBO {
    //String for userID
    String userId;
    //String for userName
    String userName;
    //String for userPassword
    String password;
    //String for userTheme
    String userTheme;

    /**
     * Get the user's username.
     *
     * @return The user's username as a string.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the user's username.
     *
     * @param userName The new username to set.
     */

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the user's password.
     *
     * @return The user's password as a string.
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set the user's password.
     *
     * @param password The new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Get the user's selected theme.
     *
     * @return The user's selected theme as a string.
     */
    public String getUserTheme() {
        return userTheme;
    }
    /**
     * Set the user's selected theme.
     *
     * @param userTheme The new theme to set.
     */
    public void setUserTheme(String userTheme) {
        this.userTheme = userTheme;
    }
    /**
     * Get the user's unique identifier (ID).
     *
     * @return The user's unique ID as a string.
     */


    public String getUserId() {
        return userId;
    }

    /**
     * Set the user's unique identifier (ID).
     *
     * @param userId The new user ID to set.
     */

    public void setUserId(String userId) {
        this.userId = userId;
    }






}
