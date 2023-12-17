# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keepclassmembers class com.longtailvideo.jwplayer.** {
   @android.webkit.JavascriptInterface *;
}

# Block warnings about missing module classes
-dontwarn com.longtailvideo.jwplayer.**
-dontwarn com.google.ads.interactivemedia.**

# Classes get rejected without this when running the app if the app has been run through ProGuard
-keepattributes InnerClasses,EnclosingMethod

# Keep module indicator classes
-keep class com.longtailvideo.jwplayer.modules.** { *; }

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keep class com.google.common.** { *; }
-dontwarn com.google.common.**

-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**