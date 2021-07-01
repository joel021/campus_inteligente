package com.rv.pij2021.VUFRB.service;

/*
Código adaptado de https://www.codegrepper.com/code-examples/java/how+to+read+file+from+assets+folder+in+android,
acesso em 27/06/2021
 */

import android.content.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileResourcesService {

    public static List<float[]> getFileFromResource(Context context, String fileName) throws IOException {
        List<float[]> content = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));
        String line;

        int i = 0;
        while( (line = reader.readLine()) != null){
            if(i == 0){
                i++;
                continue;
            }

            String[] vLine = line.split(",");
            float[] v = new float[vLine.length];

            for (int j = 0; j < v.length; j++){
                v[j] = Float.parseFloat(vLine[i]);
            }

            content.add(v);

            i++;
        }

        reader.close();
        return content;
    }

}