package com.rv.pij2021.VUFRB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BlocoUFRBActivity extends AppCompatActivity {
    private DecisaoBlocosRA.BlocoRA blocoRA;
    private InfosDecisaoBlocos.BlocoInfo infoBloco;
    private CustomRectF customRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloco_ufrb);

        customRect = new CustomRectF(getIntent().getFloatExtra("left",0),
                getIntent().getFloatExtra("top",0),
                getIntent().getFloatExtra("right",0),
                getIntent().getFloatExtra("bottom",0),
                getIntent().getFloatExtra("width", 0),
                getIntent().getFloatExtra("height", 0));

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        blocoRA = new DecisaoBlocosRA().bloco(lat, lon);
        infoBloco = (new InfosDecisaoBlocos()).infos.get(blocoRA.titulo);

    }

    @Override
    protected void onResume(){
        super.onResume();

        setInfo(blocoRA,infoBloco);
        draw(customRect.getRelativeValues(),blocoRA);
    }

    private void setInfo(DecisaoBlocosRA.BlocoRA blocoRA, InfosDecisaoBlocos.BlocoInfo infoBloco){

        ((TextView) findViewById(R.id.textViewTitulo)).setText(blocoRA.titulo);
        ((TextView) findViewById(R.id.textViewResumo)).setText(infoBloco.resumo);

        TextView status = findViewById(R.id.textViewStatus);
        status.setText(infoBloco.status);
        if (infoBloco.status.equals("Fechada")){
            status.setTextColor(Color.RED);
        }else{
            status.setTextColor(Color.GREEN);
        }

        ((TextView) findViewById(R.id.textViewFechamento)).setText(infoBloco.fechamento);
        ((TextView) findViewById(R.id.textViewAbertura)).setText(infoBloco.abertura);

    }

    // a Atividade não é dinamica, no sentido de mudar as informações com o tempo, como a atividade principal
    // então, os recursos de view (imageView, TextView...) serão instanciados localmente.
    public void draw(RectF rect, DecisaoBlocosRA.BlocoRA blocoRA){

        final RelativeLayout linearLayout = findViewById(R.id.relativeLayout);
        final ImageView imageViewRAShow = findViewById(R.id.imageViewRAShow);

        imageViewRAShow.setImageResource(blocoRA.imgId);
        imageViewRAShow.setY(rect.top*linearLayout.getWidth());
        imageViewRAShow.setX(rect.left*linearLayout.getHeight());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap_o = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/RAUFRB/preview.png");

                final Bitmap bitmap = bitmap_o.copy(Bitmap.Config.ARGB_8888, true);

                bitmap_o = null;

                Canvas canvas = new Canvas(bitmap);

                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3.0f);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeMiter(20);

                rect.left = rect.left*bitmap.getWidth();
                rect.right =  rect.right*bitmap.getWidth();
                rect.bottom = rect.bottom*bitmap.getHeight();
                rect.top = rect.top*bitmap.getHeight();

                canvas.drawRoundRect(rect, 16, 16, paint);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setImageViewBlocoUFRB(bitmap);
                    }
                });
            }
        }).start();
    }

    private void setImageViewBlocoUFRB(Bitmap bitmap){
        final ImageView imageViewBlocoUFRB = findViewById(R.id.imageViewBlocoUFRB);
        imageViewBlocoUFRB.setImageBitmap(bitmap);
        imageViewBlocoUFRB.setX(0);
        imageViewBlocoUFRB.setY(0);
        imageViewBlocoUFRB.setScaleType(ImageView.ScaleType.FIT_XY);

        findViewById(R.id.progressBarImageViewBlocoUFRB).setVisibility(View.INVISIBLE);
    }
}