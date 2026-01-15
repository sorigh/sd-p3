import React, { useState, useEffect, useContext, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { AuthContext } from '../context/authContext';
import { Container, Row, Col, Form, Button, Card } from 'react-bootstrap';

const ChatInterface = () => {
    const { user } = useContext(AuthContext); 
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const scrollRef = useRef();

    useEffect(() => {
        // Connect using the same logic as WebSocketNotification.js
        const socket = new SockJS('http://localhost:8084/ws'); 
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("Current user object:", user);
            console.log('✅ Connected to Chat Service');
            
            // Subscribe to private topic for this specific user
            client.subscribe(`/topic/chat/${user.sub}`, (message) => {
                const receivedMsg = JSON.parse(message.body);
                setMessages((prev) => [...prev, receivedMsg]);
            });
        });

        setStompClient(client);

        return () => { if (client) client.disconnect(); };
    }, [user]);

    // Auto-scroll to latest message
    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages]);

    const sendMessage = (e) => {
    e.preventDefault();
    if (stompClient && input.trim() !== "") {
        const chatMessage = {
            senderId: user.sub,
            content: input,
            // USE toISOString() so new Date() can parse it later
            timestamp: new Date().toISOString(), 
            isBot: false,
            isAdmin: false
        };

        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        
        setMessages((prev) => [...prev, chatMessage]);
        setInput("");
    }
};

    return (
        <Container className="mt-4">
            <Card className="chat-card">
                <Card.Header>Customer Support Chat</Card.Header>
                <Card.Body ref={scrollRef} style={{ height: '400px', overflowY: 'auto' }}>
                    {messages.map((msg, index) => {
                        const dateObj = new Date(msg.timestamp);
                        const timeStr = isNaN(dateObj.getTime()) ? msg.timestamp : dateObj.toLocaleTimeString();

                        // Determinăm dacă mesajul vine de la sistem (Bot sau Admin)
                        const isAssistant = msg.isBot || msg.isAdmin;

                        return (
                            <div key={index} className={`d-flex mb-2 ${isAssistant ? 'justify-content-start' : 'justify-content-end'}`}>
                                <div className={`p-2 rounded ${isAssistant ? 'bg-light text-dark' : 'bg-primary text-white'}`} style={{ maxWidth: '70%' }}>
                                    <strong>
                                        {msg.isBot ? "Support Bot" : (msg.isAdmin ? "Admin Support" : "You")}
                                    </strong>: 
                                    {msg.content}
                                    <div style={{ fontSize: '0.7rem', opacity: 0.8 }}>{timeStr}</div>
                                </div>
                            </div>
                        );
                    })}
                </Card.Body>
                <Card.Footer>
                    <Form onSubmit={sendMessage} className="d-flex">
                        <Form.Control 
                            type="text" 
                            placeholder="Type a message..." 
                            value={input} 
                            onChange={(e) => setInput(e.target.value)}
                        />
                        <Button type="submit" variant="primary" className="ms-2">Send</Button>
                    </Form>
                </Card.Footer>
            </Card>
        </Container>
    );
};

export default ChatInterface;