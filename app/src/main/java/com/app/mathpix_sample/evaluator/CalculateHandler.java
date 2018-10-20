package com.app.mathpix_sample.evaluator;

import android.content.Context;

import com.app.mathpix_sample.evaluator.convert.ConvertFactory;
import com.app.mathpix_sample.evaluator.convert.Converter;

import java.util.ArrayList;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：数学计算总的处理器
 */
public class CalculateHandler {

    public String getCalculateResult(Context context,ArrayList<String> scanResultList) throws Exception{
        Converter converter = ConvertFactory.createConverter(scanResultList,context);

        if (converter == null){
            return "";
        }

        return converter.getConvertResult();
    }
}
