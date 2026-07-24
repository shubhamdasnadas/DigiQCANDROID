package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Contact
import com.example.data.Issue
import com.example.ui.*
import com.example.ui.components.Valid8Logo
import com.example.ui.components.Valid8LogoIcon
import com.example.ui.theme.OrangeLight
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TextGrey
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// ==========================================
// 1. SIGN IN SCREEN
// ==========================================
@Composable
fun SignInScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val signInInput by viewModel.signInInput.collectAsState()
    val signInError by viewModel.signInError.collectAsState()
    val loginMethod by viewModel.loginMethod.collectAsState()
    val passwordInput by viewModel.passwordInput.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val context = LocalContext.current

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030712), // Deep Onyx
                    Color(0xFF0C0E1E), // Obsidian Navy
                    Color(0xFF1E1B4B)  // Midnight Indigo
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9FAFB),
                    Color(0xFFEEF2FF), // Lavender mist
                    Color(0xFFE0E7FF)  // Slate blue-grey light
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        val isWide = maxWidth > 600.dp
        
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.85f else 1.0f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f),
                    shape = RoundedCornerShape(24.dp)
                )
                .shadow(
                    elevation = if (isWide) 16.dp else 4.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .verticalScroll(rememberScrollState())
                .padding(all = if (isWide) 32.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Valid8Logo()
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Sign in",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter username, mobile number or email",
                fontSize = 16.sp,
                color = TextGrey,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = signInInput,
                onValueChange = { viewModel.updateSignInInput(it) },
                placeholder = { Text("Username, Email or phone") },
                modifier = Modifier.fillMaxWidth(),
                isError = signInError != null,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            if (signInError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = signInError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            // Beautiful selector for OTP / Password (matching the screenshot exactly!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(Color(0xFF0F172A).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LoginMethod.values().forEach { method ->
                    val isSelected = loginMethod == method
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color(0xFF0A0F1D) else Color(0xFF1E293B).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) Color.White.copy(alpha = 0.12f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.updateLoginMethod(method) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (method == LoginMethod.PASSWORD) "Password" else "OTP Code",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            if (loginMethod == LoginMethod.PASSWORD) {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { viewModel.updatePasswordInput(it) },
                    placeholder = { Text("Enter password") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                if (passwordError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = passwordError ?: "",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            } else {
                Text(
                    text = "A 4-digit code will be sent to confirm your identity.",
                    fontSize = 13.sp,
                    color = TextGrey,
                    modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Switch Organisation Selector
            var orgExpanded by remember { mutableStateOf(false) }
            val organizations by viewModel.organizations.collectAsState()
            val selectedOrganization by viewModel.selectedOrganization.collectAsState()

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Organisation",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGrey,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { orgExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Business,
                                contentDescription = "Organisation",
                                tint = Color(0xFF3B82F6), // Blue Business Icon
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedOrganization,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = if (orgExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Indicator",
                            tint = TextGrey
                        )
                    }
                    DropdownMenu(
                        expanded = orgExpanded,
                        onDismissRequest = { orgExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        organizations.forEach { org ->
                            DropdownMenuItem(
                                text = { Text(org, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    viewModel.updateSelectedOrganization(org)
                                    orgExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Server Configuration Dropdown (matches the screenshot exactly!)
            var serverExpanded by remember { mutableStateOf(false) }
            val serverOptions = listOf(
                "Server Configuration",
                "Next.js Local API (10.0.2.2:3000)",
                "Production Cloud Server",
                "Next.js Staging Server (Port 3001)",
                "Custom IP Address/Port"
            )
            var selectedServer by remember { mutableStateOf("Server Configuration") }
            var customUrlInput by remember { mutableStateOf("http://10.0.2.2:3000/") }

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { serverExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                                contentDescription = "Server Configuration",
                                tint = Color(0xFF8B5CF6), // Purple Gear Icon
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedServer,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = if (serverExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Indicator",
                            tint = TextGrey
                        )
                    }
                    DropdownMenu(
                        expanded = serverExpanded,
                        onDismissRequest = { serverExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        serverOptions.forEach { srv ->
                            DropdownMenuItem(
                                text = { Text(srv, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    selectedServer = srv
                                    serverExpanded = false
                                    if (srv != "Custom IP Address/Port") {
                                        // Dynamically switch Base Url of the API & reload
                                        val newUrl = when (srv) {
                                            "Next.js Local API (10.0.2.2:3000)" -> "http://10.0.2.2:3000/"
                                            "Production Cloud Server" -> "https://api.valid8.com/"
                                            "Next.js Staging Server (Port 3001)" -> "http://10.0.2.2:3001/"
                                            else -> "http://10.0.2.2:3000/"
                                        }
                                        com.example.data.CisoBackendApi.updateBaseUrl(newUrl)
                                        viewModel.fetchDatabaseData()
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (selectedServer == "Custom IP Address/Port") {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Custom Server Base URL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGrey,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customUrlInput,
                        onValueChange = { customUrlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. http://192.168.1.100:3000/") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            com.example.data.CisoBackendApi.updateBaseUrl(customUrlInput)
                            viewModel.fetchDatabaseData()
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Connect & Verify", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Developer Hub Card
            var showDevHub by remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDevHub = !showDevHub },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Developer Hub",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Developer Backend Hub",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showDevHub) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand Developer Hub",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (showDevHub) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Expose your PostgreSQL database ('digiQC') inside your Next.js project using these API routes. This allows both your web frontend and this Android client to securely share the same PostgreSQL backend:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Tab selectors for Next.js App Router, Pages Router, Database SQL, .env.local, Setup Guide
                        var selectedDevTab by remember { mutableStateOf(0) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("App Router", "Pages Router", "Database SQL", ".env.local", "Setup Guide").forEachIndexed { idx, tabTitle ->
                                Button(
                                    onClick = { selectedDevTab = idx },
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedDevTab == idx) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedDevTab == idx) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text(tabTitle, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = LocalContext.current

                        val codeText = when (selectedDevTab) {
                            0 -> """
                            // 1. Create app/api/login/route.ts
                            import { NextResponse } from 'next/server';
                            import { Pool } from 'pg';
                            import bcrypt from 'bcrypt';

                            const pool = new Pool({
                              connectionString: process.env.DATABASE_URL
                            });

                            export async function POST(request: Request) {
                              try {
                                const { username, password, organization } = await request.json();
                                
                                // Find organisation by name or slug
                                const orgRes = await pool.query(
                                  'SELECT * FROM organisations WHERE org_name = ${'$'}1 OR slug = ${'$'}2', 
                                  [organization, organization]
                                );
                                if (orgRes.rows.length === 0) {
                                  return NextResponse.json({ success: false, message: 'Organisation not found' });
                                }
                                const org = orgRes.rows[0];

                                // Check central users table
                                const userRes = await pool.query('SELECT * FROM users WHERE username = ${'$'}1', [username]);
                                if (userRes.rows.length > 0) {
                                  const user = userRes.rows[0];
                                  const match = await bcrypt.compare(password, user.password);
                                  const isPlaintextMatch = password === user.password; // fallback
                                  
                                  if ((match || isPlaintextMatch) && user.org_ids.includes(org.id)) {
                                    return NextResponse.json({
                                      success: true,
                                      message: 'Authentication successful',
                                      username: user.username,
                                      organization: org.org_name
                                    });
                                  }
                                }

                                // Check org-level users fallback
                                const orgUserRes = await pool.query(
                                  'SELECT * FROM org_users WHERE (name = ${'$'}1 OR email = ${'$'}2) AND org_id = ${'$'}3',
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

                                return NextResponse.json({ success: false, message: 'Invalid credentials or unauthorized' });
                              } catch (err: any) {
                                return NextResponse.json({ success: false, message: err.message }, { status: 500 });
                              }
                            }

                            // 2. Create app/api/checklists/route.ts
                            export async function GET() {
                              try {
                                const result = await pool.query('SELECT name, category FROM checklists ORDER BY name ASC');
                                return NextResponse.json(result.rows);
                              } catch (err: any) {
                                return NextResponse.json({ error: err.message }, { status: 500 });
                              }
                            }

                            // 3. Create app/api/organizations/route.ts
                            export async function GET() {
                              try {
                                const result = await pool.query('SELECT org_name AS name, slug AS code FROM organisations ORDER BY org_name ASC');
                                return NextResponse.json(result.rows);
                              } catch (err: any) {
                                return NextResponse.json({ error: err.message }, { status: 500 });
                              }
                            }
                            """.trimIndent()

                            1 -> """
                            // 1. Create pages/api/login.ts
                            import type { NextApiRequest, NextApiResponse } from 'next';
                            import { Pool } from 'pg';
                            import bcrypt from 'bcrypt';

                            const pool = new Pool({
                              connectionString: process.env.DATABASE_URL
                            });

                            export default async function handler(req: NextApiRequest, res: NextApiResponse) {
                              if (req.method !== 'POST') return res.status(405).json({ message: 'Method Not Allowed' });
                              try {
                                const { username, password, organization } = req.body;
                                const orgRes = await pool.query('SELECT * FROM organisations WHERE org_name = ${'$'}1 OR slug = ${'$'}2', [organization, organization]);
                                if (orgRes.rows.length === 0) return res.status(200).json({ success: false, message: 'Organisation not found' });
                                const org = orgRes.rows[0];

                                const userRes = await pool.query('SELECT * FROM users WHERE username = ${'$'}1', [username]);
                                if (userRes.rows.length > 0) {
                                  const user = userRes.rows[0];
                                  if ((await bcrypt.compare(password, user.password) || password === user.password) && user.org_ids.includes(org.id)) {
                                    return res.status(200).json({ success: true, message: 'Authentication successful', username: user.username, organization: org.org_name });
                                  }
                                }
                                res.status(200).json({ success: false, message: 'Invalid credentials' });
                              } catch (err: any) {
                                res.status(500).json({ success: false, message: err.message });
                              }
                            }
                            """.trimIndent()

                            2 -> """
                            -- ============================================================
                            -- PostgreSQL database setup for 'cisodashboard'
                            -- ============================================================
                            CREATE DATABASE cisodashboard;
                            \c cisodashboard

                            CREATE TABLE organisations (
                              id          SERIAL       PRIMARY KEY,
                              org_name    VARCHAR(100) NOT NULL,
                              address     TEXT,
                              mobile_no   VARCHAR(20),
                              slug        VARCHAR(100) UNIQUE,
                              is_active   BOOLEAN      DEFAULT TRUE
                            );

                            CREATE TABLE users (
                              id SERIAL PRIMARY KEY,
                              username VARCHAR(100) UNIQUE NOT NULL,
                              password VARCHAR(255) NOT NULL,
                              role VARCHAR(50) NOT NULL,
                              org_ids INTEGER[]
                            );

                            CREATE TABLE checklists (
                              id SERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              category VARCHAR(100) DEFAULT 'General'
                            );

                            -- Seed organizations
                            INSERT INTO organisations (org_name, slug) VALUES
                            ('Techsec Global Private Ltd', 'techsec'),
                            ('PCPL Construction', 'pcpl'),
                            ('Acme Cyber Defense', 'acme'),
                            ('Northwind Logistics', 'northwind'),
                            ('BlueShield Healthcare', 'blueshield');

                            -- Seed checklists
                            INSERT INTO checklists (name, category) VALUES
                            ('Safety Audit Checklist', 'Safety'),
                            ('Concrete Strength Inspection', 'Quality Control'),
                            ('HVAC Operational Verification', 'HVAC'),
                            ('Electrical Systems Compliance', 'Electrical'),
                            ('Final Finish & Polish Punchlist', 'Final Delivery'),
                            ('Structural Weld Integrity', 'Structural');
                            """.trimIndent()

                            3 -> """
                            # Place these in .env.local at the root of your Next.js project
                            PG_HOST=localhost
                            PG_PORT=5432
                            PG_DATABASE=cisodashboard
                            PG_USER=postgres
                            PG_PASSWORD=your_postgres_password_here
                            DATABASE_URL=postgresql://postgres:your_postgres_password_here@localhost:5432/cisodashboard
                            """.trimIndent()

                            else -> """
                            1. Save the 'setup.sql' file created at the project root to your computer.
                            
                            2. Run the database setup script in cmd/PowerShell:
                               psql -U postgres -d postgres -f "C:\setup.sql"

                            3. Install dependencies in your Next.js directory:
                               npm install pg bcrypt
                               npm install --save-dev @types/pg @types/bcrypt

                            4. Add your database configuration in '.env.local' and run:
                               npm run dev

                            5. To connect this Android app to your running local server:
                               - EMULATOR: Select "Next.js Local API (10.0.2.2:3000)".
                               - PHYSICAL PHONE: Ensure phone and PC are on the same Wi-Fi.
                                 Select "Custom IP Address/Port", input:
                                 http://YOUR_PC_IP:3000
                                 and click "Connect & Verify".
                            """.trimIndent()
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color(0xFF0F172A), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF334155), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when(selectedDevTab) {
                                            0 -> "App Router API"
                                            1 -> "Pages Router API"
                                            2 -> "Database SQL"
                                            3 -> ".env.local"
                                            else -> "Connection Guide"
                                        },
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(codeText))
                                            android.widget.Toast.makeText(context, "Copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy code",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 4.dp))
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = codeText,
                                        color = if (selectedDevTab == 4) Color(0xFFE2E8F0) else Color(0xFF34D399),
                                        fontSize = 11.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // PostgreSQL Connection Status Bar / Offline database fallback
            val dbStatus by viewModel.dbStatus.collectAsState()
            val dbConnected by viewModel.dbConnected.collectAsState()
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (dbConnected) Color(0xFF10B981) else Color(0xFFFFA500), // Amber/Orange
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dbStatus,
                    fontSize = 12.sp,
                    color = if (dbConnected) Color(0xFF10B981) else Color(0xFFF59E0B), // Glowing orange/amber text
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.handleSignIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A) // Dark Indigo-Slate Button matching screenshot
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// ==========================================
// 2. HOME SCREEN (Responsive with Rail / Bottom Nav)
// ==========================================
@Composable
fun SidebarDrawerContent(
    viewModel: AppViewModel,
    onClose: () -> Unit
) {
    val signInInput by viewModel.signInInput.collectAsState()
    val email = if (signInInput.contains("@")) signInInput else "saharsh.s@techsecdigital.com"
    val displayName = if (signInInput.contains("@") && signInInput.substringBefore("@").isNotBlank()) {
        signInInput.substringBefore("@").split(".").joinToString(" ") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    } else {
        "Saharsh Sathyanarayanan"
    }

    val isDarkModeState by viewModel.isDarkMode.collectAsState()
    val adaptiveGrey = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        // 1. User details section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Profile circular avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Avatar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = displayName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = email,
                fontSize = 13.sp,
                color = adaptiveGrey
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Phone and VERIFIED status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "+919664734717",
                    fontSize = 13.sp,
                    color = adaptiveGrey
                )

                // Verified Badge
                Box(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VERIFIED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = adaptiveGrey
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Organization row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                .clickable { /* company action */ }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Organization Globe",
                    tint = OrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Pranav Constructions Limited",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Navigation List Items
        val items = listOf(
            Triple("Web portal", Icons.Default.Laptop, "Web portal"),
            Triple("Support", Icons.Default.HelpOutline, "Support"),
            Triple("Find Location", Icons.Default.Explore, "Find Location"),
            Triple("View logs", Icons.Default.List, "View logs"),
            Triple("Change password", Icons.Default.Edit, "Change password")
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items.forEach { (label, icon, action) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onClose()
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = adaptiveGrey,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4. Beautiful Interactive Theme Mode Switcher
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "THEME MODE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = adaptiveGrey,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Light Mode option
                val isLightSelected = isDarkModeState == false
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isLightSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { viewModel.setDarkMode(false) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Light Mode",
                            tint = if (isLightSelected) OrangePrimary else adaptiveGrey,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Light",
                            fontSize = 12.sp,
                            fontWeight = if (isLightSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isLightSelected) MaterialTheme.colorScheme.onSurface else adaptiveGrey
                        )
                    }
                }

                // Dark Mode option
                val isDarkSelected = isDarkModeState == true
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isDarkSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { viewModel.setDarkMode(true) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = "Dark Mode",
                            tint = if (isDarkSelected) OrangePrimary else adaptiveGrey,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Dark",
                            fontSize = 12.sp,
                            fontWeight = if (isDarkSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isDarkSelected) MaterialTheme.colorScheme.onSurface else adaptiveGrey
                        )
                    }
                }

                // System Mode option
                val isSystemSelected = isDarkModeState == null
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSystemSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { viewModel.setDarkMode(null) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsSuggest,
                            contentDescription = "System Mode",
                            tint = if (isSystemSelected) OrangePrimary else adaptiveGrey,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "System",
                            fontSize = 12.sp,
                            fontWeight = if (isSystemSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSystemSelected) MaterialTheme.colorScheme.onSurface else adaptiveGrey
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Logout and Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Log out button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClose()
                        viewModel.logout()
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Log out",
                    tint = OrangePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Log out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valid8-mobile",
                    fontSize = 11.sp,
                    color = adaptiveGrey
                )
                Text(
                    text = "App Ver: 3.21.0.255",
                    fontSize = 11.sp,
                    color = adaptiveGrey
                )
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val currentTab by viewModel.currentBottomTab.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030712), // Deep Onyx
                    Color(0xFF0C0E1E), // Obsidian Navy
                    Color(0xFF1E1B4B)  // Midnight Indigo
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9FAFB),
                    Color(0xFFEEF2FF), // Lavender mist
                    Color(0xFFE0E7FF)  // Slate blue-grey light
                )
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarDrawerContent(
                viewModel = viewModel,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(bgBrush)
        ) {
            val isWide = maxWidth > 720.dp

            if (isWide) {
                // Wide Screen: Navigation Rail + Side-by-side Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Logo",
                            tint = OrangePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        BottomNavTab.values().forEach { tab ->
                            NavigationRailItem(
                                selected = currentTab == tab,
                                onClick = { viewModel.updateBottomTab(tab) },
                                icon = { Icon(getTabIcon(tab), contentDescription = tab.name) },
                                label = { Text(getTabLabel(tab)) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = OrangePrimary,
                                    selectedTextColor = OrangePrimary,
                                    indicatorColor = OrangeLight
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        // Logout button
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    VerticalDivider(color = Color.LightGray)

                    // Content Panel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        HomeContent(viewModel = viewModel, isWide = true, onMenuClick = { scope.launch { drawerState.open() } })
                    }
                }
            } else {
                // Compact Screen: Standard Scaffold with Bottom Navigation
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            BottomNavTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = currentTab == tab,
                                    onClick = { viewModel.updateBottomTab(tab) },
                                    icon = { Icon(getTabIcon(tab), contentDescription = tab.name) },
                                    label = { Text(getTabLabel(tab), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = OrangePrimary,
                                        selectedTextColor = OrangePrimary,
                                        indicatorColor = OrangeLight
                                    )
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        HomeContent(viewModel = viewModel, isWide = false, onMenuClick = { scope.launch { drawerState.open() } })
                    }
                }
            }
        }
    }
}

fun getTabIcon(tab: BottomNavTab): ImageVector {
    return when (tab) {
        BottomNavTab.DASHBOARD -> Icons.Filled.Explore
        BottomNavTab.INSPECTION -> Icons.Filled.FactCheck
        BottomNavTab.INSTRUCTION -> Icons.Filled.Article
        BottomNavTab.TODO -> Icons.Filled.ShoppingBag
    }
}

fun getTabLabel(tab: BottomNavTab): String {
    return when (tab) {
        BottomNavTab.DASHBOARD -> "Dashboard"
        BottomNavTab.INSPECTION -> "Inspection"
        BottomNavTab.INSTRUCTION -> "Instruction"
        BottomNavTab.TODO -> "To-Do"
    }
}


@Composable
fun HomeContent(viewModel: AppViewModel, isWide: Boolean, onMenuClick: () -> Unit) {
    val currentTab by viewModel.currentBottomTab.collectAsState()
    
    // Smooth animated tab shifting with logo loader
    var activeTabState by remember { mutableStateOf(currentTab) }
    var isTransitioning by remember { mutableStateOf(false) }

    LaunchedEffect(currentTab) {
        if (currentTab != activeTabState) {
            isTransitioning = true
            delay(500) // Beautiful 500ms transitional animated logo loading
            activeTabState = currentTab
            isTransitioning = false
        }
    }

    Crossfade(
        targetState = if (isTransitioning) null else activeTabState,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "TabSwitchTransition"
    ) { tab ->
        if (tab == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Valid8LogoIcon(
                        sizeDp = 96.dp,
                        animate = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = when (currentTab) {
                            BottomNavTab.DASHBOARD -> "Assembling Dashboard..."
                            BottomNavTab.INSPECTION -> "Loading Inspections..."
                            BottomNavTab.INSTRUCTION -> "Fetching Guides..."
                            BottomNavTab.TODO -> "Preparing Tasks..."
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981), // Neon Mint
                        letterSpacing = 0.5.sp
                    )
                }
            }
        } else {
            when (tab) {
                BottomNavTab.DASHBOARD -> DashboardTabView(viewModel, isWide, onMenuClick)
                BottomNavTab.INSPECTION -> InspectionTabView(viewModel, isWide)
                BottomNavTab.INSTRUCTION -> InstructionTabView(viewModel, isWide)
                BottomNavTab.TODO -> TodoTabView(viewModel, isWide)
            }
        }
    }
}

// ==========================================
// A. DASHBOARD TAB VIEW
// ==========================================
@Composable
fun ToDoStatusCard(
    label: String,
    count: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top half: background adapts automatically based on light/dark mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Bottom half: adapts based on light/dark mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = count,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardProjectCard(
    initials: String,
    fullName: String,
    timezone: String,
    onChecklistClick: () -> Unit,
    onReportClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Initials (Large Bold)
                Text(
                    text = initials,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Full name
                Text(
                    text = fullName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Timezone
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Timezone",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = timezone,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Two split action buttons at the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checklist/pencil button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onChecklistClick() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = "Checklist",
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )

                // Document/report button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onReportClick() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Report",
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardTabView(viewModel: AppViewModel, isWide: Boolean, onMenuClick: () -> Unit) {
    val issues by viewModel.allIssues.collectAsState()
    
    // Dynamic counts from database based on status
    val approvedCount = issues.count { it.status == "Approved" || it.status == "Resolved" }
    val pausedCount = issues.count { it.status == "Paused" }
    val redoCount = issues.count { it.status == "Redo" }
    val failedCount = issues.count { it.status == "Failed" }

    val approvedStr = approvedCount.toString().padStart(2, '0')
    val pausedStr = pausedCount.toString().padStart(2, '0')
    val redoStr = redoCount.toString().padStart(2, '0')
    val failedStr = failedCount.toString().padStart(2, '0')

    val signInInput by viewModel.signInInput.collectAsState()
    val displayName = if (signInInput.contains("@") && signInInput.substringBefore("@").isNotBlank()) {
        signInInput.substringBefore("@").split(".").joinToString(" ") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    } else {
        "Saharsh Sathyanarayanan"
    }

    val nameParts = displayName.split(" ")
    val firstName = nameParts.getOrNull(0) ?: "Saharsh"
    val lastName = nameParts.drop(1).joinToString(" ") { it }.ifBlank { "Sathyanarayanan" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile & Menu button overlay
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onMenuClick() }
                ) {
                    // Profile Circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Overlapping small menu badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.BottomStart)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Greeting text
                Column {
                    Text(
                        text = "Hello $firstName",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = lastName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Sync and Notification Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = { /* sync action */ }) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Sync",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(modifier = Modifier.size(24.dp)) {
                    IconButton(onClick = { /* notify action */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Red dot with count 2
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "To Do" Section
        Text(
            text = "To Do",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 To Do cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ToDoStatusCard(
                        label = "Approved",
                        count = approvedStr,
                        icon = Icons.Default.DoneAll,
                        iconColor = Color(0xFF64748B),
                        onClick = {
                            // Filter/select action
                        }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToDoStatusCard(
                        label = "Paused",
                        count = pausedStr,
                        icon = Icons.Default.PauseCircle,
                        iconColor = Color(0xFF64748B),
                        onClick = {
                            // Filter/select action
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ToDoStatusCard(
                        label = "Redo",
                        count = redoStr,
                        icon = Icons.Default.Refresh,
                        iconColor = OrangePrimary,
                        onClick = {
                            // Filter/select action
                        }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ToDoStatusCard(
                        label = "Failed",
                        count = failedStr,
                        icon = Icons.Default.Cancel,
                        iconColor = Color.Red,
                        onClick = {
                            // Filter/select action
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Projects" Section
        Text(
            text = "Projects",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Side-by-side Project cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardProjectCard(
                    initials = "ANK",
                    fullName = "ANKUR",
                    timezone = "Asia/Calcutta",
                    onChecklistClick = {
                        viewModel.selectProject("ANKUR")
                        viewModel.updateBottomTab(BottomNavTab.INSPECTION)
                    },
                    onReportClick = {
                        viewModel.selectProject("ANKUR")
                        viewModel.updateBottomTab(BottomNavTab.INSTRUCTION)
                    },
                    onClick = {
                        viewModel.selectProject("ANKUR")
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                DashboardProjectCard(
                    initials = "TP",
                    fullName = "Training Project",
                    timezone = "Asia/Calcutta",
                    onChecklistClick = {
                        viewModel.selectProject("Training Project")
                        viewModel.updateBottomTab(BottomNavTab.INSPECTION)
                    },
                    onReportClick = {
                        viewModel.selectProject("Training Project")
                        viewModel.updateBottomTab(BottomNavTab.INSTRUCTION)
                    },
                    onClick = {
                        viewModel.selectProject("Training Project")
                    }
                )
            }
        }
    }
}

// ==========================================
// B. INSPECTION TAB VIEW
// ==========================================
@Composable
fun InspectionTabView(viewModel: AppViewModel, isWide: Boolean) {
    val context = LocalContext.current
    val isAddingEqc by viewModel.isAddingEqc.collectAsState()
    val eqcList by viewModel.eqcList.collectAsState()

    val selectedProject by viewModel.selectedProject.collectAsState()
    val selectedEqcProject by viewModel.selectedEqcProject.collectAsState()
    val selectedEqcChecklist by viewModel.selectedEqcChecklist.collectAsState()
    val eqcLocation by viewModel.eqcLocationInput.collectAsState()
    val dbChecklists by viewModel.checklists.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Available, 1 = Paused, 2 = Approval
    var searchQuery by remember { mutableStateOf("") }
    var projectDropdownExpanded by remember { mutableStateOf(false) }

    var showLocationPicker by remember { mutableStateOf(false) }
    val pickerPath = remember { mutableStateListOf<String>() }
    var customNodeInput by remember { mutableStateOf("") }

    LaunchedEffect(showLocationPicker) {
        if (showLocationPicker) {
            val root = if (selectedEqcProject.equals("ANKUR", ignoreCase = true)) "ANKUR" else "TP"
            pickerPath.clear()
            pickerPath.add(root)
            customNodeInput = ""
        }
    }

    val adaptiveGrey = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    if (isAddingEqc) {
        if (showLocationPicker) {
            // ==========================================
            // EQC LOCATION PICKER SCREEN (Screenshots 1-5 of picker)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EQC Location",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { showLocationPicker = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Picker",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Breadcrumbs Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    pickerPath.forEachIndexed { index, node ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF1E293B), // Deep navy/black
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .border(
                                    border = BorderStroke(1.dp, OrangePrimary), // Orange border glow
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = node,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (index > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color(0xFFCBD5E1), CircleShape)
                                        .clickable {
                                            // Remove this node and all following nodes from the path
                                            while (pickerPath.size > index) {
                                                pickerPath.removeAt(pickerPath.lastIndex)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove node",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                        if (index < pickerPath.lastIndex) {
                            Text(
                                text = " / ",
                                fontSize = 15.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val nextNodes = when (pickerPath.size) {
                    1 -> listOf("Wing - NA")
                    2 -> listOf("F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10")
                    3 -> listOf("F1 - Lobby Area", "Flat 101", "Flat 102", "Flat 103", "Flat 104")
                    4 -> {
                        val last = pickerPath.lastOrNull() ?: ""
                        if (last.contains("Lobby", ignoreCase = true)) {
                            emptyList()
                        } else {
                            listOf("Living Room", "Bedroom", "Master Bedroom", "Common Washroom", "Master Washroom", "Balcony", "Kitchen", "Passage")
                        }
                    }
                    else -> emptyList()
                }

                val filteredNodes = nextNodes.filter {
                    customNodeInput.isBlank() || it.contains(customNodeInput, ignoreCase = true)
                }

                val isCustomScreen = pickerPath.size >= 5 || nextNodes.isEmpty() || (customNodeInput.isNotBlank() && filteredNodes.isEmpty())

                // Card content
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search / Create input inside card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customNodeInput,
                                onValueChange = { customNodeInput = it },
                                placeholder = {
                                    Text(
                                        text = if (isCustomScreen) "Custom name" else "Search or create custom",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(48.dp)
                                    .background(Color(0xFFCBD5E1), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                    .clickable {
                                        if (customNodeInput.isNotBlank()) {
                                            pickerPath.add(customNodeInput)
                                            customNodeInput = ""
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "/",
                                    color = Color(0xFF475569),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isCustomScreen) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Type above to create custom name",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(filteredNodes) { node ->
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    pickerPath.add(node)
                                                    customNodeInput = ""
                                                }
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = node,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = "Next Level",
                                                tint = adaptiveGrey,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom persistent Create button
                val canCreate = if (isCustomScreen) {
                    customNodeInput.isNotBlank()
                } else {
                    pickerPath.isNotEmpty()
                }
                Button(
                    onClick = {
                        val finalPathList = if (customNodeInput.isNotBlank()) {
                            pickerPath + customNodeInput
                        } else {
                            pickerPath
                        }
                        val finalLocation = finalPathList.joinToString("/")
                        viewModel.updateEqcLocation(finalLocation)
                        showLocationPicker = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canCreate) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        contentColor = if (canCreate) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    enabled = canCreate
                ) {
                    Text(
                        text = "Create",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // ==========================================
            // 1. ADD EQC FORM VIEW (Screenshots 2 and 3)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Form Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setAddingEqc(false) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add EQC",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val dbConnected by viewModel.dbConnected.collectAsState()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (dbConnected) Color(0xFF10B981) else Color(0xFFF59E0B),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (dbConnected) "Connected (PostgreSQL)" else "Offline (Room Cache)",
                                fontSize = 11.sp,
                                color = if (dbConnected) Color(0xFF10B981) else adaptiveGrey,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.resetEqcForm() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Start QC button (disabled if location blank)
                    Button(
                        onClick = {
                            val saved = viewModel.startQc()
                            if (saved) {
                                android.widget.Toast.makeText(context, "QC inspection started successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = eqcLocation.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (eqcLocation.isNotBlank()) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            contentColor = if (eqcLocation.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = "Start QC",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Project Field (Dropdown)
                var projectExpanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Project",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = adaptiveGrey
                        )
                        Text(text = " *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .clickable { projectExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedEqcProject.ifBlank { "Select Project" },
                                color = if (selectedEqcProject.isBlank()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Icon(
                                imageVector = if (projectExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Indicator",
                                tint = adaptiveGrey
                            )
                        }
                        DropdownMenu(
                            expanded = projectExpanded,
                            onDismissRequest = { projectExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            listOf("ANKUR", "Training Project").forEach { proj ->
                                DropdownMenuItem(
                                    text = { Text(proj, fontSize = 15.sp) },
                                    onClick = {
                                        viewModel.updateEqcProject(proj)
                                        projectExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Checklist Field (Dropdown)
                var checklistExpanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Checklist",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = adaptiveGrey
                        )
                        Text(text = " *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .clickable { checklistExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedEqcChecklist.ifBlank { "Select" },
                                color = if (selectedEqcChecklist.isBlank()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Icon(
                                imageVector = if (checklistExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Indicator",
                                tint = adaptiveGrey
                            )
                        }
                        DropdownMenu(
                            expanded = checklistExpanded,
                            onDismissRequest = { checklistExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            dbChecklists.forEach { chk ->
                                DropdownMenuItem(
                                    text = { Text(chk, fontSize = 15.sp) },
                                    onClick = {
                                        viewModel.updateEqcChecklist(chk)
                                        checklistExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // EQC Location Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "EQC location",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = adaptiveGrey
                        )
                        Text(text = " *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        
                        IconButton(
                            onClick = { showLocationPicker = true },
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF1E293B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Location",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (eqcLocation.isBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLocationPicker = true }
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Select Location Path", color = adaptiveGrey, fontSize = 14.sp)
                        }
                    } else {
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val parts = eqcLocation.split("/")
                            parts.forEachIndexed { idx, part ->
                                if (idx > 0) {
                                    Text(
                                        text = "/",
                                        color = adaptiveGrey,
                                        fontSize = 14.sp,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                                
                                val isLast = idx == parts.lastIndex
                                val containerColor = if (isLast) Color(0xFFE2E8F0) else Color(0xFF111E30)
                                val textColor = if (isLast) Color(0xFF1E293B) else Color.White
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(containerColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = part,
                                        color = textColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // ==========================================
        // 2. INSPECTION LIST VIEW (Screenshot 1)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Dropdown & Add Button Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Interactive Project Selection Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .clickable { projectDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedProject, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (projectDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Indicator",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = projectDropdownExpanded,
                        onDismissRequest = { projectDropdownExpanded = false }
                    ) {
                        listOf("Training Project", "ANKUR").forEach { proj ->
                            DropdownMenuItem(
                                text = { Text(proj) },
                                onClick = {
                                    viewModel.selectProject(proj)
                                    projectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Add Button (Orange, bold)
                Row(
                    modifier = Modifier
                        .clickable { viewModel.setAddingEqc(true) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Add EQC icon",
                        tint = OrangePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Add",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search and Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                    ),
                    singleLine = true
                )
                IconButton(
                    onClick = { /* active filter dialog */ },
                    modifier = Modifier
                        .size(54.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filter icon",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab counts calculated dynamically
            val availCount = eqcList.count { it.status == "Available" && it.project == selectedProject }
            val pausedCount = eqcList.count { it.status == "Paused" && it.project == selectedProject }
            val approvalCount = eqcList.count { it.status == "Approval" && it.project == selectedProject }

            // Status Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Available
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 0 }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Available ($availCount)",
                        fontSize = 14.sp,
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 0) OrangePrimary else adaptiveGrey
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(3.dp)
                            .background(if (activeTab == 0) OrangePrimary else Color.Transparent)
                    )
                }

                // Tab 2: Paused
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 1 }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Paused ($pausedCount)",
                        fontSize = 14.sp,
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 1) OrangePrimary else adaptiveGrey
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(3.dp)
                            .background(if (activeTab == 1) OrangePrimary else Color.Transparent)
                    )
                }

                // Tab 3: Approval
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 2 }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Approval ($approvalCount)",
                        fontSize = 14.sp,
                        fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 2) OrangePrimary else adaptiveGrey
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(3.dp)
                            .background(if (activeTab == 2) OrangePrimary else Color.Transparent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter EQC list by tab status, search query, and selected project
            val targetStatus = when (activeTab) {
                0 -> "Available"
                1 -> "Paused"
                else -> "Approval"
            }

            val filteredEqcList = eqcList.filter {
                it.status == targetStatus &&
                it.project == selectedProject &&
                (it.title.contains(searchQuery, ignoreCase = true) ||
                 it.checklistName.contains(searchQuery, ignoreCase = true) ||
                 it.inspector.contains(searchQuery, ignoreCase = true))
            }

            if (filteredEqcList.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Inbox,
                            contentDescription = "Empty icon",
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No EQC found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try adding a new checklist or check different status tabs.",
                            fontSize = 14.sp,
                            color = adaptiveGrey,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lazy list of EQC Cards
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEqcList, key = { it.id }) { item ->
                        var showMenu by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Title
                                    Text(
                                        text = item.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // Checklist description
                                    Text(
                                        text = item.checklistName,
                                        fontSize = 13.sp,
                                        color = adaptiveGrey
                                    )

                                    // Inspect stage
                                    Text(
                                        text = item.inspectStage,
                                        fontSize = 13.sp,
                                        color = adaptiveGrey
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Inspector Person Row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Inspector Person icon",
                                            tint = adaptiveGrey,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = item.inspector,
                                            fontSize = 13.sp,
                                            color = adaptiveGrey
                                        )
                                    }

                                    // Date Alert/Notification Row (Styled in orange as in screenshot)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Notifications,
                                            contentDescription = "Notification icon",
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = item.dateTime,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = OrangePrimary
                                        )
                                    }
                                }

                                // Interactive Three-Dots Option Menu
                                Box {
                                    IconButton(
                                        onClick = { showMenu = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Options menu",
                                            tint = adaptiveGrey
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Delete Inspection") },
                                            onClick = {
                                                // Implement quick delete to keep it very pristine
                                                android.widget.Toast.makeText(context, "Inspection deleted!", android.widget.Toast.LENGTH_SHORT).show()
                                                showMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// C. INSTRUCTION TAB VIEW (THE MAIN ISSUES INTERACTIVE VIEW)
// ==========================================
@Composable
fun InstructionTabView(viewModel: AppViewModel, isWide: Boolean) {
    val selectedProject by viewModel.selectedProject.collectAsState()
    val activeTab by viewModel.currentInstructionTab.collectAsState()
    val issues by viewModel.allIssues.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Filter issues by active instruction tab ("To Do", "Raised", "Draft")
    val filteredIssues = issues.filter { issue ->
        val matchesTab = when (activeTab) {
            InstructionStatusTab.TO_DO -> issue.status == "To Do"
            InstructionStatusTab.RAISED -> issue.status == "Raised"
            InstructionStatusTab.DRAFT -> issue.status == "Draft"
        }
        val matchesSearch = searchQuery.isBlank() || 
                issue.locationPath.contains(searchQuery, ignoreCase = true) || 
                issue.category.contains(searchQuery, ignoreCase = true) ||
                issue.description.contains(searchQuery, ignoreCase = true)
                
        matchesTab && matchesSearch
    }

    // Tab counts
    val todoCount = issues.count { it.status == "To Do" }
    val raisedCount = issues.count { it.status == "Raised" }
    val draftCount = issues.count { it.status == "Draft" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Header Project + ADD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .clickable { /* drop down action */ }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedProject, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
            }

            // ADD Button
            TextButton(
                onClick = {
                    viewModel.resetCreationState()
                    viewModel.navigateTo(Screen.ADD_LOCATION)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = OrangePrimary)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", tint = OrangePrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Tabs (To Do, Raised, Draft)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InstructionTabItem(
                title = "To Do ($todoCount)",
                isSelected = activeTab == InstructionStatusTab.TO_DO,
                onClick = { viewModel.updateInstructionTab(InstructionStatusTab.TO_DO) }
            )
            InstructionTabItem(
                title = "Raised ($raisedCount)",
                isSelected = activeTab == InstructionStatusTab.RAISED,
                onClick = { viewModel.updateInstructionTab(InstructionStatusTab.RAISED) }
            )
            InstructionTabItem(
                title = "Draft ($draftCount)",
                isSelected = activeTab == InstructionStatusTab.DRAFT,
                onClick = { viewModel.updateInstructionTab(InstructionStatusTab.DRAFT) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Issue lists
        if (filteredIssues.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No issue found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredIssues) { issue ->
                    IssueItemCard(issue = issue, onDelete = { viewModel.deleteIssue(issue) })
                }
            }
        }
    }
}

@Composable
fun InstructionTabItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) OrangePrimary else Color.Gray,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(64.dp)
                .background(if (isSelected) OrangePrimary else Color.Transparent)
        )
    }
}

@Composable
fun IssueItemCard(issue: Issue, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Left content side
            Column(modifier = Modifier.weight(1f)) {
                // Card Title & Badge & Action Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = issue.locationPath,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (issue.status == "Raised") OrangeLight else Color.LightGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = issue.status,
                            color = if (issue.status == "Raised") OrangePrimary else Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Options menu trigger
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete Issue", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Due text
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Due since 2 days ",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "[${issue.deadline}]",
                        color = TextGrey,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Details/Tags rows with custom Material Symbols mockups
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    IssueDetailRow(icon = Icons.Filled.Sell, text = issue.category)
                    IssueDetailRow(icon = Icons.Filled.Description, text = issue.description)
                    IssueDetailRow(icon = Icons.Filled.PinDrop, text = "Assigned: ${issue.assignee}")
                    IssueDetailRow(icon = Icons.Filled.Person, text = "Reporter: ${issue.reporter}")
                }
            }

            // Right image thumbnail side if present
            if (!issue.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = issue.imageUri,
                    contentDescription = "Issue Thumbnail",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun IssueDetailRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGrey,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// D. TO-DO TAB VIEW
// ==========================================
@Composable
fun TodoTabView(viewModel: AppViewModel, isWide: Boolean) {
    val selectedProject by viewModel.selectedProject.collectAsState()
    val activeTab by viewModel.currentTodoTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Top Project
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .clickable { /* drop down action */ }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedProject, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs "To-be approved", "Draft"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.updateTodoTab(TodoStatusTab.TO_BE_APPROVED) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "To-be approved (0)",
                    fontWeight = if (activeTab == TodoStatusTab.TO_BE_APPROVED) FontWeight.Bold else FontWeight.Medium,
                    color = if (activeTab == TodoStatusTab.TO_BE_APPROVED) OrangePrimary else Color.Gray,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(110.dp)
                        .background(if (activeTab == TodoStatusTab.TO_BE_APPROVED) OrangePrimary else Color.Transparent)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.updateTodoTab(TodoStatusTab.DRAFT) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Draft (0)",
                    fontWeight = if (activeTab == TodoStatusTab.DRAFT) FontWeight.Bold else FontWeight.Medium,
                    color = if (activeTab == TodoStatusTab.DRAFT) OrangePrimary else Color.Gray,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(64.dp)
                        .background(if (activeTab == TodoStatusTab.DRAFT) OrangePrimary else Color.Transparent)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Empty state matching the PDF mock (No EQC Found)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Inbox,
                    contentDescription = "Empty",
                    modifier = Modifier.size(64.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No eqc found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


// ==========================================
// 3. EQC LOCATION PICKER SCREEN (Screens 13, 14, 15)
// ==========================================
@Composable
fun AddLocationScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val creationState by viewModel.creationState.collectAsState()
    val locationPath = creationState.locationPath
    val selectedProject by viewModel.selectedProject.collectAsState()

    var showProjectDropdown by remember { mutableStateOf(false) }
    var showIssueLocationPicker by remember { mutableStateOf(false) }
    var customTagDialog by remember { mutableStateOf(false) }
    var customTagValue by remember { mutableStateOf("") }
    val tagsList = remember { mutableStateListOf<String>() }
    var showImageSourceOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Predefined construction defect images for cycling mockup
    val mockupImages = listOf(
        "https://images.unsplash.com/photo-1590069261209-f8e9b8642343?auto=format&fit=crop&q=80&w=600",
        "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&q=80&w=600",
        "https://images.unsplash.com/photo-1541888946425-d81bb19240f5?auto=format&fit=crop&q=80&w=600",
        "https://images.unsplash.com/photo-1562259949-e8e7689d7828?auto=format&fit=crop&q=80&w=600"
    )
    val mockupIndex = remember { mutableStateOf(0) }

    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addImageUri(uri.toString())
        } else {
            // Smart fallback if selection cancelled/failed on headless device
            val nextImg = mockupImages[mockupIndex.value]
            viewModel.addImageUri(nextImg)
            mockupIndex.value = (mockupIndex.value + 1) % mockupImages.size
        }
    }

    // Launcher for taking a picture with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val cacheFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                val out = FileOutputStream(cacheFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                viewModel.addImageUri(cacheFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to mockup images if write fails
                val nextImg = mockupImages[mockupIndex.value]
                viewModel.addImageUri(nextImg)
                mockupIndex.value = (mockupIndex.value + 1) % mockupImages.size
            }
        } else {
            // Smart fallback if selection cancelled/failed on headless device
            val nextImg = mockupImages[mockupIndex.value]
            viewModel.addImageUri(nextImg)
            mockupIndex.value = (mockupIndex.value + 1) % mockupImages.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header (top close bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("close_issue_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Add Issue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                // empty spacer to center text
                Spacer(modifier = Modifier.width(48.dp))
            }

            // 2. Camera Preview / Image display container (Upper section)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (creationState.imageUris.isNotEmpty()) {
                    val activeUri = creationState.imageUri ?: creationState.imageUris.first()
                    // Display selected image
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = activeUri,
                            contentDescription = "Issue Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // Close button to remove image
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .clickable { viewModel.removeImageUri(activeUri) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Thumbnail carousel at the bottom of the image box
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            creationState.imageUris.forEach { uri ->
                                val isSelected = uri == activeUri
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) OrangePrimary else Color.White.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.selectActiveImage(uri) }
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            // A neat "+" button to add more images
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .clickable { showImageSourceOptions = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add More",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Add Images button
                    Button(
                        onClick = {
                            showImageSourceOptions = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("add_images_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Add Images icon",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Add Images",
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // 3. Dark Bottom Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row 1: Dropdown + Location picker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Project Short Code Dropdown ("TP" or "ANKUR")
                        Box {
                            val shortCode = if (selectedProject.contains("ANKUR", ignoreCase = true)) "ANKUR" else "TP"
                            Row(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clickable { showProjectDropdown = true }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = shortCode,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown arrow",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showProjectDropdown,
                                onDismissRequest = { showProjectDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Training Project (TP)") },
                                    onClick = {
                                        viewModel.selectProject("Training Project")
                                        val root = "TP"
                                        viewModel.updateLocationPath(listOf(root))
                                        showProjectDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("ANKUR Project") },
                                    onClick = {
                                        viewModel.selectProject("ANKUR")
                                        val root = "ANKUR"
                                        viewModel.updateLocationPath(listOf(root))
                                        showProjectDropdown = false
                                    }
                                )
                            }
                        }

                        // Location Path Selection Box with Trailing '/' Button
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .background(Color(0xFF27272A), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(8.dp))
                                .clickable { showIssueLocationPicker = true }
                                .padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val pathString = locationPath.joinToString(" / ")
                            Text(
                                text = if (locationPath.size > 1) pathString else "Location or Reference",
                                color = if (locationPath.size > 1) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            // Trailing '/' button with solid blue-grey background
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(46.dp)
                                    .background(Color(0xFF1E293B), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                    .clickable { showIssueLocationPicker = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "/",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Row 2: Category Pills: Quality, Safety, Material, Other
                    val categories = listOf("Quality", "Safety", "Material", "Other")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = creationState.category == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSelected) Color.White else Color(0xFF27272A),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else Color(0xFF3F3F46),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { viewModel.updateCategory(cat) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color(0xFF1E293B) else Color.LightGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Tag displays if any
                    if (tagsList.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tagsList.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .background(OrangeLight, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        color = OrangePrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Tag",
                                        tint = OrangePrimary,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable { tagsList.remove(tag) }
                                    )
                                }
                            }
                        }
                    }

                    // Row 3: Add Tags button
                    Button(
                        onClick = { customTagDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Add Tags",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Row 4: Next Button
                    val isReady = locationPath.size > 1
                    Button(
                        onClick = { viewModel.navigateTo(Screen.ADD_DETAILS) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReady) OrangePrimary else Color(0xFF3F3F46)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Next",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Custom Tag Input Dialog
        if (customTagDialog) {
            AlertDialog(
                onDismissRequest = { customTagDialog = false },
                title = { Text("Add Custom Tag") },
                text = {
                    OutlinedTextField(
                        value = customTagValue,
                        onValueChange = { customTagValue = it },
                        placeholder = { Text("Enter tag name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (customTagValue.isNotBlank()) {
                            tagsList.add(customTagValue)
                            customTagValue = ""
                        }
                        customTagDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { customTagDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 4. Integrated EQC Location Selection Dialog
        if (showIssueLocationPicker) {
            val pickerPath = remember { mutableStateListOf<String>() }
            var customNodeInput by remember { mutableStateOf("") }

            LaunchedEffect(showIssueLocationPicker) {
                pickerPath.clear()
                pickerPath.addAll(locationPath)
                val root = if (selectedProject.contains("ANKUR", ignoreCase = true)) "ANKUR" else "TP"
                if (pickerPath.isEmpty()) {
                    pickerPath.add(root)
                }
            }

            val nextNodes = when (pickerPath.size) {
                1 -> listOf("Wing - NA", "Wing - A", "Wing - B", "Wing - C")
                2 -> listOf("F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10")
                3 -> listOf("F1 - Lobby Area", "Flat 101", "Flat 102", "Flat 103", "Flat 104")
                4 -> listOf("Living Room", "Bedroom", "Master Bedroom", "Common Washroom", "Master Washroom", "Balcony", "Kitchen", "Passage")
                5 -> listOf("Lights", "Wiring", "Ceiling", "Plumbing", "Flooring", "Painting", "Wall Finish")
                else -> emptyList()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Title Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Issue Location Selection",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { showIssueLocationPicker = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Picker", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Path Breadcrumbs Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            pickerPath.forEachIndexed { index, node ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(
                                            color = if (index == pickerPath.lastIndex) OrangePrimary else MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = node,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (index > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(Color(0xFFCBD5E1), CircleShape)
                                                .clickable {
                                                    while (pickerPath.size > index) {
                                                        pickerPath.removeAt(pickerPath.lastIndex)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove node",
                                                tint = Color(0xFF1E293B),
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                                if (index < pickerPath.lastIndex) {
                                    Text(
                                        text = " / ",
                                        fontSize = 15.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Node Input inside the picker card container
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customNodeInput,
                                onValueChange = { customNodeInput = it },
                                placeholder = { Text("Search or create custom", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(48.dp)
                                    .background(Color(0xFFCBD5E1), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                    .clickable {
                                        if (customNodeInput.isNotBlank()) {
                                            pickerPath.add(customNodeInput)
                                            customNodeInput = ""
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Add custom node",
                                    tint = Color(0xFF1E293B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Suggestions / Next Node choices
                        Box(modifier = Modifier.weight(1f)) {
                            if (nextNodes.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color.Green, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Location Path Ready!", fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(nextNodes) { node ->
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        pickerPath.add(node)
                                                    }
                                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(node, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            }
                                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showIssueLocationPicker = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f))
                            ) {
                                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                            }

                            Button(
                                onClick = {
                                    viewModel.updateLocationPath(pickerPath.toList())
                                    showIssueLocationPicker = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                            ) {
                                Text("Create", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Image source choice dialog (Camera or Gallery)
        if (showImageSourceOptions) {
            AlertDialog(
                onDismissRequest = { showImageSourceOptions = false },
                title = { Text(text = "Select Image Source", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Choose how you want to add the issue photograph:",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Camera Option Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showImageSourceOptions = false
                                    try {
                                        cameraLauncher.launch(null)
                                    } catch (e: Exception) {
                                        val nextImg = mockupImages[mockupIndex.value]
                                        viewModel.updateImageUri(nextImg)
                                        mockupIndex.value = (mockupIndex.value + 1) % mockupImages.size
                                    }
                                }
                                .background(Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Open Camera",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Take a new photograph of the defect",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Gallery Option Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showImageSourceOptions = false
                                    try {
                                        galleryLauncher.launch("image/*")
                                    } catch (e: Exception) {
                                        val nextImg = mockupImages[mockupIndex.value]
                                        viewModel.updateImageUri(nextImg)
                                        mockupIndex.value = (mockupIndex.value + 1) % mockupImages.size
                                    }
                                }
                                .background(Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Gallery",
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Choose from Gallery",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Select an existing photo from your library",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImageSourceOptions = false }) {
                        Text("Cancel", color = OrangePrimary)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}


// ==========================================
// 4. ADD TAGS SCREEN (Screens 8 & 9)
// ==========================================
@Composable
fun AddTagsScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val creationState by viewModel.creationState.collectAsState()
    val categories = listOf("Quality", "Safety", "Material", "Other")
    
    var customTagDialog by remember { mutableStateOf(false) }
    var customTagValue by remember { mutableStateOf("") }
    val tagsList = remember { mutableStateListOf<String>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Tags & Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom "Add Images" mockup card on top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { /* add mock image upload action */ },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(40.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Add Images", fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TP indicator
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "TP", fontWeight = FontWeight.Bold, color = TextGrey)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Location path chip
                Text(
                    text = creationState.locationPath.joinToString(" / "),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category choice Pills
            Text(text = "Category", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextGrey)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = creationState.category == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .clickable { viewModel.updateCategory(cat) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add Tags Button
            Button(
                onClick = { customTagDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add tags", tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Tags", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }

            // Tag list flow
            if (tagsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tagsList.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(OrangeLight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = tag, color = OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Next button
            Button(
                onClick = { viewModel.navigateTo(Screen.ADD_DETAILS) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Custom Tag Input Dialog
        if (customTagDialog) {
            AlertDialog(
                onDismissRequest = { customTagDialog = false },
                title = { Text("Add Custom Tag") },
                text = {
                    OutlinedTextField(
                        value = customTagValue,
                        onValueChange = { customTagValue = it },
                        placeholder = { Text("Enter tag name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (customTagValue.isNotBlank()) {
                            tagsList.add(customTagValue)
                            customTagValue = ""
                        }
                        customTagDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { customTagDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


// ==========================================
// 5. ADD DETAILS SCREEN (Screens 6 & 7)
// ==========================================
@Composable
fun AddDetailsScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val creationState by viewModel.creationState.collectAsState()
    val context = LocalContext.current
    
    var desc by remember { mutableStateOf("") }
    
    // Calendar instance
    val calendar = Calendar.getInstance()

    // DatePicker dialogue
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val format = SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.getDefault())
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            viewModel.updateDeadline(format.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Issue Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display selected images or fallback card
            if (creationState.imageUris.isNotEmpty()) {
                val activeUri = creationState.imageUri ?: creationState.imageUris.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.1f))
                ) {
                    // Display the selected main image
                    AsyncImage(
                        model = activeUri,
                        contentDescription = "Active Issue Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Small thumbnail row at the bottom of the preview box
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        creationState.imageUris.forEach { uri ->
                            val isSelected = uri == activeUri
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) OrangePrimary else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.selectActiveImage(uri) }
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Thumbnail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            } else {
                // Add Images mockup card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(40.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Add Images", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Issue description text input box
            OutlinedTextField(
                value = desc,
                onValueChange = {
                    desc = it
                    viewModel.updateDescription(it)
                },
                placeholder = { Text("What is the issue?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Deadline selection box with 1-day, 2-day quick buttons
            Text(text = "Deadline", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextGrey)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current deadline trigger button
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .clickable { datePickerDialog.show() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.NotificationsActive, contentDescription = "Alarm", tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = creationState.deadline,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // "1 day" button
                Button(
                    onClick = {
                        val format = SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.getDefault())
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                        viewModel.updateDeadline(format.format(cal.time))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "1 day", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // "2 days" button
                Button(
                    onClick = {
                        val format = SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.getDefault())
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, 2)
                        viewModel.updateDeadline(format.format(cal.time))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "2 days", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recommendation
            Button(
                onClick = { /* Add recommendation recommendation mockup dialogue */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Add recommendation", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation back / next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Text(text = "Back", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = { viewModel.navigateTo(Screen.ASSIGN_CONTACTS) },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Next", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


// ==========================================
// 6. ASSIGN ISSUE SCREEN (Screen 5)
// ==========================================
@Composable
fun AssignContactsScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val creationState by viewModel.creationState.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    
    val teamContacts = allContacts.filter { !it.isPhoneContact }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Assign Issue",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Find user by name, team, or mobile number") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Contact Picker Row with Chevron
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.PHONE_CONTACTS_PICKER) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Contacts, contentDescription = "Contacts", tint = OrangePrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Phone Contact",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Pick Phone Contact", tint = Color.Gray)
                }
            }

            // Display picked phone contact under the Phone Contact row if exists
            if (creationState.assigneePhone.isNotBlank()) {
                val isPhoneContactSelected = creationState.assigneeCompany == "Phone Contact"
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.updateAssignee(creationState.assignee, "Phone Contact", creationState.assigneePhone)
                        }
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(
                            width = if (isPhoneContactSelected) 2.dp else 1.dp,
                            color = if (isPhoneContactSelected) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.LightGray.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = creationState.assignee.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = creationState.assignee,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = creationState.assigneePhone,
                                color = TextGrey,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Radio check icon
                    RadioButton(
                        selected = isPhoneContactSelected,
                        onClick = {
                            viewModel.updateAssignee(creationState.assignee, "Phone Contact", creationState.assigneePhone)
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // List header
            Text(
                text = "PROJECT TEAM USERS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextGrey
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Team users list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(teamContacts) { index, contact ->
                    val isChecked = creationState.assignee == contact.name && creationState.assigneeCompany == contact.company
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateAssignee(contact.name, contact.company, contact.phone)
                            }
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isChecked) 2.dp else 1.dp,
                                color = if (isChecked) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${index + 1}. ${contact.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contact.phone,
                                color = TextGrey,
                                fontSize = 13.sp
                            )
                            Text(
                                text = contact.company,
                                color = TextGrey,
                                fontSize = 13.sp
                            )
                        }

                        // Radio check icon
                        RadioButton(
                            selected = isChecked,
                            onClick = {
                                viewModel.updateAssignee(contact.name, contact.company, contact.phone)
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back / Continue buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Text(text = "Back", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        viewModel.navigateTo(Screen.ISSUE_REVIEW)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}


// ==========================================
// 7. PHONE CONTACTS PICKER SCREEN (Screen 4)
// ==========================================
private fun fetchDeviceContacts(context: android.content.Context): List<Contact> {
    val contactsList = mutableListOf<Contact>()
    try {
        val resolver = context.contentResolver
        val cursor = resolver.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            val seenNumbers = mutableSetOf<String>()
            while (it.moveToNext()) {
                val name = if (nameIndex >= 0) it.getString(nameIndex) ?: "" else ""
                val number = if (numberIndex >= 0) it.getString(numberIndex) ?: "" else ""
                if (name.isNotBlank() && number.isNotBlank()) {
                    val normalized = number.replace("\\s".toRegex(), "").replace("-", "")
                    if (!seenNumbers.contains(normalized)) {
                        seenNumbers.add(normalized)
                        contactsList.add(
                            Contact(
                                name = name,
                                phone = number,
                                company = "Phone Contact",
                                isPhoneContact = true
                            )
                        )
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return contactsList
}

@Composable
fun PhoneContactsPickerScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val creationState by viewModel.creationState.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val phoneContacts = allContacts.filter { it.isPhoneContact }
    
    val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_CONTACTS
    )
    var hasContactsPermission by remember {
        mutableStateOf(permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    val deviceContacts = remember { mutableStateListOf<Contact>() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasContactsPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasContactsPermission) {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    LaunchedEffect(hasContactsPermission) {
        if (hasContactsPermission) {
            val fetched = fetchDeviceContacts(context)
            if (fetched.isNotEmpty()) {
                deviceContacts.clear()
                deviceContacts.addAll(fetched)
                viewModel.insertContacts(fetched)
            }
        }
    }

    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val displayContacts = if (hasContactsPermission && deviceContacts.isNotEmpty()) {
        deviceContacts
    } else {
        phoneContacts
    }

    val filteredPhoneContacts = displayContacts.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Contacts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search phone contacts") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone users list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPhoneContacts) { contact ->
                    val isChecked = creationState.assigneePhone == contact.phone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateAssignee(contact.name, "Phone Contact", contact.phone)
                            }
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .border(
                                width = if (isChecked) 2.dp else 1.dp,
                                color = if (isChecked) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar placeholder
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color.LightGray.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = contact.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = contact.phone,
                                    color = TextGrey,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Check circle selector
                        RadioButton(
                            selected = isChecked,
                            onClick = {
                                viewModel.updateAssignee(contact.name, "Phone Contact", contact.phone)
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Select button
            Button(
                onClick = {
                    viewModel.navigateBack() // Go back to assign screen
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Confirm Selection", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ==========================================
// OTP VERIFICATION SCREEN
// ==========================================
@Composable
fun OtpVerifyScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val otpText by viewModel.otpInput.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val signInInput by viewModel.signInInput.collectAsState()
    val countdown by viewModel.otpRemainingSeconds.collectAsState()

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030712), // Deep Onyx
                    Color(0xFF0C0E1E), // Obsidian Navy
                    Color(0xFF1E1B4B)  // Midnight Indigo
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9FAFB),
                    Color(0xFFEEF2FF), // Lavender mist
                    Color(0xFFE0E7FF)  // Slate blue-grey light
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        val isWide = maxWidth > 600.dp

        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.85f else 1.0f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f),
                    shape = RoundedCornerShape(24.dp)
                )
                .shadow(
                    elevation = if (isWide) 16.dp else 4.dp,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .verticalScroll(rememberScrollState())
                .padding(all = if (isWide) 32.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verify OTP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Valid8Logo()
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Enter Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We have sent a 4-digit verification code to $signInInput",
                fontSize = 15.sp,
                color = TextGrey,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Beautiful 4 digit interactive OTP boxes backed by a transparent BasicTextField
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = otpText,
                    onValueChange = { viewModel.updateOtpInput(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("otp_input_field"),
                    decorationBox = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (i in 0 until 4) {
                                val char = if (i < otpText.length) otpText[i].toString() else ""
                                val isFocused = otpText.length == i
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                )
            }

            if (otpError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = otpError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.handleOtpVerify() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Verify & Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Resend Code Option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Didn't receive OTP? ", fontSize = 14.sp, color = TextGrey)
                Text(
                    text = if (countdown > 0) "Resend in ${countdown}s" else "Resend OTP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (countdown > 0) TextGrey else OrangePrimary,
                    modifier = Modifier.clickable(enabled = countdown == 0) {
                        viewModel.generateAndSendOtp()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// SPLASH SCREEN WITH LOGO ANIMATION ONLY
// ==========================================
@Composable
fun SplashScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    // 2.2 seconds delay before navigating to SIGN_IN screen
    LaunchedEffect(Unit) {
        delay(2200)
        viewModel.completeSplashAndGoToSignIn()
    }

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030712), // Deep Onyx
                    Color(0xFF0C0E1E), // Obsidian Navy
                    Color(0xFF1E1B4B)  // Midnight Indigo
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9FAFB),
                    Color(0xFFEEF2FF), // Lavender mist
                    Color(0xFFE0E7FF)  // Slate blue-grey light
                )
            )
        }
    }

    // Animation states
    val scale = remember { Animatable(0f) }
    val opacity = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Animate scale up with spring physics
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(Unit) {
        // Fade in opacity
        opacity.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )
    }

    // Continuous breathing scale/rotation infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val softRotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "softRotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush),
        contentAlignment = Alignment.Center
    ) {
        // Radial light glow behind the logo
        Box(
            modifier = Modifier
                .size(320.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isDark) {
                            listOf(
                                Color(0xFF6366F1).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                Color(0xFF6366F1).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        }
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale.value * pulseScale
                    scaleY = scale.value * pulseScale
                    rotationZ = softRotation
                    alpha = opacity.value
                }
        ) {
            Valid8LogoIcon(sizeDp = 120.dp, animate = true, modifier = Modifier.padding(16.dp))
        }
    }
}

// ==========================================
// LOGO TRANSITION & LOADING SCREEN (POST LOGIN)
// ==========================================
@Composable
fun LogoAnimationScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    // 2.0 seconds delay before navigating to HOME screen
    LaunchedEffect(Unit) {
        delay(2000)
        viewModel.completeLogoAnimationAndGoHome()
    }

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030712), // Deep Onyx
                    Color(0xFF0C0E1E), // Obsidian Navy
                    Color(0xFF1E1B4B)  // Midnight Indigo
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF9FAFB),
                    Color(0xFFEEF2FF), // Lavender mist
                    Color(0xFFE0E7FF)  // Slate blue-grey light
                )
            )
        }
    }

    // Animation values
    val scale = remember { Animatable(0.2f) }
    val rotate = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Pop scaling
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    LaunchedEffect(Unit) {
        // Spin entry
        rotate.animateTo(
            targetValue = 360f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    // Continuous breath pulse
    val infiniteTransition = rememberInfiniteTransition(label = "loginSuccessPulse")
    val breathingValue by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Elegant render of company logo with beautiful rotation & scale pop
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value * breathingValue
                        scaleY = scale.value * breathingValue
                        rotationZ = rotate.value
                    }
            ) {
                Valid8LogoIcon(sizeDp = 120.dp, animate = true)
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Beautiful glowing linear loader bar with gradient indicator
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(
                            alpha = 0.05f
                        )
                    )
            ) {
                val shimmerTransition = rememberInfiniteTransition(label = "shimmerLoader")
                val progressOffset by shimmerTransition.animateFloat(
                    initialValue = -0.5f,
                    targetValue = 1.5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1400, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.4f)
                        .graphicsLayer {
                            translationX = progressOffset * 200.dp.toPx()
                        }
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1).copy(alpha = 0.1f),
                                    Color(0xFF10B981), // Neon Mint
                                    Color(0xFF6366F1).copy(alpha = 0.1f)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Success! Initializing...",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Preparing your inspection cockpit",
                fontSize = 14.sp,
                color = TextGrey,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

// ==========================================
// ISSUE REVIEW SCREEN
// ==========================================
@Composable
fun IssueReviewScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val creationState by viewModel.creationState.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Issue",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                // Custom Bulletproof Divider Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )

                // Scrollable details form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReviewItemRow(label = "Project", value = selectedProject)
                    
                    val locationStr = creationState.locationPath.joinToString(" - ")
                    ReviewItemRow(label = "Location", value = locationStr)
                    
                    ReviewItemRow(label = "Raise By", value = creationState.reporter)
                    
                    ReviewItemRow(label = "Type", value = creationState.category)
                    
                    ReviewItemRow(label = "Description", value = creationState.description.ifBlank { "Wall" })

                    // Display images
                    if (creationState.imageUris.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Photos",
                                fontSize = 13.sp,
                                color = TextGrey,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                creationState.imageUris.forEach { uri ->
                                    Card(
                                        modifier = Modifier.size(70.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Review Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ReviewItemRow(label = "Deadline", value = creationState.deadline)
                    
                    ReviewItemRow(label = "Assign To", value = creationState.assignee)

                    // Phone row under Assign To
                    Column {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Country code picker box
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "+91",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }

                            // Phone number box
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = creationState.assigneePhone.ifBlank { "Enter phone number" },
                                    fontSize = 14.sp,
                                    color = if (creationState.assigneePhone.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Raise button
                Button(
                    onClick = {
                        viewModel.saveCreatedIssue(asDraft = false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = "Raise",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItemRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextGrey,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ==========================================
// ISSUE SUCCESS SCREEN
// ==========================================
@Composable
fun IssueSuccessScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val latestSavedIssue by viewModel.latestSavedIssue.collectAsState()

    // --- ANIMATIONS ---
    // Star Entry Scale animation (scales up with a bouncy spring)
    val starScaleEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        starScaleEntry.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // Continuous breathing/pulsing & subtle rotation of the star
    val infiniteTransition = rememberInfiniteTransition(label = "starPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val starRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Confetti shooting progress (burst out after star begins to grow)
    val confettiProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(150)
        confettiProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Gentle hovering/floating offset for the confetti
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))

        // Large Star with Confetti
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Success Star",
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = starScaleEntry.value * pulseScale
                        scaleY = starScaleEntry.value * pulseScale
                        rotationZ = starRotation
                    },
                tint = Color(0xFFFFC107) // Yellow star
            )

            // Confetti
            val dotColors = listOf(
                Color(0xFF2196F3), // Blue
                Color(0xFF4CAF50), // Green
                Color(0xFFFF9800), // Orange
                Color(0xFF9C27B0), // Purple
                Color(0xFFE91E63)  // Pink
            )

            // Animated Confetti Dots shooting out from the center and floating gently
            Box(
                modifier = Modifier
                    .offset(
                        x = ((-55) * confettiProgress.value).dp + floatOffset.dp,
                        y = ((-45) * confettiProgress.value).dp - floatOffset.dp
                    )
                    .size(6.dp)
                    .background(dotColors[0], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((55) * confettiProgress.value).dp - floatOffset.dp,
                        y = ((-55) * confettiProgress.value).dp + floatOffset.dp
                    )
                    .size(8.dp)
                    .background(dotColors[1], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((-70) * confettiProgress.value).dp + floatOffset.dp,
                        y = ((25) * confettiProgress.value).dp + floatOffset.dp
                    )
                    .size(7.dp)
                    .background(dotColors[2], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((75) * confettiProgress.value).dp - floatOffset.dp,
                        y = ((35) * confettiProgress.value).dp - floatOffset.dp
                    )
                    .size(5.dp)
                    .background(dotColors[3], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((-25) * confettiProgress.value).dp - floatOffset.dp,
                        y = ((-75) * confettiProgress.value).dp + floatOffset.dp
                    )
                    .size(5.dp)
                    .background(dotColors[4], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((35) * confettiProgress.value).dp + floatOffset.dp,
                        y = ((70) * confettiProgress.value).dp - floatOffset.dp
                    )
                    .size(6.dp)
                    .background(dotColors[0], CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = ((-45) * confettiProgress.value).dp - floatOffset.dp,
                        y = ((75) * confettiProgress.value).dp + floatOffset.dp
                    )
                    .size(8.dp)
                    .background(dotColors[1], CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WELL DONE!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OrangePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Identifying problems helps resolve matters.",
            fontSize = 14.sp,
            color = TextGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "RAISED",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Issue Item Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            contentAlignment = Alignment.Center
        ) {
            latestSavedIssue?.let { issue ->
                IssueItemCard(
                    issue = issue,
                    onDelete = {}
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Go to Issue List Button
        Button(
            onClick = {
                viewModel.updateBottomTab(BottomNavTab.INSTRUCTION)
                viewModel.updateInstructionTab(InstructionStatusTab.RAISED)
                viewModel.resetCreationState()
                viewModel.screenStack.clear()
                viewModel.screenStack.add(Screen.HOME)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(
                text = "Go to Issue List",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// ==========================================
// EQC ACTIVE INSPECTION FLOW SCREENS
// ==========================================

enum class PhotoTargetType {
    NONE, DRAWING, GENERAL, QUESTION
}

@Composable
fun EqcChecklistScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val checklistName by viewModel.activeChecklistName.collectAsState()
    val locationPath by viewModel.activeLocationPath.collectAsState()
    val witnesses by viewModel.activeWitnesses.collectAsState()
    val drawingsCount by viewModel.activeDrawingsCount.collectAsState()
    val photosCount by viewModel.activePhotosCount.collectAsState()
    val questions by viewModel.activeQuestions.collectAsState()

    val context = LocalContext.current
    var remarkQuestionId by remember { mutableStateOf<Int?>(null) }
    var remarkText by remember { mutableStateOf("") }

    var showPhotoSourceSelector by remember { mutableStateOf(false) }
    var activePhotoTarget by remember { mutableStateOf(PhotoTargetType.NONE) }
    var activePhotoQuestionId by remember { mutableStateOf<Int?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            when (activePhotoTarget) {
                PhotoTargetType.DRAWING -> {
                    viewModel.addActiveDrawing()
                    android.widget.Toast.makeText(context, "Drawing captured from Camera successfully!", android.widget.Toast.LENGTH_SHORT).show()
                }
                PhotoTargetType.GENERAL -> {
                    viewModel.addActivePhoto()
                    android.widget.Toast.makeText(context, "General photo captured from Camera successfully!", android.widget.Toast.LENGTH_SHORT).show()
                }
                PhotoTargetType.QUESTION -> {
                    activePhotoQuestionId?.let { id ->
                        viewModel.addQuestionPhoto(id)
                        android.widget.Toast.makeText(context, "Question photo captured from Camera successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        } else {
            android.widget.Toast.makeText(context, "Camera capture cancelled", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error launching camera: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
            }
        } else {
            android.widget.Toast.makeText(context, "Camera permission denied", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            when (activePhotoTarget) {
                PhotoTargetType.DRAWING -> {
                    viewModel.addActiveDrawing()
                    android.widget.Toast.makeText(context, "Drawing selected from Gallery successfully!", android.widget.Toast.LENGTH_SHORT).show()
                }
                PhotoTargetType.GENERAL -> {
                    viewModel.addActivePhoto()
                    android.widget.Toast.makeText(context, "General photo selected from Gallery successfully!", android.widget.Toast.LENGTH_SHORT).show()
                }
                PhotoTargetType.QUESTION -> {
                    activePhotoQuestionId?.let { id ->
                        viewModel.addQuestionPhoto(id)
                        android.widget.Toast.makeText(context, "Question photo selected from Gallery successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        } else {
            android.widget.Toast.makeText(context, "Gallery selection cancelled", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val answeredCount = questions.count { q -> q.answer.isNotBlank() }
    val totalCount = questions.size

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(Color(0xFF030712), Color(0xFF0C0E1E), Color(0xFF1E1B4B))
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFF9FAFB), Color(0xFFEEF2FF), Color(0xFFE0E7FF))
            )
        }
    }

    // Validation rules:
    // 1. At least one witness is selected
    // 2. All questions are answered (Yes/No/Skip)
    // 3. To make it extremely clear and satisfy photo validations, each question or the overall drawing must have at least 1 photo!
    val isWitnessValid = witnesses.isNotEmpty()
    val areAllQuestionsAnswered = answeredCount == totalCount
    val arePhotosValid = (drawingsCount > 0 || photosCount > 0 || questions.any { it.photosCount > 0 })
    val isPassEnabled = isWitnessValid && areAllQuestionsAnswered && arePhotosValid

    Scaffold(
        topBar = {
            Surface(
                color = if (isDark) Color(0xFF111827) else Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = if (isDark) Color.White else Color.Black)
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = checklistName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Single Stage (1/1)",
                            fontSize = 12.sp,
                            color = if (isDark) Color.LightGray else Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "10:19",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Text(
                            text = "20 Jul 2026",
                            fontSize = 11.sp,
                            color = if (isDark) Color.LightGray else Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                // Answered status card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1F2937) else Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Checked icon", tint = if (answeredCount == totalCount) Color(0xFF10B981) else Color(0xFFF59E0B))
                                Text(
                                    text = "Answered",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                            
                            val statusColor = when {
                                answeredCount == totalCount -> Color(0xFF10B981)
                                answeredCount > 0 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }
                            Text(
                                text = "$answeredCount/$totalCount",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = statusColor
                            )
                        }
                    }
                }

                // Current Activity summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Current activity:",
                                fontSize = 12.sp,
                                color = if (isDark) Color.LightGray else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = locationPath,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1E293B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.FactCheck, contentDescription = "Stage", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Inspection: Single Stage (1/1)",
                                    fontSize = 13.sp,
                                    color = if (isDark) Color.LightGray else Color.Gray
                                )
                            }
                        }
                    }
                }

                // INSPECTION WITNESS Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "INSPECTION WITNESS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                            Text(text = " *", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val witnessOptions = listOf("Client", "Contractor", "Vendor", "Consultant", "Developer", "Other")
                            witnessOptions.forEach { witness ->
                                val isSelected = witnesses.contains(witness)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color(0xFF111E30) else if (isDark) Color(0xFF1F2937) else Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) OrangePrimary else if (isDark) Color.Transparent else Color.LightGray,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { viewModel.toggleActiveWitness(witness) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                        Text(
                                            text = witness,
                                            color = if (isSelected) Color.White else if (isDark) Color.LightGray else Color.DarkGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // DRAWING DETAILS AND CAMERA ATTACHMENTS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Drawing Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DRAWING DETAILS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                                Text(text = " *", color = OrangePrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDark) Color(0xFF1F2937) else Color.White)
                                        .border(
                                            BorderStroke(1.dp, if (isDark) Color.DarkGray else Color.LightGray),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            activePhotoTarget = PhotoTargetType.DRAWING
                                            activePhotoQuestionId = null
                                            showPhotoSourceSelector = true
                                        },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camera",
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (drawingsCount > 0) "$drawingsCount Attached" else "Add Drawing",
                                        fontSize = 11.sp,
                                        color = if (isDark) Color.LightGray else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // General Photos Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GENERAL PHOTOS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isDark) Color(0xFF1F2937) else Color.White)
                                        .border(
                                            BorderStroke(1.dp, if (isDark) Color.DarkGray else Color.LightGray),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            activePhotoTarget = PhotoTargetType.GENERAL
                                            activePhotoQuestionId = null
                                            showPhotoSourceSelector = true
                                        },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camera",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (photosCount > 0) "$photosCount Attached" else "Add Photo",
                                        fontSize = 11.sp,
                                        color = if (isDark) Color.LightGray else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Divider Label for Questions
                item {
                    Text(
                        text = "CHECKLIST QUESTIONS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.LightGray else Color.DarkGray
                    )
                }

                // Checklist Question cards
                items(questions) { question ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1F2937) else Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${question.id}. ${question.text}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1F2937),
                                    modifier = Modifier.weight(1f)
                                )
                                
                                if (question.answer.isNotBlank()) {
                                    val badgeColor = when (question.answer) {
                                        "Yes" -> Color(0xFF10B981)
                                        "No" -> Color(0xFFEF4444)
                                        else -> Color(0xFF64748B)
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (question.answer == "Skip") "SKIPPED" else "ANSWERED",
                                            color = badgeColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Segmented Option Buttons (Yes / No / Skip)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Yes", "No", "Skip").forEach { opt ->
                                    val isSel = question.answer == opt
                                    val btnBg = if (isSel) {
                                        when (opt) {
                                            "Yes" -> Color(0xFF10B981)
                                            "No" -> Color(0xFFEF4444)
                                            else -> Color(0xFF64748B)
                                        }
                                    } else {
                                        if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6)
                                    }
                                    val btnTextCol = if (isSel) Color.White else if (isDark) Color.LightGray else Color.DarkGray

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(btnBg)
                                            .clickable { viewModel.updateQuestionAnswer(question.id, opt) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt,
                                            color = btnTextCol,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = if (isDark) Color(0xFF374151) else Color(0xFFE5E7EB))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Remark & Photos inside Question
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Add Remark Row
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable {
                                            remarkQuestionId = question.id
                                            remarkText = question.remark
                                        }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Message,
                                        contentDescription = "Remark Icon",
                                        tint = if (question.remark.isNotBlank()) OrangePrimary else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (question.remark.isNotBlank()) question.remark else "Remark",
                                        fontSize = 12.sp,
                                        color = if (question.remark.isNotBlank()) OrangePrimary else Color.Gray,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 120.dp)
                                    )
                                }

                                // Photo attach box for Question
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6))
                                        .clickable {
                                            activePhotoTarget = PhotoTargetType.QUESTION
                                            activePhotoQuestionId = question.id
                                            showPhotoSourceSelector = true
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera Icon", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Photos (${question.photosCount}) *",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Buttons Panel
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = if (isDark) Color(0xFF111827) else Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // FAIL button
                    Button(
                        onClick = {
                            android.widget.Toast.makeText(context, "Inspection marked as Failed!", android.widget.Toast.LENGTH_SHORT).show()
                            viewModel.navigateBack()
                        },
                        enabled = !isPassEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isPassEnabled) Color(0xFF64748B) else Color.LightGray,
                            contentColor = if (!isPassEnabled) Color.White else Color.DarkGray
                        )
                    ) {
                        Text("Fail", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // PASS button (enabled conditionally)
                    Button(
                        onClick = {
                            if (isPassEnabled) {
                                viewModel.navigateTo(Screen.EQC_SELECT_TEAM)
                            }
                        },
                        enabled = isPassEnabled,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPassEnabled) Color(0xFF111E30) else Color.LightGray,
                            contentColor = if (isPassEnabled) Color.White else Color.DarkGray
                        )
                    ) {
                        Text("Pass", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Remark Dialog Box
    if (remarkQuestionId != null) {
        AlertDialog(
            onDismissRequest = { remarkQuestionId = null },
            title = { Text("Add Remark") },
            text = {
                OutlinedTextField(
                    value = remarkText,
                    onValueChange = { remarkText = it },
                    placeholder = { Text("Enter your remark...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        remarkQuestionId?.let { id ->
                            viewModel.updateQuestionRemark(id, remarkText)
                        }
                        remarkQuestionId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { remarkQuestionId = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Photo Source Selector Dialog
    if (showPhotoSourceSelector) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceSelector = false },
            title = {
                Text(
                    text = "Attach Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDark) Color.White else Color(0xFF1E293B)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select a source to add your photo:",
                        fontSize = 14.sp,
                        color = if (isDark) Color.LightGray else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Option 1: Take Photo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6))
                            .clickable {
                                val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                )
                                if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    try {
                                        cameraLauncher.launch(null)
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(context, "Error launching camera: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                                showPhotoSourceSelector = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Take Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                    }

                    // Option 2: Choose from Gallery
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6))
                            .clickable {
                                try {
                                    galleryLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "Error opening gallery: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                showPhotoSourceSelector = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Gallery",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Choose from Gallery",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoSourceSelector = false }) {
                    Text("Cancel", color = OrangePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun EqcSelectTeamScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val checklistName by viewModel.activeChecklistName.collectAsState()
    val locationPath by viewModel.activeLocationPath.collectAsState()
    val selectedTeam by viewModel.selectedTeam.collectAsState()

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(Color(0xFF030712), Color(0xFF0C0E1E), Color(0xFF1E1B4B))
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFF9FAFB), Color(0xFFEEF2FF), Color(0xFFE0E7FF))
            )
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = if (isDark) Color(0xFF111827) else Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = if (isDark) Color.White else Color.Black)
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = locationPath,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = checklistName,
                            fontSize = 12.sp,
                            color = if (isDark) Color.LightGray else Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Post-Inspection Flow",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color(0xFF1E293B)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1F2937) else Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Select Team Form Field
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Select team",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                                Text(text = " *", color = OrangePrimary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Non-editable display field showing Test Agency as requested
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6))
                                    .border(BorderStroke(1.dp, if (isDark) Color.Transparent else Color.LightGray), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Test Agency",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1E293B)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow",
                                        tint = if (isDark) Color.LightGray else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Buttons panel
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = if (isDark) Color(0xFF111827) else Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isDark) Color.LightGray else Color.DarkGray),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isDark) Color.White else Color.Black)
                    ) {
                        Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.completeQc() },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF111E30),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Done", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EqcSuccessScreen(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val checklistName by viewModel.activeChecklistName.collectAsState()
    val locationPath by viewModel.activeLocationPath.collectAsState()

    val isDark = isSystemInDarkTheme()
    val bgBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(Color(0xFF030712), Color(0xFF0C0E1E), Color(0xFF111827))
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFF9FAFB), Color(0xFFEEF2FF), Color(0xFFE0E7FF))
            )
        }
    }

    // Interactive custom infinite animations
    val infiniteTransition = rememberInfiniteTransition(label = "StarAnimation")
    
    // Star scale dynamic breathing pulse
    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarScale"
    )

    // Gentle star rotation swing
    val starRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarRotation"
    )

    // Expanding backdrop success halo
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HaloScale"
    )

    // Halo alpha fading out
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HaloAlpha"
    )

    // Entry anim state for the card and subheadings
    var animatedEntry by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animatedEntry = true
    }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (animatedEntry) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "EntranceAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Sync Successful Header Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .graphicsLayer(alpha = entranceAlpha),
            color = Color(0xFF10B981)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sync successful!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.15f))

        // Large Green Success Badge Animated Visuals
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Expanding background aura/halo (Green themed to match Success!)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(
                        scaleX = haloScale,
                        scaleY = haloScale,
                        alpha = haloAlpha
                    )
                    .background(Color(0xFFD1FAE5), shape = CircleShape)
            )

            // Primary emerald base with dynamic breathing scaling
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(
                        scaleX = starScale,
                        scaleY = starScale
                    ),
                shape = CircleShape,
                color = Color(0xFF10B981), // Emerald Green
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Success check",
                        tint = Color.White,
                        modifier = Modifier
                            .size(54.dp)
                            .graphicsLayer(
                                rotationZ = starRotation
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "WELL DONE!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OrangePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer(alpha = entranceAlpha)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Each EQC stage is a step towards Quality.",
            fontSize = 14.sp,
            color = if (isDark) Color.LightGray else Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer(alpha = entranceAlpha)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Success PASS Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .graphicsLayer(alpha = entranceAlpha)
        ) {
            Text(
                text = "PASS",
                color = Color(0xFF10B981),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.weight(0.15f))

        // Bottom EQC details overview card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(alpha = entranceAlpha),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1F2937) else Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location icon", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = locationPath,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.FactCheck, contentDescription = "Checklist icon", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(
                        text = checklistName,
                        fontSize = 13.sp,
                        color = if (isDark) Color.LightGray else Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Pending, contentDescription = "Stage icon", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Inspect Stage - Single Stage (1/1)",
                        fontSize = 13.sp,
                        color = if (isDark) Color.LightGray else Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Person, contentDescription = "Inspector icon", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Saharsh Sathyanarayanan",
                        fontSize = 13.sp,
                        color = if (isDark) Color.LightGray else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.2f))

        // Go to EQC List persistent CTA button
        Button(
            onClick = { viewModel.goToEqcList() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111E30))
        ) {
            Text(
                text = "Go to EQC List",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

