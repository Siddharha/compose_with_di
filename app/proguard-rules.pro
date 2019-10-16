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
-ignorewarnings
-dontwarn okio.**

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepattributes *Annotation*

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepattributes Signature
-keepattributes *Annotation*

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-dontwarn android.support.**

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class android.support.v7.widget.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

#-keep class * {
    #public private *;
#}

#AWS Proguard starts here

# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**

#AWS Proguard ends here

#Glide Proguard starts here

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Glide Proguard ends here

-keepattributes Exceptions, Signature, InnerClasses

-keep class com.app.l_pesa.common.** { *; }
-keepclassmembers class com.app.l_pesa.common.** { *; }

-keep class com.app.l_pesa.API.** { *; }
-keepclassmembers class com.app.l_pesa.API.** { *; }

-keep class com.app.l_pesa.splash.** { *; }
-keepclassmembers class com.app.l_pesa.splash.** { *; }

-keep class com.app.l_pesa.login.** { *; }
-keepclassmembers class com.app.l_pesa.login.** { *; }

-keep class com.app.l_pesa.registration.** { *; }
-keepclassmembers class com.app.l_pesa.registration.** { *; }

-keep class com.app.l_pesa.pin.** { *; }
-keepclassmembers class com.app.l_pesa.pin.** { *; }

-keep class com.app.l_pesa.lpk.** { *; }
-keepclassmembers class com.app.l_pesa.lpk.** { *; }

-keep class com.app.l_pesa.dashboard.** { *; }
-keepclassmembers class com.app.l_pesa.dashboard.** { *; }

-keep class com.app.l_pesa.loanplan.** { *; }

-keep class com.app.l_pesa.loanHistory.** { *; }
-keepclassmembers class com.app.l_pesa.loanHistory.** { *; }

-keep class com.app.l_pesa.wallet.** { *; }
-keepclassmembers class com.app.l_pesa.wallet.** { *; }

-keep class com.app.l_pesa.investment.** { *; }
-keepclassmembers class com.app.l_pesa.investment.** { *; }

-keep class com.app.l_pesa.profile.** { *; }
-keepclassmembers class com.app.l_pesa.profile.** { *; }

-keep class com.app.l_pesa.notification.** { *; }
-keepclassmembers class com.app.l_pesa.notification.** { *; }

-keep class com.app.l_pesa.pinview.** { *; }
-keepclassmembers class com.app.l_pesa.pinview.** { *; }

-keep class com.app.l_pesa.otpview.** { *; }
-keepclassmembers class com.app.l_pesa.otpview.** { *; }

-keep class com.app.l_pesa.help.** { *; }
-keepclassmembers class com.app.l_pesa.help.** { *; }

-keep class com.app.l_pesa.calculator.** { *; }
-keepclassmembers class com.app.l_pesa.calculator.** { *; }

-keep class com.app.l_pesa.logout.** { *; }
-keepclassmembers class com.app.l_pesa.logout.** { *; }

-keep class com.app.l_pesa.settings.** { *; }
-keepclassmembers class com.app.l_pesa.settings.** { *; }


-keep class com.google.android.gms.internal.** { *; }
-keepnames class com.facebook.FacebookActivity




