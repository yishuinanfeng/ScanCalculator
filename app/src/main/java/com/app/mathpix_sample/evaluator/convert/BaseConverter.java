package com.app.mathpix_sample.evaluator.convert;

import com.app.mathpix_sample.evaluator.MathEvaluator;

import java.util.ArrayList;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：基本运算式转化器
 */
public class BaseConverter implements Converter {
    private String scanResult = "";

    public BaseConverter(ArrayList<String> scanResultList) {
        if (scanResultList != null && scanResultList.size() > 0) {
            scanResult = scanResultList.get(0);
        }
    }

    @Override
    public String getConvertResult() throws Exception{
        // 移除末尾的“=”
        if (scanResult.endsWith("=")){
            scanResult = scanResult.replace("=","");
        }

        MathEvaluator mathEvaluator = MathEvaluator.getInstance();
        return mathEvaluator.evaluateWithResultNormal(scanResult);
    }
}
