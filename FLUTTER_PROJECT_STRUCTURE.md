# Modular Flutter Project Structure & Android Setup Guide

This guide gives you the standard, production-ready Flutter folder structure for `FLUTTER_APPLICATION_1`, along with code for every file and step-by-step instructions to run the application on an Android device or emulator on a Windows local machine.

---

## 📂 Standard Flutter Directory Structure

```text
FLUTTER_APPLICATION_1/
├── android/                          # Android Native Config
├── ios/                              # iOS Native Config
├── windows/                          # Windows Native Config
├── pubspec.yaml                      # Dependencies & Package Config
├── setup.sql                         # PostgreSQL Database Initialization Script
│
└── lib/                              # Main Flutter Application Code
    ├── main.dart                     # App Entry point & Material Theme configuration
    │
    ├── constants/
    │   └── app_colors.dart           # Theme colors, constants & styling defaults
    │
    ├── models/
    │   ├── user_model.dart           # User & authentication models
    │   └── organization_model.dart   # Multi-tenant organization models
    │
    ├── services/
    │   └── api_service.dart          # HTTP API Service (Connects to Next.js + PostgreSQL)
    │
    ├── screens/
    │   ├── sign_in_screen.dart       # Authentication & Connection Selector Screen
    │   └── dashboard_screen.dart     # Main CISO Inspection Dashboard Screen
    │
    └── widgets/
        ├── custom_text_field.dart    # Reusable dark-themed input text field
        └── server_config_card.dart   # Server target switch (10.0.2.2 / LAN IP / Cloud)
```

---

## 🛠️ Required Software for Windows Machine

To run this app on your local machine, ensure you have the following installed:

1. **Flutter SDK** (v3.0.0 or higher) → [Download Flutter for Windows](https://docs.flutter.dev/get-started/install/windows)
2. **Android Studio** → [Download Android Studio](https://developer.android.com/studio)
   - Ensure **Android SDK Build-Tools** and **Android Emulator** are checked under *SDK Manager -> SDK Tools*.
3. **PostgreSQL** (v15 or v16) → [Download PostgreSQL](https://www.postgresql.org/download/windows/)
4. **Node.js** (v18 or higher) → For running the Next.js API backend.

---

## 💻 Step-by-Step Implementation Files

### 1. `pubspec.yaml`
```yaml
name: flutter_application_1
description: "CISO Multi-Tenant Security & Quality Inspection App"
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  http: ^1.2.0
  google_fonts: ^6.1.0
  cupertino_icons: ^1.0.6

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0

flutter:
  uses-material-design: true
```

---

### 2. `lib/constants/app_colors.dart`
```dart
import 'package:flutter/material.dart';

class AppColors {
  static const Color background = Color(0xFF020617);
  static const Color cardSurface = Color(0xFF0F172A);
  static const Color cardBorder = Color(0xFF1E293B);
  static const Color inputFill = Color(0xFF1E293B);
  static const Color inputBorder = Color(0xFF334155);
  static const Color primaryBlue = Color(0xFF2563EB);
  static const Color accentBlue = Color(0xFF3B82F6);
  static const Color textPrimary = Colors.white;
  static const Color textMuted = Color(0xFF94A3B8);
  static const Color successBg = Color(0xFF064E3B);
  static const Color successText = Color(0xFF6EE7B7);
  static const Color errorBg = Color(0xFF7F1D1D);
  static const Color errorText = Color(0xFFFCA5A5);
}
```

---

### 3. `lib/models/organization_model.dart`
```dart
class OrganizationModel {
  final int id;
  final String orgName;
  final String slug;

  OrganizationModel({
    required this.id,
    required this.orgName,
    required this.slug,
  });

  factory OrganizationModel.fromJson(Map<String, dynamic> json) {
    return OrganizationModel(
      id: json['id'] ?? 0,
      orgName: json['org_name'] ?? json['name'] ?? '',
      slug: json['slug'] ?? json['code'] ?? '',
    );
  }
}
```

---

### 4. `lib/models/user_model.dart`
```dart
class UserModel {
  final String username;
  final String organization;
  final String role;
  final bool isAuthenticated;

  UserModel({
    required this.username,
    required this.organization,
    required this.role,
    required this.isAuthenticated,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      username: json['username'] ?? '',
      organization: json['organization'] ?? '',
      role: json['role'] ?? 'member',
      isAuthenticated: json['success'] ?? false,
    );
  }
}
```

---

### 5. `lib/services/api_service.dart`
```dart
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/user_model.dart';

class ApiService {
  static Future<UserModel> login({
    required String baseUrl,
    required String username,
    required String password,
    required String organization,
  }) async {
    final url = Uri.parse('$baseUrl/api/login');
    final response = await http
        .post(
          url,
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'username': username,
            'password': password,
            'organization': organization,
          }),
        )
        .timeout(const Duration(seconds: 8));

    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return UserModel.fromJson(json);
    } else {
      throw Exception('Server Error ${response.statusCode}: ${response.reasonPhrase}');
    }
  }

  static Future<List<String>> fetchOrganizations(String baseUrl) async {
    final url = Uri.parse('$baseUrl/api/organizations');
    final response = await http.get(url).timeout(const Duration(seconds: 5));
    if (response.statusCode == 200) {
      final List data = jsonDecode(response.body);
      return data.map((item) => item['name'].toString()).toList();
    }
    return [
      'Techsec Global Private Ltd',
      'PCPL Construction',
      'Acme Cyber Defense',
      'Northwind Logistics',
      'BlueShield Healthcare'
    ];
  }
}
```

---

### 6. `lib/widgets/custom_text_field.dart`
```dart
import 'package:flutter/material.dart';
import '../constants/app_colors.dart';

class CustomTextField extends StatelessWidget {
  final TextEditingController controller;
  final String label;
  final IconData icon;
  final bool obscureText;
  final Widget? suffixIcon;

  const CustomTextField({
    super.key,
    required this.controller,
    required this.label,
    required this.icon,
    this.obscureText = false,
    this.suffixIcon,
  });

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: controller,
      obscureText: obscureText,
      style: const TextStyle(color: AppColors.textPrimary),
      decoration: InputDecoration(
        labelText: label,
        labelStyle: const TextStyle(color: AppColors.textMuted, fontSize: 13),
        prefixIcon: Icon(icon, color: AppColors.textMuted, size: 20),
        suffixIcon: suffixIcon,
        filled: true,
        fillColor: AppColors.inputFill,
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.inputBorder),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
          borderSide: const BorderSide(color: AppColors.primaryBlue, width: 1.5),
        ),
      ),
    );
  }
}
```

---

### 7. `lib/widgets/server_config_card.dart`
```dart
import 'package:flutter/material.dart';
import '../constants/app_colors.dart';

class ServerConfigCard extends StatelessWidget {
  final String selectedMode;
  final ValueChanged<String> onModeChanged;
  final TextEditingController customIpController;

  const ServerConfigCard({
    super.key,
    required this.selectedMode,
    required this.onModeChanged,
    required this.customIpController,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.inputFill,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.inputBorder),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Server Connection Target',
            style: TextStyle(color: AppColors.textMuted, fontSize: 11, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          SegmentedButton<String>(
            segments: const [
              ButtonSegment(value: 'emulator', label: Text('Emulator (10.0.2.2)', style: TextStyle(fontSize: 10))),
              ButtonSegment(value: 'lan', label: Text('Custom Wi-Fi IP', style: TextStyle(fontSize: 10))),
              ButtonSegment(value: 'cloud', label: Text('Cloud API', style: TextStyle(fontSize: 10))),
            ],
            selected: {selectedMode},
            onSelectionChanged: (set) => onModeChanged(set.first),
          ),
          if (selectedMode == 'lan') ...[
            const SizedBox(height: 8),
            TextFormField(
              controller: customIpController,
              style: const TextStyle(color: Colors.white, fontSize: 12),
              decoration: InputDecoration(
                labelText: 'Target Local Server URL',
                hintText: 'http://192.168.1.100:3000',
                labelStyle: const TextStyle(color: AppColors.textMuted, fontSize: 11),
                filled: true,
                fillColor: AppColors.cardSurface,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
              ),
            ),
          ],
        ],
      ),
    );
  }
}
```

---

### 8. `lib/screens/dashboard_screen.dart`
```dart
import 'package:flutter/material.dart';
import '../constants/app_colors.dart';

class DashboardScreen extends StatelessWidget {
  final String username;
  final String organization;

  const DashboardScreen({
    super.key,
    required this.username,
    required this.organization,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: AppColors.cardSurface,
        title: Text('CISO Dashboard — $organization', style: const TextStyle(color: Colors.white, fontSize: 16)),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout, color: AppColors.textMuted),
            onPressed: () => Navigator.pop(context),
          )
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Card(
              color: AppColors.cardSurface,
              shape: RoundedRectangleBorder(
                side: const BorderSide(color: AppColors.cardBorder),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    const CircleAvatar(
                      backgroundColor: AppColors.primaryBlue,
                      child: Icon(Icons.person, color: Colors.white),
                    ),
                    const SizedBox(width: 12),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Welcome, $username', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
                        Text('Organization: $organization', style: const TextStyle(color: AppColors.textMuted, fontSize: 12)),
                        const Text('Status: Authenticated via PostgreSQL', style: TextStyle(color: AppColors.successText, fontSize: 12)),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

### 9. `lib/screens/sign_in_screen.dart`
```dart
import 'package:flutter/material.dart';
import '../constants/app_colors.dart';
import '../services/api_service.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/server_config_card.dart';
import 'dashboard_screen.dart';

class SignInScreen extends StatefulWidget {
  const SignInScreen({super.key});

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen> {
  final _usernameController = TextEditingController(text: 'Shubham');
  final _passwordController = TextEditingController(text: 'Shubham@123');
  final _customIpController = TextEditingController(text: 'http://192.168.1.100:3000');

  bool _obscurePassword = true;
  String _selectedOrg = 'Techsec Global Private Ltd';
  String _serverMode = 'emulator';
  bool _isLoading = false;
  String? _statusMessage;
  bool _isSuccess = false;

  final List<String> _organizations = [
    'Techsec Global Private Ltd',
    'PCPL Construction',
    'Acme Cyber Defense',
    'Northwind Logistics',
    'BlueShield Healthcare',
  ];

  String get _activeBaseUrl {
    if (_serverMode == 'emulator') return 'http://10.0.2.2:3000';
    if (_serverMode == 'cloud') return 'https://api.cisodashboard.com';
    return _customIpController.text.trim();
  }

  Future<void> _handleLogin() async {
    setState(() {
      _isLoading = true;
      _statusMessage = null;
    });

    try {
      final user = await ApiService.login(
        baseUrl: _activeBaseUrl,
        username: _usernameController.text.trim(),
        password: _passwordController.text.trim(),
        organization: _selectedOrg,
      );

      if (user.isAuthenticated) {
        setState(() {
          _isSuccess = true;
          _statusMessage = 'Success! Authenticated ${user.username} for ${user.organization}';
        });

        if (mounted) {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => DashboardScreen(
                username: user.username,
                organization: user.organization,
              ),
            ),
          );
        }
      }
    } catch (e) {
      setState(() {
        _isSuccess = false;
        _statusMessage = 'Error: $e\nVerify Next.js API is running on $_activeBaseUrl';
      });
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Container(
              maxWidth: 500,
              decoration: BoxDecoration(
                color: AppColors.cardSurface,
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: AppColors.cardBorder),
              ),
              padding: const EdgeInsets.all(24.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const Row(
                    children: [
                      Icon(Icons.security, color: AppColors.accentBlue, size: 28),
                      SizedBox(width: 12),
                      Text('CISO Dashboard', style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold)),
                    ],
                  ),
                  const SizedBox(height: 24),
                  CustomTextField(
                    controller: _usernameController,
                    label: 'Username',
                    icon: Icons.person_outline,
                  ),
                  const SizedBox(height: 16),
                  CustomTextField(
                    controller: _passwordController,
                    label: 'Password',
                    icon: Icons.lock_outline,
                    obscureText: _obscurePassword,
                    suffixIcon: IconButton(
                      icon: Icon(_obscurePassword ? Icons.visibility_off : Icons.visibility, color: AppColors.textMuted),
                      onPressed: () => setState(() => _obscurePassword = !_obscurePassword),
                    ),
                  ),
                  const SizedBox(height: 16),
                  DropdownButtonFormField<String>(
                    value: _selectedOrg,
                    dropdownColor: AppColors.inputFill,
                    style: const TextStyle(color: Colors.white),
                    decoration: const InputDecoration(
                      labelText: 'Organization',
                      labelStyle: TextStyle(color: AppColors.textMuted),
                      filled: true,
                      fillColor: AppColors.inputFill,
                    ),
                    items: _organizations.map((org) => DropdownMenuItem(value: org, child: Text(org))).toList(),
                    onChanged: (val) => setState(() => _selectedOrg = val!),
                  ),
                  const SizedBox(height: 16),
                  ServerConfigCard(
                    selectedMode: _serverMode,
                    onModeChanged: (mode) => setState(() => _serverMode = mode),
                    customIpController: _customIpController,
                  ),
                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: _isLoading ? null : _handleLogin,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryBlue,
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                    child: _isLoading
                        ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                        : const Text('Connect & Verify PostgreSQL', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                  ),
                  if (_statusMessage != null) ...[
                    const SizedBox(height: 16),
                    Container(
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: _isSuccess ? AppColors.successBg : AppColors.errorBg,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(_statusMessage!, style: TextStyle(color: _isSuccess ? AppColors.successText : AppColors.errorText, fontSize: 12)),
                    ),
                  ],
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
```

---

### 10. `lib/main.dart`
```dart
import 'package:flutter/material.dart';
import 'constants/app_colors.dart';
import 'screens/sign_in_screen.dart';

void main() {
  runApp(const CisoDashboardApp());
}

class CisoDashboardApp extends StatelessWidget {
  const CisoDashboardApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CISO Quality & Security Dashboard',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: AppColors.background,
        colorScheme: ColorScheme.fromSeed(
          seedColor: AppColors.cardSurface,
          primary: AppColors.primaryBlue,
          surface: AppColors.cardSurface,
        ),
      ),
      home: const SignInScreen(),
    );
  }
}
```

---

## 🚀 Steps to Run on Android Device / Emulator (Windows)

### Step 1: Database Setup
1. Open Command Prompt (cmd) on Windows as Administrator.
2. Run PostgreSQL script:
   ```cmd
   psql -U postgres -d postgres -f "C:\setup.sql"
   ```

### Step 2: Start Next.js Local Backend
1. Go to your Next.js project directory:
   ```cmd
   cd C:\path-to-nextjs-backend
   npm run dev
   ```

### Step 3: Launch Android Device / Emulator in Android Studio
1. Open **Android Studio**.
2. Open the Flutter project directory (`FLUTTER_APPLICATION_1`).
3. **For Emulator**: Open Device Manager -> Click **Play** on an Android Virtual Device (AVD).
4. **For Physical Android Phone**:
   - Plug phone via USB.
   - Enable **Developer Options -> USB Debugging**.
   - Make sure PC and phone are on the **same Wi-Fi network**.

### Step 4: Run Flutter Command
Open terminal inside Android Studio or VS Code and run:

```cmd
flutter pub get
flutter run
```

- If using **Emulator**: Choose **"Emulator (10.0.2.2)"** in the app UI.
- If using **Physical Phone**: Choose **"Custom Wi-Fi IP"** and enter your PC's IP address (from `ipconfig`, e.g., `http://192.168.1.100:3000`).
