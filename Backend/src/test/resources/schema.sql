CREATE SCHEMA IF NOT EXISTS public;
DROP TABLE IF EXISTS public.address,public.actor,public.country,public.language CASCADE;
CREATE TABLE IF NOT EXISTS public.actor (
                    actor_id SERIAL PRIMARY KEY,
                              active boolean,
                              first_name text,
                              last_name text,
                              last_update timestamp with time zone
);

CREATE TABLE IF NOT EXISTS public.address (
                                address_id SERIAL PRIMARY KEY,
                                address text,
                                address2 text,
                                district text,
                                city_id smallint,
                                postal_code text,
                                phone text,
                                last_update timestamp with time zone
);

CREATE TABLE IF NOT EXISTS public.country (
                                country_id SERIAL PRIMARY KEY,
                                country text,
                                active boolean,
                                last_update timestamp with time zone
);

CREATE TABLE IF NOT EXISTS public.language (
                                 language_id SERIAL PRIMARY KEY,
                                 name character varying,
                                 active boolean,
                                 last_update timestamp with time zone
);

