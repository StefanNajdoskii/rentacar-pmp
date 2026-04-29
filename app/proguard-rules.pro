# Add project specific ProGuard rules here.
-keep class com.rentacar.model.** { *; }
-keep class com.rentacar.data.local.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.firebase.**
