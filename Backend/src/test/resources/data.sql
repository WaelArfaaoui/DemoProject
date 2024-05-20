-- Actors
INSERT INTO public.actor (active, first_name, last_name, last_update)
VALUES  (true, 'John', 'Doe', CURRENT_TIMESTAMP),
       (true, 'Jane', 'Smith', CURRENT_TIMESTAMP),
       (true, 'Michael', 'Johnson', CURRENT_TIMESTAMP);


-- Addresses
INSERT INTO public.address (address, address2, district, city_id, postal_code, phone, last_update)
VALUES ('123 Main St', 'Apt 101', 'Downtown', 1, '12345', '555-1234', CURRENT_TIMESTAMP),
       ('456 Elm St', NULL, 'Suburbia', 2, '67890', '555-5678', CURRENT_TIMESTAMP),
       ('789 Oak St', 'Unit 3', 'Rural', 3, '54321', '555-9876', CURRENT_TIMESTAMP);

-- Countries
INSERT INTO public.country (country, active, last_update)
VALUES ('Tunisia', true, CURRENT_TIMESTAMP),
       ('United Kingdom', true, CURRENT_TIMESTAMP),
       ('Canada', true, CURRENT_TIMESTAMP);

-- Languages
INSERT INTO public.language (name, active, last_update)
VALUES ('English', true, CURRENT_TIMESTAMP),
       ('Spanish', true, CURRENT_TIMESTAMP),
       ('French', true, CURRENT_TIMESTAMP);
