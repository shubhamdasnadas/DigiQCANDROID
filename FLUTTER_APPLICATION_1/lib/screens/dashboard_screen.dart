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
