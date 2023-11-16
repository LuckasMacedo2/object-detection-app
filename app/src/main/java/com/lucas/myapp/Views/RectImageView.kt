package com.lucas.myapp.Views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lucas.myapp.ClasseObjeto
import com.lucas.myapp.ClasseObjetoCor
import com.lucas.myapp.Data.DetectedObject
import com.lucas.myapp.Data.ObjectDraw

class RectImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private val paint = Paint()
    private val paintText = Paint()
    private var rectanglesToDraw = mutableListOf<ObjectDraw>()

    private var xmin: Int = 0
    private var xmax: Int = 0
    private var ymin: Int = 0
    private var ymax: Int = 0

    fun addRectangle(objeto: DetectedObject, imagemInvertida: Boolean) {
        xmin = if(imagemInvertida) 0 else 2
        xmax = if(imagemInvertida) 1 else 3
        ymin = if(imagemInvertida) 2 else 0
        ymax = if(imagemInvertida) 3 else 1

        var objDraw = ObjectDraw(
            RectF(objeto.retangulo[xmin].toFloat(),
                objeto.retangulo[ymin].toFloat(),
                objeto.retangulo[xmax].toFloat() + if(imagemInvertida) 50 else 0,
                objeto.retangulo[ymax].toFloat()
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
            var x = rect.obj.retangulo[xmin]
            var y = rect.obj.retangulo[ymin]
            canvas.drawRect(RectF(x, y, x + 150, y + 30), paint)
            canvas.drawText("${ClasseObjeto.getObjetoPorClasse(rect.obj.classe)} - ${String.format("%.2f", rect.obj.percentualClasse * 100)}%", x, y + 20, paintText)

            // Defeituoso e nível do defeito
            y = rect.obj.retangulo[ymax]
            canvas.drawRect(RectF(x, y, x + 210, y - 30), paint)

            canvas.drawText("${rect.obj.defeituoso} - Level ${rect.obj.nivelDefeito}", x, y - 10, paintText)
        }
    }

}
