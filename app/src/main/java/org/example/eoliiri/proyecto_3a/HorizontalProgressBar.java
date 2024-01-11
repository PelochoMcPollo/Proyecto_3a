package org.example.eoliiri.proyecto_3a;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HorizontalProgressBar extends View {
    private int progress = 1600;
    private int scaleColor = Color.GRAY;
    private int progressColor ;//= Color.GREEN;
    private int textColor = Color.BLACK;
    private int borderColor = Color.GRAY;
    private int widthFix=25;//Ancho de contracción en ambos lados del marco exterior
    private int arcFix = 40; // Ancho del rectángulo circunscrito al arco
    private int inarcFix = 30; // Ancho del rectángulo inscrito al arco

    private int inheightFix = 8; // Altura de contracción del bloque de color interno


    private int max = 2150;

    public HorizontalProgressBar(Context context) {
        super(context);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(int progress) {
        this.progress = progress <=max ? progress:max;
        if (progress<650){
            progressColor=Color.GREEN;
        }else   if (progress>650&&progress<1500){
            progressColor=Color.YELLOW;
        }else{
            progressColor=Color.RED;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height =80;// getHeight();

        // Dibujar el borde
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3);
//        canvas.drawRect(0, 0, width, height, borderPaint);
// Dibujar los bordes superior e inferior
        canvas.drawLine(0 + arcFix, 1, width - arcFix, 1, borderPaint);
        canvas.drawLine(0 + arcFix, height, width - arcFix, height, borderPaint);
// Dibujar el arco izquierdo
        RectF rectf_outhead = new RectF(-arcFix, 0, arcFix, height); // Determinar el rango del rectángulo circunscrito
        rectf_outhead.offset(arcFix, 0);
        canvas.drawArc(rectf_outhead, 90, 180, false, borderPaint);
// Dibujar el arco derecho
        RectF rectf_outtail = new RectF(width - arcFix, 0, width + arcFix, height); // Determinar el rango del rectángulo circunscrito
        rectf_outtail.offset(-arcFix, 0);
        canvas.drawArc(rectf_outtail, -90, 180, false, borderPaint);





        // Dibujar la barra de progreso
        Paint progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        float progressWidth = progress * (width - 2 * arcFix) / max;
/*********** Configuración del pincel del arco izquierdo del bloque interno *************/

        if(progressWidth>=0){
            RectF rectf_inhead=new RectF(-inarcFix, 0+inheightFix, inarcFix, height-inheightFix);//确定外切矩形范围
            rectf_inhead.offset(arcFix, 0);
            canvas.drawArc(rectf_inhead, 90, 180, false, progressPaint);

            RectF rectf_intail=new RectF(progressWidth+arcFix-inarcFix, 0+inheightFix, progressWidth+arcFix+inarcFix, height-inheightFix);//确定外切矩形范围
//            rectf_intail.offset(arcFix, 0);
            canvas.drawArc(rectf_intail, -90, 180, false, progressPaint);
        }
        // Dibujar el bloque de color central
        canvas.drawRect(0 + arcFix, 0 + inheightFix, progressWidth + arcFix, height - inheightFix, progressPaint);

// Dibujar las marcas de graduación

        Paint scalePaint = new Paint();
        scalePaint.setColor(scaleColor);
        scalePaint.setStrokeWidth(3);
        Paint textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(24);

        for (int i = 0; i <= max; i += 10) {
            float x = i * (width-2*arcFix) / max;
            if(i==650 || i==1500){
                canvas.drawLine(x+arcFix, 0, x+arcFix, height, scalePaint);
            }
            if(i==0 || i==max ||i==650 || i==1500 ) {

                // Dibujar el texto de la escala

                String text = String.valueOf(i);

                Rect bounds = new Rect();
                textPaint.getTextBounds(text, 0, text.length(), bounds);
                float textWidth = bounds.width();
                if (i==max) {
                    canvas.drawText(text, x - textWidth / 2+arcFix , height + bounds.height() + 10, textPaint);
                }else if (i==0){
                    canvas.drawText(text, x - textWidth / 2+arcFix , height + bounds.height() + 10, textPaint);
                }else{
                    canvas.drawText(text, x - textWidth / 2+arcFix, height + bounds.height() + 10, textPaint);
                }
            }
        }
    }
}
