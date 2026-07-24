-- ============================================================
-- CISO Dashboard — SINGLE FILE COMPLETE SETUP
-- For Windows Local Machine & Android Client Connectivity
-- ============================================================

DO $$
DECLARE
  r RECORD;
BEGIN
  FOR r IN SELECT datname FROM pg_database WHERE datname LIKE 'ciso_org_%' LOOP
    EXECUTE format('SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = %L', r.datname);
    EXECUTE format('DROP DATABASE IF EXISTS %I', r.datname);
  END LOOP;
  EXECUTE 'SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = ''cisodashboard''';
  EXECUTE 'DROP DATABASE IF EXISTS cisodashboard';
END $$;

CREATE DATABASE cisodashboard;
\c cisodashboard

CREATE TABLE IF NOT EXISTS organisations (
  id          SERIAL       PRIMARY KEY,
  org_name    VARCHAR(100) NOT NULL,
  address     TEXT,
  mobile_no   VARCHAR(20),
  slug        VARCHAR(100) UNIQUE,
  is_active   BOOLEAN      DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  org_ids INTEGER[]
);

CREATE TABLE IF NOT EXISTS checklists (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(100) DEFAULT 'General'
);

INSERT INTO organisations (org_name, address, mobile_no, slug) VALUES
('Techsec Global Private Ltd', 'Mumbai, MH', '022-11110000', 'techsec'),
('PCPL Construction', 'Pune, MH', '020-22220000', 'pcpl'),
('Acme Cyber Defense', 'Bangalore, KA', '080-33330000', 'acme'),
('Northwind Logistics', 'Delhi, DL', '011-44440000', 'northwind'),
('BlueShield Healthcare', 'Chennai, TN', '044-55550000', 'blueshield')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO users (username, password, role, org_ids) VALUES
('Radhesh', '$2b$10$ij5fm1V4Je4XdszZYxe.qejgZm1dUT00QUSWiqu60dGm86T6snuIm', 'member', ARRAY[1]),
('Ramesh',  '$2b$10$2tPIscmvkAyA2UDPDf7auuWMCOlbd/WRz3X5iLOjyJxV.0bsQSlBG', 'admin', ARRAY[1,2]),
('Raju',    '$2b$10$6kDbdUa51QkzjDGpdBNegeFdkcDl3giQVxLO5BceuROACDgfo7xc.', 'member', ARRAY[2]),
('Shubham', '$2b$10$aLIQUYdvwCbrD6pZyCGPFeAlMQ2lhHLbxk3aVIgbkK4b6G2vIYCPi', 'superAdmin', ARRAY[1,2,3,4,5]),
('Priya',   '$2b$10$B9rqc7BvA8N8n4.eJ77l/.vIpJnMhNawaYEgd15h/7ffBxKEc/aK2', 'admin', ARRAY[3]),
('Karan',   '$2b$10$ji19lQ2vyN9fIXK.ex.nbeKUDWFQ7nnFxS2S0BTGAbJkcprCWAMtm', 'admin', ARRAY[4]),
('Anita',   '$2b$10$ZujRaLJAWA6m1Mii3qdVWeoA3uL5GBISoM5cD0PvzvuLzozimqVPK', 'admin', ARRAY[5])
ON CONFLICT (username) DO NOTHING;

INSERT INTO checklists (name, category) VALUES
('Safety Audit Checklist', 'Safety'),
('Concrete Strength Inspection', 'Quality Control'),
('HVAC Operational Verification', 'HVAC'),
('Electrical Systems Compliance', 'Electrical'),
('Final Finish & Polish Punchlist', 'Final Delivery'),
('Structural Weld Integrity', 'Structural');
