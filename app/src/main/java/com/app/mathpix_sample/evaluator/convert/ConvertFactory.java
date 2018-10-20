package com.app.mathpix_sample.evaluator.convert;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：转化器工厂
 */
public class ConvertFactory {
    /**
     * 正则表达式匹配是否包含英文字母
     */
    private static final String regex=".*[x-zX-Z]+.*";

    public static String TAG = ConvertFactory.class.getSimpleName();

    public static  Converter createConverter(ArrayList<String> scanResultList, Context context){
        if (scanResultList == null||scanResultList.size() == 0){
            Log.e(TAG,"scanResultList is empty");
            return null;
        }

        // 包含字母则为方程
        // TODO: 2018/10/12 有待检验该判断条件
        Pattern pattern = Pattern.compile(regex);
        boolean isEquation = false;
        ArrayList<String> formatTransResult = new ArrayList<>();
        for (String result:scanResultList) {
            // 每条式子要先格式处理之后才可以判断是否为方程
            FormatTransformer formatTransformer = FormatTransformer.getInstance();
            String replaceResult = formatTransformer.basicFormat(result);
            formatTransResult.add(replaceResult);
            Matcher matcher = pattern.matcher(replaceResult);
            if (matcher.find()){
                isEquation = true;
            }
        }

        Converter converter;
        if (isEquation){
            if (formatTransResult.size() == 1){
                converter = new EquationConverter(context,formatTransResult);
            }else {
                converter = new SystemEquationConverter(context,formatTransResult);
            }

        }else {
            converter = new BaseConverter(formatTransResult);
        }
        return converter;
    }

}
