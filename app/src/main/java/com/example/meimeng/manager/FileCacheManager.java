package com.example.meimeng.manager;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by HIAPAD on 2018/4/8.
 */

public class FileCacheManager {

    private static Context sContext;

    private FileCacheManager() {
    }

    private static FileCacheManager mInstance;

    private static class SingleFileCacheManager {
        private static FileCacheManager instance = new FileCacheManager();
    }

    public static FileCacheManager getInstance(Context context) {
        sContext = context.getApplicationContext();
        return SingleFileCacheManager.instance;
    }

    public void fileCacheByString(String content, String cacheFileName) {
        writeContent(content, cacheFileName);
    }

    public void fileCacheByBody(Class<?> clazz) {

    }

    private void writeContent(final String content, final String fileName) {
        ThreaManager.pushRunnable(new Runnable() {
            @Override
            public void run() {
                File cacheDir = sContext.getCacheDir();
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(new File(cacheDir, fileName)));
                    bw.write(content);
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    public String readFileContent(final String fileName) {
        return ThreaManager.callResults(new Callable<String>() {
            @Override
            public String call() throws Exception {
                File cacheDir = sContext.getCacheDir();
                StringBuffer result = new StringBuffer();
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(new File(cacheDir, fileName)));
                    String str;
                    while ((str = br.readLine()) != null) {
                        result.append(str);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        br.close();
                    }
                }
                return result.toString();
            }
        });
    }
}
