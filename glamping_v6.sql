CREATE DATABASE glamping;

USE glamping;

CREATE TABLE `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `email` VARCHAR(255),
    `password` VARCHAR(255),
    `first_name` VARCHAR(255),
    `last_name` VARCHAR(255),
    `phone_number` VARCHAR(255),
    `address` VARCHAR(255),
    `role` ENUM('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER', 'ROLE_STAFF'),
    `created_at` DATETIME,
    `status` BOOLEAN DEFAULT TRUE
);

CREATE TABLE `booking` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `id_user` INT,
    `id_camp_site` INT,
    `created_at` DATETIME,
    `status` ENUM('Pending', 'Deposit', 'Accepted', 'Completed', 'Cancelled', 'Denied', 'Refund'),
    `total_amount` DECIMAL(10,2)
);

CREATE TABLE `camp_site` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `address` VARCHAR(255),
    `city` varchar(255),
    `latitude` DECIMAL(9,6),
    `longitude` DECIMAL(9,6),
    `created_at` DATETIME,
    `status` ENUM('Pending', 'Not_Available', 'Available'),
    `id_user` INT
);

CREATE TABLE `camp` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `created_at` DATETIME,
    `status` enum("Unavailable", "Not_Assigned", "Assigned") DEFAULT "Not_Assigned",
    `updated_at` DATETIME,
    `id_camp_type` INT
);

CREATE TABLE `payment` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `id_booking` INT,
    `payment_method` VARCHAR(255),
    `total_amount` DECIMAL(10,2),
    `status` ENUM('Pending', 'Completed', 'Failed'),
    `id_transaction` VARCHAR(255),
    `completed_at` DATETIME
);

CREATE TABLE `service` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `description` TEXT,
    `price` DECIMAL(10,2),
    `image` VARCHAR(255) DEFAULT '',
    `status` BOOLEAN DEFAULT TRUE,
    `updated_at` DATETIME
);

CREATE TABLE `booking_service` (
    `id_booking` INT,
    `id_service` INT,
    `name` VARCHAR(255),
    `quantity` DECIMAL(10,2),
    PRIMARY KEY (`id_booking`, `id_service`)
);

CREATE TABLE `facility` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `description` TEXT,
    `image` VARCHAR(255) DEFAULT '',
    `status` BOOLEAN DEFAULT TRUE
);

CREATE TABLE `camp_type_facility` (
    `id_facility` INT,
    `id_camp_type` INT,
    `status` BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (`id_facility`, `id_camp_type`)
);

CREATE TABLE `image` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `id_camp_site` INT,
    `path` VARCHAR(255)
);

CREATE TABLE `report` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `id_camp_site` INT,
    `id_user` INT,
    `status` enum('Pending', 'Resolved', 'Denied') DEFAULT 'PENDING',
    `created_at` DATETIME,
    `message` TEXT,
    `report_type` VARCHAR(255)
);

CREATE TABLE `booking_detail` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `id_booking` INT,
    `id_camp_type` INT,
    `id_camp` INT,
    `check_in_at` DATETIME,
    `check_out_at` DATETIME,
    `amount` DECIMAL(10,2),
    `comment` VARCHAR(255),
    `rating` INT,
    `created_at` DATETIME,
    `add_on` DECIMAL(10,2),
    `status` ENUM('Waiting', 'Check_In', 'Check_Out') DEFAULT 'WAITING'
);

CREATE TABLE `camp_type` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `type` VARCHAR(255),
    `capacity` INT,
    `price` DECIMAL(10,2),
    `weekend_rate` DECIMAL(10,2),
    `holiday_rate` DECIMAL(10,2),
    `updated_at` DATETIME,
    `id_camp_site` INT,
    `quantity` INT,
    `status` BOOLEAN DEFAULT TRUE
);

CREATE TABLE `order` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `price` DECIMAL(10,2),
    `status` BOOLEAN DEFAULT TRUE,
    `updated_at` DATETIME
);

CREATE TABLE `booking_detail_order` (
    `id_booking_detail` INT,
    `id_order` INT,
    `quantity` INT,
    `total_amount` DECIMAL(10,2),
    PRIMARY KEY (`id_booking_detail`, `id_order`)
);

CREATE TABLE `utility` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255),
    `image` VARCHAR(255) DEFAULT '',
    `status` BOOLEAN DEFAULT TRUE
);

CREATE TABLE `camp_site_utility` (
    `id_camp_site` INT,
    `id_utility` INT,
    `status` BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (`id_camp_site`, `id_utility`)
);

CREATE TABLE place_type(
	`id` int AUTO_INCREMENT,
	`name` varchar(255),
	`status` boolean DEFAULT true,
	`image` varchar(255) DEFAULT '',
	
	PRIMARY KEY (`id`)
);

CREATE TABLE camp_site_place_type(
	`id_camp_site` int,
	`id_place_type` int,
	
	PRIMARY KEY (`id_camp_site`, `id_place_type`)
);



ALTER TABLE `booking`
    ADD FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

ALTER TABLE `booking`
    ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `camp_site`
    ADD FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

ALTER TABLE `payment`
    ADD FOREIGN KEY (`id_booking`) REFERENCES `booking` (`id`);

ALTER TABLE `booking_service`
    ADD FOREIGN KEY (`id_booking`) REFERENCES `booking` (`id`);

ALTER TABLE `booking_service`
    ADD FOREIGN KEY (`id_service`) REFERENCES `service` (`id`);

ALTER TABLE `camp_type_facility`
    ADD FOREIGN KEY (`id_camp_type`) REFERENCES `camp_type` (`id`);

ALTER TABLE `camp_type_facility`
    ADD FOREIGN KEY (`id_facility`) REFERENCES `facility` (`id`);

ALTER TABLE `image`
    ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `report`
    ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `report`
    ADD FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

ALTER TABLE `booking_detail`
    ADD FOREIGN KEY (`id_camp_type`) REFERENCES `camp_type` (`id`);

ALTER TABLE `booking_detail`
    ADD FOREIGN KEY (`id_booking`) REFERENCES `booking` (`id`);

ALTER TABLE `camp_type`
    ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `booking_detail_order`
    ADD FOREIGN KEY (`id_booking_detail`) REFERENCES `booking_detail` (`id`);

ALTER TABLE `booking_detail_order`
    ADD FOREIGN KEY (`id_order`) REFERENCES `order` (`id`);

ALTER TABLE `camp_site_utility`
    ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `camp_site_utility`
    ADD FOREIGN KEY (`id_utility`) REFERENCES `utility` (`id`);

ALTER TABLE `camp`
    ADD FOREIGN KEY (`id_camp_type`) REFERENCES `camp_type` (`id`);

ALTER TABLE `camp_site_place_type`
	ADD FOREIGN KEY (`id_camp_site`) REFERENCES `camp_site` (`id`);

ALTER TABLE `camp_site_place_type`
	ADD FOREIGN KEY (`id_place_type`) REFERENCES `place_type` (`id`);

-- Insert Data into `user` Table
INSERT INTO `user` (`id`, `email`, `password`, `first_name`, `last_name`, `phone_number`, `address`, `role`, `created_at`, `status`)
VALUES
    (1, 'admin@example.com', 'admin123', 'Admin', 'User', '1234567890', '123 Admin St', 'ROLE_ADMIN', NOW(), true),
    (2, 'manager@example.com', 'manager123', 'Manager', 'User', '1234567891', '456 Manager Ave', 'ROLE_MANAGER', NOW(), true),
    (3, 'user@example.com', 'user123', 'John', 'Doe', '1234567892', '789 User Rd', 'ROLE_USER', NOW(), true),
    (4, 'staff@example.com', 'staff123', 'Jane', 'Doe', '1234567893', '123 Staff Blvd', 'ROLE_STAFF', NOW(), true);

-- Insert Data into `camp_site` Table
INSERT INTO `camp_site` (`id`, `name`, `address`, `latitude`, `longitude`, `created_at`, `status`, `id_user`)
VALUES
    (1, 'Mountain Base Camp', '123 Mountain Rd', 35.2431, 78.1234, NOW(), 'Available', 1),
    (2, 'Lake Side Glamping', '456 Lake St', 36.1234, 77.2345, NOW(), 'Available', 2),
    (3, 'Forest Eco-Cabin', '789 Forest Blvd', 37.5678, 76.3456, NOW(),  'Available', 3),
    (4, 'Desert RV Park', '101 Desert Ave', 39.1234, 75.5678, NOW(),  'Available', 4),
    (5, 'Savanna Safari Camp', '111 Savanna St', 40.2345, 74.6789, NOW(), 'Available', 4);

-- Insert Data into `camp_type` Table
INSERT INTO `camp_type` (`id`, `type`, `capacity`, `price`, `weekend_rate`, `holiday_rate`, `updated_at`, `id_camp_site`, `quantity`)
VALUES
    (1, 'Tent', 4, 100.00, 120.00, 150.00, NOW(), 1, 3),
    (2, 'Glamping', 2, 250.00, 280.00, 320.00, NOW(), 2, 2),
    (3, 'Eco-Cabin', 3, 180.00, 200.00, 240.00, NOW(), 3, 1),
    (4, 'RV', 6, 120.00, 150.00, 180.00, NOW(), 4, 2),
    (5, 'Safari Tent', 5, 220.00, 250.00, 300.00, NOW(), 5, 1);

-- Insert Data into `camp` Table (generated from camp_type quantity)
-- For "Tent" (quantity = 3)
INSERT INTO `camp` (`id`, `name`, `created_at`, `updated_at`, `id_camp_type`)
VALUES
    (1, 'Tent Camp 1', NOW(), NOW(), 1),
    (2, 'Tent Camp 2', NOW(), NOW(), 1),
    (3, 'Tent Camp 3', NOW(), NOW(), 1);

-- For "Glamping" (quantity = 2)
INSERT INTO `camp` (`id`, `name`, `created_at`, `updated_at`, `id_camp_type`)
VALUES
    (4, 'Glamping Tent 1', NOW(), NOW(), 2),
    (5, 'Glamping Tent 2', NOW(), NOW(), 2);

-- For "Eco-Cabin" (quantity = 1)
INSERT INTO `camp` (`id`, `name`, `created_at`, `updated_at`, `id_camp_type`)
VALUES
    (6, 'Eco-Cabin 1', NOW(), NOW(), 3);

-- For "RV" (quantity = 2)
INSERT INTO `camp` (`id`, `name`, `created_at`, `updated_at`, `id_camp_type`)
VALUES
    (7, 'RV Park 1', NOW(), NOW(), 4),
    (8, 'RV Park 2', NOW(), NOW(), 4);

-- For "Safari Tent" (quantity = 1)
INSERT INTO `camp` (`id`, `name`, `created_at`, `updated_at`, `id_camp_type`)
VALUES
    (9, 'Safari Tent 1', NOW(), NOW(), 5);

-- Insert Data into `booking` Table
INSERT INTO `booking` (`id`, `id_user`, `id_camp_site`, `created_at`, `status`, `total_amount`)
VALUES
    (1, 3, 1, NOW(), 'Pending', 300.00),
    (2, 4, 2, NOW(), 'Accepted', 500.00),
    (3, 2, 3, NOW(), 'Pending', 180.00),
    (4, 1, 4, NOW(), 'Completed', 240.00),
    (5, 4, 5, NOW(), 'Cancelled', 220.00);

-- Insert Data into `payment` Table
INSERT INTO `payment` (`id`, `id_booking`, `payment_method`, `total_amount`, `status`, `id_transaction`, `completed_at`)
VALUES
    (1, 1, 'Credit Card', 300.00, 'Completed', 'TXN12345', NOW()),
    (2, 2, 'PayPal', 500.00, 'Pending', 'TXN67890', NULL),
    (3, 3, 'Debit Card', 180.00, 'Failed', 'TXN11223', NULL),
    (4, 4, 'Credit Card', 240.00, 'Completed', 'TXN44556', NOW()),
    (5, 5, 'Credit Card', 220.00, 'Completed', 'TXN78901', NOW());

-- Insert Data into `facility` Table
INSERT INTO `facility` (`name`, `description`)
VALUES
    ('WiFi', 'High-speed internet access'),
    ('Hot Tub', 'Relaxing hot water pool'),
    ('Private toilet', 'Toilet in camp'),
    ('Air-conditioner', 'There is air-conditioner in camp'),
    ('Online Payment', 'Can payment through Visa'),
    ('No-smoking', 'Camp for no smoking'),
    ('Smoking', 'Camp allowed for smoking'),
    ('Socket', 'There are sockets in camp'),
    ('Public toilet', 'There is a toilet near camp'),
    ('Private bathroom', 'A bathroom in camp'),
    ('Public bathroom', 'There is a bathroom near camp'),
    ('Outside pool', 'A beautiful swimming pool next to the camp');

-- Insert Data into `camp_type_facility` Table
INSERT INTO `camp_type_facility` (`id_facility`, `id_camp_type`)
VALUES
    (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (8, 1),
    (1, 2), (2, 2), (4, 2), (5, 2), (6, 2), (8, 2),
    (1, 3), (3, 3), (5, 3), (6, 3), (8, 3);

-- Insert Data into `image` Table
INSERT INTO `image` (`id`, `id_camp_site`, `path`)
VALUES
    (1, 1, '/images/mountain_base.jpg'),
    (2, 2, '/images/lake_side.jpg'),
    (3, 3, '/images/forest_cabin.jpg'),
    (4, 4, '/images/desert_rv.jpg'),
    (5, 5, '/images/savanna_safari.jpg');

-- Insert Data into `report` Table
INSERT INTO `report` (`id`, `id_camp_site`, `id_user`, `status`, `created_at`, `message`, `report_type`)
VALUES
    (1, 1, 3, 'Resolved', NOW(), 'Eco-cabin was wonderful but the WiFi didn\'t work well.', 'Suggestion'),
    (2, 2, 2, 'Resolved', NOW(), 'Great camping experience. Would return again!', 'Complaint');

-- Insert Data into `booking_detail` Table
INSERT INTO `booking_detail` (`id`, `id_booking`, `id_camp_type`, `check_in_at`, `check_out_at`, `amount`, `comment`, `rating`, `created_at`, `add_on`)
VALUES
    (1, 1, 1, NOW(), NOW() + INTERVAL 2 DAY, 300.00, 'Great stay!', 5, NOW(), 0.00),
    (2, 2, 1, NOW(), NOW() + INTERVAL 3 DAY, 450.00, 'Wonderful experience', 4, NOW(), 50.00),
    (3, 3, 2, NOW(), NOW() + INTERVAL 1 DAY, 180.00, 'Good but noisy', 3, NOW(), 20.00),
    (4, 4, 3, NOW(), NOW() + INTERVAL 4 DAY, 240.00, 'Peaceful and relaxing', 5, NOW(), 0.00),
    (5, 5, 4, NOW(), NOW() + INTERVAL 2 DAY, 220.00, 'Too hot in the RV', 2, NOW(), 10.00);


-- Insert Data into `utility` Table
INSERT INTO `utility` (`name`)
VALUES
('Bar'),
('Parking area'),
('Waste Disposal');

-- Insert Data into `camp_site_utility` Table
INSERT INTO `camp_site_utility` (`id_camp_site`, `id_utility`)
VALUES
(1, 1), (1, 2), (1, 3),  -- Mountain Base Camp
(2, 1), (2, 2), (2, 3);  -- Lake Side Glamping

-- SELECT ct.id AS camp_type_id, c.id AS camp_id, c.name AS camp_name, ct.type AS camp_type, bd.id AS booking_detail_id
-- FROM camp_type ct
-- JOIN camp c ON c.id_camp_type = ct.id
-- LEFT JOIN booking_detail bd ON bd.id_camp = c.id
-- AND (
--     (DATE(bd.check_in_at) < '2025-01-20' AND DATE(bd.check_out_at) > '2025-01-19')
-- )
-- WHERE ct.id = 1
-- AND (bd.id IS NULL)
-- ORDER BY c.id;

SELECT ct.quantity - COUNT(bd.id)
FROM camp_type ct
JOIN booking_detail bd 
ON bd.id_camp_type = ct.id
AND(
	(DATE(bd.check_in_at) < '2025-02-05' AND DATE(bd.check_out_at) > '2025-02-06')
)
WHERE ct.id = 1
GROUP BY ct.id