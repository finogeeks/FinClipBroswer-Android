
#Specifying the compression level
-optimizationpasses 5

# No case mixing is used; the resulting class name is lowercase
-dontusemixedcaseclassnames

#Non-public library class members are not skipped
-dontskipnonpubliclibraryclassmembers

#Algorithm to use when obfuscating
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#The method names in the obfuscated class are also obfuscated
-useuniqueclassmembernames

#Modifier classes and class members are allowed to be accessed and modified during optimization
-allowaccessmodification

# With the verbose sentence, a mapping file is generated after obfuscation
# Contains the class name ->; Mapping of obfuscated class names
# You then use printmapping to specify the name of the mapping file
-verbose
-printmapping priguardMapping.txt

#Rename the file source to the "SourceFile" string
-renamesourcefileattribute SourceFile
#Preserving line numbers
-keepattributes SourceFile,LineNumberTable

-keep class org.bouncycastle.** {*;}

# ijkplayer
-keep class tv.danmaku.ijk.media.player.** {*;}
-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer{*;}
-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi{*;}

# J2V8
-keep class com.eclipsesource.v8.** {*;}

# encent Cloud one-click login
-keep class cn.com.chinatelecom.account.**{*;}
-keep class com.sdk.** { *;}
-keep class com.cmic.sso.sdk.**{*;}
-keep class com.rich.oauth.**{*;}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# tbs
-keep class com.tencent.smtt.** {*;}
-keep class com.tencent.tbs.** {*;}