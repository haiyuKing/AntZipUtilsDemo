# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\software\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#=====================基于Ant的Zip压缩工具类 =====================
#android Studio环境中不需要，eclipse环境中需要
#-libraryjars libs/ant.jar
#不混淆第三方jar包中的类
-dontwarn org.apache.tools.**
-keep class org.apache.tools.**{*;}
