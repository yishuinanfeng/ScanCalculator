package com.app.mathpix_sample.evaluator.convert;

import android.content.Context;
import android.support.annotation.NonNull;

import com.app.mathpix_sample.evaluator.EvaluateConfig;
import com.app.mathpix_sample.evaluator.MathEvaluator;

import java.util.ArrayList;

/**
 * 创建时间： 2018/10/16
 * 作者：yanyinan
 * 功能描述：方程组转换器
 */
public class SystemEquationConverter implements Converter {

    private ArrayList<String> scanResult = new ArrayList<>();
    private Context context;

    public SystemEquationConverter(@NonNull Context context, ArrayList<String> scanResultList) {
        this.context = context;
        if (scanResultList != null && scanResultList.size() > 0) {
            scanResult = scanResultList;
        }
    }

    @Override
    public String getConvertResult() throws Exception{
        String input = FormatTransformer.getInstance().createSystemEquationExpr(scanResult);

        if (MathEvaluator.getInstance().isSyntaxError(input)) {
            Exception exception = MathEvaluator.getError(input);
            if (exception != null) {
                return exception.getMessage();
            }
        }

        EvaluateConfig config = EvaluateConfig.loadFromSetting(context);
        String result = MathEvaluator.getInstance().solveSystemEquation(input, config.setEvalMode(EvaluateConfig.FRACTION), context);

        // TODO: 2018/10/18 调试用
        if (result.contains("root")){
            return input;
        }

        return result;
    }
}
