package com.farhanadi.horryapp.user_ui_page.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.horryapp.databinding.ItemLoadingBinding

class HomeStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<HomeStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(private val binding: ItemLoadingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonLoading.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.txtLoading.text = loadState.error.localizedMessage
            }
            binding.prgrsLoading.isVisible = loadState is LoadState.Loading
            binding.buttonLoading.isVisible = loadState is LoadState.Error
            binding.txtLoading.isVisible = loadState is LoadState.Error
        }
    }
}

