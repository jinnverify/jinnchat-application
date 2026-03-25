package com.jinnchat.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jinnchat.app.R
import com.jinnchat.app.model.Message

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentLayout: LinearLayout = itemView.findViewById(R.id.sentMessageLayout)
        val receivedLayout: LinearLayout = itemView.findViewById(R.id.receivedMessageLayout)
        val systemText: TextView = itemView.findViewById(R.id.systemMessageText)
        val sentText: TextView = itemView.findViewById(R.id.sentMessageText)
        val receivedText: TextView = itemView.findViewById(R.id.receivedMessageText)
        val sentTime: TextView = itemView.findViewById(R.id.sentMessageTime)
        val receivedTime: TextView = itemView.findViewById(R.id.receivedMessageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        if (message.isSystem) {
            holder.systemText.visibility = View.VISIBLE
            holder.sentLayout.visibility = View.GONE
            holder.receivedLayout.visibility = View.GONE
            holder.systemText.text = message.text
        } else if (message.isSent) {
            holder.sentLayout.visibility = View.VISIBLE
            holder.receivedLayout.visibility = View.GONE
            holder.systemText.visibility = View.GONE
            holder.sentText.text = message.text
            holder.sentTime.text = message.timestamp
        } else {
            holder.receivedLayout.visibility = View.VISIBLE
            holder.sentLayout.visibility = View.GONE
            holder.systemText.visibility = View.GONE
            holder.receivedText.text = message.text
            holder.receivedTime.text = message.timestamp
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun addMessages(newMessages: List<Message>) {
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }
}
