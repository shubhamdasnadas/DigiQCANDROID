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
