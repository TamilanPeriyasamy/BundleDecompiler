/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bundle.conversion;

public class TypedValue {
	
    public static final int TYPE_NULL = 0x00;

    public static final int TYPE_REFERENCE = 0x01;
   
    public static final int TYPE_ATTRIBUTE = 0x02;
   
    public static final int TYPE_STRING = 0x03;
   
    public static final int TYPE_FLOAT = 0x04;
    
    public static final int TYPE_DIMENSION = 0x05;
   
    public static final int TYPE_FRACTION = 0x06;

    public static final int TYPE_FIRST_INT = 0x10;

    public static final int TYPE_INT_DEC = 0x10;
  
    public static final int TYPE_INT_HEX = 0x11;
   
    public static final int TYPE_INT_BOOLEAN = 0x12;

    public static final int TYPE_FIRST_COLOR_INT = 0x1c;

    public static final int TYPE_INT_COLOR_ARGB8 = 0x1c;
  
    public static final int TYPE_INT_COLOR_RGB8 = 0x1d;
  
    public static final int TYPE_INT_COLOR_ARGB4 = 0x1e;
  
    public static final int TYPE_INT_COLOR_RGB4 = 0x1f;

    public static final int TYPE_LAST_COLOR_INT = 0x1f;

    public static final int TYPE_LAST_INT = 0x1f;
    

    public static final int COMPLEX_UNIT_SHIFT = 0;
   
    public static final int COMPLEX_UNIT_MASK = 0xf;

    public static final int COMPLEX_UNIT_PX = 0;
  
    public static final int COMPLEX_UNIT_DIP = 1;
  
    public static final int COMPLEX_UNIT_SP = 2;
   
    public static final int COMPLEX_UNIT_PT = 3;
   
    public static final int COMPLEX_UNIT_IN = 4;
  
    public static final int COMPLEX_UNIT_MM = 5;

    public static final int COMPLEX_UNIT_FRACTION = 0;

    public static final int COMPLEX_UNIT_FRACTION_PARENT = 1;

    public static final int COMPLEX_RADIX_SHIFT = 4;
   
    public static final int COMPLEX_RADIX_MASK = 0x3;

    public static final int COMPLEX_RADIX_23p0 = 0;
   
    public static final int COMPLEX_RADIX_16p7 = 1;
   
    public static final int COMPLEX_RADIX_8p15 = 2;
   
    public static final int COMPLEX_RADIX_0p23 = 3;

    public static final int COMPLEX_MANTISSA_SHIFT = 8;
  
    public static final int COMPLEX_MANTISSA_MASK = 0xffffff;

    public static final int DENSITY_DEFAULT = 0;

    public static final int DENSITY_NONE = 0xffff;

    public int type;
    public CharSequence string;
    public int data;
    public int assetCookie;
    public int resourceId;
    public int changingConfigurations = -1;
    public int density;
    
    public final float getFloat() {
        return Float.intBitsToFloat(data);
    }

    private static final float MANTISSA_MULT = 1.0f / (1 << TypedValue.COMPLEX_MANTISSA_SHIFT);
    private static final float[] RADIX_MULTS = new float[] {
            1.0f * MANTISSA_MULT,
            1.0f / (1 << 7) * MANTISSA_MULT,
            1.0f / (1 << 15) * MANTISSA_MULT,
            1.0f / (1 << 23) * MANTISSA_MULT
    };

    public static float complexToFloat(int complex) {
        return (complex & (TypedValue.COMPLEX_MANTISSA_MASK << TypedValue.COMPLEX_MANTISSA_SHIFT)) * RADIX_MULTS[(complex >> TypedValue.COMPLEX_RADIX_SHIFT) & TypedValue.COMPLEX_RADIX_MASK];
    }

    public static float complexToFraction(int data, float base, float pbase) {
        switch ((data >> COMPLEX_UNIT_SHIFT) & COMPLEX_UNIT_MASK) {
        case COMPLEX_UNIT_FRACTION:
            return complexToFloat(data) * base;
        case COMPLEX_UNIT_FRACTION_PARENT:
            return complexToFloat(data) * pbase;
        }
        return 0;
    }

    public float getFraction(float base, float pbase) {
        return complexToFraction(data, base, pbase);
    }
    
    public void setTo(TypedValue other) {
        type = other.type;
        string = other.string;
        data = other.data;
        assetCookie = other.assetCookie;
        resourceId = other.resourceId;
        density = other.density;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypedValue{t=0x").append(Integer.toHexString(type));
        sb.append("/d=0x").append(Integer.toHexString(data));
        if (type == TYPE_STRING) {
            sb.append(" \"").append(string != null ? string : "<null>").append("\"");
        }
        if (assetCookie != 0) {
            sb.append(" a=").append(assetCookie);
        }
        if (resourceId != 0) {
            sb.append(" r=0x").append(Integer.toHexString(resourceId));
        }
        sb.append("}");
        return sb.toString();
    }

    public final CharSequence coerceToString() {
        int t = type;
        if (t == TYPE_STRING) {
            return string;
        }
        return typeValueToString(t, data);
    }

    private static final String[] DIMENSION_UNIT_STRS = new String[] { "px", "dip", "sp", "pt", "in", "mm" };
    private static final String[] FRACTION_UNIT_STRS = new String[] { "%", "%p" };
    
    public static final String typeValueToString(int type, int data) {
        switch (type) {
        case TYPE_NULL:
            return null;
        case TYPE_REFERENCE:
            return String.format("@0x%08x", data);
        case TYPE_ATTRIBUTE:
            return String.format("?0x%08x", data);
        case TYPE_FLOAT:
            return Float.toString(Float.intBitsToFloat(data));
        case TYPE_DIMENSION:
            return Float.toString(complexToFloat(data)) + DIMENSION_UNIT_STRS[(data >> COMPLEX_UNIT_SHIFT) & COMPLEX_UNIT_MASK];
        case TYPE_FRACTION:
            return Float.toString(complexToFloat(data) * 100) + FRACTION_UNIT_STRS[(data >> COMPLEX_UNIT_SHIFT) & COMPLEX_UNIT_MASK];
        case TYPE_INT_HEX:
            return String.format("0x%08x", data);
        case TYPE_INT_BOOLEAN:
            return data != 0 ? "true" : "false";
        }

        if (type >= TYPE_FIRST_COLOR_INT && type <= TYPE_LAST_COLOR_INT) {
            return "#" + Integer.toHexString(data);
        } else if (type >= TYPE_FIRST_INT && type <= TYPE_LAST_INT) {
            return Integer.toString(data);
        }
        
        return null;
    }
};
