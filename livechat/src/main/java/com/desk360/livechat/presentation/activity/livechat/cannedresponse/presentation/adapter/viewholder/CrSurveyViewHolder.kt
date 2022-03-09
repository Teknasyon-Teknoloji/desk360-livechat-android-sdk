package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.viewholder

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.desk360.livechat.databinding.ItemCrSurveyBinding
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedActionType
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.BaseCannedBindViewModel.Companion.ACTION_SURVEY
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.CannedResponseViewModel

class CrSurveyViewHolder(
    private val binding: ItemCrSurveyBinding,
    onClick: (Int?, Int, String) -> Unit
) :
    BaseCannedResponseViewHolder(binding.root, onClick) {

    companion object {
        fun create(parent: ViewGroup, onClick: (Int?, Int, String) -> Unit) = CrSurveyViewHolder(
            ItemCrSurveyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onClick
        )
    }

    override fun bindTo(response: CannedActionType, position: Int) {
        super.bindTo(response, position)
        val viewModel = CannedResponseViewModel()
        val hold = response as CannedActionType.CRSurvey
        binding.viewModel = viewModel

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(hold.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.btnSurveyTwo.text = Html.fromHtml(viewModel.language.value?.crFeedBackButtonBad, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.btnSurveyOne.text = Html.fromHtml(viewModel.language.value?.crFeedBackButtonGood, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.btnSurveyTwo.text= Html.fromHtml(viewModel.language.value?.crFeedBackButtonBad)
            binding.btnSurveyOne.text= Html.fromHtml(viewModel.language.value?.crFeedBackButtonGood)
        }*/

        binding.clButtonPanelOne.setOnClickListener {
            binding.clButtonPanelOne.isClickable = false
            binding.clButtonPanelTwo.isClickable = false
            onClick(null,1,ACTION_SURVEY)
        }
        binding.clButtonPanelTwo.setOnClickListener {
            binding.clButtonPanelOne.isClickable = false
            binding.clButtonPanelTwo.isClickable = false
            onClick(null,2,ACTION_SURVEY)
        }
    }


}