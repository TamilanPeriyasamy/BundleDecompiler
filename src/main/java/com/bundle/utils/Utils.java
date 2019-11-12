/**
 * Copyright 2013 - 2014 Lakeba Corporation
 *
 * Licensed under the Lakeba License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *     http://www.lakeba.com/licenses/LICENSE-1.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author : Ragupathy 
 * date : Oct 30, 2014
 * Project :ApkCodeInjectionTool
 * Package : com.lakeba.licensing.utils
 */
package com.bundle.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Ragupathy
 *
 */
public class Utils {

	public static String getFileNameFromPath(String filepath){
		if(filepath == null){
			return "";
		}
		File file = new File(filepath);
		return file.getName();
	}
	
	public static String getFilenameWithoutExtension(String path){
		if(path == null){
			return "";
		}
		File file = new File(path);
		String filename = file.getName();
		String filenameWithOutExtension = filename.replaceFirst("[.][^.]+$", "");
		return filenameWithOutExtension;
	}



}
