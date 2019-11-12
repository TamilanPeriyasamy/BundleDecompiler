# ApkDecompiler
    ApkDecompiler is a tool for reverse engineering Android APK files.
    This tool decode apk resources and rebuild them after making some modifications.
 
### USAGE: ApkDecompiler < command > [options]
    ApkDecompiler decompile
    ApkDecompiler build
  
### EXAMPLE:
    java -jar ApkDecompiler.jar decompile    -in app.apk   -out app-dir
    java -jar ApkDecompiler.jar build        -in app-dir   -out app.apk 
    java -jar ApkDecompiler.jar build -sign  -in app-dir   -out app.apk 

### COMMANDS
    decompile   Decompile the provided APK resources
    build       Build decompiled APK resources files
    -in         Input file or Input Directory
    -out        Output file or Output Directory
    -sign       Sign the provided APK (use a test certificate)

    
### DOWNLOAD JAR 
  [ApkDecompiler.jar](https://github.com/TamilanPeriyasamy/ApkDecompiler/raw/master/out/ApkDecompiler.jar)  
