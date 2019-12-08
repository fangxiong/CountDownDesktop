package com.fax.cddt.view.sticker

import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader

/**
 * shimmer 接口
 */
interface ShimmerStyle{

    fun setAnimating(boolean: Boolean=true)
    fun setLinearGradient(linearGradient: LinearGradient?=null)
    fun setGradientMatrix(matrix: Matrix?=null)
    fun setDefaultTranslate(default: Int=0)
    fun setLinearGradientColors(colors:IntArray?)
    fun setShaderTileMode(tileMode: Shader.TileMode?=Shader.TileMode.CLAMP)
    fun setLinearGradientPositions(position:FloatArray?)
    fun setContent(content:String)
    fun setViewWidth(width:Int=0)
    fun setWidthDivideBy(num:Int=5)
    fun setPaint(paint: Paint?)
    fun setTextSize(size:Float?)
    fun initShimmerDefault(){
        setAnimating()
        setContent("点击输入文字")
        setLinearGradient()
        setGradientMatrix()
        setDefaultTranslate()
    }
    fun initShimmer(){
        setLinearGradient()
        setGradientMatrix()
    }
}