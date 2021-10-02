-verbose

# dontpreverify should only be used when targeting Java 6 or below.

-target 1.4
-dontpreverify

# Make sure to check everything always, can't hurt.

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing

# Adapt any changed strings

#-adaptclassstrings
#-adaptresourcefilenames
#-adaptresourcefilecontents
-adaptresourcefilecontents **.MF

# Preserve main entrypoint, but remove class name.

-keepclasseswithmembers,allowoptimization,allowobfuscation public class com.github.nerdthened.mctexturegen.MCTextureGenerator {
    public static void main(java.lang.String[]);
}

-keepclassmembers public class com.github.nerdthened.mctexturegen.MCTextureGenerator {
    public static void main(java.lang.String[]);
    #public static boolean hasDebugInfo;
}

# Strip debug code

-assumevalues public class com.github.nerdthened.mctexturegen.MCTextureGenerator {
    boolean hasDebugInfo = false;
}

-assumenosideeffects class java.lang.Class {
    public boolean desiredAssertionStatus();
}

# TODO Replace the above with something like this

#-assumevalues class java.lang.Class {
#    public boolean desiredAssertionStatus return false;
#}

# Remove any traces of debug code

-assumenoexternalsideeffects class java.lang.StringBuilder {
    public java.lang.StringBuilder();
    public java.lang.StringBuilder(int);
    public java.lang.StringBuilder(java.lang.String);
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
    public java.lang.String toString();
}

-assumenoexternalreturnvalues public final class java.lang.StringBuilder {
    public java.lang.StringBuilder append(java.lang.Object);
    public java.lang.StringBuilder append(java.lang.String);
    public java.lang.StringBuilder append(java.lang.StringBuffer);
    public java.lang.StringBuilder append(char[]);
    public java.lang.StringBuilder append(char[], int, int);
    public java.lang.StringBuilder append(boolean);
    public java.lang.StringBuilder append(char);
    public java.lang.StringBuilder append(int);
    public java.lang.StringBuilder append(long);
    public java.lang.StringBuilder append(float);
    public java.lang.StringBuilder append(double);
}

# Debug ProGuard

-printusage
-whyareyoukeeping class com.github.nerdthened.mctexturegen.**

# Bonus optimisations

-optimizationpasses 64
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses
