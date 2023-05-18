package com.example.diseasedetector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.diseasedetector.databinding.DialogCustomBinding


class CustomDialog(private val getMoreInfo: (() -> Unit)? = null) : DialogFragment() {

    private lateinit var binding: DialogCustomBinding
    private var title: String? = null
    private var info: String? = null
    private var isHealthy: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.card_rounded)
        binding = DialogCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            title.text = this@CustomDialog.title
            infoText.text = info
            if (isHealthy) {
                moreInfoButton.text = getString(R.string.try_next)
            } else {
                moreInfoButton.text = getString(R.string.find_more)
            }

            closeIcon.setOnClickListener {
                dismiss()
            }

            moreInfoButton.setOnClickListener {
                if (isHealthy) {
                    dismiss()
                } else {
                    getMoreInfo?.invoke()
                    dismiss()
                }
            }
        }
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setDesc(desc: String) {
        this.info = desc
    }

    fun setStatus(isHealthy: Boolean) {
        this.isHealthy = isHealthy
    }
}