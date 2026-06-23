package com.serveterdogan.playbook.domain.model // Paket adını kendi yapına göre düzeltmeyi unutma

val mockPickAndRoll = Playbook(
    id = "play_1",
    name = "Pick & Roll (Sahte Veri)",
    players = listOf(
        Player("O1", PlayerRole.PG),
        Player("O2", PlayerRole.SG),
        Player("O3", PlayerRole.SF),
        Player("O4", PlayerRole.PF),
        Player("O5", PlayerRole.C)
    ),
    initialSetups = listOf(
        // Sahayı x (0.0 sol - 1.0 sağ) ve y (0.0 üst - 1.0 alt) olarak düşün
        InitialSetup("O1", Position(0.5f, 0.2f)), // Tepe (Top of the key)
        InitialSetup("O2", Position(0.8f, 0.4f)), // Sağ Kanat
        InitialSetup("O3", Position(0.2f, 0.4f)), // Sol Kanat
        InitialSetup("O4", Position(0.3f, 0.8f)), // Sol Post (Aşağı)
        InitialSetup("O5", Position(0.5f, 0.6f))  // High Post (Yüksek)
    ),
    steps = listOf(
        // 1. Adım: O1, O2'ye pas atar
        Step(id = "s1", type = StepType.PASS, primaryPlayerId = "O1", targetPlayerId = "O2", description = "O1 -> O2 PAS"),

        // 2. Adım: O5, O2 için perdelemeye (Screen) gider
        Step(id = "s2", type = StepType.SCREEN, primaryPlayerId = "O5", targetPlayerId = "O2", description = "O5 -> O2 PERDE"),

        // 3. Adım: O2 boşluğa doğru hareketlenir (Move)
        Step(id = "s3", type = StepType.MOVE, primaryPlayerId = "O2", targetPosition = Position(0.6f, 0.8f), description = "O2 Pota Altına DRİVE"),

        // 4. Adım: O2 şutu atar (Shoot)
        Step(id = "s4", type = StepType.SHOOT, primaryPlayerId = "O2", description = "O2 ŞUT ATIYOR")
    )
)

val mockHornsSet = Playbook(
    id = "play_2",
    name = "Spain Pick & Roll (Zor Set)",
    players = listOf(
        Player("O1", PlayerRole.PG),
        Player("O2", PlayerRole.SG),
        Player("O3", PlayerRole.SF),
        Player("O4", PlayerRole.PF),
        Player("O5", PlayerRole.C)
    ),
    initialSetups = listOf(
        InitialSetup("O1", Position(0.5f, 0.15f)), // Top (Top of key)
        InitialSetup("O2", Position(0.1f, 0.85f)), // Sol Dip (Corner)
        InitialSetup("O3", Position(0.9f, 0.85f)), // Sağ Dip (Corner)
        InitialSetup("O4", Position(0.35f, 0.4f)), // Sol Dirsek (Elbow)
        InitialSetup("O5", Position(0.65f, 0.4f))  // Sağ Dirsek (Elbow)
    ),
    steps = listOf(
        // 1. Adım: O1, sağa doğru dripling yapar
        Step(id = "hs1", type = StepType.MOVE, primaryPlayerId = "O1", targetPosition = Position(0.7f, 0.25f), description = "O1 Sağa Dripling"),
        
        // 2. Adım: O5, O1'in adamına perdeye gelir
        Step(id = "hs2", type = StepType.SCREEN, primaryPlayerId = "O5", targetPlayerId = "O1", description = "O5'ten O1'e Pick (Perde)"),
        
        // 3. Adım: O1, O5'e pas atar
        Step(id = "hs3", type = StepType.PASS, primaryPlayerId = "O1", targetPlayerId = "O5", description = "O1 -> O5 Pas (Roll)"),
        
        // 4. Adım: O5 potaya doğru devrilir
        Step(id = "hs4", type = StepType.MOVE, primaryPlayerId = "O5", targetPosition = Position(0.5f, 0.7f), description = "O5 Potaya Devriliyor (Roll)"),
        
        // 5. Adım: O4, O5'in adamına ters perde (Back-screen) yapar (Spain P&R alamet-i farikası)
        Step(id = "hs5", type = StepType.MOVE, primaryPlayerId = "O4", targetPosition = Position(0.5f, 0.5f), description = "O4'ten Ters Perde (Back-screen)"),
        
        // 6. Adım: O5, boşta kalan O4'e pas çıkartır
        Step(id = "hs6", type = StepType.PASS, primaryPlayerId = "O5", targetPlayerId = "O4", description = "O5 -> O4 Dışarı Pas"),
        
        // 7. Adım: O4, üçlük çizgisine çıkar
        Step(id = "hs7", type = StepType.MOVE, primaryPlayerId = "O4", targetPosition = Position(0.5f, 0.3f), description = "O4 Üçlüğe Çıkıyor (Pop)"),
        
        // 8. Adım: O4 şut atar
        Step(id = "hs8", type = StepType.SHOOT, primaryPlayerId = "O4", description = "O4 ÜÇLÜK ŞUT!")
    )
)
