package com.SignUpform.form.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.SignUpform.form.model.User;

public interface UserRepository extends MongoRepository<User,String>{

    User findByEmail(String email);

}