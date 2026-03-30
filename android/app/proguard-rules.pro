# Keep line numbers for meaningful stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep the IME service — Android resolves it by class name from the manifest
-keep class com.vatoo.erick.MyInputMethodService { *; }

# Keep KMP shared module public API (accessed from Android + iOS via reflection/name)
-keep class com.vatoo.erick.shared.** { *; }

# Keep Kotlin metadata needed by KMP and serialization
-keepattributes *Annotation*, InnerClasses
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Jetpack Compose — R8 full mode compatibility
-dontwarn androidx.compose.**

# DataStore
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }
