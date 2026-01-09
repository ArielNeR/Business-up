# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room entities
-keep class com.businessup.data.model.** { *; }

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep iText classes
-keep class com.itextpdf.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
