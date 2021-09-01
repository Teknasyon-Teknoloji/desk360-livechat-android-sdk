package com.desk360.base.presentation.menu

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.desk360.livechat.R
import com.skydoves.balloon.*

class MenuBalloonFactory : Balloon.Factory() {
    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        return Balloon.Builder(context)
            .setLayout(R.layout.layout_menu_list)
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(1f)
            .setArrowSize(12)
            .setTextSize(12f)
            .setCornerRadius(8f)
            .setMarginRight(8)
            .setElevation(8)
            .setBackgroundColorResource(R.color.white)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setIsVisibleOverlay(true)
            .setOverlayPadding(12.5f)
            .setDismissWhenShowAgain(true)
            .setDismissWhenTouchOutside(false)
            .setDismissWhenOverlayClicked(false)
            .setLifecycleOwner(lifecycle)
            .setDismissWhenTouchOutside(true)
            .build()
    }
}