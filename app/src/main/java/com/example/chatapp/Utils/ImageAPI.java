package com.example.chatapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

public class ImageAPI {

    /**
     * Get normal image with optional size, then load it to ImageView.
     *
     * @param path is a string path image
     * @param view is a ImageView need render image
     */
    static public void get(String path, ImageView view) {
        Picasso.get().load(path).into(view);
    }

    static public void getDefaultImage(String name, ImageView view) {
        String path = String.format("https://avatars.dicebear.com/api/initials/%s.png?size=128", name);
        Picasso.get().load(path).into(view);
    }

    /**
     * Get normal image with options size, then load it to background View.
     *
     * @param path       is a string path image
     * @param background is a View need render background
     */
    static public void loadBackground(String path, View background) {
        Picasso.get().load(path).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                background.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    /**
     * Get and transform rounded image, then load it to ImageView
     *
     * @param path is a string path image.
     * @param view is a ImageView need render image.
     */
    static public void getCorner(String path, ImageView view) {
        Picasso.get().load(path).transform(new RoundedTransformation(50, 0)).into(view);
    }

    /**
     * Get and transform circle image, then load it to ImageView
     *
     * @param path is a string path image.
     * @param view is a ImageView need render image.
     */
    static public void getCircle(String path, ImageView view) {
        Picasso.get().load(path).transform(new CircleTransform()).into(view);
    }


    /**
     * Object transform from normal to rounded image.
     */
    public static class RoundedTransformation implements Transformation {
        private final int radius;
        private final int margin;

        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin,
                    source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }
            return output;
        }

        @Override
        public String key() {
            return "rounded(r=" + radius + ", m=" + margin + ")";
        }
    }

    /**
     * Object transform from normal to circle image.
     */
    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
