package com.app.mathpix_sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.app.mathpix_sample.api.request.SingleProcessRequest;
import com.app.mathpix_sample.api.response.DetectionResult;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class UploadImageTask extends AsyncTask<UploadImageTask.UploadParams, Void, UploadImageTask.Result> {

    private final ResultListener listener;
    private WeakReference<Context> contextWeakReference;

    UploadImageTask(ResultListener listener, Context context) {
        this.listener = listener;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected Result doInBackground(UploadParams... arr) {
        UploadParams params = arr[0];
        Result result;
        try {
            SingleProcessRequest singleProcessRequest = new SingleProcessRequest(params.image);
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(singleProcessRequest));

            Log.d("Bitmap length:",requestBody.contentLength() + "");

            Request request = new Request.Builder()
                    .url("https://api.mathpix.com/v3/latex")
                    .addHeader("content-type", "application/json")
                    .addHeader("app_id", "mathpix")
                    .addHeader("app_key", "139ee4b61be2e4abcfb1238d9eb99902")

                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            DetectionResult detectionResult = new Gson().fromJson(responseString, DetectionResult.class);
            if (detectionResult != null && detectionResult.latex != null) {
                result = new ResultSuccessful(detectionResult.latex_list);
            } else if (detectionResult != null && detectionResult.error != null) {
                result = new ResultFailed(detectionResult.error);
            } else {
                result = new ResultFailed("Math not found");
            }
        } catch (Exception e) {

            result = new ResultFailed(e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result instanceof ResultSuccessful) {
            ResultSuccessful successful = (ResultSuccessful) result;
            listener.onSuccess(successful.latex_list);
        } else if (result instanceof ResultFailed) {
            ResultFailed failed = (ResultFailed) result;
            listener.onError(failed.message);
        }
    }

    interface ResultListener {
        void onError(String message);

        void onSuccess(ArrayList<String> url);
    }

    static class UploadParams {
        private Bitmap image;

        UploadParams(Bitmap image) {
            this.image = image;
        }
    }

    static class Result {
    }

    private static class ResultSuccessful extends Result {
        private String latex;
        private ArrayList<String> latex_list;


        ResultSuccessful(String latex) {
            this.latex = latex;
        }

        public ResultSuccessful(ArrayList<String> latex_list) {
            this.latex_list = latex_list;
        }
    }

    private static class ResultFailed extends Result {
        String message;

        ResultFailed(String message) {
            this.message = message;
        }
    }
}
