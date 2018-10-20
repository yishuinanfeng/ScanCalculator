package com.app.mathpix_sample.evaluator.convert;

import android.support.annotation.NonNull;

import com.app.mathpix_sample.evaluator.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：每一个基本运算式子文本格式转化器：扫描文本-->计算库可识别文本
 */

// TODO: 2018/10/15 性能考虑优化
public class FormatTransformer {

    private ArrayList<Replacement> sBasicReplacements = new ArrayList<>();
    ArrayList<Character> sXYZList = new ArrayList<>();

    {
        sBasicReplacements.add(new Replacement("\\div", "/"));
        sBasicReplacements.add(new Replacement("\\times", "*"));
        //开方
        sBasicReplacements.add(new Replacement("\\sqrt", "Sqrt"));
        //三角函数
        sBasicReplacements.add(new Replacement("\\sin", "sin"));
        sBasicReplacements.add(new Replacement("\\cos", "cos"));
        sBasicReplacements.add(new Replacement("\\tan", "tan"));
        //角度
        sBasicReplacements.add(new Replacement("^{\\circ}", "degree"));

        sXYZList.add('x');
        sXYZList.add('y');
        sXYZList.add('z');


    }

    public static FormatTransformer getInstance() {
        return SingleInstanceHolder.INSTANCE;
    }

    private FormatTransformer() {
    }

    /**
     * 替换扫描得到的lateX英文符号为计算机符号
     *
     * @return
     */
    public String basicFormat(@NonNull String formula) {
        //删除空格
        String result = formula.replaceAll(" +", "");
        for (Replacement replacement : sBasicReplacements) {
            result = result.replace(replacement.oldExpr, replacement.newExpr);
        }

        result = replaceFraction(result);
        result = replaceSqrtBracket(result);
        //   result = replaceSqrtBracket(result);
        result = replaceLogSympol(result);
        result = deletePowerBracket(result);
        return result;
    }

    /**
     * 删除开方的括号
     *
     * @param result
     * @return
     */
    private String deletePowerBracket(String result) {
        if (!result.contains("^")) {
            return result;
        }

        StringBuilder sb = new StringBuilder(result.trim());
        int index1 = 0;

        while ((index1 = sb.indexOf("^", index1)) != -1) {
            index1 += "^".length();
            int leftBracketIndex = sb.indexOf("{", index1);
            sb.replace(leftBracketIndex, leftBracketIndex + 1, "(");
            int rightBracketIndex = sb.indexOf("}", leftBracketIndex);
            sb.replace(rightBracketIndex, rightBracketIndex + 1, ")");
        }

        return sb.toString();
    }

    private String replaceSqrtBracket(String result) {
        if (!result.contains("Sqrt")) {
            return result;
        }

        StringBuilder sb = new StringBuilder(result.trim());
        int index1 = 0;
        // TODO: 2018/10/15 性能考虑优化
        while ((index1 = sb.indexOf("Sqrt", index1)) != -1) {
            index1 += "Sqrt".length();
            int leftBracketIndex = sb.indexOf("{", index1);
            sb.replace(leftBracketIndex, leftBracketIndex + 1, "(");
            int rightBracketIndex = sb.indexOf("}", leftBracketIndex);
            sb.replace(rightBracketIndex, rightBracketIndex + 1, ")");
        }

        return sb.toString();
    }

    /**
     * 处理对数
     *
     * @param input
     * @return
     */
    private String replaceLogSympol(String input) {
        if (!input.contains("\\log_")) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input);
        int index1 = 0;
        // TODO: 2018/10/15 性能考虑优化
        while ((index1 = sb.indexOf("\\log_", index1)) != -1) {
            index1 += "\\log_".length();
            int leftBracketIndex = sb.indexOf("{", index1);
            sb.replace(leftBracketIndex, leftBracketIndex + 1, "");
            int rightBracketIndex = sb.indexOf("}", leftBracketIndex);
            sb.replace(rightBracketIndex, rightBracketIndex + 1, "(");

            int realNumIndex = -1;
            for (int i = rightBracketIndex + 1; i < sb.length(); i++) {
                if (!Character.isDigit(sb.charAt(i))) {
                    realNumIndex = i;
                    break;
                }
            }

            if (realNumIndex == -1) {
                realNumIndex = sb.length();
            }

            sb.insert(realNumIndex, ")");
        }

        return sb.toString().replace("\\log_","log");
    }

    /**
     * 替换开方括号
     *
     * @param
     * @return
     */
//    private String replaceSqrtBracket(String result) {
//
//        if (!result.contains("Sqrt(")) {
//            return result;
//        }
//
//        StringBuilder sb = new StringBuilder(result.trim());
//        int index = 0;
//
//        while ((index = sb.indexOf("Sqrt(", index)) != -1) {
//            index += "Sqrt(".length();
//
//            int rightBracketIndex = sb.indexOf("}", index);
//            if (rightBracketIndex > 0) {
//                sb.replace(rightBracketIndex, rightBracketIndex + 1, ")");
//            }
//        }
//
//        return sb.toString();
//    }
    private String replaceEqualSymbol(String s) {
        if (!s.contains("=")) s = s + "==0";
        if (!s.contains("==")) s = s.replace("=", "==");
        while (s.contains("===")) s = s.replace("===", "==");
        return s;
    }

    /**
     * 含分式的转化
     *
     * @param input
     * @return
     */
    private String replaceFraction(String input) {
        if (!input.contains("\\frac")) {
            return input;
        }

        StringBuilder stringBuilder = new StringBuilder(input);

        int fractionIndex = 0;
        int endIndex = 0;

        while ((fractionIndex = stringBuilder.indexOf("\\frac", endIndex)) != -1) {
            //删除"\frac"
            int num = 0;

            for (int i = fractionIndex; i < stringBuilder.length(); i++) {
                if (stringBuilder.charAt(i) == '{' || stringBuilder.charAt(i) == '}') {
                    num++;
                }

                //两个括号结束
                if (num == 4) {
                    endIndex = i;
                    break;
                }
            }

            String formula = stringBuilder.substring(fractionIndex, endIndex + 1);
            formula = formula.replace("}{", "/");
            formula = formula.replace("{", "");
            formula = formula.replace("}", "");

            stringBuilder.replace(fractionIndex, endIndex + 1, formula);

        }

        return stringBuilder.toString().replace("\\frac", "");
    }

    /**
     * 给方程组字母乘以字母添加“*”。例如：方程组中的xy转为x*y
     *
     * @param stringBuilder
     * @return
     */
    private String addTimeSymbolSystemEquation(StringBuilder stringBuilder) {

        // 排除开头的“Solve”，所以从i = 5开始
        for (int i = 5; i < stringBuilder.length(); i++) {
            if (sXYZList.contains(Character.toLowerCase(stringBuilder.charAt(i)))) {
                if (i == stringBuilder.length() - 1) {
                    break;
                }
                if (sXYZList.contains(Character.toLowerCase(stringBuilder.charAt(i + 1)))) {
                    stringBuilder.insert(i + 1, "*");
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 合成方程组需要的输入文本格式
     *
     * @param inputList
     * @return
     */
    public String createSystemEquationExpr(ArrayList<String> inputList) {
        ArrayList<String> arrayList = new ArrayList<>();
        //存x，y,z
        Set<String> alphabetSet = new HashSet<>();

        for (String input : inputList) {
            Tokenizer tokenizer = new Tokenizer();
            String expression = tokenizer.getNormalExpression(input);
            expression = replaceEqualSymbol(expression);
            if (expression.isEmpty()) {
                continue;
            }

            arrayList.add(expression);

            if (expression.toLowerCase().contains("x")) {
                alphabetSet.add("x");
            }

            if (expression.toLowerCase().contains("y")) {
                alphabetSet.add("y");
            }

            if (expression.toLowerCase().contains("z")) {
                alphabetSet.add("z");
            }
        }

        StringBuilder equation = new StringBuilder();
        equation.append("Solve({");
        for (int i = 0; i < arrayList.size(); i++) {
            String s = arrayList.get(i);
            s = replaceEqualSymbol(s);
            if (i != arrayList.size() - 1) {
                equation.append(s);
                equation.append(",");
            } else {
                equation.append(s);
            }
        }
        equation.append("}");
        equation.append(",");
        equation.append("{");
        for (String s : alphabetSet) {
            equation.append(s);
            equation.append(",");
        }
        int lastDotIndex = equation.lastIndexOf(",");
        equation.deleteCharAt(lastDotIndex);
        equation.append("}");
        equation.append(")");
        return addTimeSymbolSystemEquation(equation);
    }

    private static class Replacement {
        String oldExpr;
        String newExpr;

        Replacement(String oldExpr, String newExpr) {
            this.oldExpr = oldExpr;
            this.newExpr = newExpr;
        }
    }

    private static class SingleInstanceHolder {
        private static final FormatTransformer INSTANCE = new FormatTransformer();
    }

}
