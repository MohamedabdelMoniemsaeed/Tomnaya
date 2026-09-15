import 'package:flutter/material.dart';

class DriverDashboardWidget extends StatefulWidget {
  final VoidCallback onSwitchToPassenger;

  const DriverDashboardWidget({Key? key, required this.onSwitchToPassenger}) : super(key: key);

  @override
  State<DriverDashboardWidget> createState() => _DriverDashboardWidgetState();
}

class _DriverDashboardWidgetState extends State<DriverDashboardWidget> {
  int occupiedSeats = 4;
  bool isOnline = true;
  double earningsToday = 480.0;
  int tripsCompleted = 7;

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Online / Offline Status Switch
          Card(
            color: isOnline ? const Color(0xFF10B981).withOpacity(0.1) : const Color(0xFFEF4444).withOpacity(0.1),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Icon(
                        isOnline ? Icons.check_circle : Icons.pause_circle_filled,
                        color: isOnline ? const Color(0xFF10B981) : const Color(0xFFEF4444),
                        size: 32,
                      ),
                      const SizedBox(width: 12),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            isOnline ? 'الكابتن متاح لاستقبال الركاب' : 'أنت غير متصل الآن',
                            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                          ),
                          Text(
                            isOnline ? 'خط: الجيزة ⇄ الهرم ⇄ حدائق الأهرام' : 'اضغط للاتصال وبدء تحميل الركاب',
                            style: const TextStyle(fontSize: 12, color: Colors.grey),
                          ),
                        ],
                      ),
                    ],
                  ),
                  Switch(
                    value: isOnline,
                    activeColor: const Color(0xFF10B981),
                    onChanged: (val) => setState(() => isOnline = val),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Occupancy Counter (7 Seats)
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  const Text(
                    'عداد تقفيل العربية (7 ركاب)',
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF0F172A)),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '$occupiedSeats / 7 ركاب محملين',
                    style: TextStyle(
                      fontSize: 26,
                      fontWeight: FontWeight.bold,
                      color: occupiedSeats == 7 ? const Color(0xFF10B981) : const Color(0xFFF59E0B),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      ElevatedButton.icon(
                        onPressed: occupiedSeats > 0 ? () => setState(() => occupiedSeats--) : null,
                        icon: const Icon(Icons.remove),
                        label: const Text('نزل راكب'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.grey.shade200,
                          foregroundColor: Colors.black87,
                        ),
                      ),
                      ElevatedButton.icon(
                        onPressed: occupiedSeats < 7 ? () => setState(() => occupiedSeats++) : null,
                        icon: const Icon(Icons.add),
                        label: const Text('ركب راكب'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF0F172A),
                          foregroundColor: Colors.white,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Today's Stats
          Row(
            children: [
              Expanded(
                child: _buildStatCard('إيراد اليوم', '${earningsToday.toStringAsFixed(0)} ج.م', Icons.monetization_on, const Color(0xFF10B981)),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: _buildStatCard('رحلات اليوم', '$tripsCompleted رحلات', Icons.directions_car, const Color(0xFF0F172A)),
              ),
            ],
          ),
          const SizedBox(height: 24),

          OutlinedButton.icon(
            onPressed: widget.onSwitchToPassenger,
            icon: const Icon(Icons.person),
            label: const Text('العودة إلى وضع الراكب وحجز الكراسي'),
            style: OutlinedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 14),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Icon(icon, color: color, size: 28),
            const SizedBox(height: 6),
            Text(title, style: const TextStyle(fontSize: 12, color: Colors.grey)),
            const SizedBox(height: 4),
            Text(value, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: color)),
          ],
        ),
      ),
    );
  }
}
