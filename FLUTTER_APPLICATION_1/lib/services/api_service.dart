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
