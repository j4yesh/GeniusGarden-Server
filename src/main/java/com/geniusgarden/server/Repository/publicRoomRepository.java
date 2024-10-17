package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.publicRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface publicRoomRepository extends MongoRepository<publicRoom, String> {
    List<publicRoom> findByIsUsedTrue();

    List<publicRoom> findByIsUsedFalse();
}
