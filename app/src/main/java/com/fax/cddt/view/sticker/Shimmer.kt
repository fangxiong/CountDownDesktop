package com.fax.cddt.view.sticker

import android.graphics.*
import android.util.Log

/**
 * shimmer 实现类,使用在 CustomDynamicWallpaperView 中
 */
class Shimmer : ShimmerStyle {


    private var mLinearGradient: LinearGradient? = null
    private var mGradientMatrix: Matrix? = null
    private var mPaint: Paint? = null
    private var mViewWidth = 100
    private var isAnimating = true
    private var mTranslate = 0
    private var DEFAULT_TRANSLATE = 0
        set(value) {
            field = value
            mTranslate = DEFAULT_TRANSLATE
        }
    private var mWidthDivideBy = 5
    private val DEFAULT_COLORS = intArrayOf(0xff000000.toInt(), 0xffffffff.toInt(), 0xff000000.toInt())
    private val DEFAULT_POSITIONS = floatArrayOf(0.0f, 0.5f, 1.0f)
    private val DEFAULT_SHADER_TILEMODE = Shader.TileMode.CLAMP
    private var colors = DEFAULT_COLORS//intArrayOf(0xff00ffff.toInt(), 0xffffffff.toInt(), 0xff00ffff.toInt())
    private var positions = DEFAULT_POSITIONS
    private var content: String = ""
    private var tileMode: Shader.TileMode = DEFAULT_SHADER_TILEMODE
    private var textSize = 0f
    private var switch = false
    private var isPause = false

    /**
     * 初始化，所有配置设置好以后调用，调用以后设置关联，  LinearGradient首次实例化之后不可修改，因此采用每次修改都需实例化
     */
    fun init() {
        initShimmer().apply {
        }
        mPaint!!.shader = mLinearGradient
    }

    /**
     * [setPaint]
     * @exception  method setPaint(paint: Paint?) should be initialized first.
     */
    override fun setContent(content: String) {
        this.content = content
        if (textSize == 0f) {
            throw Exception("textSize is 0f,so method setTextSize() should be initialized before setContent().")
        }
        mPaint?.let {
            val rect = Rect()
            it.getTextBounds(content, 0, 1, rect)
            mViewWidth = it.measureText(content).toInt()
        } ?: mPaint.run {
            throw Exception("method setPaint(paint: Paint?) should be initialized first.")
        }

    }

    override fun setViewWidth(width: Int) {
        mViewWidth = width
    }

    override fun setWidthDivideBy(num: Int) {
        mWidthDivideBy = num
    }

    override fun setPaint(paint: Paint?) {
        mPaint = paint ?: Paint()
    }

    override fun setTextSize(size: Float?) {
        textSize = size!!
        mPaint?.let {
            it.textSize = textSize
        } ?: mPaint.let {
            throw Exception("method setPaint(paint: Paint?) should be initialized first.")
        }
    }

    override fun setShaderTileMode(tileMode: Shader.TileMode?) {
        tileMode?.let {
            this.tileMode = it
            return
        }
        this.tileMode = DEFAULT_SHADER_TILEMODE
    }

    override fun setLinearGradientColors(colors: IntArray?) {
        colors?.let {
            this.colors = it
            return
        } ?: colors.let { this.colors = DEFAULT_COLORS }
    }

    override fun setLinearGradientPositions(position: FloatArray?) {
        position?.let {
            this.positions = it
            return
        } ?: position.let { this.positions = DEFAULT_POSITIONS }

    }

    override fun setAnimating(b: Boolean) {
        isAnimating = b
    }
    override fun setLinearGradient(linearGradient: LinearGradient?) {
        mLinearGradient =  LinearGradient(
                (-mViewWidth).toFloat() * 0.6f, 0f, 0f, 0f,
                colors,
                positions, tileMode
        )
    }

    override fun setGradientMatrix(matrix: Matrix?) {
        mGradientMatrix = matrix ?: Matrix()
    }

    override fun setDefaultTranslate(i: Int) {
        this.DEFAULT_TRANSLATE = i
    }

    /**
     * 绘制函数,调用画出阴影
     */
    fun run() {
        if (switch && isAnimating && mGradientMatrix != null) {
            mTranslate += mViewWidth / mWidthDivideBy
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = DEFAULT_TRANSLATE
            }
            mGradientMatrix!!.setTranslate(mTranslate.toFloat(), 0f)
            this.mLinearGradient?.setLocalMatrix(mGradientMatrix)
            log()
        }
    }

    /**
     * 关闭流光效果
     */
    fun stop() {
        switch = false

    }

    /**
     * 开启流光效果
     */
    fun start() {
        switch = true
        isPause = false
    }

    fun log() {
        Log.i(
                "Shimmer",
                "content:$content mPaint:$mPaint   mViewWidth:$mViewWidth textSize:$textSize isAnimating:$isAnimating DEFAULT_TRANSLATE:$DEFAULT_TRANSLATE mTranslate:$mTranslate  " +
                        "tileMode：$tileMode mWidthDivideBy:$mWidthDivideBy colors:${colors.toList()} positions:${positions.toList()}"
        )
    }

    fun getContent() = content
    fun getMPaint() = mPaint
}