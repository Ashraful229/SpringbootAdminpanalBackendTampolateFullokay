package com.digitech.hrms.service.acl;

import com.digitech.hrms.request.ResetPasswordRequest;
import com.digitech.hrms.entity.acl.User;
import com.digitech.hrms.error.SimilarPasswordException;
import com.digitech.hrms.error.UserNotFoundException;
import com.digitech.hrms.error.UsernameOrPasswordNotMatchedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {

    //creating user
    public User createUser(User user,HttpServletRequest request) throws Exception;

    //get user by username
    public User getUser(String username) throws UserNotFoundException;

    List<User> fetchUserList();

    //delete user by id
    public void deleteUser(Long userId);

    //update user by id
    User updateUser(Long userId, User user);


    /** User created By System Only
     * This function use only for System Auto creation User
     * ***[Don't be use for another purpose]
     * Its a danger if you are use
     * */
    public User createUser(User user) throws Exception;


    /** reset password */
    User resetPassword(ResetPasswordRequest resetPasswordRequest, Long id)
            throws UsernameOrPasswordNotMatchedException, UserNotFoundException, SimilarPasswordException;


}
