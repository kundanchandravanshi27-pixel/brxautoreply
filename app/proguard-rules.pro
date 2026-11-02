# Add project specific ProGuard rules here.

-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepattributes *Sealed*, *InnerClasses*
-dontwarn kotlinx.serialization.internal.**
-keep class com.example.data.ai.** { *; }
-keep class com.example.data.room.** { *; }
