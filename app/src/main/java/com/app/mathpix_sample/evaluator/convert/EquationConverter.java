package com.app.mathpix_sample.evaluator.convert;

import android.content.Context;

import com.app.mathpix_sample.evaluator.EvaluateConfig;
import com.app.mathpix_sample.evaluator.MathEvaluator;
import com.app.mathpix_sample.evaluator.model.SolveItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：方程转化器
 */
public class EquationConverter implements Converter {

    private ArrayList<String> scanResult = new ArrayList<>();
    private Context context;

    public EquationConverter(Context context, ArrayList<String> scanResultList) {
        this.context = context;
        if (scanResultList != null && scanResultList.size() > 0) {
            scanResult = scanResultList;
        }
    }

    @Override
    public String getConvertResult() throws Exception{
        String result = "";

        if (scanResult.size() == 1) {
            SolveItem solveItem = new SolveItem(scanResult.get(0));
            EvaluateConfig config = EvaluateConfig.loadFromSetting(context);
            String input = solveItem.getInput();
            if (input.contains("=")&&!input.contains("==")){
                input = input.replace("=","==");
            }
            result = MathEvaluator.getInstance().solveEquation(input,config.setEvalMode(EvaluateConfig.FRACTION) , context);


            // TODO: 2018/10/18 调试用
            if (result.contains("root")){
                return solveItem.getInput();
            }

            // TODO: 2018/10/18 result为非法格式处理

        }
//        result = result.replace("$","");


        return result;
    }
}
