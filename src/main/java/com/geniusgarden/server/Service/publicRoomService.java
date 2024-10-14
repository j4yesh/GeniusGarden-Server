package com.geniusgarden.server.Service;

import com.geniusgarden.server.Model.publicRoom;
import com.geniusgarden.server.Repository.unusedPublicRoom;
import com.geniusgarden.server.Repository.usedPublicRoom;
import com.geniusgarden.server.Service.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class publicRoomService {

    @Autowired
    private usedPublicRoom usedpublicRoom;

    @Autowired
    private unusedPublicRoom unusedpublicRoom;

    private Integer ptr = 0;

    public synchronized String getRoom() {
        if (unusedpublicRoom.notempty()) {
            publicRoom publicroom = unusedpublicRoom.findFirstBy();
            publicroom.setPlayers(publicroom.getPlayers() + 1);

            if (publicroom.getPlayers() >= GameHandler.playerLimitForRoom) {
                unusedpublicRoom.delete(publicroom);
                usedpublicRoom.save(publicroom);
            } else {
                unusedpublicRoom.save(publicroom);
            }

            return publicroom.getId();
        } else {
            publicRoom newPublicRoom = new publicRoom("random" + ptr.toString(), 0);
            ptr++;
            unusedpublicRoom.save(newPublicRoom);
            return getRoom();
        }
    }

    public void deallocateRoom(String roomid) {
        if (usedpublicRoom.existsById(roomid)) {
            Optional<publicRoom> optionalRoom = usedpublicRoom.findById(roomid);

            if (optionalRoom.isPresent()) {
                publicRoom publicroom = optionalRoom.get();
                usedpublicRoom.deleteById(roomid);
                publicroom.setPlayers(0);
                unusedpublicRoom.save(publicroom);
            }
        }
    }

}
