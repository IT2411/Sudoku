# Keep Compose Multiplatform internals
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep shared core models and storage
-keep class com.sudoku.core.** { *; }

# Keep Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
