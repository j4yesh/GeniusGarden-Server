package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.publicRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface usedPublicRoom extends MongoRepository<publicRoom,String> {
}
