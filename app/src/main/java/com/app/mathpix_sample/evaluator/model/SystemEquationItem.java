/*
 * Copyright (C) 2018 Duy Tran Le
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.app.mathpix_sample.evaluator.model;

import android.content.Context;
import android.util.Log;


import com.app.mathpix_sample.R;
import com.app.mathpix_sample.evaluator.MathEvaluator;

import java.util.ArrayList;

/**
 * Created by Duy on 09-Jan-17.
 */

public class SystemEquationItem extends ExprInput {
    private static final String TAG = SystemEquationItem.class.getSimpleName();
    private boolean define = true;
    private int row, col;
    private String[][] matrix;
    private String[] vector; //do not uses
    private String[] variable;
    private ArrayList<String> equations = new ArrayList<>();

    public SystemEquationItem(ArrayList<EquationItem> equations, String var) {
        this.define = false;
        variable = new String[1];
        variable[0] = var;
        for (EquationItem equationItem : equations) {
            this.equations.add(equationItem.getInput());
        }
    }

    public SystemEquationItem(int row, int col, String[][] matrix, String[] vector, String[] variable) {
        this.row = row;
        this.col = col;
        this.matrix = matrix;
        this.vector = vector;
        this.variable = variable;
    }

    public SystemEquationItem(int row, int col, String[][] matrix, String[] variable) {
        this.row = row;
        this.col = col;
        this.matrix = matrix;
        this.variable = variable;
    }

    public String[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(String[][] matrix) {
        this.matrix = matrix;
    }

    @Override
    public boolean isError(MathEvaluator evaluator) {
        if (define) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (evaluator.isSyntaxError(matrix[i][j])) return true;
                }
            }
        } else {
            for (String s : equations) {
                if (evaluator.isSyntaxError(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getInput() {
        if (define) {
            equations.clear();
            StringBuilder result = new StringBuilder();
            result.append("Solve(");
            result.append("{");
            //{{1, 1}, {1, 2}}
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col - 1; j++) {
                    //(25 -32) * x +
                    result.append("(")
                            .append(matrix[i][j])
                            .append(")")
                            .append("*")
                            .append(variable[j]);
                    if (j != col - 2) result.append("+");
                }
                result.append("==").append(matrix[i][col - 1]);
                if (i != row - 1) result.append(",");
            }
            result.append("},{");
            for (int i = 0; i < variable.length; i++) {
                result.append(variable[i]);
                if (i != variable.length - 1) result.append(",");
            }
            result.append("})");
            Log.d(TAG, "getInput: " + result.toString());
            return result.toString();
        } else {
            StringBuilder result = new StringBuilder();
            result.append("Solve(");
            result.append("{");
            for (int i = 0; i < equations.size(); i++) {
                result.append(equations.get(i));
                if (i != equations.size() - 1) {
                    result.append(",");
                }
            }
            result.append("},{");
            result.append(variable[0]);
            result.append("})");
            Log.d(TAG, "getInput: " + result.toString());
            return result.toString();
        }
    }

    @Override
    public String toString() {
        return getInput();
    }

    @Override
    public String getError(MathEvaluator evaluator, Context context) {
        if (define) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    if (evaluator.isSyntaxError(matrix[i][j])) {
                        Log.d(TAG, "getError: " + matrix[i][j]);
                        return
                                "<h2>" + context.getString(R.string.error)
                                        + "[" + (i + 1) + "," + (j + 1) + "]" + "</h2>";
                    }
                }
            }
        } else {
            for (int i = 0; i < equations.size(); i++) {
                if (evaluator.isSyntaxError(equations.get(i))) {
                    return "<h2>" + context.getString(R.string.error)
                            + " " + equations.get(i);
                }
            }
        }
        return "";
    }
}
