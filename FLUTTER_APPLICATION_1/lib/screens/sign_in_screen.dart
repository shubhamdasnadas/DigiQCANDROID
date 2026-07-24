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
