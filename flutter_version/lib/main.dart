import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:google_fonts/google_fonts.dart';
import 'models/trip_model.dart';
import 'widgets/seat_selector_widget.dart';
import 'widgets/driver_dashboard.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  try {
    await Firebase.initializeApp();
  } catch (error) {
    debugPrint('Firebase is not configured yet: $error');
  }

  runApp(const TomnayaApp());
}

class TomnayaApp extends StatelessWidget {
  const TomnayaApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'تَمْنَايَة - فان 7 راكب',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        primaryColor: const Color(0xFF0F172A),
        scaffoldBackgroundColor: const Color(0xFFF8FAFC),
        textTheme: GoogleFonts.cairoTextTheme(Theme.of(context).textTheme),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF0F172A),
          primary: const Color(0xFF0F172A),
          secondary: const Color(0xFFF59E0B),
        ),
      ),
      locale: const Locale('ar', 'EG'),
      supportedLocales: const [
        Locale('ar', 'EG'),
        Locale('en', 'US'),
      ],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentTab = 0;
  bool _isDriverMode = false;

  // Passenger Booking State
  String _pickup = 'ميدان الجيزة - محطة المترو';
  String _dropoff = 'حدائق الأهرام - البوابة الأولى';
  RideType _rideType = RideType.seat;
  final Set<int> _selectedSeats = {1};
  double _proposedFare = 15.0;
  bool _isSearching = false;
  DriverOffer? _acceptedOffer;

  final List<TripBooking> _tripHistory = [
    TripBooking(
      id: 1,
      pickupLocation: 'ميدان الرماية - الهرم',
      dropoffLocation: 'ميدان الحصري - 6 أكتوبر',
      rideType: 'SEAT',
      selectedSeatsCount: 2,
      selectedSeatsDesc: 'كرسي قدام، يمين وسط',
      farePriceEgp: 30.0,
      driverName: 'كابتن صابر الجدع',
      driverPhone: '01023456789',
      carPlate: 'ط ر ق ٥٨٢١',
      carColor: 'أبيض لؤلؤي',
      status: 'COMPLETED',
      timestamp: DateTime.now().subtract(const Duration(hours: 3)).millisecondsSinceEpoch,
      rating: 5.0,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F172A),
        elevation: 0,
        title: Row(
          children: [
            Container(
              padding: const EdgeInsets.all(6),
              decoration: BoxDecoration(
                color: const Color(0xFFF59E0B),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(Icons.airport_shuttle, color: Colors.white, size: 20),
            ),
            const SizedBox(width: 8),
            const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('تَمْنَايَة', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18, color: Colors.white)),
                Text('سوزوكي فان 7 راكب', style: TextStyle(fontSize: 10, color: Color(0xFFF59E0B))),
              ],
            ),
          ],
        ),
        actions: [
          // Mode Toggle
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: ActionChip(
              avatar: Icon(
                _isDriverMode ? Icons.directions_car : Icons.person,
                size: 16,
                color: const Color(0xFFF59E0B),
              ),
              label: Text(
                _isDriverMode ? 'وضع الكابتن' : 'وضع الراكب',
                style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.white),
              ),
              backgroundColor: const Color(0xFF1E293B),
              onPressed: () {
                setState(() {
                  _isDriverMode = !_isDriverMode;
                });
              },
            ),
          ),
        ],
      ),
      body: _isDriverMode
          ? DriverDashboardWidget(onSwitchToPassenger: () => setState(() => _isDriverMode = false))
          : _buildPassengerBody(),
      bottomNavigationBar: _isDriverMode
          ? null
          : NavigationBar(
              selectedIndex: _currentTab,
              onDestinationSelected: (idx) => setState(() => _currentTab = idx),
              destinations: const [
                NavigationDestination(icon: Icon(Icons.directions_bus), label: 'حجز مشوار'),
                NavigationDestination(icon: Icon(Icons.history), label: 'مشاويري'),
                NavigationDestination(icon: Icon(Icons.alt_route), label: 'الخطوط والمواقف'),
              ],
            ),
    );
  }

  Widget _buildPassengerBody() {
    if (_currentTab == 1) {
      return _buildHistoryTab();
    } else if (_currentTab == 2) {
      return _buildRoutesTab();
    }
    return _buildBookingTab();
  }

  Widget _buildBookingTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // Pickup and Dropoff Card
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  TextField(
                    controller: TextEditingController(text: _pickup),
                    decoration: const InputDecoration(
                      prefixIcon: Icon(Icons.my_location, color: Color(0xFF0D9488)),
                      labelText: 'نقطة الركوب (الموقف أو الشارع)',
                      border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(12))),
                    ),
                    onChanged: (val) => _pickup = val,
                  ),
                  const SizedBox(height: 12),
                  TextField(
                    controller: TextEditingController(text: _dropoff),
                    decoration: const InputDecoration(
                      prefixIcon: Icon(Icons.location_on, color: Color(0xFFEF4444)),
                      labelText: 'نقطة الوصول',
                      border: OutlineInputBorder(borderRadius: BorderRadius.all(Radius.circular(12))),
                    ),
                    onChanged: (val) => _dropoff = val,
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 14),

          // Ride Type Selector
          SegmentedButton<RideType>(
            segments: const [
              ButtonSegment(value: RideType.seat, label: Text('حجز كراسي فردية (بالنفر)'), icon: Icon(Icons.person)),
              ButtonSegment(value: RideType.van, label: Text('العربية كلها مخصوص (7 راكب)'), icon: Icon(Icons.airport_shuttle)),
            ],
            selected: {_rideType},
            onSelectionChanged: (set) {
              setState(() {
                _rideType = set.first;
                _proposedFare = _rideType == RideType.seat ? 15.0 : 85.0;
              });
            },
          ),
          const SizedBox(height: 14),

          // Seat Selector if Individual Seat
          if (_rideType == RideType.seat)
            SeatSelectorWidget(
              selectedSeats: _selectedSeats,
              onSeatToggled: (index) {
                setState(() {
                  if (_selectedSeats.contains(index)) {
                    if (_selectedSeats.length > 1) _selectedSeats.remove(index);
                  } else {
                    _selectedSeats.add(index);
                  }
                  _proposedFare = _selectedSeats.length * 15.0;
                });
              },
            ),
          const SizedBox(height: 14),

          // Fare Bidding (inDrive style)
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                children: [
                  const Text('عرض الأجرة المقترحة للكباتن (نظام التفاوض)', style: TextStyle(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      IconButton(
                        onPressed: _proposedFare > 5 ? () => setState(() => _proposedFare -= 2) : null,
                        icon: const Icon(Icons.remove_circle, size: 32, color: Color(0xFF0F172A)),
                      ),
                      const SizedBox(width: 12),
                      Text(
                        '${_proposedFare.toStringAsFixed(0)} ج.م',
                        style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: Color(0xFF10B981)),
                      ),
                      const SizedBox(width: 12),
                      IconButton(
                        onPressed: () => setState(() => _proposedFare += 2),
                        icon: const Icon(Icons.add_circle, size: 32, color: Color(0xFF0F172A)),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Search Suzuki Drivers Button
          ElevatedButton.icon(
            onPressed: () {
              setState(() => _isSearching = true);
              Future.delayed(const Duration(seconds: 2), () {
                if (mounted) {
                  setState(() {
                    _isSearching = false;
                    _acceptedOffer = DriverOffer(
                      driverId: 'drv_1',
                      driverName: 'كابتن محروس السوزوكي',
                      phone: '01198765432',
                      rating: 4.8,
                      totalTrips: 1420,
                      carPlate: 'س و ز ٧٧٤٢',
                      carModel: 'سوزوكي فان 7 راكب',
                      carColor: 'أبيض لؤلؤي',
                      offeredPriceEgp: _proposedFare,
                      etaMinutes: 4,
                      availableSeats: 3,
                      isAirConditioned: true,
                    );
                  });
                  _showDriverDialog();
                }
              });
            },
            icon: _isSearching
                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                : const Icon(Icons.search),
            label: Text(_isSearching ? 'جاري إرسال طلبك لكباتن السوزوكي...' : 'طلب تمناية بالسعر ده'),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF0F172A),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 16),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            ),
          ),
        ],
      ),
    );
  }

  void _showDriverDialog() {
    if (_acceptedOffer == null) return;
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (ctx) => Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('كابتن تمناية قبل عرضك! 🚐🎉', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            ListTile(
              leading: const CircleAvatar(
                backgroundColor: Color(0xFF0F172A),
                child: Icon(Icons.person, color: Colors.white),
              ),
              title: Text(_acceptedOffer!.driverName, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${_acceptedOffer!.carPlate} • ${_acceptedOffer!.carColor}'),
              trailing: Text(
                '${_acceptedOffer!.offeredPriceEgp.toStringAsFixed(0)} ج.م',
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF10B981)),
              ),
            ),
            const SizedBox(height: 8),

            // Quick Actions: Voice Alert & WhatsApp Safety
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('📢 تنبيه صوتي: انتبه! التمناية قربت من الموقف، استعد للركوب!')),
                      );
                    },
                    icon: const Icon(Icons.volume_up, size: 16, color: Color(0xFFF59E0B)),
                    label: const Text('تنبيه صوتي 📢', style: TextStyle(fontSize: 11)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF0F172A),
                      foregroundColor: Colors.white,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('🛡️ تم تجهيز رسالة أمان المشوار للمشاركة على واتساب!')),
                      );
                    },
                    icon: const Icon(Icons.share, size: 16, color: Colors.white),
                    label: const Text('أمان واتساب 🛡️', style: TextStyle(fontSize: 11)),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF25D366),
                      foregroundColor: Colors.white,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),

            // Vodafone Cash & InstaPay
            OutlinedButton.icon(
              onPressed: () {
                showDialog(
                  context: context,
                  builder: (dCtx) => AlertDialog(
                    title: const Text('دفع الأجرة (فودافون كاش / InstaPay)'),
                    content: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('المبلغ: ${_acceptedOffer!.offeredPriceEgp.toStringAsFixed(0)} ج.م',
                            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF10B981))),
                        const SizedBox(height: 8),
                        Text('رقم كابتن السوزوكي: ${_acceptedOffer!.phone}'),
                        const SizedBox(height: 4),
                        Text('معرف InstaPay: ${_acceptedOffer!.phone}@instapay'),
                      ],
                    ),
                    actions: [
                      TextButton(onPressed: () => Navigator.pop(dCtx), child: const Text('إغلاق')),
                    ],
                  ),
                );
              },
              icon: const Icon(Icons.payment, color: Color(0xFFE60000), size: 18),
              label: const Text('📲 الدفع السريع بـ (فودافون كاش و InstaPay)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              style: OutlinedButton.styleFrom(minimumSize: const Size.fromHeight(40)),
            ),
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(ctx);
                setState(() {
                  _tripHistory.insert(
                    0,
                    TripBooking(
                      id: DateTime.now().millisecondsSinceEpoch,
                      pickupLocation: _pickup,
                      dropoffLocation: _dropoff,
                      rideType: _rideType == RideType.seat ? 'SEAT' : 'FULL_VAN',
                      selectedSeatsCount: _selectedSeats.length,
                      selectedSeatsDesc: '${_selectedSeats.length} مقاعد',
                      farePriceEgp: _acceptedOffer!.offeredPriceEgp,
                      driverName: _acceptedOffer!.driverName,
                      driverPhone: _acceptedOffer!.phone,
                      carPlate: _acceptedOffer!.carPlate,
                      carColor: _acceptedOffer!.carColor,
                      status: 'COMPLETED',
                      timestamp: DateTime.now().millisecondsSinceEpoch,
                    ),
                  );
                  _currentTab = 1;
                });
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0F172A),
                foregroundColor: Colors.white,
                minimumSize: const Size.fromHeight(48),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: const Text('تأكيد الركوب وركوب التمناية'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHistoryTab() {
    if (_tripHistory.isEmpty) {
      return const Center(child: Text('لا توجد رحلات سابقة'));
    }
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _tripHistory.length,
      itemBuilder: (ctx, i) {
        final trip = _tripHistory[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(trip.driverName, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    Text('${trip.farePriceEgp.toStringAsFixed(0)} ج.م',
                        style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF10B981), fontSize: 16)),
                  ],
                ),
                const SizedBox(height: 6),
                Text('من: ${trip.pickupLocation}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                Text('إلى: ${trip.dropoffLocation}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                const Divider(height: 16),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(trip.carPlate, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                    const Text('محفوظ سحابياً ☁️✓', style: TextStyle(fontSize: 11, color: Color(0xFF10B981))),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildRoutesTab() {
    final routes = [
      {'name': 'موقف الجيزة ⇄ الهرم ⇄ حدائق الأهرام', 'price': '10 - 15 ج.م', 'seats': '7 راكب'},
      {'name': 'موقف مشعل ⇄ الرماية ⇄ الحصري 6 أكتوبر', 'price': '15 - 20 ج.م', 'seats': '7 راكب'},
      {'name': 'موقف المرج الجديدة ⇄ الخانكة ⇄ السلام', 'price': '10 - 15 ج.م', 'seats': '7 راكب'},
      {'name': 'موقف شبرا الخيمة ⇄ بهتيم ⇄ مسطرد', 'price': '8 - 12 ج.م', 'seats': '7 راكب'},
    ];
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: routes.length,
      itemBuilder: (ctx, i) {
        final r = routes[i];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
          child: ListTile(
            leading: const Icon(Icons.alt_route, color: Color(0xFF0F172A)),
            title: Text(r['name']!, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
            subtitle: Text('الأجرة العادية: ${r['price']}', style: const TextStyle(color: Color(0xFF10B981))),
            trailing: Chip(label: Text(r['seats']!, style: const TextStyle(fontSize: 10))),
          ),
        );
      },
    );
  }
}
