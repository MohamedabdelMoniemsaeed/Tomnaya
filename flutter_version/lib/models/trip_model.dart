enum RideType { seat, van }

enum TripStatus {
  searching,
  driverAssigned,
  driverArrived,
  inTransit,
  completed,
  cancelled
}

class DriverOffer {
  final String driverId;
  final String driverName;
  final String phone;
  final double rating;
  final int totalTrips;
  final String carPlate;
  final String carModel;
  final String carColor;
  final double offeredPriceEgp;
  final int etaMinutes;
  final int availableSeats;
  final bool isAirConditioned;

  DriverOffer({
    required this.driverId,
    required this.driverName,
    required this.phone,
    required this.rating,
    required this.totalTrips,
    required this.carPlate,
    required this.carModel,
    required this.carColor,
    required this.offeredPriceEgp,
    required this.etaMinutes,
    required this.availableSeats,
    required this.isAirConditioned,
  });
}

class TripBooking {
  final int id;
  final String pickupLocation;
  final String dropoffLocation;
  final String rideType;
  final int selectedSeatsCount;
  final String selectedSeatsDesc;
  final double farePriceEgp;
  final String driverName;
  final String driverPhone;
  final String carPlate;
  final String carColor;
  final String status;
  final int timestamp;
  final double? rating;

  TripBooking({
    required this.id,
    required this.pickupLocation,
    required this.dropoffLocation,
    required this.rideType,
    required this.selectedSeatsCount,
    required this.selectedSeatsDesc,
    required this.farePriceEgp,
    required this.driverName,
    required this.driverPhone,
    required this.carPlate,
    required this.carColor,
    required this.status,
    required this.timestamp,
    this.rating,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'pickupLocation': pickupLocation,
      'dropoffLocation': dropoffLocation,
      'rideType': rideType,
      'selectedSeatsCount': selectedSeatsCount,
      'selectedSeatsDesc': selectedSeatsDesc,
      'farePriceEgp': farePriceEgp,
      'driverName': driverName,
      'driverPhone': driverPhone,
      'carPlate': carPlate,
      'carColor': carColor,
      'status': status,
      'timestamp': timestamp,
      'rating': rating,
    };
  }

  factory TripBooking.fromMap(Map<String, dynamic> map) {
    return TripBooking(
      id: map['id'] ?? 0,
      pickupLocation: map['pickupLocation'] ?? '',
      dropoffLocation: map['dropoffLocation'] ?? '',
      rideType: map['rideType'] ?? 'SEAT',
      selectedSeatsCount: map['selectedSeatsCount'] ?? 1,
      selectedSeatsDesc: map['selectedSeatsDesc'] ?? '',
      farePriceEgp: (map['farePriceEgp'] as num?)?.toDouble() ?? 0.0,
      driverName: map['driverName'] ?? '',
      driverPhone: map['driverPhone'] ?? '',
      carPlate: map['carPlate'] ?? '',
      carColor: map['carColor'] ?? '',
      status: map['status'] ?? 'COMPLETED',
      timestamp: map['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      rating: (map['rating'] as num?)?.toDouble(),
    );
  }
}
