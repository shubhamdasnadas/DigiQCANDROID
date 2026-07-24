-- ============================================================
-- CISO Dashboard — SINGLE FILE COMPLETE SETUP
-- For Windows Local Machine & Android Client Connectivity
-- ============================================================
--
-- INSTRUCTIONS FOR WINDOWS LOCAL MACHINE:
--
-- 1. Prerequisites:
--    - Install PostgreSQL (v15 or higher) from https://www.postgresql.org/download/windows/
--    - Make sure "C:\Program Files\PostgreSQL\<your_version>\bin" is added to your Windows Environment PATH.
--    - Install Node.js (v18 or higher) from https://nodejs.org/
--
-- 2. Database Initialization:
--    - Save this script as "C:\setup.sql"
--    - Open Command Prompt (cmd) and run the following command:
--
--      psql -U postgres -d postgres -f "C:\setup.sql"
--
--    - Enter your PostgreSQL password (set during installation) when prompted.
--    - This script will safely wipe any old "ciso_org_*" databases and recreate the central "cisodashboard" database.
--
-- 3. Next.js API Setup:
--    - Navigate to your Next.js project directory.
--    - Install PostgreSQL client and Bcrypt dependencies:
--      npm install pg bcrypt
--      npm install --save-dev @types/pg @types/bcrypt
--
-- 4. Environment Configuration (.env.local):
--    Create a file named ".env.local" at the root of your Next.js project and add:
--
--      PG_HOST=localhost
--      PG_PORT=5432
--      PG_DATABASE=cisodashboard
--      PG_USER=postgres
--      PG_PASSWORD=your_postgres_password_here
--      DATABASE_URL=postgresql://postgres:your_postgres_password_here@localhost:5432/cisodashboard
--
-- 5. Start Your Next.js Server:
--    - Run: npm run dev
--    - Your API is now serving endpoints at: http://localhost:3000
--
-- 6. Connect This Android App:
--    - If on Android Emulator: Select "Next.js Local API (10.0.2.2:3000)" from the Server Configuration dropdown.
--    - If on Physical Device: Connect your PC and mobile to the same Wi-Fi. Find your PC's LAN IP address
--      (using "ipconfig" in cmd, e.g. 192.168.1.100). Select "Custom IP Address/Port" and enter:
--      http://192.168.1.100:3000
--    - Click "Connect & Verify" to authenticate!
--
-- ============================================================


-- ============================================================
-- PHASE 1: Clean up old databases and recreate central cisodashboard
-- ============================================================
DO $$
DECLARE
  r RECORD;
BEGIN
  -- Terminate connections and drop old organization-specific databases if they exist
  FOR r IN
    SELECT datname FROM pg_database WHERE datname LIKE 'ciso_org_%'
  LOOP
    EXECUTE format(
      'SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = %L',
      r.datname
    );
    EXECUTE format('DROP DATABASE IF EXISTS %I', r.datname);
    RAISE NOTICE 'Dropped old tenant database: %', r.datname;
  END LOOP;

  -- Terminate connections and drop central cisodashboard
  EXECUTE 'SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = ''cisodashboard''';
  EXECUTE 'DROP DATABASE IF EXISTS cisodashboard';
  RAISE NOTICE 'Dropped central database: cisodashboard';
END
$$;

CREATE DATABASE cisodashboard;

-- Reconnect to the freshly-created cisodashboard database
\c cisodashboard

-- ============================================================
-- PHASE 2: Create Central Tables & Constraints
-- ============================================================

CREATE TABLE IF NOT EXISTS super_admin (
  id SERIAL PRIMARY KEY,
  role VARCHAR(50) DEFAULT 'superAdmin',
  org_ids INTEGER[]
);

CREATE TABLE IF NOT EXISTS organisations (
  id          SERIAL       PRIMARY KEY,
  org_name    VARCHAR(100) NOT NULL,
  address     TEXT,
  mobile_no   VARCHAR(20),
  slug        VARCHAR(100) UNIQUE,
  is_active   BOOLEAN      DEFAULT TRUE,
  email       VARCHAR(255),
  website     VARCHAR(255),
  industry    VARCHAR(100),
  plan        VARCHAR(50)  DEFAULT 'free',
  color       VARCHAR(20),
  description TEXT
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  org_ids INTEGER[]
);

CREATE TABLE IF NOT EXISTS api_tokens (
  id SERIAL PRIMARY KEY,
  org_id INTEGER REFERENCES organisations(id) ON DELETE CASCADE,
  api_name VARCHAR(100) NOT NULL,
  token TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS api_responses (
  id SERIAL PRIMARY KEY,
  org_id INTEGER REFERENCES organisations(id) ON DELETE CASCADE,
  api_name VARCHAR(100) NOT NULL,
  response_data JSONB,
  fetched_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_api_responses_org_api ON api_responses(org_id, api_name, fetched_at DESC);

-- Organization-level users (non-super-admin users belonging to specific orgs)
CREATE TABLE IF NOT EXISTS org_users (
  id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        INTEGER     NOT NULL REFERENCES organisations(id) ON DELETE CASCADE,
  name          VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NOT NULL,
  password      VARCHAR(255),
  role          VARCHAR(50) NOT NULL DEFAULT 'org_user',
  department    VARCHAR(100),
  is_active     BOOLEAN     NOT NULL DEFAULT TRUE,
  allowed_pages TEXT[],
  created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (email, org_id)
);

CREATE INDEX IF NOT EXISTS idx_org_users_org_id ON org_users(org_id);
CREATE INDEX IF NOT EXISTS idx_org_users_email  ON org_users(email);

-- Automatic updated_at trigger function
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_org_users_updated_at
  BEFORE UPDATE ON org_users
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Cron sharding configuration
CREATE TABLE IF NOT EXISTS cron_config (
  key   TEXT PRIMARY KEY,
  value TEXT NOT NULL
);

INSERT INTO cron_config (key, value) VALUES ('total_shards', '1') ON CONFLICT (key) DO NOTHING;

-- Multi-Tenant database backup helper tables
CREATE TABLE IF NOT EXISTS checklists (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(100) DEFAULT 'General'
);


-- ============================================================
-- PHASE 3: Seeding Central Data
-- ============================================================

-- 1. Seed the 5 organisations
INSERT INTO organisations (org_name, address, mobile_no, slug) VALUES
('Techsec Global Private Ltd', 'Mumbai, MH',     '022-11110000', 'techsec'),
('PCPL Construction',           'Pune, MH',       '020-22220000', 'pcpl'),
('Acme Cyber Defense',          'Bangalore, KA',  '080-33330000', 'acme'),
('Northwind Logistics',         'Delhi, DL',      '011-44440000', 'northwind'),
('BlueShield Healthcare',       'Chennai, TN',    '044-55550000', 'blueshield')
ON CONFLICT (slug) DO NOTHING;

-- 2. Seed the 7 central users with REAL bcrypt password hashes (cost 10)
-- Credentials mapping for reference:
--   Radhesh  -> Radhesh@123   (member,     org 1: Techsec)
--   Ramesh   -> Ramesh@123    (admin,      orgs 1, 2: Techsec, PCPL)
--   Raju     -> Raju@123      (member,     org 2: PCPL)
--   Shubham  -> Shubham@123   (superAdmin, orgs 1, 2, 3, 4, 5)
--   Priya    -> Priya@123     (admin,      org 3: Acme)
--   Karan    -> Karan@123     (admin,      org 4: Northwind)
--   Anita    -> Anita@123     (admin,      org 5: BlueShield)
INSERT INTO users (username, password, role, org_ids) VALUES
('Radhesh', '$2b$10$ij5fm1V4Je4XdszZYxe.qejgZm1dUT00QUSWiqu60dGm86T6snuIm', 'member',     ARRAY[1]),
('Ramesh',  '$2b$10$2tPIscmvkAyA2UDPDf7auuWMCOlbd/WRz3X5iLOjyJxV.0bsQSlBG', 'admin',      ARRAY[1,2]),
('Raju',    '$2b$10$6kDbdUa51QkzjDGpdBNegeFdkcDl3giQVxLO5BceuROACDgfo7xc.', 'member',     ARRAY[2]),
('Shubham', '$2b$10$aLIQUYdvwCbrD6pZyCGPFeAlMQ2lhHLbxk3aVIgbkK4b6G2vIYCPi', 'superAdmin', ARRAY[1,2,3,4,5]),
('Priya',   '$2b$10$B9rqc7BvA8N8n4.eJ77l/.vIpJnMhNawaYEgd15h/7ffBxKEc/aK2', 'admin',      ARRAY[3]),
('Karan',   '$2b$10$ji19lQ2vyN9fIXK.ex.nbeKUDWFQ7nnFxS2S0BTGAbJkcprCWAMtm', 'admin',      ARRAY[4]),
('Anita',   '$2b$10$ZujRaLJAWA6m1Mii3qdVWeoA3uL5GBISoM5cD0PvzvuLzozimqVPK', 'admin',      ARRAY[5])
ON CONFLICT (username) DO NOTHING;

-- 3. Seed some dummy organization-level API integration tokens
INSERT INTO api_tokens (org_id, api_name, token) VALUES
(1, 'SentinelOne', 'token_s1_org1_demo_xxxxxxxxxxxx'),
(1, 'Firewall',    'token_fw_org1_demo_xxxxxxxxxxxx'),
(2, 'SentinelOne', 'token_s1_org2_demo_xxxxxxxxxxxx'),
(2, 'Checkpoint',  'token_cp_org2_demo_xxxxxxxxxxxx');

-- 4. Seed Quality & Compliance checklists for client sync
INSERT INTO checklists (name, category) VALUES
('Safety Audit Checklist', 'Safety'),
('Concrete Strength Inspection', 'Quality Control'),
('HVAC Operational Verification', 'HVAC'),
('Electrical Systems Compliance', 'Electrical'),
('Final Finish & Polish Punchlist', 'Final Delivery'),
('Structural Weld Integrity', 'Structural');

-- ============================================================
-- SETUP COMPLETE! Ready for Windows connections.
-- ============================================================
