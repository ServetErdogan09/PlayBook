package com.serveterdogan.playbook.domain.model

/**
 * Basketboldaki 5 temel pozisyonu temsil eder.
 */
enum class PlayerRole {
    PG, // Point Guard (O1)
    SG, // Shooting Guard (O2)
    SF, // Small Forward (O3)
    PF, // Power Forward (O4)
    C   // Center (O5)
}

/**
 * Sahadaki bir oyuncunun durumunu ve kimliğini temsil eder.
 */
data class Player(
    val id: String, // Örn: "O1", "O2"
    val role: PlayerRole,
    val name: String = id // UI'da göstermek için (opsiyonel)
)

/**
 * Sahadaki X ve Y koordinatlarını tutar.
 * 0.0f ile 1.0f arasında normalize edilmiş (yüzdelik) değerler kullanmak,
 * ekran boyutu değişse bile oranların aynı kalmasını sağlar.
 */
data class Position(
    val x: Float, // 0.0f (sol) - 1.0f (sağ)
    val y: Float  // 0.0f (üst) - 1.0f (alt)
)

/**
 * Bir setin başlangıç anında oyuncuların nerede durduğunu tanımlar.
 */
data class InitialSetup(
    val playerId: String,
    val position: Position
)

/**
 * Taktik tahtasında yapılabilecek eylemler.
 */
enum class StepType {
    PASS,   // Pas
    MOVE,   // Koşu/Hareket
    SCREEN, // Perdeleme
    SHOOT   // Şut
}

/**
 * Setin içerisindeki her bir adımı (animasyon dilimini) temsil eder.
 */
data class Step(
    val id: String,
    val type: StepType,
    val primaryPlayerId: String,       // Eylemi yapan oyuncu (Örn: Pası veren, koşuyu yapan)
    val targetPlayerId: String? = null, // Eylemin hedefi olan oyuncu (Pası alan, perde yapılan)
    val targetPosition: Position? = null, // Eğer boş bir alana koşuluyorsa hedef koordinat
    val description: String            // "O1 -> O2 PAS" gibi UI'da gösterilecek metin
)

/**
 * Bir basketbol setinin tamamını temsil eden Ana Veri Sınıfı (Root Model).
 */
data class Playbook(
    val id: String,
    val name: String,                  // Örn: "Saldırı 1", "Pick & Roll"
    val players: List<Player>,         // Bu sette yer alan oyuncular
    val initialSetups: List<InitialSetup>, // Oyuncuların başlangıç pozisyonları
    val steps: List<Step>              // Zaman çizelgesindeki adımlar
)
