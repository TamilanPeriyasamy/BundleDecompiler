# BundleDecompiler
    BundleDecompiler is a tool for reverse engineering Android .aab files.
    This tool decode .aab resources(.xml's) and sources(.dex's).
 
### USAGE: ApkDecompiler < command > [options]
    BundleDecompiler decompile or d
    BundleDecompiler build or b
    BundleDecompiler sign-bundle
    BundleDecompiler sign-universal

### COMMANDS
    decompile         Decompile the provided .aab resources
    --in=             Input app bundle(.aab) file
    --out=            Output decompile Directory
    
     build            build the provided decompiled resources
    --in=             Input decompiled Directory
    --out=            Output app bundle(.aab)  file
    
     sign-bundle      sign-bundle app bundle
    --in=             Input app bundle(.aab)
    --out=            Signed app bundle(.aab) file
    
     sign-universal   universal for sign-bundle app bundle
    --in=             Input app bundle(.aab)
    --out=            Signed universal apk(.apks) file
    
### EXAMPLE:
    java -jar BundleDecompiler.jar decompile --in=input_app.aab --out=output_dir
    java -jar BundleDecompiler.jar build --in=input_dir --out=output_app.aab 
    java -jar BundleDecompiler.jar sign-bundle --in=input_app.aab --out=output_app.aab 
    java -jar BundleDecompiler.jar sign-universal --in=input_app.aab --out=output_app.apks

### DOWNLOAD JAR 
  [BundleDecompiler.jar](https://github.com/TamilanPeriyasamy/BundleDecompiler/raw/master/out/BundleDecompiler.jar)  
