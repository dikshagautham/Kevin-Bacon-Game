Kevin-Bacon-Game
================
Uses breadth-first-seach to play the "Kevin Bacon game" to find the distance between two actors, measured in the number of movies they are in together.

Overview:

1) Reads input files to create maps of (1) actor IDs to actor names, (2) movie IDs to movie names, and (3) movie IDs to their actor sets 

2) Uses these maps to creates an undirected graph (a "Bacon Graph") in which vertices are actor names and edges between actors are the movies they share

3) Runs a breadth-first search on our actor-movie graph to find each actors closest distance to Kevin Bacon (or whichever actor we set as our center) 

More info from the lab description:

In the Kevin Bacon game, you give an actor and try to find the shortest sequence of actors between the given actor and Kevin Bacon, where you list actors consecutively if they appeared in a movie together. For example, the silent film star Renée Adorée was in The Blackbird (1926) with Doris Lloyd, who was in Keep 'Em Flying (1941) with Carol Bruce, who was in Planes, Trains, and Automobiles (1987) with Kevin Bacon. Since that is the shortest sequence of actors between Renée Adorée and Kevin Bacon, we say that Renée Adorée's Kevin Bacon number is 3.
