/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.rv.pij2021.VUFRB.tflite.tracking;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Pair;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.rv.pij2021.VUFRB.DecisaoBlocosRA;
import com.rv.pij2021.VUFRB.CustomRectF;
import com.rv.pij2021.VUFRB.GerencieGeoLocalizacao;
import com.rv.pij2021.VUFRB.R;
import com.rv.pij2021.VUFRB.tflite.env.ImageUtils;
import com.rv.pij2021.VUFRB.tflite.lib_task_api.Detector;

/** A tracker that handles non-max suppression and matches existing objects to new detections. */
public class MultiBoxTracker {
  private DecisaoBlocosRA decisaoBlocosRA = new DecisaoBlocosRA();
  private GerencieGeoLocalizacao gerencieGeoLocalizacao;

  private static final int[] COLORS = {
         R.color.amarelo_ufrb,
          R.color.purple_200,
          R.color.preto_ufrb
  };

  final List<Pair<Float, RectF>> screenRects = new LinkedList<Pair<Float, RectF>>();
  private final Queue<Integer> availableColors = new LinkedList<Integer>();
  public final List<TrackedRecognition> trackedObjects = new LinkedList<TrackedRecognition>();
  private final Paint boxPaint = new Paint();
  //private final float textSizePx;
  //private final BorderedText borderedText;
  private Matrix frameToCanvasMatrix;
  private int frameWidth;
  private int frameHeight;
  private int sensorOrientation;

  public MultiBoxTracker(GerencieGeoLocalizacao gerencieGeoLocalizacao) {

    this.gerencieGeoLocalizacao = gerencieGeoLocalizacao;

    for (final int color : COLORS) {
      availableColors.add(color);
    }

    boxPaint.setColor(Color.RED);
    boxPaint.setStyle(Style.STROKE);
    boxPaint.setStrokeWidth(10.0f);
    boxPaint.setStrokeCap(Cap.ROUND);
    boxPaint.setStrokeJoin(Join.ROUND);
    boxPaint.setStrokeMiter(100);

    //extSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
    //borderedText = new BorderedText(textSizePx);
  }


  public synchronized void setFrameConfiguration(
      final int width, final int height, final int sensorOrientation) {
    frameWidth = width;
    frameHeight = height;
    this.sensorOrientation = sensorOrientation;
  }

  public synchronized void trackResults(final List<Detector.Recognition> results) {
    //Log.i("RAUFRB","3° Processing "+results.size()+" results from time MultiBoxTracker.trackResults");
    processResults(results);
  }

  private Matrix getFrameToCanvasMatrix() {
    return frameToCanvasMatrix;
  }

  public synchronized void draw(final Canvas canvas, ImageView imageViewRA, CustomRectF trackedPos) {

    imageViewRA.setImageBitmap(null);

    final boolean rotated = sensorOrientation % 180 == 90;
    final float multiplier =
        Math.min(
            canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
            canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));

    int w = (int) (multiplier * (rotated ? frameHeight : frameWidth));
    int h =  (int) (multiplier * (rotated ? frameWidth : frameHeight));
    frameToCanvasMatrix =
        ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
                w,
                h,
            sensorOrientation,
            false);

    for (final TrackedRecognition recognition : trackedObjects) {

      trackedPos.set(recognition.location);
      getFrameToCanvasMatrix().mapRect(trackedPos);

      trackedPos.width = w;
      trackedPos.height = h;
      boxPaint.setColor(recognition.color);

      float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
      canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

      imageViewRA.setImageResource(recognition.imgId);
      imageViewRA.setX(trackedPos.left);
      imageViewRA.setY(trackedPos.top);

      //final String labelString = String.format("%s %.2f", recognition.title, (100 * recognition.detectionConfidence));
      //borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);

    }
  }

  private void processResults(final List<Detector.Recognition> results) {
    //Log.i("RAUFRB", "3° MultiBoxTracker.processResults()");

    trackedObjects.clear();
    //final List<Pair<Float, Detector.Recognition>> rectsToTrack = new LinkedList<Pair<Float, Detector.Recognition>>();

    screenRects.clear();
    final Matrix rgbFrameToScreen = new Matrix(getFrameToCanvasMatrix()); //possivelmente, a referencia está sendo modificada. Mas não tenho certeza.

    for (final Detector.Recognition result : results) {
      if (result.getLocation() == null) {
        continue;
      }
      final RectF detectionFrameRect = new RectF(result.getLocation());

      final RectF detectionScreenRect = new RectF();
      rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

      screenRects.add(new Pair<Float, RectF>(result.getConfidence(), detectionScreenRect));

      if (detectionFrameRect.width() <  16.0f || detectionFrameRect.height() <  16.0f) {
        continue;
      }

      Pair<Float, Detector.Recognition> potential = new Pair<Float, Detector.Recognition>(result.getConfidence(), result);
      final TrackedRecognition trackedRecognition = new TrackedRecognition();
      trackedRecognition.detectionConfidence = potential.first;
      trackedRecognition.location = new RectF(potential.second.getLocation());
      trackedRecognition.imgId = decisaoBlocosRA.imgId(gerencieGeoLocalizacao.latitude, gerencieGeoLocalizacao.longitude);
      trackedRecognition.color = COLORS[trackedObjects.size()];
      trackedObjects.add(trackedRecognition);

      //gerencieGeoLocalizacao.updateOrientationAngles();
      //rectsToTrack.add(new Pair<Float, Detector.Recognition>(result.getConfidence(), result));

      break;
    }


  }

  public static class TrackedRecognition {
    public RectF location;
    public float detectionConfidence;
    public int imgId;
    public int color;
    //String title;
  }
}
