package com.example.data.model

object PopularRoutesData {
    val routes = listOf(
        PopularRoute(
            id = "route_1",
            fromStation = "موقف رمسيس",
            toStation = "مدينة نصر (الحي العاشر)",
            defaultSeatFareEgp = 12.0,
            defaultWholeVanFareEgp = 80.0,
            estimatedDistanceKm = 14.5,
            estimatedTimeMin = 25,
            frequentTimes = "كل دقيقتين عربية"
        ),
        PopularRoute(
            id = "route_2",
            fromStation = "مشعل (شارع الهرم)",
            toStation = "ميدان الجيزة",
            defaultSeatFareEgp = 10.0,
            defaultWholeVanFareEgp = 70.0,
            estimatedDistanceKm = 9.0,
            estimatedTimeMin = 20,
            frequentTimes = "متاح على مدار الساعة"
        ),
        PopularRoute(
            id = "route_3",
            fromStation = "موقف السلام الدولي",
            toStation = "المرج الجديدة",
            defaultSeatFareEgp = 8.0,
            defaultWholeVanFareEgp = 55.0,
            estimatedDistanceKm = 6.2,
            estimatedTimeMin = 15,
            frequentTimes = "تحميل فوري"
        ),
        PopularRoute(
            id = "route_4",
            fromStation = "شبرا الخيمة (المؤسسة)",
            toStation = "ميدان التحرير / عبد المنعم رياض",
            defaultSeatFareEgp = 14.0,
            defaultWholeVanFareEgp = 95.0,
            estimatedDistanceKm = 16.0,
            estimatedTimeMin = 30,
            frequentTimes = "كل 3 دقائق"
        ),
        PopularRoute(
            id = "route_5",
            fromStation = "المعادي (جراند مول)",
            toStation = "محطة حلوان",
            defaultSeatFareEgp = 12.0,
            defaultWholeVanFareEgp = 85.0,
            estimatedDistanceKm = 13.0,
            estimatedTimeMin = 22,
            frequentTimes = "خط مستمر"
        ),
        PopularRoute(
            id = "route_6",
            fromStation = "التجمع الخامس (AUC)",
            toStation = "مدينة نصر (الحي السابع)",
            defaultSeatFareEgp = 20.0,
            defaultWholeVanFareEgp = 130.0,
            estimatedDistanceKm = 18.5,
            estimatedTimeMin = 28,
            frequentTimes = "مكيفة وعادية"
        ),
        PopularRoute(
            id = "route_7",
            fromStation = "ميدان الحصري (6 أكتوبر)",
            toStation = "موقف المنيب",
            defaultSeatFareEgp = 25.0,
            defaultWholeVanFareEgp = 160.0,
            estimatedDistanceKm = 28.0,
            estimatedTimeMin = 35,
            frequentTimes = "طريق دائري سريع"
        )
    )
}
