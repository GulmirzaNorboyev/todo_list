package uz.alif.tech.todo.common

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

fun ViewGroup.inflater(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    val inflater = LayoutInflater.from(this.context)
    return inflater.inflate(layoutRes, this, attachToRoot)
}

fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

internal val Int.px get() = this * Resources.getSystem().displayMetrics.density.toInt()

internal val Int.dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun BottomNavigationView.slideUp() {
    if (translationY == 0f) return
    this.clearAnimation()
    this.animate().translationY(0f).duration = 200
}

fun BottomNavigationView.slideDown() {
    if (translationY == this.height.toFloat()) return
    this.clearAnimation()
    this.animate().translationY(this.height.toFloat()).duration = 200
}

fun AppCompatTextView.strikeFlagText(text: String) {
    this.paintFlags = Paint.ANTI_ALIAS_FLAG
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    this.text = text
}

fun AppCompatTextView.underLineFlagText(text: String) {
    this.paintFlags = Paint.ANTI_ALIAS_FLAG
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    this.text = text
}

fun AppCompatTextView.normalFlagText(text: String) {
    this.paintFlags = Paint.ANTI_ALIAS_FLAG
    this.text = text
}

fun View.visible() {
    if (!isVisible) this.visibility = View.VISIBLE
}

fun View.gone() {
    if (!isGone) this.visibility = View.GONE
}

fun View.invisible() {
    if (!isInvisible) this.visibility = View.INVISIBLE
}

fun ViewPager2.setCurrentItem(
    position: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width
) {
    val pxToDrag: Int = pagePxWidth * (position - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator?) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator?) { /* Ignored */
        }

        override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}

fun View.alphaShow() {
    this.alpha = 1f
}

fun View.alphaHide() {
    this.alpha = 0f
}

fun View.setClickListenerWithDelay(duration: Long, function: () -> Unit) {
    this.setOnClickListener {
        this.isClickable = false
        function()
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            this.isClickable = true
        }, duration)
    }
}
