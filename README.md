# Overview

Simple turn based game made as project for Software Engineering subject at TCS, JU

The game is played between two or more players whose goal is to eliminate all entities of other players.

## Running

To start a game run `./gradlew run`

To start a localhost server run `./gradlew server`

## Game modes

There are three game modes you can select:
- Local - play with bots without server
- Localhost - connect to a server running on localhost
- Remote - connect to a remote server

# Game mechanics

## Turns
The game is turn based - only one player can perform moves at the time.

The turn is indicated by a button with base (pyramid) in the right left corner.
Pressing this button ends your turn.

## Win condition
Destroy all entities belonging to other players.

## Fog of war
There is a dynamic fog of war - players can see only events happening on fields currently seen by their entities.

## Entities
There are five types of entities:
- base (the pyramid) - Provides a starting point for players. Cannot be built.
- tower - reveals a vast area around and produces some mud every turn.
- pawn (the narrow high block) - cheap entity that can be used for scouting
- warrior (the wide short block) - a warrior that can damage other entities
- marsh wiggle (the tall one with a hat) - claims area around it which makes it a mobile spawn point

## Claiming
Base, Tower, and Marsh Wiggle claim fields around them which can bee seen as color change from white to the player's color.
The fields are claimed at the moment that entity spawns or moves to a position and are unclaimed when the entity dies or moves from the position.

When a field is already claimed then it is not claimed again i.e. the owner (and claiming entity) stays the same.

Entities can be spawned only at the claimed fields.

## Movement
Units (pawn, warrior, marsh wiggle) can move a certain amount of fields per turn.
Entity can be moved multiple times in one turn as long as total travelled distance is in the limit.
