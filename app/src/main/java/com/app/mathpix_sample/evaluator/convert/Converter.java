package com.app.mathpix_sample.evaluator.convert;

import java.util.ArrayList;

/**
 * 创建时间： 2018/10/12
 * 作者：yanyinan
 * 功能描述：图像扫描库识别结果运算式子文本转化为具体计算结果
 */
public interface Converter {
    // 输入为图像识别结果的lateX的集合
    String getConvertResult() throws Exception;

}
