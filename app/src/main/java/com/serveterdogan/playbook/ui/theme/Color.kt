package com.serveterdogan.playbook.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFFA5C8FF)
val OnPrimary = Color(0xFF00315F)
val PrimaryContainer = Color(0xFF2792FF)
val OnPrimaryContainer = Color(0xFF002A53)

val Secondary = Color(0xFFBDF4FF)
val OnSecondary = Color(0xFF00363D)
val SecondaryContainer = Color(0xFF00E3FD)
val OnSecondaryContainer = Color(0xFF00616D)

val Tertiary = Color(0xFFFFB688)
val OnTertiary = Color(0xFF512400)
val TertiaryContainer = Color(0xFFE37103)
val OnTertiaryContainer = Color(0xFF471E00)

val Error = Color(0xFFFFB4AB)
val OnError = Color(0xFF690005)
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

val Background = Color(0xFF10141A)
val OnBackground = Color(0xFFE0E2EC)
val Surface = Color(0xFF10141A)
val OnSurface = Color(0xFFE0E2EC)
val SurfaceVariant = Color(0xFF31353C)

val OnSurfaceVariant = Color(0xFFC0C7D6)
val SurfaceContainer = Color(0xFF1C2027)
val SurfaceContainerHigh = Color(0xFF262A31)
val Outline = Color(0xFF8A919F)
val OutlineVariant = Color(0xFF404753)
val GlassSurface = Color(0xCC151C2E) // rgba(21, 28, 46, 0.8) for glass-card

// Drawer specific colors
val DrawerBackground = Color(0xFF0B0F1A)  // Drawer arka planı
val DrawerSurface = Color(0xFF121A2A)     // Drawer menü öğeleri yüzeyi
val DrawerDivider = Color(0x33FFFFFF)     // %20 beyaz çizgi
val DrawerAccentCyan = Color(0xFF00E3FD)  // SecondaryContainer ile aynı, alias

// ─────────────────────────────────────────────────────────
// Light Theme Colors (HTML Tailwind config'den alınanlar)
// ─────────────────────────────────────────────────────────

// Primary
val LightPrimary = Color(0xFF005CAB)              // text-primary, bg-primary
val LightOnPrimary = Color(0xFFFFFFFF)            // text-on-primary
val LightPrimaryContainer = Color(0xFF0075D6)     // bg-primary-container
val LightOnPrimaryContainer = Color(0xFFFEFCFF)   // text-on-primary-container

// Secondary
val LightSecondary = Color(0xFF5B5E66)            // text-secondary
val LightOnSecondary = Color(0xFFFFFFFF)          // text-on-secondary
val LightSecondaryContainer = Color(0xFFDFE2EB)   // bg-secondary-container
val LightOnSecondaryContainer = Color(0xFF61646C) // text-on-secondary-container

// Tertiary
val LightTertiary = Color(0xFF934600)             // text-tertiary
val LightOnTertiary = Color(0xFFFFFFFF)           // text-on-tertiary
val LightTertiaryContainer = Color(0xFFB95A00)    // bg-tertiary-container
val LightOnTertiaryContainer = Color(0xFFFFFBFF)  // text-on-tertiary-container

// Error
val LightError = Color(0xFFBA1A1A)                // text-error / bg-error
val LightOnError = Color(0xFFFFFFFF)              // text-on-error
val LightErrorContainer = Color(0xFFFFDAD6)       // bg-error-container
val LightOnErrorContainer = Color(0xFF93000A)     // text-on-error-container

// Background & Surface
val LightBackground = Color(0xFFF8F9FF)           // bg-background
val LightOnBackground = Color(0xFF0B1C30)         // text-on-background
val LightSurface = Color(0xFFF8F9FF)              // bg-surface (same as background in light)
val LightOnSurface = Color(0xFF0B1C30)            // text-on-surface
val LightSurfaceVariant = Color(0xFFD3E4FE)       // bg-surface-variant
val LightOnSurfaceVariant = Color(0xFF404753)     // text-on-surface-variant

// Surface Containers
val LightSurfaceContainerLowest = Color(0xFFFFFFFF) // bg-surface-container-lowest
val LightSurfaceContainerLow = Color(0xFFEFF4FF)  // bg-surface-container-low
val LightSurfaceContainer = Color(0xFFE5EEFF)     // bg-surface-container
val LightSurfaceContainerHigh = Color(0xFFDCE9FF) // bg-surface-container-high
val LightSurfaceContainerHighest = Color(0xFFD3E4FE) // bg-surface-container-highest

// Outline
val LightOutline = Color(0xFF707785)              // text-outline / border-outline
val LightOutlineVariant = Color(0xFFC0C7D6)       // border-outline-variant