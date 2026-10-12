# Keep all Kotlin extensions and data classes
-keepclassmembers class * {
    *** **(
        ...
    );
}

-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile
-keepattributes LineNumberTable
-keep enum com.squareup.okhttp3.internal.http.StatusLine { *; }
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# kotlinx-serialization
-keep class kotlinx.serialization.** { *; }
-keep interface kotlinx.serialization.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keepattributes *Annotation*

# Coil
-keep class coil.** { *; }
-keep interface coil.** { *; }
