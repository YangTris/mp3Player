package com.example.mp3player;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, Integer, String> {

    private AsyncTaskCompleteListener<File> callback;
    private DownloadProgressListener progressListener;
    private File storageDir;

    public DownloadFile(AsyncTaskCompleteListener<File> fileAsyncTaskCompleteListener, DownloadProgressListener progressListener) {
        this.callback = fileAsyncTaskCompleteListener;
        this.progressListener = progressListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String fileUrl = params[0];
        String fileName = params[1];
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MyMusic");
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                return null; // Failed to create directory
            }

            File outputFile = new File(storageDir, fileName);

            input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(outputFile);

            byte data[] = new byte[8192]; // 8 KB buffer size
            int count;
            int total = 0;
            int fileLength = connection.getContentLength();

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                total += count;
                int progress = (int) ((total * 100) / fileLength);
                publishProgress(progress);
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return null;
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException e) {
                e.printStackTrace(); // Log the error
            }
            if (connection != null) connection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            callback.onTaskComplete(storageDir);
        } else {
            // Handle download failure
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (callback != null) {
            progressListener.onProgressUpdate(values[0]);
        }
    }
}

interface AsyncTaskCompleteListener<File> {
    void onTaskComplete(File url);
}

interface DownloadProgressListener {
    void onProgressUpdate(int progress);
}
