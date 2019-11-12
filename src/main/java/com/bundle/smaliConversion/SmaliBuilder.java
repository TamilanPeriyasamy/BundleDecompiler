/**
 *  Copyright (C) 2019 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2019 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.bundle.smaliConversion;

//import brut.androlib.AndrolibException;
//import brut.androlib.mod.SmaliMod;
//import brut.directory.DirectoryException;
//import brut.directory.ExtFile;

import com.bundle.utils.FileManager;
import org.antlr.runtime.RecognitionException;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SmaliBuilder {

    private final File mSmaliDir;
    private final File mDexFile;
    private int mApiLevel = 0;

    public static void build(File smaliDir, File dexFile, int apiLevel) throws Exception {
        new SmaliBuilder(smaliDir, dexFile, apiLevel).build();
    }

    public static void build(File smaliDir, File dexFile) throws Exception {
        new SmaliBuilder(smaliDir, dexFile, 0).build();
    }

    public SmaliBuilder(File smaliDir, File dexFile, int apiLevel) {
        mSmaliDir = smaliDir;
        mDexFile = dexFile;
        mApiLevel = apiLevel;
    }

    public void build() throws Exception {
        try {
            DexBuilder dexBuilder;
            if (mApiLevel > 0) {
                dexBuilder = new DexBuilder(Opcodes.forApi(mApiLevel));
            } else {
                dexBuilder = new DexBuilder(Opcodes.getDefault());
            }

            Object fileList[]= FileManager.getFileList(mSmaliDir.getAbsolutePath());
            for (int count=0;count<fileList.length;count++) {
                String fileName=fileList[count].toString();
                fileName=fileName.replace(mSmaliDir.getAbsolutePath()+File.separator,"");
                buildFile(fileName, dexBuilder);
            }
            dexBuilder.writeTo(new FileDataStore( new File(mDexFile.getAbsolutePath())));
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public void buildFile(String fileName, DexBuilder dexBuilder)
            throws Exception {
        File inFile = new File(mSmaliDir, fileName);
        InputStream inStream = new FileInputStream(inFile);

        if (fileName.endsWith(".smali")) {
            try {
                if (!SmaliMod.assembleSmaliFile(inFile, dexBuilder, mApiLevel, false, false)) {
                    throw new Exception("Could not smali file: " + fileName);
                }
            } catch (IOException | RecognitionException ex) {
                throw new Exception(ex);
            }
        } else {
            System.err.println("Unknown file type, ignoring: " + inFile);
        }
        inStream.close();
    }
}
