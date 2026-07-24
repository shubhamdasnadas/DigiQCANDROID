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
