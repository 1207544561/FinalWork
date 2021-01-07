package com.example.chat.ViewShape;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

public class BitMapShape {
    private Bitmap bitmap;
    public BitMapShape(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getCirleBitmap() {
        //获取bmp的宽高 小的一个做为圆的直径r
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int r = Math.min(w, h);

        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //新创建一个Bitmap对象newBitmap 宽高都是r
        Bitmap newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);

        //创建一个使用newBitmap的Canvas对象
        Canvas canvas = new Canvas(newBitmap);

        //创建一个BitmapShader对象 使用传递过来的原Bitmap对象bmp
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        //paint设置shader
        paint.setShader(bitmapShader);

        //canvas画一个圆 使用设置了shader的paint
        canvas.drawCircle(r / 2, r / 2, r / 2, paint);

        return newBitmap;
    }
}
