package com.rv.pij2021.VUFRB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BlocoUFRBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloco_ufrb);

        CustomRectF customRect = new CustomRectF(getIntent().getFloatExtra("left",0),
                getIntent().getFloatExtra("top",0),
                getIntent().getFloatExtra("right",0),
                getIntent().getFloatExtra("bottom",0),
                getIntent().getFloatExtra("width", 0),
                getIntent().getFloatExtra("height", 0));

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lon = getIntent().getDoubleExtra("lon", 0);

        BlocosRA.BlocoRA blocoRA = new BlocosRA().bloco(lat, lon);
        InfosBlocos.BlocoInfo infoBloco = (new InfosBlocos()).infos.get(blocoRA.titulo);

        setInfo(blocoRA,infoBloco);


        draw(customRect.getRelativeValues(),blocoRA);
    }

    private void setInfo(BlocosRA.BlocoRA blocoRA, InfosBlocos.BlocoInfo infoBloco){

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

    public void draw(RectF rect,BlocosRA.BlocoRA blocoRA){

        RelativeLayout linearLayout = findViewById(R.id.relativeLayout);

        ImageView imageViewRAShow = findViewById(R.id.imageViewRAShow);
        imageViewRAShow.setImageResource(blocoRA.imgId);
        imageViewRAShow.setY(rect.top*linearLayout.getWidth());
        imageViewRAShow.setX(rect.left*linearLayout.getHeight());

        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/RAUFRB/preview.png");
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
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

        ImageView imageViewBlocoUFRB = findViewById(R.id.imageViewBlocoUFRB);

        imageViewBlocoUFRB.setImageBitmap(bitmap);
        imageViewBlocoUFRB.setX(0);
        imageViewBlocoUFRB.setY(0);
        imageViewBlocoUFRB.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}