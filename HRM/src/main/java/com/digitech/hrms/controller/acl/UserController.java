package com.digitech.hrms.controller.acl;


import com.digitech.hrms.repository.acl.RoleRepository;
import com.digitech.hrms.entity.acl.Role;
import com.digitech.hrms.entity.acl.User;
import com.digitech.hrms.error.RoleNotFoundException;
import com.digitech.hrms.error.SimilarPasswordException;
import com.digitech.hrms.error.UserNotFoundException;
import com.digitech.hrms.error.UsernameOrPasswordNotMatchedException;
import com.digitech.hrms.request.ResetPasswordRequest;
import com.digitech.hrms.request.SignupRequest;
import com.digitech.hrms.service.acl.UserService;
import com.digitech.hrms.service.acl.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * Bulbul Ahmed
 * Date: 12/07/2021
 * */

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserVerificationService verificationService;


    @Autowired
    private RoleRepository roleRepository;

    public UserController() {
    }

    //creating user
    @PostMapping("/register")
    public User createUser(@Valid @RequestBody SignupRequest signUpRequest,HttpServletRequest request) throws Exception {

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                this.bCryptPasswordEncoder.encode(signUpRequest.getPassword()));


        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.getRoleByRoleName("ROLE_USER");
            if (userRole==null){
                throw new RoleNotFoundException("Role not found");
            }
            roles.add(userRole);
        }else{
            strRoles.forEach(role -> {
                if ("super_admin".equals(role)) {
                    Role superAdminRole = roleRepository.getRoleByRoleName("ROLE_SUPER_ADMIN");
                    if (superAdminRole == null) {
                        try {
                            throw new RoleNotFoundException("Role not found");
                        } catch (RoleNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    roles.add(superAdminRole);
                } else if ("admin".equals(role)) {
                    Role adminRole = roleRepository.getRoleByRoleName("ROLE_ADMIN");
                    if (adminRole == null) {
                        try {
                            throw new RoleNotFoundException("Role not found");
                        } catch (RoleNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.getRoleByRoleName("ROLE_USER");
                    if (userRole == null) {
                        try {
                            throw new RoleNotFoundException("Role not found");
                        } catch (RoleNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        return this.userService.createUser(user,request);
    }

    //getting user by username
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserNotFoundException {
        User user = this.userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    //update user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Long userId,
                           @RequestBody User user){
        return userService.updateUser(userId,user);

    }

    //delete user by id
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){
        this.userService.deleteUser(userId);
    }

    /**
     * For Account Verification Section
     * After Register User must have to register
     * */

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@Param("code") String code) {
        if (verificationService.verify(code)) {
            return ResponseEntity.ok("Successfully verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify");
        }
    }

    /**Change password
     * user can change password
     * */
    @PostMapping("/{id}/resetPassword")
    public User resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest, @PathVariable("id") Long id) throws
            UsernameOrPasswordNotMatchedException, UserNotFoundException, SimilarPasswordException {

        return userService.resetPassword(resetPasswordRequest,id);
    }




}