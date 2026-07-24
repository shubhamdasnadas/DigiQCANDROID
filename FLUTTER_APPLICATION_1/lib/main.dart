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
