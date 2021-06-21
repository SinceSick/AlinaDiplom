package com.example.alinadiplom.adapter

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alinadiplom.R
import com.example.alinadiplom.databinding.InputItemBinding
import com.example.alinadiplom.pojo.AnalysisInput

class AnalyzesListAdapter(var analyzesInput: ArrayList<AnalysisInput>): RecyclerView.Adapter<AnalyzesListAdapter.AnalysisViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val binding = InputItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnalysisViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        holder.binding.inputNameTv.text = analyzesInput[position].name

        if(analyzesInput[position].value != null){
            holder.binding.inputValueEt.setText(analyzesInput[position].value.toString())
        }

        if(analyzesInput[position].question){
            holder.binding.itemCv.setCardBackgroundColor(Color.parseColor("#E57373"))
        }
        holder.binding.inputValueEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(value: Editable?) {
                if(value.toString().isNotEmpty()){
                    analyzesInput[position].value = value.toString().toFloat()
                    holder.binding.itemCv.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }
        })

        holder.binding.itemCv.setOnClickListener {
            holder.binding.inputValueEt.requestFocus()
        }
    }

    override fun getItemCount(): Int {
        return analyzesInput.size
    }

    class AnalysisViewHolder(val binding: InputItemBinding): RecyclerView.ViewHolder(binding.root){

    }
}