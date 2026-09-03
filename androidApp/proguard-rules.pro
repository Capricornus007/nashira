# Kotlin Metadata（反射序列化需要）
-keep class kotlin.Metadata { *; }
# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses, Signature
-dontnote kotlinx.serialization.**
# Trixnity（kotlinx-serialization 數據類）
-keepclassmembers class net.folivo.trixnity.** { *; }
-keep class de.connect2x.trixnity.** { *; }
# vodozemac JNI 綁定
-keep class de.connect2x.trixnity.vodozemac.** { *; }
