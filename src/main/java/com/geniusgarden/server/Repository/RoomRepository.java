package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findById(String id);
}
