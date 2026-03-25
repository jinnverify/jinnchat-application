package com.jinnchat.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinnchat.app.adapter.MessageAdapter
import com.jinnchat.app.model.Message
import com.jinnchat.app.websocket.WebSocketManager
import com.jinnchat.app.websocket.CustomWebSocketListener
import com.jinnchat.app.model.ChatMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var findPartnerButton: Button
    private lateinit var endChatButton: Button
    private lateinit var sendButtonInline: ImageButton
    private lateinit var statusText: TextView
    private lateinit var onlineCountText: TextView
    private lateinit var typingIndicator: LinearLayout
    private lateinit var inputContainer: LinearLayout
    private lateinit var buttonContainer: LinearLayout

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var webSocketManager: WebSocketManager
    private val handler = Handler(Looper.getMainLooper())

    private var userId: String? = null
    private var isChatting = false
    private var isTyping = false
    private val typingRunnable = Runnable { sendTypingStatus(false) }

    // Production server URL (Render)
    private val SERVER_URL = "wss://jinnchat-backend.onrender.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupWebSocket()
        setupClickListeners()
    }

    private fun initViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        findPartnerButton = findViewById(R.id.findPartnerButton)
        endChatButton = findViewById(R.id.endChatButton)
        sendButtonInline = findViewById(R.id.sendButtonInline)
        statusText = findViewById(R.id.statusText)
        onlineCountText = findViewById(R.id.onlineCountText)
        typingIndicator = findViewById(R.id.typingIndicator)
        inputContainer = findViewById(R.id.inputContainer)
        buttonContainer = findViewById(R.id.buttonContainer)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        messagesRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messagesRecyclerView.adapter = messageAdapter
    }

    private fun setupWebSocket() {
        webSocketManager = WebSocketManager(SERVER_URL)

        val listener = CustomWebSocketListener { message ->
            handleWebSocketMessage(message)
        }

        webSocketManager.connect(listener)
    }

    private fun handleWebSocketMessage(message: ChatMessage) {
        runOnUiThread {
            when (message.type) {
                "connected" -> {
                    userId = message.userId
                    updateStatus("Ready", R.color.secondary)
                    Toast.makeText(this, "Connected to JinnChat", Toast.LENGTH_SHORT).show()
                }
                "matched" -> {
                    isChatting = true
                    messageAdapter.addMessage(Message(text = message.message ?: "Partner found!", isSystem = true))
                    updateStatus("Chatting", R.color.primary)
                    updateUI()
                    scrollToBottom()
                }
                "waiting" -> {
                    updateStatus("Looking for partner…", R.color.warning)
                }
                "chat_message" -> {
                    messageAdapter.addMessage(Message(text = message.message ?: "", isSent = false, isSystem = false))
                    scrollToBottom()
                }
                "chat_ended", "partner_ended" -> {
                    isChatting = false
                    messageAdapter.addMessage(Message(text = message.message ?: "Chat ended", isSystem = true))
                    updateUI()
                    scrollToBottom()
                }
                "partner_disconnected" -> {
                    isChatting = false
                    messageAdapter.addMessage(Message(text = "Partner disconnected", isSystem = true))
                    updateUI()
                    scrollToBottom()
                }
                "user_count" -> {
                    onlineCountText.text = "${message.count} online"
                }
                "typing" -> {
                    if (message.isTyping == true && isChatting) {
                        typingIndicator.visibility = View.VISIBLE
                        handler.removeCallbacks(typingRunnable)
                        handler.postDelayed(typingRunnable, 2000)
                    } else {
                        typingIndicator.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        findPartnerButton.setOnClickListener {
            if (!webSocketManager.isConnected) {
                Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isChatting) {
                Toast.makeText(this, "Already in a chat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            messageAdapter.clearMessages()
            webSocketManager.findPartner()
        }

        sendButtonInline.setOnClickListener {
            sendMessage()
        }

        messageEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                messageEditText.setBackgroundResource(R.drawable.input_background_ios)
            }
        }

        messageEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isChatting) {
                    handler.removeCallbacks(typingRunnable)
                    if (!isTyping) {
                        isTyping = true
                        webSocketManager.sendTypingStatus(true)
                    }
                    handler.postDelayed(typingRunnable, 2000)
                }
                
                // Show/hide inline send button
                sendButtonInline.visibility = if (s.toString().trim().isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        endChatButton.setOnClickListener {
            if (isChatting) {
                webSocketManager.endChat()
                messageAdapter.addMessage(Message(text = "You ended the chat", isSystem = true))
                isChatting = false
                updateUI()
                scrollToBottom()
            }
        }
    }

    private fun sendMessage() {
        val text = messageEditText.text.toString().trim()
        if (text.isEmpty()) {
            messageEditText.error = "Type a message"
            return
        }
        if (!isChatting) {
            Toast.makeText(this, "Find a partner first", Toast.LENGTH_SHORT).show()
            return
        }

        webSocketManager.sendChatMessage(text)
        messageAdapter.addMessage(Message(text = text, isSent = true, isSystem = false))
        messageEditText.text.clear()
        sendButtonInline.visibility = View.GONE
        scrollToBottom()
    }

    private fun sendTypingStatus(isTyping: Boolean) {
        if (isChatting) {
            this.isTyping = isTyping
            webSocketManager.sendTypingStatus(isTyping)
        }
    }

    private fun updateUI() {
        findPartnerButton.isEnabled = !isChatting
        endChatButton.isEnabled = isChatting
        messageEditText.isEnabled = isChatting
        
        // Alpha effect for disabled buttons
        findPartnerButton.alpha = if (!isChatting) 1.0f else 0.5f
        endChatButton.alpha = if (isChatting) 1.0f else 0.5f

        if (isChatting) {
            messageEditText.hint = "iMessage"
            inputContainer.visibility = View.VISIBLE
        } else {
            messageEditText.hint = "Find a partner to start chatting"
        }
    }

    private fun updateStatus(status: String, colorResId: Int) {
        statusText.text = status
        statusText.setTextColor(getColor(colorResId))
    }

    private fun scrollToBottom() {
        messagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    override fun onBackPressed() {
        if (isChatting) {
            webSocketManager.endChat()
        }
        webSocketManager.disconnect()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
        handler.removeCallbacksAndMessages(null)
    }
}
