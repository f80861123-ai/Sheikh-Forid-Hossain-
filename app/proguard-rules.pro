# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep models to prevent Moshi / JSON serialization issues due to obfuscation
-keep class com.example.data.model.** { *; }
-keep class com.example.data.local.** { *; }

# Also keep classes used with reflection or retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod

# Keep Retrofit and Moshi rules
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okio.**
-dontwarn javax.annotation.**
