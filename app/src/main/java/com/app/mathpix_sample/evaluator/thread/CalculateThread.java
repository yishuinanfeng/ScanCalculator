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

package com.app.mathpix_sample.evaluator.thread;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.app.mathpix_sample.evaluator.CalculatorPresenter;
import com.app.mathpix_sample.evaluator.EvaluateConfig;
import com.app.mathpix_sample.evaluator.MathEvaluator;

import java.util.ArrayList;

import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static android.os.Process.setThreadPriority;

/**
 * Created by Duy on 24-Jun-17.
 */

public class CalculateThread extends BaseThread {

    public CalculateThread(CalculatorPresenter presenter, EvaluateConfig config,
                           ResultCallback resultCallback) {
        super(presenter, config, resultCallback);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(String expr) {
        execute(expr, mConfig);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(String expr, final EvaluateConfig config) {
        Command<ArrayList<String>, String> task = new Command<ArrayList<String>, String>() {
            @Override
            public ArrayList<String> execute(String input) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(MathEvaluator.getInstance().evaluateWithResultAsTex(input, config));
                return arrayList;
            }
        };

        //Passes the rest onto the Thread
        Thread thread = new Thread(task, resultCallback);
        thread.executeOnExecutor(EXECUTOR, expr);
    }

    @Override
    public void execute(@Nullable Command<ArrayList<String>, String> command, String expr) {

        //Passes the rest onto the Thread
        Thread thread = new Thread(command, resultCallback);
        thread.executeOnExecutor(EXECUTOR, expr);
    }


    /**
     * A generalization of the Thread that all the heavy worload calculus functions will use.
     */
    private static class Thread extends AsyncTask<String, Void, ArrayList<String>> {
        public Exception error; //If any Exception were to occur
        private Command<ArrayList<String>, String> task;
        private ResultCallback resultCallback;


        public Thread(Command<ArrayList<String>, String> task,
                      ResultCallback resultCallback) {
            this.task = task;
            this.resultCallback = resultCallback;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public final ArrayList<String> doInBackground(String... params) {
            try {
                setThreadPriority(THREAD_PRIORITY_MORE_FAVORABLE); //Higher priority
                return task.execute(params[0]);
            } catch (Exception e) {
                error = e;
            } catch (StackOverflowError e) {
                error = new IllegalArgumentException(e.getMessage());
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<String> result) {
            if (result == null) {
                resultCallback.onError(error);
            } else {
                resultCallback.onSuccess(result);
            }
        }
    }


}
