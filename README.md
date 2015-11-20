# Vacant Room Finder (Server)

Vacant Room Finder is a system enabling students and lecturers at the [Hochschule für Technik und Wirtschaft Berlin (University of Applied Sciences)](http://www-en.htw-berlin.de/) to find the nearest vacant work area/room on campus. It is designed to require no lead time and little interaction.

This is the server of the system. The server is a REST-based web service. It provides a list of vacant rooms to a client. The list is ordered by the estimated distance to the client's user's location. The web service relies on JAX-RS and a private web service of the university providing a detailed list of vacant rooms. This web service filters the relevant room data, estimates the distance and orders the list. It has been tested on the GlassFish application server.