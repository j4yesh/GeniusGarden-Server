package com.geniusgarden.server.Controller;


import com.geniusgarden.server.GameplayModel.player;
import com.geniusgarden.server.Model.*;
import com.geniusgarden.server.Repository.AuthUserRepository;
import com.geniusgarden.server.Repository.ResultRepository;
import com.geniusgarden.server.Repository.RoomRepository;
import com.geniusgarden.server.Service.GameHandler;
import com.geniusgarden.server.Service.JsonUtil;
import com.geniusgarden.server.Service.Util;
import com.geniusgarden.server.Service.publicRoomService;
import com.geniusgarden.server.env;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/repo")
public class GameRepoController {

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    publicRoomService publicroomService;


    @PostMapping("/pushresult")
    public ResponseEntity<String> pushResult(@RequestBody Result result) {
        GameHandler.logger.info(result.toString());
        try {
            if (authUserRepository.findByUsername(result.getUsername()).isPresent() && result.getConKey().equals(env.conKey)) {
                Optional<AuthUser> user1 = authUserRepository.findByUsername(result.getUsername());
                AuthUser user = user1.get();

                user.setCorrect(user.getCorrect() + result.getCorrect());
                user.setWrong(user.getWrong() + result.getWrong());
                user.setGames(user.getGames() + 1);

                DecimalFormat df = new DecimalFormat("0.00");

                if (user.getCorrect() + user.getWrong() != 0) {
                    float newAcceptance = (float) user.getCorrect() * 100 / (user.getCorrect() + user.getWrong());
                    user.setAcceptance(Float.parseFloat(df.format(newAcceptance)));
                }

                String uniqueResultId = new ObjectId().toHexString();
                result.setId(uniqueResultId);
                float resultAcceptance = (float) result.getCorrect() * 100 / (result.getWrong() + result.getCorrect());
                result.setAcceptance(Float.parseFloat(df.format(resultAcceptance)));

                result.setConKey(":)");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = LocalDateTime.now().format(formatter);
                result.setTime(formattedDateTime);

                Optional<Room> correspondingRoom = roomRepository.findById(result.getRoomId());
                if(correspondingRoom.isPresent()){
                    Room correspondingRoom1 = correspondingRoom.get();
                    String startingTime = correspondingRoom1.getStartingTime();
                    String duration = Util.findDuration(startingTime,formattedDateTime);
                    result.setDuration(duration);
                }

                resultRepository.save(result);
                authUserRepository.save(user);

                return ResponseEntity.status(HttpStatus.OK).body("successful.");
            } else {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("user not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/getresult")   //not used till now.
    private ResponseEntity<String> getResult(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication.isAuthenticated()) {
                if(resultRepository.findByUsername(authentication.getName())==null){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No games found!");
                }
                List<Result> results = resultRepository.findByUsername(authentication.getName());
                if (!results.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(results));
                } else {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No result found.");
                }
            }else{
                return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body("Not Authorized");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getplayers")
    private ResponseEntity<String> getPlayers(){
        try{
            List<AuthUser> players = authUserRepository.findAll();
            if(!players.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(players));
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No result found.");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/leaderboard")
    private ResponseEntity<String> leaderboard() {
        try {
            List<AuthUser> players = authUserRepository.findAll();
            for(AuthUser authUser : players){
                authUser.setPassword(":)");
            }
            if (!players.isEmpty()) {
                players.sort(new Comparator<AuthUser>() {
                    @Override
                    public int compare(AuthUser o1, AuthUser o2) {
                        return Float.compare(o2.getAcceptance(), o1.getAcceptance());
                    }
                });
//                int count = Math.min(18, players.size());
//                players = players.subList(0, count);
                return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(players));
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No result found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getroom")
    private ResponseEntity<String>getroom(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username =  authentication.getName();
            String roomId = Util.generateRandomString();
            Room room = new Room(roomId,new ArrayList<>(),"");
            room.addPlayer(username);
            roomRepository.save(room);
            return ResponseEntity.status(HttpStatus.OK).body(roomId);
        }
        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body("");
    }

    @PostMapping("/joinroom")
    private ResponseEntity<String> joinRoom(@RequestBody JoinDTO joinDTO){
        System.out.println(joinDTO.getRoomId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username =  authentication.getName();
            String roomId = joinDTO.getRoomId();
            if(roomRepository.findById(roomId).isPresent()){
                Room room = roomRepository.findById(roomId).get();
                room.addPlayer(username);
                roomRepository.save(room);
                return ResponseEntity.status(HttpStatus.OK).body("Good to go.");
            }else{
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Not eligible.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
    }

    @PostMapping("/valid")
    private ResponseEntity<String> valid(@RequestBody ValidityDTO validity) {
        try {
            String roomId = validity.getRoomId();
            String username = validity.getUsername();
            String key = validity.getKey();

            if (roomId != null && username != null && key.equals(env.conKey)) {
                Optional<Room> optionalRoom = roomRepository.findById(roomId);
                if (optionalRoom.isPresent()) {
                    Room room = optionalRoom.get();
                    List<String> players = room.getPlayers();

                    if (players.contains(username)) {
                        return ResponseEntity.status(HttpStatus.OK).body("Good to go.");
                    } else {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not valid");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Need all fields.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removePlayer(@RequestBody ValidityDTO validity) {
        try {
            String roomId = validity.getRoomId();
            String username = validity.getUsername();
            String key = validity.getKey();

            if (roomId != null && username != null && key.equals(env.conKey)) {
                Optional<Room> optionalRoom = roomRepository.findById(roomId);
                if (optionalRoom.isPresent()) {
                    Room room = optionalRoom.get();
                    List<String> players = new ArrayList<>(room.getPlayers());

                    if (players.contains(username)) {
                        players.remove(username);

                        if (players.isEmpty()) {
                            roomRepository.delete(room);
                        } else {
                            room.setPlayers(players);
                            roomRepository.save(room);
                        }

                        return ResponseEntity.status(HttpStatus.OK).body("Player removed successfully.");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player not found in room.");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input or missing fields.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getusername")
    ResponseEntity<String> getUsername(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication!=null){
                AuthUser authUser = authUserRepository.findByUsername(authentication.getName()).get();
                authUser.setPassword(":)");
                return ResponseEntity.status(HttpStatus.OK).body(JsonUtil.toJson(authUser));

            }else{
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No response");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getpublic")
    ResponseEntity<String> getPublic(){
        try{
            String roomId = publicroomService.getRoom();
            roomRepository.save(new Room(roomId,null,null));
           return ResponseEntity.status(HttpStatus.OK).body(roomId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/startingmatch")
    public ResponseEntity<String> startMatchTime(@RequestBody MatchDTO matchDTO){
        try{
            Optional<Room> room = roomRepository.findById(matchDTO.getRoomId());
            if(room.isPresent()){
                Room room1 = room.get();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = LocalDateTime.now().format(formatter);
                room1.setStartingTime(formattedDateTime);
                roomRepository.save(room1);
                return ResponseEntity.status(HttpStatus.OK).body("Pushed Successfully");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getspeed")
    private ResponseEntity<String> getSpeed(){
        return ResponseEntity.status(HttpStatus.OK).body(Float.toString(player.speed));
    }
}
