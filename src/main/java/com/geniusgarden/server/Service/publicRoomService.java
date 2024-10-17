package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.publicRoom;
import com.geniusgarden.server.Repository.publicRoomRepository;
import com.geniusgarden.server.Repository.unusedPublicRoom;
import com.geniusgarden.server.Repository.usedPublicRoom;
import com.geniusgarden.server.Service.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class publicRoomService {

    @Autowired
    private publicRoomRepository publicroomRepository;

    private Integer ptr = 0;

    public synchronized String getRoom() {
        List<publicRoom> used = publicroomRepository.findByIsUsedTrue();
        List<publicRoom> unused = publicroomRepository.findByIsUsedFalse();

        if (unused.size() > 0) {
            publicRoom publicroom = unused.get(0);
            publicroom.setPlayers(publicroom.getPlayers() + 1);

            if (publicroom.getPlayers() >= GameHandler.playerLimitForRoom) {
                publicroom.setUsed(true);
                publicroomRepository.save(publicroom);
            } else {
                publicroomRepository.save(publicroom);
            }

            return publicroom.getId();
        } else {
            publicRoom newPublicRoom = new publicRoom("random" + ptr.toString(), 0, false);  // New room with 0 players and isUsed = false
            ptr++;
            publicroomRepository.save(newPublicRoom);
            return newPublicRoom.getId();
        }
    }

    public synchronized void deallocateRoom(String roomId) {
        Optional<publicRoom> optionalRoom = publicroomRepository.findById(roomId);

        if (optionalRoom.isPresent()) {
            publicRoom publicroom = optionalRoom.get();
            if (publicroom.isUsed()) {
                publicroom.setPlayers(0);
                publicroom.setUsed(false);
                publicroomRepository.save(publicroom);
            }
        }
    }
}

