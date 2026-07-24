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
