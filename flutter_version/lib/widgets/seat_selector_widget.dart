import 'package:flutter/material.dart';

class SeatSelectorWidget extends StatelessWidget {
  final Set<int> selectedSeats;
  final Function(int seatIndex) onSeatToggled;
  final Set<int> occupiedSeats;

  const SeatSelectorWidget({
    Key? key,
    required this.selectedSeats,
    required this.onSeatToggled,
    this.occupiedSeats = const {2, 5},
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      shape: RoundedCornerShape(16),
      elevation: 3,
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Row(
                  children: [
                    Icon(Icons.event_seat, color: Color(0xFF0F172A), size: 20),
                    SizedBox(width: 8),
                    Text(
                      'صالون السوزوكي (7 راكب)',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF0F172A),
                      ),
                    ),
                  ],
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: const Color(0xFFF59E0B).withOpacity(0.15),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    '${selectedSeats.length} كراسي محجوزة',
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFFB45309),
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // Suzuki Van Interior Container
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFF8FAFC),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: const Color(0xFFCBD5E1), width: 2),
              ),
              child: Column(
                children: [
                  // Dashboard & Front Row
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Driver Seat (Fixed)
                      _buildFixedSeat(
                        icon: Icons.sports_motorsports,
                        label: 'الكابتن',
                        color: const Color(0xFF0F172A),
                      ),
                      // Steering wheel indicator
                      Container(
                        padding: const EdgeInsets.all(6),
                        decoration: BoxDecoration(
                          color: const Color(0xFFE2E8F0),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(Icons.circle_outlined, size: 20, color: Colors.grey),
                      ),
                      // Front Passenger Seat (Seat 1)
                      _buildInteractiveSeat(
                        index: 1,
                        label: 'كرسي قدام',
                        isOccupied: occupiedSeats.contains(1),
                        isSelected: selectedSeats.contains(1),
                      ),
                    ],
                  ),
                  const Divider(height: 24, thickness: 1, color: Color(0xFFE2E8F0)),

                  // Middle Row (3 Seats: 2, 3, 4)
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _buildInteractiveSeat(
                        index: 2,
                        label: 'يمين وسط',
                        isOccupied: occupiedSeats.contains(2),
                        isSelected: selectedSeats.contains(2),
                      ),
                      _buildInteractiveSeat(
                        index: 3,
                        label: 'نص وسط',
                        isOccupied: occupiedSeats.contains(3),
                        isSelected: selectedSeats.contains(3),
                      ),
                      _buildInteractiveSeat(
                        index: 4,
                        label: 'شمال وسط',
                        isOccupied: occupiedSeats.contains(4),
                        isSelected: selectedSeats.contains(4),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),

                  // Back Row (3 Seats: 5, 6, 7)
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      _buildInteractiveSeat(
                        index: 5,
                        label: 'يمين ورا',
                        isOccupied: occupiedSeats.contains(5),
                        isSelected: selectedSeats.contains(5),
                      ),
                      _buildInteractiveSeat(
                        index: 6,
                        label: 'نص ورا',
                        isOccupied: occupiedSeats.contains(6),
                        isSelected: selectedSeats.contains(6),
                      ),
                      _buildInteractiveSeat(
                        index: 7,
                        label: 'شمال ورا',
                        isOccupied: occupiedSeats.contains(7),
                        isSelected: selectedSeats.contains(7),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 14),

            // Legend
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                _buildLegendItem('متاح', const Color(0xFFE2E8F0), Colors.black87),
                const SizedBox(width: 16),
                _buildLegendItem('محجوز لك', const Color(0xFF0F172A), Colors.white),
                const SizedBox(width: 16),
                _buildLegendItem('راكب آخر', const Color(0xFFCBD5E1), Colors.grey),
              ],
            )
          ],
        ),
      ),
    );
  }

  Widget _buildInteractiveSeat({
    required int index,
    required String label,
    required bool isOccupied,
    required bool isSelected,
  }) {
    Color bgColor = isSelected
        ? const Color(0xFF0F172A)
        : (isOccupied ? const Color(0xFFE2E8F0) : Colors.white);
    Color textColor = isSelected ? Colors.white : (isOccupied ? Colors.grey : const Color(0xFF0F172A));
    Color borderColor = isSelected
        ? const Color(0xFFF59E0B)
        : (isOccupied ? Colors.transparent : const Color(0xFF94A3B8));

    return InkWell(
      onTap: isOccupied ? null : () => onSeatToggled(index),
      borderRadius: BorderRadius.circular(12),
      child: Container(
        width: 72,
        height: 68,
        padding: const EdgeInsets.all(6),
        decoration: BoxDecoration(
          color: bgColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: borderColor, width: isSelected ? 2 : 1),
          boxShadow: isSelected
              ? [BoxShadow(color: Colors.amber.withOpacity(0.3), blurRadius: 6, offset: const Offset(0, 3))]
              : [],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              isOccupied ? Icons.person : Icons.chair,
              size: 24,
              color: isSelected ? const Color(0xFFF59E0B) : textColor,
            ),
            const SizedBox(height: 2),
            Text(
              isOccupied ? 'مشغول' : label,
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 10,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                color: textColor,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFixedSeat({required IconData icon, required String label, required Color color}) {
    return Container(
      width: 72,
      height: 68,
      padding: const EdgeInsets.all(6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.08),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, size: 24, color: color),
          const SizedBox(height: 2),
          Text(label, style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: color)),
        ],
      ),
    );
  }

  Widget _buildLegendItem(String label, Color color, Color textColor) {
    return Row(
      children: [
        Container(
          width: 14,
          height: 14,
          decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(4)),
        ),
        const SizedBox(width: 4),
        Text(label, style: const TextStyle(fontSize: 11, color: Colors.grey)),
      ],
    );
  }
}

RoundedCornerShape(int i) => RoundedRectangleBorder(borderRadius: BorderRadius.circular(i.toDouble()));
