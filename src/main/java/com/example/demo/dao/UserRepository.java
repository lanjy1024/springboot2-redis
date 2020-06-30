package com.example.demo.dao;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author：lanjy
 * @date：2020/6/11
 * @description：
 */
public interface UserRepository extends JpaRepository<User,Long>,JpaSpecificationExecutor<User> {
    User findByUserName(String userName);
}
