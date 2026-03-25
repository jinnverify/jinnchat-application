const express = require('express');
const WebSocket = require('ws');
const cors = require('cors');
const { v4: uuidv4 } = require('uuid');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'ok', message: 'JinnChat server is running' });
});

// Create HTTP server
const server = app.listen(PORT, () => {
  console.log(`🚀 JinnChat server running on port ${PORT}`);
});

// WebSocket server for real-time chat
const wss = new WebSocket.Server({ server });

// Store connected users and their status
const users = new Map(); // userId -> { ws, status, partnerId }
const waitingQueue = []; // Users waiting for a partner

// Broadcast user count
function broadcastUserCount() {
  const count = users.size;
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(JSON.stringify({
        type: 'user_count',
        count: count
      }));
    }
  });
}

// Find a partner for a user
function findPartner(userId) {
  if (waitingQueue.length > 0) {
    const partnerId = waitingQueue.shift();
    const partner = users.get(partnerId);
    const user = users.get(userId);
    
    if (partner && partner.ws.readyState === WebSocket.OPEN) {
      // Match found!
      users.set(partnerId, { ...partner, status: 'chatting', partnerId: userId });
      users.set(userId, { ...user, status: 'chatting', partnerId: partnerId });
      
      // Notify both users
      partner.ws.send(JSON.stringify({
        type: 'matched',
        message: 'New partner found! Start chatting.',
        partnerId: userId
      }));
      
      user.ws.send(JSON.stringify({
        type: 'matched',
        message: 'New partner found! Start chatting.',
        partnerId: partnerId
      }));
      
      return true;
    }
  }
  return false;
}

// Handle WebSocket connections
wss.on('connection', (ws) => {
  const userId = uuidv4();
  console.log(`👤 User connected: ${userId}`);
  
  // Add user to the system
  users.set(userId, {
    ws: ws,
    status: 'connecting',
    partnerId: null
  });
  
  // Send user ID to client
  ws.send(JSON.stringify({
    type: 'connected',
    userId: userId,
    message: 'Connected to JinnChat server'
  }));
  
  broadcastUserCount();
  
  // Handle incoming messages
  ws.on('message', (data) => {
    try {
      const message = JSON.parse(data);
      const user = users.get(userId);
      
      switch (message.type) {
        case 'find_partner':
          console.log(`🔍 User ${userId} looking for partner`);
          users.set(userId, { ...user, status: 'waiting' });
          
          if (!findPartner(userId)) {
            // No partner available, add to waiting queue
            waitingQueue.push(userId);
            ws.send(JSON.stringify({
              type: 'waiting',
              message: 'Looking for a partner...'
            }));
          }
          break;
          
        case 'chat_message':
          if (user && user.partnerId) {
            const partner = users.get(user.partnerId);
            if (partner && partner.ws.readyState === WebSocket.OPEN) {
              partner.ws.send(JSON.stringify({
                type: 'chat_message',
                message: message.text,
                from: 'partner',
                timestamp: new Date().toISOString()
              }));
              console.log(`💬 Message from ${userId} to ${user.partnerId}`);
            }
          }
          break;
          
        case 'end_chat':
          if (user && user.partnerId) {
            const partner = users.get(user.partnerId);
            if (partner) {
              partner.ws.send(JSON.stringify({
                type: 'partner_ended',
                message: 'Partner ended the chat'
              }));
              users.set(user.partnerId, { ...partner, status: 'idle', partnerId: null });
            }
            users.set(userId, { ...user, status: 'idle', partnerId: null });
          }
          ws.send(JSON.stringify({
            type: 'chat_ended',
            message: 'Chat ended'
          }));
          break;
          
        case 'typing':
          if (user && user.partnerId) {
            const partner = users.get(user.partnerId);
            if (partner && partner.ws.readyState === WebSocket.OPEN) {
              partner.ws.send(JSON.stringify({
                type: 'typing',
                isTyping: message.isTyping
              }));
            }
          }
          break;
      }
    } catch (error) {
      console.error('Error processing message:', error);
    }
  });
  
  // Handle disconnection
  ws.on('close', () => {
    console.log(`👋 User disconnected: ${userId}`);
    const user = users.get(userId);
    
    if (user && user.partnerId) {
      const partner = users.get(user.partnerId);
      if (partner) {
        partner.ws.send(JSON.stringify({
          type: 'partner_disconnected',
          message: 'Partner disconnected'
        }));
        users.set(user.partnerId, { ...partner, status: 'idle', partnerId: null });
      }
    }
    
    // Remove from waiting queue if present
    const queueIndex = waitingQueue.indexOf(userId);
    if (queueIndex > -1) {
      waitingQueue.splice(queueIndex, 1);
    }
    
    users.delete(userId);
    broadcastUserCount();
  });
  
  // Handle errors
  ws.on('error', (error) => {
    console.error(`WebSocket error for ${userId}:`, error);
  });
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('🛑 Shutting down server...');
  wss.clients.forEach(client => {
    client.close(1001, 'Server shutting down');
  });
  server.close(() => {
    console.log('Server closed');
    process.exit(0);
  });
});
