package com.nrwall.flightlog.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrwall.flightlog.users.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByUsername(String username);
  boolean existsByUsername(String username);
}
