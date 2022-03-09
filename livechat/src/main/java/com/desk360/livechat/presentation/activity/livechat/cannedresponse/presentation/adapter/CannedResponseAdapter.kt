package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.desk360.base.presentation.animationTranslationY
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder.*

class CannedResponseAdapter(var onClick: (Int?, Int, String) -> Unit) :
    RecyclerView.Adapter<BaseCannedResponseViewHolder>() {

    private val adapterData = mutableListOf<CannedActionType>()
    private var newDataSize: Int = 0

    companion object {
        private const val TYPE_REPLY_BUTTON = 0
        private const val TYPE_MESSAGE = 1
        private const val TYPE_CLOSE_MENU_BUTTON = 2
        private const val TYPE_CLOSE_AND_SURVEY = 3
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseCannedResponseViewHolder {
        return when (viewType) {
            TYPE_REPLY_BUTTON -> CRButtonViewHolder.create(parent, onClick)
            TYPE_MESSAGE -> CRMessageViewHolder.create(parent, onClick)
            TYPE_CLOSE_AND_SURVEY -> CrSurveyViewHolder.create(parent, onClick).apply {
                this.itemView.animationTranslationY(1000L,400f)
            }
            TYPE_CLOSE_MENU_BUTTON -> CrMenuButtonViewHolder.create(parent, onClick)
            else -> throw IllegalArgumentException("Invalid type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (adapterData[position]) {
            is CannedActionType.CRSurvey -> TYPE_CLOSE_AND_SURVEY
            is CannedActionType.CRMenuButton -> TYPE_CLOSE_MENU_BUTTON
            is CannedActionType.CRButton -> TYPE_REPLY_BUTTON
            else -> TYPE_MESSAGE
        }
    }

    override fun onBindViewHolder(holder: BaseCannedResponseViewHolder, position: Int) {
        holder.bindTo(adapterData[position], position)
    }

    override fun getItemCount(): Int = adapterData.size

    fun setData(data: List<CannedActionType>, isStart: Boolean) {
        adapterData.apply {
            if (isStart) {
                notifyItemRangeRemoved(0, adapterData.size)
                adapterData.clear()
                adapterData.addAll(data)
                newDataSize = data.size
                notifyItemRangeInserted(0, adapterData.size)
            } else {
                adapterData += data
                newDataSize = data.size
                notifyItemRangeChanged(0, adapterData.size)
            }
        }
    }

    fun updateClickable(id: Int) {
        var size = 0
        if (adapterData.size > newDataSize) {
            size = adapterData.size - newDataSize
        }
        adapterData.drop(size).forEach { data ->
            if (data is CannedActionType.CRButton) {
                data.isClickable = false
                if (data.id == id && !data.isSelected) data.isSelected = true
            }
            if (data is CannedActionType.CRMenuButton) {
                data.isClickable = false
                if (data.id == id && !data.isSelected) data.isSelected = true
            }
        }
    }
}

