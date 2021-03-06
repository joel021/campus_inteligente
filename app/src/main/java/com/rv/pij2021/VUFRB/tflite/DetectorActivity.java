/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rv.pij2021.VUFRB.tflite;

/*
Este codigo foi encontrado em https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection/android
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.ImageReader.OnImageAvailableListener;
import android.util.Size;
import android.widget.Toast;

import com.rv.pij2021.VUFRB.R;
import com.rv.pij2021.VUFRB.model.CustomRectF;
import com.rv.pij2021.VUFRB.tflite.env.ImageUtils;
import com.rv.pij2021.VUFRB.tflite.lib_task_api.Detector;
import com.rv.pij2021.VUFRB.tflite.lib_task_api.TFLiteObjectDetectionAPIModel;
import com.rv.pij2021.VUFRB.tflite.tracking.MultiBoxTracker;
import com.rv.pij2021.VUFRB.tflite.customview.OverlayView;
import com.rv.pij2021.VUFRB.tflite.customview.OverlayView.DrawCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {

  // Configuration values for the prepackaged SSD model.
  private static final int TF_OD_API_INPUT_SIZE = 320;
  private static final String TF_OD_API_MODEL_FILE = "model-06-05-2021.tflite";
  // Minimum detection confidence to track a detection.
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.2f;
  private static final boolean MAINTAIN_ASPECT = false;
  private static final Size DESIRED_PREVIEW_SIZE = new Size(320, 480);

  public boolean inference = true;

  OverlayView trackingOverlay;

  private Detector detector;

  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private boolean computingDetection = false;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private Detector.Recognition result;
  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {

    //Log.i("RAUFRB","DetectorActivity.onPreviewSizeChosen()");

    tracker = new MultiBoxTracker(sensorService);

    int cropSize = TF_OD_API_INPUT_SIZE;

    try {
      detector =
          TFLiteObjectDetectionAPIModel.create(
              this,
              TF_OD_API_MODEL_FILE);
      cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      //Log.e("RAUFRB", "Exception initializing Detector! \n"+e);
      Toast toast =
          Toast.makeText(
              getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();


    //Log.i("RAUFRB","Camera orientation relative to screen canvas: "+sensorOrientation);

    //Log.i("RAUFRB","Initializing at size "+previewWidth+"x"+previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

    int sensorOrientation = rotation - getScreenOrientation();
    frameToCropTransform =
        ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
                sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
        new DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {

            // Ao chave ou n??o draw ao inv??s de destruir e reconstruir a thread toda vez que
            // precisar desativar e ativar a infer??ncia. Destruia e reconstrua apenas quando a tela
            // for fechada e aberta novamente.
            if(inference){
              // trackedPos ?? uma passagem por refer??ncia.
              tracker.draw(canvas, imageViewRA, trackedPos);
            }

          }
        });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);

    setNumThreads();
  }

  @Override
  protected void processImage() {
    //Log.i("RAUFRB","2?? DetectorActivity().processImage()");
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    rectByInference();
  }

  private void rectByInference(){
    runInBackground(
            new Runnable() {
              @Override
              public void run() {

                final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);

                cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                final Canvas canvas = new Canvas(cropCopyBitmap);
                final Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(2.0f);

                final List<Detector.Recognition> mappedRecognitions = new ArrayList<Detector.Recognition>();

                for (final Detector.Recognition result0 : results) {
                  CustomRectF location = result0.getLocation();
                  location.setPhi0(sensorService.getPhi());
                  location.setTheta0(sensorService.getTheta());
                  if (location != null && result0.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                    canvas.drawRect(location, paint);

                    cropToFrameTransform.mapRect(location);

                    result0.setLocation(location);
                    mappedRecognitions.add(result0);

                    result = result0;
                  }
                }

                tracker.trackResults(mappedRecognitions);
                trackingOverlay.postInvalidate();

                computingDetection = false;

            /*
            runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {

                      //***Feedback para usu??rio

                  }

             });
           */
              }
            });
  }

  @Override
  protected int getLayoutId() {
    return R.layout.tfe_od_camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }

  protected void setNumThreads() {
    runInBackground(() -> detector.setNumThreads(2));
  }

  @Override
  protected void savePreviewBitmap(){
    ImageUtils.saveBitmap(croppedBitmap);
  }
}
