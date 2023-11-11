package com.lucas.myapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lucas.myapp.Data.DetectedObject
import com.lucas.myapp.Data.ObjectDraw

class RectImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private val paint = Paint()
    private val paintText = Paint()
    private var rectanglesToDraw = mutableListOf<ObjectDraw>()

    fun addRectangle(objeto: DetectedObject) {
        var objDraw = ObjectDraw(
            RectF(objeto.retangulo[2].toFloat() + 50,
            objeto.retangulo[0].toFloat(),
            objeto.retangulo[3].toFloat() + 50,
            objeto.retangulo[1].toFloat()
            ),
            objeto
        );
        rectanglesToDraw.add(objDraw)
        invalidate()
    }

    fun clearRectangles(){
        rectanglesToDraw = mutableListOf<ObjectDraw>()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.strokeWidth = 5f

        paintText.color = Color.BLACK
        paintText.textSize = 20f
        paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        for (rect in rectanglesToDraw) {
            // Bbox Objeto
            paint.style = Paint.Style.STROKE
            paint.color = ClasseObjetoCor.getCorPorClasse(rect.obj.classe)!!
            canvas.drawRect(rect.retangulo, paint)

            // Informações do Objeto
            paint.style = Paint.Style.FILL
            var x = rect.obj.retangulo[2] + 50
            var y = rect.obj.retangulo[0]
            canvas.drawRect(RectF(x, y, x + 150, y + 30), paint)
            canvas.drawText("${ClasseObjeto.getObjetoPorClasse(rect.obj.classe)} - ${String.format("%.2f", rect.obj.percentualClasse * 100)}%", x, y + 20, paintText)

            // Defeituoso e nível do defeito
            y = rect.obj.retangulo[1]
            canvas.drawRect(RectF(x, y, x + 210, y - 30), paint)

            canvas.drawText("${rect.obj.defeituoso} - Level ${rect.obj.nivelDefeito}", x, y - 10, paintText)
        }
    }
}
