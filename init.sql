DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS waitlist;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS flight_instance;
DROP TABLE IF EXISTS flight;
DROP TABLE IF EXISTS aircraft;
DROP TABLE IF EXISTS airport;
DROP TABLE IF EXISTS airline;
DROP TABLE IF EXISTS qna;
DROP TABLE IF EXISTS user_account;

CREATE TABLE user_account (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    email VARCHAR(100),
    role ENUM('admin', 'customer', 'rep') NOT NULL
);

CREATE TABLE airline (
    airlineID VARCHAR(5) PRIMARY KEY,
    name VARCHAR(100),
    country VARCHAR(50)
);

CREATE TABLE airport (
    airportID VARCHAR(5) PRIMARY KEY,
    name VARCHAR(100),
    city VARCHAR(50),
    country VARCHAR(50)
);

CREATE TABLE aircraft (
    aircraftID VARCHAR(10) PRIMARY KEY,
    airlineID VARCHAR(5),
    model VARCHAR(50),
    capacity INT,
    range_km INT,
    FOREIGN KEY (airlineID) REFERENCES airline(airlineID)
);

CREATE TABLE flight (
    flightID VARCHAR(10) PRIMARY KEY,
    airlineID VARCHAR(5),
    aircraftID VARCHAR(10),
    flightNum INT,
    depAirport VARCHAR(5),
    arrAirport VARCHAR(5),
    depTIME TIME,
    arrTIME TIME,
    dom_or_int CHAR(1),
    dayofweek VARCHAR(20),
    FOREIGN KEY (airlineID) REFERENCES airline(airlineID),
    FOREIGN KEY (aircraftID) REFERENCES aircraft(aircraftID),
    FOREIGN KEY (depAirport) REFERENCES airport(airportID),
    FOREIGN KEY (arrAirport) REFERENCES airport(airportID)
);

CREATE TABLE flight_instance (
    instanceID INT PRIMARY KEY AUTO_INCREMENT,
    flightID VARCHAR(10) NOT NULL,
    flightDate DATE NOT NULL,
    seatsAvail INT NOT NULL,
    baseFare DECIMAL(10,2) NOT NULL DEFAULT 100.00,
    FOREIGN KEY (flightID) REFERENCES flight(flightID)
);

CREATE TABLE reservation (
    reservationID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    instanceID INT NOT NULL,
    totalFare DECIMAL(10,2),
    bookingFee DECIMAL(10,2),
    cancelFee DECIMAL(10,2) DEFAULT 0,
    purchaseTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES user_account(userID),
    FOREIGN KEY (instanceID) REFERENCES flight_instance(instanceID)
);

CREATE TABLE ticket (
    ticketID INT PRIMARY KEY AUTO_INCREMENT,
    reservationID INT NOT NULL,
    instanceID INT NOT NULL,
    seatNum VARCHAR(10),
    class ENUM('economy', 'business', 'first') DEFAULT 'economy',
    meal VARCHAR(30),
    price DECIMAL(10,2),
    FOREIGN KEY (reservationID) REFERENCES reservation(reservationID),
    FOREIGN KEY (instanceID) REFERENCES flight_instance(instanceID)
);

CREATE TABLE waitlist (
    waitlistID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    instanceID INT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_waitlist_user_flight (userID, instanceID),
    FOREIGN KEY (userID) REFERENCES user_account(userID),
    FOREIGN KEY (instanceID) REFERENCES flight_instance(instanceID)
);

CREATE TABLE qna (
    questionID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    questionText VARCHAR(500) NOT NULL,
    answerText VARCHAR(500),
    askedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    answeredAt DATETIME NULL,
    FOREIGN KEY (userID) REFERENCES user_account(userID)
);

CREATE TABLE alerts (
    alertID INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    message TEXT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES user_account(userID)
);
