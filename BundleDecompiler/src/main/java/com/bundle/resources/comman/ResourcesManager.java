package com.bundle.resources.comman;
import com.bundle.conversion.Convert;

public class ResourcesManager {

    private final static int TYPE_REFERENCE   = 0x01;
    private final static int TYPE_STRING      = 0x02;
    private final static int TYPE_INT         = 0x04;
    private final static int TYPE_BOOL        = 0x08;
    private final static int TYPE_COLOR       = 0x10;
    private final static int TYPE_FLOAT       = 0x20;
    private final static int TYPE_DIMEN       = 0x40;
    private final static int TYPE_FRACTION    = 0x80;
    public final static int  TYPE_ANY_STRING  = 0xee;
    public static final int TYPE_ENUM         = 0x00010000;
    public static final int TYPE_FLAGS        = 0x00020000;

    /**
     * @param arrayValues
     */
    public static int[] convertIntegerArray(String arrayValues) {
        // TODO Auto-generated method stub
        //System.out.println("arrayValues "+arrayValues);
        String values[]=arrayValues.split(",");
        int intFalgs[]=new int[values.length];
        for(int count=0;count<values.length;count++) {
            intFalgs[count]=Convert.hex2Decimal(values[count]);
        }
        return intFalgs;
    }

    public static  String getCombinationValues(int intVal,int mFlags[],String attributeRawType) {

        if (intVal == 0) {
            return null;
        }
        int[] flagItems = new int[mFlags.length];
        int[] flags = new int[mFlags.length];
        int flagsCount = 0;
        for (int i = 0; i < mFlags.length; i++) {
            int flagItem = mFlags[i];
            int flag = flagItem;
            if ((intVal & flag) != flag) {
                continue;
            }
            if (!isSubpartOf(flag, flags)) {
                flags[flagsCount] = flag;
                flagItems[flagsCount++] =flagItem;
            }
        }
        String formatType = "";
        for(int count=0;count<flagItems.length;count++) {
            if(flagItems[count]!=0) {
               String mType=getResItemsValues(attributeRawType,Convert.decimal2Hex(flagItems[count]));
              // System.out.println("mType "+mType);
                formatType=formatType+"|"+mType;
            }
        }
        if(formatType.startsWith("|")){
            formatType=formatType.substring(1);
        }
        return formatType;
    }

    private static boolean isSubpartOf(int flag, int[] flags) {
        for (int i = 0; i < flags.length; i++) {
            if ((flags[i] & flag) == flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param mAttributeRawType android:widgetFeatures(0x01010579)=0x0000000e | app:widgetFeatures(0x7f02012a)=0x0000000e
     */
    public static  String getAttributeName(String mAttributeRawType){
        return mAttributeRawType.substring(0,mAttributeRawType.indexOf("("));
    }

    /**
     * @param mAttributeRawType android:widgetFeatures(0x01010579)=0x0000000e | app:widgetFeatures(0x7f02012a)=0x0000000e
     */
    public static String getAttributeId(String mAttributeRawType) {
        return mAttributeRawType.substring(mAttributeRawType.indexOf("(")+1, mAttributeRawType.lastIndexOf(")"));
    }

    /**
     * @param mAttributeRawType android:widgetFeatures(0x01010579)=0x0000000e | app:widgetFeatures(0x7f02012a)=0x0000000e
     */
    public static  String getAttributeHexaValue(String mAttributeRawType){
        return mAttributeRawType.substring(mAttributeRawType.lastIndexOf(")=")+2);
    }


    /**
     * @param mAttributeRawType android:widgetFeatures(0x01010579)=0x0000000e | app:widgetFeatures(0x7f02012a)=0x0000000e
     */
    public static  String getAttributeValue(String mAttributeRawType){
        String mAttributeValue=null;
        //System.out.println("mAttributeRawType "+mAttributeRawType);
        //System.out.println(" "+getAttributeId(mAttributeRawType));
        if(getAttributeId(mAttributeRawType).startsWith(FrameworkResources.mFrameworkId)){
            mAttributeValue= FrameworkResources.mFrameworkAttributeValues.get(mAttributeRawType);

        }else if(getAttributeId(mAttributeRawType).startsWith(ApplicationResources.mApplicationId)) {
            mAttributeValue = ApplicationResources.mApplicationAttributeValues.get(mAttributeRawType);

        }else {
            System.err.println("STDERR mAttributeRawType is wrong "+mAttributeRawType);
        }
        return mAttributeValue;
    }

    /**
     * @param mResourceId 0x01010579 | 0x7f02012a
     */
    public static String getResourceValue(String mResourceId){
        String mResourceValue=null;
        if(mResourceId.trim().startsWith(FrameworkResources.mFrameworkId)) {
            mResourceValue= FrameworkResources.mFrameworkResourcesValues.get(mResourceId);

        }else if(mResourceId.trim().startsWith(ApplicationResources.mApplicationId)) {
            mResourceValue=ApplicationResources.mApplicationResourcesValues.get(mResourceId);

        }else{
            System.err.println("STDERR mResourceId is wrong "+mResourceId);
        }
        return mResourceValue;
    }

    /**
     * @param mItemRawType android:widgetFeatures(0x01010579) | app:widgetFeatures(0x7f02012a)
     * @param mHexaValue 0x0000000e
     */
    public static String getFlagTypeValues(String mItemRawType, String mHexaValue) {
        String finalCombinationValues=null;
        String arrayValues= null;
        if(getAttributeId(mItemRawType).startsWith(FrameworkResources.mFrameworkId)){
            arrayValues=FrameworkResources. mFrameworkAttributeArrayValues.get(mItemRawType);

        }else if(getAttributeId(mItemRawType).startsWith(ApplicationResources.mApplicationId)) {
            arrayValues = ApplicationResources.mApplicationAttributeArrayValues.get(mItemRawType);

        }else {
            System.err.println("STDERR mItemRawType is wrong "+mItemRawType);
        }

        if(arrayValues!=null) {
            int intFalgs[]=ResourcesManager.convertIntegerArray(arrayValues);
            int myType=Convert.hex2Decimal(mHexaValue);
            finalCombinationValues=ResourcesManager.getCombinationValues(myType,intFalgs,mItemRawType);
        }
        return finalCombinationValues;
    }

    /**
     * @param mItemRawType android:widgetFeatures(0x01010579) | app:widgetFeatures(0x7f02012a)
     * @param mHexaValue 0x0000000e
     */
    public static String getResItemsValues(String mItemRawType, String mHexaValue) {
        int decimal=Convert.hex2Decimal(mHexaValue);
        mItemRawType=mItemRawType+"="+Convert.getHexFormat(decimal);
        return getAttributeValue(mItemRawType);
    }


}
