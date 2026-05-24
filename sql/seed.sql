INSERT INTO
    user_account (
        username,
        password,
        firstName,
        lastName,
        email,
        role
    )
VALUES
    (
        'admin',
        'admin123',
        'Site',
        'Admin',
        'admin@example.com',
        'admin'
    ),
    (
        'alice',
        'pass123',
        'Alice',
        'Adams',
        'alice@example.com',
        'customer'
    ),
    (
        'bob',
        'pass123',
        'Bob',
        'Brown',
        'bob@example.com',
        'customer'
    ),
    (
        'rep1',
        'pass123',
        'Rita',
        'Reeves',
        'rita@example.com',
        'rep'
    ),
    (
        'carol',
        'pass123',
        'Carol',
        'Cruz',
        'carol@example.com',
        'customer'
    ),
    (
        'dave',
        'pass123',
        'Dave',
        'Diaz',
        'dave@example.com',
        'customer'
    ),
    (
        'eve',
        'pass123',
        'Eve',
        'Edwards',
        'eve@example.com',
        'customer'
    ),
    (
        'frank',
        'pass123',
        'Frank',
        'Fischer',
        'frank@example.com',
        'customer'
    ),
    (
        'grace',
        'pass123',
        'Grace',
        'Gomez',
        'grace@example.com',
        'customer'
    ),
    (
        'henry',
        'pass123',
        'Henry',
        'Hoffman',
        'henry@example.com',
        'customer'
    ),
    (
        'rep2',
        'pass123',
        'Roman',
        'Patel',
        'roman@example.com',
        'rep'
    ),
    (
        'admin2',
        'admin123',
        'Sandra',
        'Stone',
        'sandra@example.com',
        'admin'
    ),
    (
        'ivy',
        'pass123',
        'Ivy',
        'Iverson',
        'ivy@example.com',
        'customer'
    ),
    (
        'jack',
        'pass123',
        'Jack',
        'Johnson',
        'jack@example.com',
        'customer'
    ),
    (
        'kate',
        'pass123',
        'Kate',
        'Kim',
        'kate@example.com',
        'customer'
    );

INSERT INTO
    airline (airlineID, name, country)
VALUES
    ('AA', 'American Airlines', 'USA'),
    ('UA', 'United Airlines', 'USA'),
    ('DL', 'Delta Air Lines', 'USA'),
    ('BA', 'British Airways', 'UK'),
    ('LH', 'Lufthansa', 'Germany'),
    ('AF', 'Air France', 'France'),
    ('JL', 'Japan Airlines', 'Japan');

INSERT INTO
    airport (airportID, name, city, country)
VALUES
    (
        'EWR',
        'Newark Liberty International Airport',
        'Newark',
        'USA'
    ),
    (
        'JFK',
        'John F. Kennedy International Airport',
        'New York',
        'USA'
    ),
    ('LGA', 'LaGuardia Airport', 'New York', 'USA'),
    (
        'LAX',
        'Los Angeles International Airport',
        'Los Angeles',
        'USA'
    ),
    (
        'ORD',
        'Chicago O Hare International Airport',
        'Chicago',
        'USA'
    ),
    (
        'SFO',
        'San Francisco International Airport',
        'San Francisco',
        'USA'
    ),
    (
        'MIA',
        'Miami International Airport',
        'Miami',
        'USA'
    ),
    (
        'ATL',
        'Hartsfield-Jackson Atlanta International',
        'Atlanta',
        'USA'
    ),
    ('LHR', 'Heathrow Airport', 'London', 'UK'),
    (
        'CDG',
        'Charles de Gaulle Airport',
        'Paris',
        'France'
    ),
    (
        'FRA',
        'Frankfurt Airport',
        'Frankfurt',
        'Germany'
    ),
    (
        'NRT',
        'Narita International Airport',
        'Tokyo',
        'Japan'
    );

INSERT INTO
    aircraft (aircraftID, airlineID, model, capacity, range_km)
VALUES
    ('AC1', 'AA', 'Boeing 737', 150, 5000),
    ('AC2', 'UA', 'Airbus A320', 160, 4500),
    ('AC3', 'DL', 'Boeing 757', 180, 6500),
    ('AC4', 'AA', 'Boeing 777', 300, 12000),
    ('AC5', 'UA', 'Boeing 787', 250, 14000),
    ('AC6', 'DL', 'Airbus A330', 280, 13000),
    ('AC7', 'BA', 'Airbus A380', 500, 15000),
    ('AC8', 'LH', 'Boeing 747', 400, 14000),
    ('AC9', 'AF', 'Airbus A350', 310, 15000),
    ('AC10', 'JL', 'Boeing 787', 240, 14000);

INSERT INTO
    flight (
        flightID,
        airlineID,
        aircraftID,
        flightNum,
        depAirport,
        arrAirport,
        depTIME,
        arrTIME,
        dom_or_int,
        dayofweek
    )
VALUES
    (
        'F1',
        'AA',
        'AC1',
        101,
        'EWR',
        'LAX',
        '08:00:00',
        '11:00:00',
        'D',
        'Friday'
    ),
    (
        'F2',
        'UA',
        'AC2',
        202,
        'EWR',
        'LAX',
        '14:00:00',
        '17:30:00',
        'D',
        'Friday'
    ),
    (
        'F3',
        'AA',
        'AC1',
        303,
        'JFK',
        'LAX',
        '09:30:00',
        '13:00:00',
        'D',
        'Friday'
    ),
    (
        'F4',
        'UA',
        'AC2',
        404,
        'EWR',
        'ORD',
        '10:00:00',
        '12:00:00',
        'D',
        'Friday'
    ),
    (
        'F5',
        'DL',
        'AC3',
        505,
        'EWR',
        'LAX',
        '18:00:00',
        '21:15:00',
        'D',
        'Saturday'
    ),
    (
        'F6',
        'DL',
        'AC3',
        606,
        'JFK',
        'MIA',
        '07:00:00',
        '10:30:00',
        'D',
        'Monday'
    ),
    (
        'F7',
        'AA',
        'AC4',
        707,
        'LAX',
        'EWR',
        '22:00:00',
        '06:00:00',
        'D',
        'Friday'
    ),
    (
        'F8',
        'UA',
        'AC5',
        808,
        'SFO',
        'ORD',
        '11:00:00',
        '17:00:00',
        'D',
        'Wednesday'
    ),
    (
        'F9',
        'DL',
        'AC6',
        909,
        'ATL',
        'LAX',
        '13:00:00',
        '15:30:00',
        'D',
        'Tuesday'
    ),
    (
        'F10',
        'AA',
        'AC1',
        111,
        'LGA',
        'ORD',
        '06:30:00',
        '08:30:00',
        'D',
        'Thursday'
    ),
    (
        'F11',
        'UA',
        'AC2',
        212,
        'ORD',
        'SFO',
        '15:00:00',
        '17:30:00',
        'D',
        'Sunday'
    ),
    (
        'F12',
        'BA',
        'AC7',
        100,
        'JFK',
        'LHR',
        '20:00:00',
        '08:00:00',
        'I',
        'Daily'
    ),
    (
        'F13',
        'BA',
        'AC7',
        101,
        'LHR',
        'JFK',
        '11:00:00',
        '14:00:00',
        'I',
        'Daily'
    ),
    (
        'F14',
        'LH',
        'AC8',
        400,
        'EWR',
        'FRA',
        '21:30:00',
        '11:00:00',
        'I',
        'Daily'
    ),
    (
        'F15',
        'AF',
        'AC9',
        207,
        'JFK',
        'CDG',
        '19:00:00',
        '08:30:00',
        'I',
        'Daily'
    ),
    (
        'F16',
        'JL',
        'AC10',
        5,
        'LAX',
        'NRT',
        '12:00:00',
        '16:00:00',
        'I',
        'Daily'
    ),
    (
        'F17',
        'AA',
        'AC4',
        150,
        'LAX',
        'JFK',
        '23:00:00',
        '07:00:00',
        'D',
        'Daily'
    ),
    (
        'F18',
        'DL',
        'AC3',
        650,
        'MIA',
        'JFK',
        '15:00:00',
        '18:00:00',
        'D',
        'Monday'
    );

INSERT INTO
    flight_instance (flightID, flightDate, seatsAvail, baseFare)
VALUES
    ('F1', '2025-12-15', 0, 240.00),
    ('F2', '2025-12-20', 0, 290.00),
    ('F6', '2026-01-12', 0, 200.00),
    ('F12', '2026-02-03', 0, 850.00),
    ('F14', '2026-02-18', 0, 920.00),
    ('F4', '2026-03-09', 0, 175.00),
    ('F9', '2026-03-22', 0, 310.00),
    ('F1', '2026-04-05', 0, 250.00),
    ('F3', '2026-04-12', 0, 225.00),
    ('F8', '2026-04-18', 0, 340.00),
    ('F15', '2026-04-25', 0, 880.00),
    ('F1', '2026-05-01', 20, 250.00),
    ('F1', '2026-05-02', 10, 275.00),
    ('F1', '2026-05-04', 5, 230.00),
    ('F2', '2026-05-01', 15, 300.00),
    ('F2', '2026-05-03', 0, 320.00),
    ('F3', '2026-05-01', 8, 220.00),
    ('F4', '2026-05-01', 25, 180.00),
    ('F5', '2026-05-02', 12, 260.00),
    ('F6', '2026-05-05', 18, 210.00),
    ('F7', '2026-05-06', 0, 270.00),
    ('F8', '2026-05-08', 22, 350.00),
    ('F9', '2026-05-10', 3, 305.00),
    ('F10', '2026-05-12', 30, 165.00),
    ('F11', '2026-05-14', 14, 195.00),
    ('F12', '2026-05-15', 6, 875.00),
    ('F13', '2026-05-22', 4, 890.00),
    ('F14', '2026-06-01', 9, 950.00),
    ('F15', '2026-06-10', 0, 1100.00),
    ('F16', '2026-06-20', 7, 1450.00),
    ('F17', '2026-05-15', 0, 285.00),
    ('F18', '2026-05-18', 1, 215.00),
    ('F1', '2026-05-20', 0, 260.00),
    ('F2', '2026-05-25', 0, 310.00),
    ('F3', '2026-05-28', 0, 235.00),
    ('F12', '2026-06-15', 0, 920.00);

INSERT INTO
    reservation (
        userID,
        instanceID,
        totalFare,
        bookingFee,
        purchaseTime
    )
VALUES
    (2, 1, 260.00, 20.00, '2025-12-01 09:00:00'),
    (3, 2, 310.00, 20.00, '2025-12-05 14:30:00'),
    (5, 3, 220.00, 20.00, '2025-12-28 11:15:00'),
    (6, 4, 870.00, 20.00, '2026-01-10 16:00:00'),
    (7, 5, 940.00, 20.00, '2026-01-25 10:00:00'),
    (8, 6, 195.00, 20.00, '2026-02-15 08:30:00'),
    (9, 7, 330.00, 20.00, '2026-03-01 13:45:00'),
    (2, 8, 270.00, 20.00, '2026-03-25 19:20:00'),
    (3, 9, 245.00, 20.00, '2026-04-01 07:50:00'),
    (5, 10, 360.00, 20.00, '2026-04-08 12:00:00'),
    (10, 11, 900.00, 20.00, '2026-04-15 18:00:00'),
    (2, 12, 270.00, 20.00, '2026-04-20 10:00:00'),
    (2, 13, 295.00, 20.00, '2026-04-21 11:30:00'),
    (3, 14, 250.00, 20.00, '2026-04-22 09:15:00'),
    (5, 15, 320.00, 20.00, '2026-04-23 13:00:00'),
    (6, 20, 230.00, 20.00, '2026-04-24 15:45:00'),
    (7, 22, 370.00, 20.00, '2026-04-25 09:00:00'),
    (8, 23, 325.00, 20.00, '2026-04-26 16:20:00'),
    (9, 26, 895.00, 20.00, '2026-04-27 12:30:00'),
    (2, 18, 200.00, 20.00, '2026-04-28 08:00:00'),
    (13, 32, 235.00, 20.00, '2026-04-29 10:00:00'),
    (14, 32, 235.00, 20.00, '2026-04-29 10:30:00'),
    (15, 32, 235.00, 20.00, '2026-04-29 11:00:00');

INSERT INTO
    ticket (
        reservationID,
        instanceID,
        seatNum,
        class,
        meal,
        price
    )
VALUES
    (1, 1, '12A', 'business', 'vegetarian', 260.00),
    (2, 2, '4F', 'first', 'kosher', 310.00),
    (3, 3, '22C', 'economy', NULL, 220.00),
    (4, 4, '8B', 'business', 'standard', 870.00),
    (5, 5, '2A', 'first', 'gluten-free', 940.00),
    (6, 6, '15D', 'economy', NULL, 195.00),
    (7, 7, '11E', 'business', 'standard', 330.00),
    (8, 8, '19A', 'economy', NULL, 270.00),
    (9, 9, '21B', 'economy', NULL, 245.00),
    (10, 10, '7C', 'business', 'vegan', 360.00),
    (11, 11, '3F', 'first', 'kosher', 900.00),
    (12, 12, '14A', 'business', 'standard', 270.00),
    (13, 13, '1A', 'first', 'vegetarian', 295.00),
    (14, 14, '23D', 'economy', NULL, 250.00),
    (15, 15, '9B', 'business', 'standard', 320.00),
    (16, 20, '17E', 'economy', NULL, 230.00),
    (17, 22, '6A', 'business', 'vegan', 370.00),
    (18, 23, '10C', 'business', 'standard', 325.00),
    (19, 26, '5F', 'first', 'gluten-free', 895.00),
    (20, 18, '24B', 'economy', NULL, 200.00),
    (21, 32, '14C', 'economy', NULL, 235.00),
    (22, 32, '14D', 'economy', NULL, 235.00),
    (23, 32, '14E', 'economy', NULL, 235.00);

INSERT INTO
    waitlist (userID, instanceID, timestamp)
VALUES
    (3, 16, '2026-04-20 09:00:00'),
    (5, 16, '2026-04-22 14:15:00'),
    (13, 16, '2026-04-24 08:30:00'),
    (14, 16, '2026-04-26 17:45:00'),
    (6, 21, '2026-04-15 11:00:00'),
    (15, 21, '2026-04-18 13:20:00'),
    (8, 29, '2026-04-10 10:00:00'),
    (9, 29, '2026-04-12 14:30:00'),
    (10, 29, '2026-04-15 09:15:00'),
    (2, 31, '2026-04-25 16:00:00'),
    (13, 33, '2026-04-26 12:00:00'),
    (14, 33, '2026-04-27 10:30:00'),
    (15, 33, '2026-04-28 14:00:00'),
    (2, 34, '2026-04-28 09:00:00'),
    (3, 35, '2026-04-29 11:00:00'),
    (7, 36, '2026-04-29 13:00:00'),
    (13, 21, '2026-04-30 08:00:00');

INSERT INTO
    qna (
        userID,
        questionText,
        answerText,
        askedAt,
        answeredAt
    )
VALUES
    (
        2,
        'Can I cancel an economy ticket?',
        'Yes, but a $75 cancellation fee applies. Business and first class are free to cancel.',
        '2026-03-15 10:00:00',
        '2026-03-15 14:30:00'
    ),
    (
        3,
        'How does the waitlist work?',
        'When a seat opens up, the first person on the waitlist gets an alert and can book it.',
        '2026-03-20 09:00:00',
        '2026-03-20 11:00:00'
    ),
    (
        5,
        'Do you offer student discounts?',
        'We do not currently offer student-specific discounts.',
        '2026-04-01 16:45:00',
        '2026-04-02 09:00:00'
    ),
    (
        6,
        'Can I bring a pet on board?',
        NULL,
        '2026-04-10 14:20:00',
        NULL
    ),
    (
        7,
        'What meal options are available?',
        'Standard, vegetarian, vegan, kosher, and gluten-free options are available.',
        '2026-04-12 08:30:00',
        '2026-04-12 13:00:00'
    ),
    (
        8,
        'How early should I arrive at the airport?',
        NULL,
        '2026-04-25 19:15:00',
        NULL
    ),
    (
        9,
        'Do tickets include checked baggage?',
        NULL,
        '2026-04-28 11:00:00',
        NULL
    ),
    (
        13,
        'Are there discounts for booking multiple flights at once?',
        NULL,
        '2026-04-29 14:00:00',
        NULL
    ),
    (
        14,
        'Can I change my seat after booking?',
        'Yes, contact a representative to change your seat assignment.',
        '2026-04-29 16:00:00',
        '2026-04-30 08:30:00'
    ),
    (
        15,
        'What happens if my flight gets canceled by the airline?',
        NULL,
        '2026-04-30 09:45:00',
        NULL
    );

INSERT INTO
    alerts (userID, message, createdAt)
VALUES
    (
        3,
        'A seat is available on Flight F2 May 3. Please book within 24 hours.',
        '2026-04-15 10:00:00'
    ),
    (
        5,
        'Your reservation for F2 May 1 has been confirmed.',
        '2026-04-23 13:05:00'
    ),
    (
        2,
        'Reminder: your flight F1 on May 1 departs in 5 days.',
        '2026-04-26 08:00:00'
    ),
    (
        6,
        'Your waitlist position on F7 May 6 has moved up.',
        '2026-04-29 12:00:00'
    ),
    (
        7,
        'Your international flight F8 on May 8 has been confirmed.',
        '2026-04-25 09:05:00'
    ),
    (
        13,
        'You are #3 on the waitlist for F2 on May 3.',
        '2026-04-24 09:00:00'
    ),
    (
        8,
        'Your waitlist entry for F15 on June 10 is now position #1.',
        '2026-04-30 10:00:00'
    ),
    (
        2,
        'You have multiple upcoming reservations this month. Review them now.',
        '2026-05-01 07:00:00'
    );