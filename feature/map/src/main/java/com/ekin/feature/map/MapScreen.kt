package com.ekin.feature.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RegionData(
    val name: String,
    val plants: List<String>,
    val endemicPlants: List<String>,
    val climate: String,
    val soilType: String
)

@Composable
fun MapScreen() {
    var selectedContinent by remember { mutableStateOf<String?>(null) }
    var selectedCountry by remember { mutableStateOf<String?>(null) }
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedDistrict by remember { mutableStateOf<String?>(null) }
    var showContinentMenu by remember { mutableStateOf(false) }
    var showCountryMenu by remember { mutableStateOf(false) }
    var showCityMenu by remember { mutableStateOf(false) }
    var showDistrictMenu by remember { mutableStateOf(false) }
    var regionData by remember { mutableStateOf<RegionData?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Örnek veriler
    val continents = listOf("Asya", "Avrupa", "Afrika", "Kuzey Amerika", "Güney Amerika", "Okyanusya", "Antarktika")
    val countries = listOf("Türkiye", "Yunanistan", "İtalya", "Fransa", "İspanya")
    val cities = listOf("İstanbul", "Ankara", "İzmir", "Antalya", "Bursa")
    val districts = listOf("Kadıköy", "Beşiktaş", "Şişli", "Beyoğlu", "Üsküdar")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Harita görseli
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1B5E20),
                                Color(0xFF2E7D32),
                                Color(0xFF4CAF50)
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Basit dünya haritası çizimi
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 3

                    // Dünya çemberi
                    drawCircle(
                        color = Color(0xFF0D47A1),
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )

                    // Kıtalar (basitleştirilmiş)
                    // Avrupa
                    val europePath = Path().apply {
                        moveTo(centerX * 0.9f, centerY * 0.7f)
                        lineTo(centerX * 1.1f, centerY * 0.65f)
                        lineTo(centerX * 1.15f, centerY * 0.85f)
                        lineTo(centerX * 0.95f, centerY * 0.9f)
                        close()
                    }
                    drawPath(europePath, Color(0xFF4CAF50))

                    // Asya
                    val asiaPath = Path().apply {
                        moveTo(centerX * 1.2f, centerY * 0.5f)
                        lineTo(centerX * 1.5f, centerY * 0.55f)
                        lineTo(centerX * 1.55f, centerY * 0.85f)
                        lineTo(centerX * 1.25f, centerY * 0.9f)
                        close()
                    }
                    drawPath(asiaPath, Color(0xFF66BB6A))

                    // Afrika
                    val africaPath = Path().apply {
                        moveTo(centerX * 0.95f, centerY * 1.1f)
                        lineTo(centerX * 1.05f, centerY * 1.15f)
                        lineTo(centerX * 1.1f, centerY * 1.4f)
                        lineTo(centerX * 0.9f, centerY * 1.35f)
                        close()
                    }
                    drawPath(africaPath, Color(0xFF81C784))

                    // Kuzey Amerika
                    val naPath = Path().apply {
                        moveTo(centerX * 0.3f, centerY * 0.4f)
                        lineTo(centerX * 0.5f, centerY * 0.45f)
                        lineTo(centerX * 0.55f, centerY * 0.7f)
                        lineTo(centerX * 0.35f, centerY * 0.75f)
                        close()
                    }
                    drawPath(naPath, Color(0xFFA5D6A7))

                    // Güney Amerika
                    val saPath = Path().apply {
                        moveTo(centerX * 0.4f, centerY * 1.0f)
                        lineTo(centerX * 0.5f, centerY * 1.05f)
                        lineTo(centerX * 0.52f, centerY * 1.3f)
                        lineTo(centerX * 0.38f, centerY * 1.28f)
                        close()
                    }
                    drawPath(saPath, Color(0xFFC8E6C9))

                    // Kıta sınırları
                    drawPath(europePath, Color(0xFF1B5E20), style = Stroke(width = 2f))
                    drawPath(asiaPath, Color(0xFF1B5E20), style = Stroke(width = 2f))
                    drawPath(africaPath, Color(0xFF1B5E20), style = Stroke(width = 2f))
                    drawPath(naPath, Color(0xFF1B5E20), style = Stroke(width = 2f))
                    drawPath(saPath, Color(0xFF1B5E20), style = Stroke(width = 2f))
                }

                // Harita üzerinde bilgi
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.width(48.dp).height(48.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Dünya Haritası",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bölge seçerek bitki bilgilerini keşfedin",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Arama çubuğu
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Bölge ara...") },
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Bölge seçim kontrolleri
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Bölge Seçimi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Kıta seçimi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kıta:", modifier = Modifier.width(80.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { showContinentMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedContinent ?: "Kıta Seçin", modifier = Modifier.weight(1f))
                        }
                        DropdownMenu(
                            expanded = showContinentMenu,
                            onDismissRequest = { showContinentMenu = false }
                        ) {
                            continents.forEach { continent ->
                                DropdownMenuItem(
                                    text = { Text(continent) },
                                    onClick = {
                                        selectedContinent = continent
                                        selectedCountry = null
                                        selectedCity = null
                                        selectedDistrict = null
                                        showContinentMenu = false
                                        regionData = getSampleRegionData(continent, null, null, null)
                                    }
                                )
                            }
                        }
                    }
                }

                // Ülke seçimi
                if (selectedContinent != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ülke:", modifier = Modifier.width(80.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            TextButton(
                                onClick = { showCountryMenu = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedCountry ?: "Ülke Seçin")
                            }
                            DropdownMenu(
                                expanded = showCountryMenu,
                                onDismissRequest = { showCountryMenu = false }
                            ) {
                                countries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text(country) },
                                        onClick = {
                                            selectedCountry = country
                                            selectedCity = null
                                            selectedDistrict = null
                                            showCountryMenu = false
                                            regionData = getSampleRegionData(selectedContinent, country, null, null)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // İl seçimi
                if (selectedCountry != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("İl:", modifier = Modifier.width(80.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            TextButton(
                                onClick = { showCityMenu = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedCity ?: "İl Seçin")
                            }
                            DropdownMenu(
                                expanded = showCityMenu,
                                onDismissRequest = { showCityMenu = false }
                            ) {
                                cities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            selectedCity = city
                                            selectedDistrict = null
                                            showCityMenu = false
                                            regionData = getSampleRegionData(selectedContinent, selectedCountry, city, null)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // İlçe seçimi
                if (selectedCity != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("İlçe:", modifier = Modifier.width(80.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            TextButton(
                                onClick = { showDistrictMenu = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedDistrict ?: "İlçe Seçin")
                            }
                            DropdownMenu(
                                expanded = showDistrictMenu,
                                onDismissRequest = { showDistrictMenu = false }
                            ) {
                                districts.forEach { district ->
                                    DropdownMenuItem(
                                        text = { Text(district) },
                                        onClick = {
                                            selectedDistrict = district
                                            showDistrictMenu = false
                                            regionData = getSampleRegionData(selectedContinent, selectedCountry, selectedCity, district)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bölge bilgileri
        regionData?.let { data ->
            item {
                Text(
                    text = "Bölge Bilgileri: ${data.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🌱 Yetişen Bitkiler",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        data.plants.forEach { plant ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(plant)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⭐ Endemik Bitkiler",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        if (data.endemicPlants.isNotEmpty()) {
                            data.endemicPlants.forEach { plant ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(plant, fontWeight = FontWeight.Medium)
                                }
                            }
                        } else {
                            Text("Bu bölgede endemik bitki bulunmamaktadır.")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🌡️ İklim",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(data.climate)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🌍 Toprak Yapısı",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(data.soilType)
                    }
                }
            }
        }
    }
}

fun getSampleRegionData(
    continent: String?,
    country: String?,
    city: String?,
    district: String?
): RegionData {
    val regionName = listOfNotNull(district, city, country, continent).joinToString(", ")
    
    return when {
        district != null -> RegionData(
            name = regionName,
            plants = listOf("Domates", "Biber", "Patlıcan", "Salatalık", "Marul", "Soğan", "Sarımsak"),
            endemicPlants = listOf("Türkiye Endemik Domatesi", "İstanbul Endemik Biberi"),
            climate = "Akdeniz iklimi - Sıcak ve kurak yazlar (25-35°C), ılık ve yağışlı kışlar (8-15°C). Yıllık yağış: 600-800mm",
            soilType = "Kumlu-tınlı toprak, iyi drenajlı, pH: 6.5-7.5, organik madde: %2-4"
        )
        city != null -> RegionData(
            name = regionName,
            plants = listOf("Zeytin", "İncir", "Üzüm", "Nar", "Turunçgiller", "Badem", "Antep fıstığı"),
            endemicPlants = listOf("Türkiye Endemik Zeytini", "Akdeniz Endemik İnciri"),
            climate = "Akdeniz iklimi - Ortalama sıcaklık: 18-25°C. Yazlar sıcak ve kurak, kışlar ılık ve yağışlı",
            soilType = "Kalkerli toprak, organik madde bakımından zengin, pH: 7.0-8.5"
        )
        country != null -> RegionData(
            name = regionName,
            plants = listOf("Buğday", "Arpa", "Mısır", "Ayçiçeği", "Pamuk", "Şeker pancarı", "Tütün", "Çay"),
            endemicPlants = listOf("Anadolu Gladyolü", "Türkiye Endemik Buğdayı", "Kapadokya Endemik Lalesi"),
            climate = "Karadeniz, Akdeniz, Karasal ve Marmara iklimleri görülür. Çeşitli mikro iklimler mevcuttur",
            soilType = "Çeşitli toprak tipleri: Kırmızı topraklar, kahverengi orman toprakları, alüvyal topraklar, kalkerli topraklar"
        )
        else -> RegionData(
            name = regionName,
            plants = listOf("Geniş bitki çeşitliliği", "Tahıllar", "Meyve ağaçları", "Sebzeler", "Endüstri bitkileri"),
            endemicPlants = listOf("Bölgeye özgü endemik türler", "Nadir bitki türleri"),
            climate = "Çeşitli iklim tipleri - Farklı bölgelerde farklı iklim özellikleri görülür",
            soilType = "Farklı toprak yapıları - Bölgeye göre değişkenlik gösterir"
        )
    }
}
