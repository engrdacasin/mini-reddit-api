package com.example.JuniorInterview.Service;

import com.example.JuniorInterview.Exception.BadRequestException;
import com.example.JuniorInterview.Model.AppUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
        private final Map<String, AppUser> users = new HashMap<>();

        public void signup(String username, String password) {
            if(users.containsKey(username)) {
                throw new BadRequestException("User already exists");
            }
            users.put(username, new AppUser(username, "{noop}" + password));
        }

        public AppUser findByUsername(String username) {
            return users.get(username);
        }
    }
