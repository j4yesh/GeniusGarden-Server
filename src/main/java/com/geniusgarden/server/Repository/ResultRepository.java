package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.Result;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends MongoRepository<Result, String> {
    Optional<Result> findById(String id);
    List<Result> findByUsername(String username);

}
