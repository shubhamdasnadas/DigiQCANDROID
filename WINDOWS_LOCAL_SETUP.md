# Windows Local Machine Setup Guide
## Connecting Android Studio App with Local PostgreSQL & Next.js Backend

This guide walks you through setting up your local database, configuring your Next.js backend, and connecting the Android app running in Android Studio to your local server.

---

### Phase 1: PostgreSQL Database Setup

1. **Install PostgreSQL**:
   - Download and install PostgreSQL (version 15 or 16 recommended) from the official website:
     [PostgreSQL Windows Download](https://www.postgresql.org/download/windows/)
   - Set a memorable password for the default `postgres` user during the installation wizard (e.g., `root` or `admin123`).
   - Ensure the box for **pgAdmin 4** and **Command Line Tools** is checked.

2. **Add PostgreSQL to your Windows Environment PATH**:
   - Open Windows Search, type `Environment Variables`, and edit the system environment variables.
   - Click **Environment Variables**, find the **Path** variable under **System Variables**, and click **Edit**.
   - Add a new line pointing to your PostgreSQL binary directory, e.g.:
     `C:\Program Files\PostgreSQL\16\bin` (replace `16` with your installed version).
   - Click **OK** to save and close.

3. **Run the Database Setup Script**:
   - The setup script is located at the root of your Android project folder as `setup.sql`.
   - Copy `setup.sql` to a simple directory, like `C:\setup.sql`.
   - Open **Command Prompt (cmd)** or **PowerShell** as Administrator, and run:
     ```cmd
     psql -U postgres -d postgres -f "C:\setup.sql"
     ```
   - Enter your PostgreSQL password when prompted.
   - This script automatically:
     - Wipes any old tenant databases safely.
     - Recreates the central database `cisodashboard`.
     - Automatically seeds the **5 Organisations**, **7 Multi-tenant Users** (with secure bcrypt hashes), and **6 Inspection Checklists**.

---

### Phase 2: Next.js Backend Configuration

1. **Install PostgreSQL Client & Bcrypt**:
   - Navigate to the root folder of your **Next.js** project in cmd:
     ```cmd
     cd C:\path-to-your-nextjs-project
     ```
   - Install the required packages for PostgreSQL connectivity and password decryption:
     ```cmd
     npm install pg bcrypt
     npm install --save-dev @types/pg @types/bcrypt
     ```

2. **Configure Environment Variables (`.env.local`)**:
   - Create a `.env.local` file at the root of your Next.js project.
   - Add the following configuration (replace `your_postgres_password_here` with your actual password):
     ```env
     PG_HOST=localhost
     PG_PORT=5432
     PG_DATABASE=cisodashboard
     PG_USER=postgres
     PG_PASSWORD=your_postgres_password_here
     DATABASE_URL=postgresql://postgres:your_postgres_password_here@localhost:5432/cisodashboard
     ```

3. **Create the Next.js App Router API Routes**:

   - **Login Endpoint** (`app/api/login/route.ts`):
     ```typescript
     import { NextResponse } from 'next/server';
     import { Pool } from 'pg';
     import bcrypt from 'bcrypt';

     const pool = new Pool({
       connectionString: process.env.DATABASE_URL
     });

     export async function POST(request: Request) {
       try {
         const { username, password, organization } = await request.json();
         
         // 1. Fetch organization by name or slug
         const orgRes = await pool.query(
           'SELECT * FROM organisations WHERE org_name = $1 OR slug = $2', 
           [organization, organization]
         );
         if (orgRes.rows.length === 0) {
           return NextResponse.json({ success: false, message: 'Organisation not found' });
         }
         const org = orgRes.rows[0];

         // 2. Query central users table
         const userRes = await pool.query('SELECT * FROM users WHERE username = $1', [username]);
         if (userRes.rows.length > 0) {
           const user = userRes.rows[0];
           
           // Decrypt and compare password
           const match = await bcrypt.compare(password, user.password);
           const isPlaintextMatch = password === user.password; // fallback security check
           
           if ((match || isPlaintextMatch) && user.org_ids.includes(org.id)) {
             return NextResponse.json({
               success: true,
               message: 'Authentication successful',
               username: user.username,
               organization: org.org_name
             });
           }
         }

         // 3. Optional org-level users lookup fallback
         const orgUserRes = await pool.query(
           'SELECT * FROM org_users WHERE (name = $1 OR email = $2) AND org_id = $3',
           [username, username, org.id]
         );
         if (orgUserRes.rows.length > 0) {
           const orgUser = orgUserRes.rows[0];
           if (orgUser.password === password) {
             return NextResponse.json({
               success: true,
               message: 'Authentication successful',
               username: orgUser.name,
               organization: org.org_name
             });
           }
         }

         return NextResponse.json({ success: false, message: 'Invalid credentials or unauthorized for this org' });
       } catch (err: any) {
         return NextResponse.json({ success: false, message: err.message }, { status: 500 });
       }
     }
     ```

   - **Checklists Endpoint** (`app/api/checklists/route.ts`):
     ```typescript
     import { NextResponse } from 'next/server';
     import { Pool } from 'pg';

     const pool = new Pool({
       connectionString: process.env.DATABASE_URL
     });

     export async function GET() {
       try {
         const result = await pool.query('SELECT name, category FROM checklists ORDER BY name ASC');
         return NextResponse.json(result.rows);
       } catch (err: any) {
         return NextResponse.json({ error: err.message }, { status: 500 });
       }
     }
     ```

   - **Organizations Endpoint** (`app/api/organizations/route.ts`):
     ```typescript
     import { NextResponse } from 'next/server';
     import { Pool } from 'pg';

     const pool = new Pool({
       connectionString: process.env.DATABASE_URL
     });

     export async function GET() {
       try {
         const result = await pool.query('SELECT org_name AS name, slug AS code FROM organisations ORDER BY org_name ASC');
         return NextResponse.json(result.rows);
       } catch (err: any) {
         return NextResponse.json({ error: err.message }, { status: 500 });
       }
     }
     ```

4. **Start local Next.js**:
   - Run the development server:
     ```cmd
     npm run dev
     ```
   - Verify it's active by visiting `http://localhost:3000/api/organizations` in your browser.

---

### Phase 3: Android App Setup in Android Studio

1. **Open Project**:
   - Launch **Android Studio**.
   - Select **Open an Existing Project** and browse to the extracted root folder.
   - Let Gradle sync completely (this usually takes 1-2 minutes).

2. **Run on Emulator (Localhost Sync)**:
   - Start an Android Virtual Device (AVD) from the Android Studio Device Manager.
   - Run the app by clicking the green **Run** (Play) button.
   - On the login screen, expand the **Server Configuration** card.
   - Choose **"Next.js Local API (10.0.2.2:3000)"** on the server dropdown.
   - *Why `10.0.2.2`?* The Android Emulator runs in a virtual sandbox; `10.0.2.2` is a special alias configured by Android to loop back to your local Windows PC host machine (`localhost`).

3. **Run on Physical Android Phone (Wi-Fi Sync)**:
   - Enable **Developer Options** and **USB Debugging** on your phone.
   - Connect both your PC and your phone to the **same Wi-Fi network**.
   - Find your PC's local Wi-Fi IP address:
     - Open cmd and type `ipconfig`.
     - Look for the **IPv4 Address** of your Wireless LAN adapter (e.g., `192.168.1.100`).
   - In the Android app, expand **Server Configuration**, select **"Custom IP Address/Port"**, and enter:
     `http://192.168.1.100:3000`
   - Click **Connect & Verify** to establish a secure, local link.

---

### Phase 4: Test Credentials

Use these seeded credentials inside the app once connected:

| Username | Password | Role | Authorized Organisations |
| :--- | :--- | :--- | :--- |
| **Shubham** | `Shubham@123` | superAdmin | *All 5 organizations!* |
| **Ramesh** | `Ramesh@123` | admin | Techsec Global, PCPL Construction |
| **Radhesh** | `Radhesh@123` | member | Techsec Global Private Ltd |
| **Raju** | `Raju@123` | member | PCPL Construction |
| **Priya** | `Priya@123` | admin | Acme Cyber Defense |
| **Karan** | `Karan@123` | admin | Northwind Logistics |
| **Anita** | `Anita@123` | admin | BlueShield Healthcare |
