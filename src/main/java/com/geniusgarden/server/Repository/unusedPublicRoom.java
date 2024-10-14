package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.publicRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.Repository;

public interface unusedPublicRoom extends MongoRepository<publicRoom,String> {
    default boolean notempty() {
        return count() > 0;
    }

    publicRoom findFirstBy();
}
