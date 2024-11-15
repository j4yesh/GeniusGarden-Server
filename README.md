# Multiplayer Game Server with Spring Boot and WebSockets

This repository contains the server-side implementation of a **multiplayer game** developed using **Spring Boot** and **WebSockets**. The game is built with **Unity** on the client side, while the server provides the infrastructure to support real-time multiplayer interactions.

The project was initially prototyped using **Cocos Creator** and **TypeScript** but later transitioned to Unity for enhanced performance and flexibility. Unlike backend-as-a-service (BaaS) solutions like Photon, this server is custom-built, emphasizing a **client-server architecture** and **multithreading** to handle concurrent player sessions.

## Table of Contents
- [Goals](#goals)
- [Features](#features)
- [Architecture](#architecture)
- [Gameplay Overview](#gameplay-overview)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Goals
- **Excellent Game Mechanism**
- **Game Engagement**
- **Online Multiplayer**
- **Learning Objective**
- **Player Performance Tracker**
- **Global All-Time Leaderboard**

## Features
- **Custom Backend**: Developed with **Spring Boot** and WebSockets for real-time communication.
- **Multithreading**: Each player is handled by a separate thread, dynamically instantiated at runtime.
- **Client-Server Model**: Efficient communication between the Unity-based client and the backend server.
- **Scalability**: The server can scale by managing multiple player threads concurrently, each adhering to game rules.

## Architecture

### Client
- **Unity**: The game is developed in Unity, using its **WebSocket** capabilities to interact with the server.
- **DOTween**: For smooth in-game animations.
- **TextMeshPro**: Used for rendering text elements in the UI.

### Server
- **Spring Boot**: Backend framework managing game sessions and WebSocket communication.
- **WebSockets**: Real-time communication between players and the server.
- **Multithreading**: Each player is assigned a separate thread to ensure responsive gameplay.
- **Game Logic**: Server-side validation of player moves and adherence to game rules.

## Gameplay Overview
Players are placed in an arena where they must collect nodes by answering questions. A new question and node appear every 5 seconds, and players must navigate without touching the arena's borders or incorrect nodes. Points are awarded for collecting the correct nodes, and the first player to collect 10 nodes wins. Players can touch each other without penalty, but incorrect actions result in point loss.

## Screenshots

![Gameplay Screenshot 1](https://i.ibb.co/vwDV7Lf/Screenshot-2024-09-30-235632.png)
*Gameplay Screenshot 1*

![Gameplay Screenshot 2](https://i.ibb.co/9y7qR09/Screenshot-2024-09-30-235723.png)
*Gameplay Screenshot 2*

## Screenshots

<p float="left">
  <img src="https://i.ibb.co/k5xdLTb/Whats-App-Image-2024-10-22-at-11-54-41.jpg" width="300" />
  <img src="https://i.ibb.co/ZSx4Xrm/Whats-App-Image-2024-10-22-at-08-55-16.jpg" width="300" />
</p>
<p float="left">
  <img src="https://i.ibb.co/bvpdcHB/Whats-App-Image-2024-10-22-at-08-58-24.jpg" width="300" />
  <img src="https://i.ibb.co/GpJL7yk/Whats-App-Image-2024-10-22-at-09-27-44.jpg" width="300" />
</p>


### Prototype Demo
A demonstration of the core gameplay was initially built using **Cocos Creator** with **TypeScript**. You can view the prototype here: [Prototype Demo](https://65ffff55fb849bc3e6c1255f--lucky-halva-6ea51c.netlify.app/).

Watch the gameplay demonstration on YouTube: [YouTube Video](https://www.youtube.com/watch?v=NNf5iNHen_4).

## Installation

### Prerequisites
- **Java 17+**
- **Maven**
- **Unity 2021.3+**
- **Spring Boot 2.7+**

### Steps
1. Clone the repository:
    ```bash
    git clone git@github.com:j4yesh/GeniusGarden-Server.git
    ```

2. Navigate to the server directory and build the project:
    ```bash
    cd GeniusGarden-Server
    mvn clean install
    ```

3. Start the Spring Boot server:
    ```bash
    mvn spring-boot:run
    ```

4. Open the Unity project and start the game to connect to the server.

## Usage
1. Start the server as described above.
2. Run the Unity client and connect using the server's WebSocket URL. [Unity Client](https://github.com/j4yesh/GeniusGarden)
3. Play the game, and the server will handle real-time interactions and multithreaded game logic.


## Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request.

## License
This project is licensed under the MIT License.
