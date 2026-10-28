CREATE TABLE IF NOT EXISTS Airport (
           AirportID INTEGER PRIMARY KEY AUTOINCREMENT,
           Name TEXT NOT NULL,
           City TEXT NOT NULL,
           Country TEXT NOT NULL,
           Latitude REAL NOT NULL,
           Longitude REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS Flight (
          FlightID INTEGER PRIMARY KEY AUTOINCREMENT,
          OriginAirportID INTEGER NOT NULL REFERENCES Airport(AirportID),
          DestinationAirportID INTEGER NOT NULL REFERENCES Airport(AirportID),
          ArrivalDateTime TEXT NOT NULL,
          DepartureDateTime TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS "User" (
          UserID INTEGER PRIMARY KEY AUTOINCREMENT,
          Email TEXT NOT NULL UNIQUE,
          HashPassword TEXT NOT NULL,
          Role TEXT NOT NULL,
          Name TEXT,
          Surname TEXT,
          DateOfBirth TEXT
);

CREATE TABLE IF NOT EXISTS Trip (
        TripID INTEGER PRIMARY KEY AUTOINCREMENT,
        UserID INTEGER NOT NULL REFERENCES "User"(UserID)
);

CREATE TABLE IF NOT EXISTS TripItinerary (
         TripID INTEGER NOT NULL REFERENCES Trip(TripID),
         FlightID INTEGER NOT NULL REFERENCES Flight(FlightID),
         LegOrder INTEGER NOT NULL,
         PRIMARY KEY (TripID, FlightID)
);