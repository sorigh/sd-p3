import React, { useState, useEffect, useContext, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { AuthContext } from '../context/authContext';
import { Container, Row, Col, ListGroup, Card, Form, Button } from 'react-bootstrap';

const AdminChatDashboard = () => {
    const { user } = useContext(AuthContext);
    const [stompClient, setStompClient] = useState(null);
    const [chats, setChats] = useState({}); // { userId: [messages] }
    const [selectedUser, setSelectedUser] = useState(null);
    const [adminInput, setAdminInput] = useState("");
    const scrollRef = useRef();

    useEffect(() => {
        const socket = new SockJS('http://localhost:8084/ws');
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log('✅ Admin Connected to Chat Monitor');
            
            // Ne abonăm la topicul unde vin TOATE mesajele (User + Bot)
            client.subscribe('/topic/admin/messages', (message) => {
                const msg = JSON.parse(message.body);
                
                setChats((prevChats) => {
                    const userId = msg.senderId;
                    const existingMessages = prevChats[userId] || [];
                    return {
                        ...prevChats,
                        [userId]: [...existingMessages, msg]
                    };
                });
            });
        });

        setStompClient(client);
        return () => { if (client) client.disconnect(); };
    }, []);

    // Auto-scroll la ultimul mesaj
    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [chats, selectedUser]);

    const sendAdminReply = (e) => {
        e.preventDefault();
        if (stompClient && selectedUser && adminInput.trim() !== "") {
            const adminMessage = {
                senderId: selectedUser, // Trimitem către acest user
                content: adminInput,
                timestamp: new Date().toISOString(),
                isBot: false,
                isAdmin: true
            };

            // Trimitem prin endpoint-ul dedicat adminului
            stompClient.send("/app/admin/reply", {}, JSON.stringify(adminMessage));
            setAdminInput("");
        }
    };
    const handleSelectUser = (userId) => {
        if (selectedUser === userId) return;
        setSelectedUser(userId);
        
        // Trimitem o notificare de "Join" doar dacă nu am mai vorbit cu el în această sesiune
        // sau pur și simplu ca un mesaj de sistem
        const joinMessage = {
            senderId: userId,
            content: "An administrator has joined the chat to assist you.",
            timestamp: new Date().toISOString(),
            isBot: false,
            isAdmin: true // Va apărea în stânga la user
        };

        // Trimitem către backend pentru a marca chat-ul ca "admin handled" 
        // și pentru a informa userul
        stompClient.send("/app/admin/reply", {}, JSON.stringify(joinMessage));
    };

    return (
        <Container fluid className="mt-4">
            <Row style={{ height: '80vh' }}>
                {/* Lista de Chat-uri Active (Stânga) */}
                <Col md={4} className="border-end overflow-auto">
                    <h5>Active Conversations</h5>
                    <ListGroup>
                        {Object.keys(chats).map((userId) => (
                            <ListGroup.Item 
                                key={userId} 
                                action 
                                active={selectedUser === userId}
                                onClick={() => handleSelectUser(userId)}
                            >
                                User: {userId}
                                <div style={{ fontSize: '0.7rem' }}>
                                    Last message: {chats[userId][chats[userId].length - 1].content.substring(0, 30)}...
                                </div>
                            </ListGroup.Item>
                        ))}
                    </ListGroup>
                </Col>

                {/* Fereastra de Chat Selectată (Dreapta) */}
                <Col md={8} className="d-flex flex-column">
                    {selectedUser ? (
                        <Card className="flex-grow-1">
                            <Card.Header>Chatting with: {selectedUser}</Card.Header>
                            <Card.Body ref={scrollRef} style={{ overflowY: 'auto' }}>
                                {chats[selectedUser].map((msg, index) => (
                                    <div key={index} className={`d-flex mb-2 ${msg.isAdmin ? 'justify-content-end' : 'justify-content-start'}`}>
                                        <div className={`p-2 rounded ${msg.isAdmin ? 'bg-success text-white' : (msg.isBot ? 'bg-light text-dark' : 'bg-primary text-white')}`} style={{ maxWidth: '70%' }}>
                                            <strong>{msg.isAdmin ? "Me (Admin)" : (msg.isBot ? "Bot" : "User")}: </strong>
                                            {msg.content}
                                        </div>
                                    </div>
                                ))}
                            </Card.Body>
                            <Card.Footer>
                                <Form onSubmit={sendAdminReply} className="d-flex">
                                    <Form.Control 
                                        type="text" 
                                        placeholder="Type your support message..." 
                                        value={adminInput} 
                                        onChange={(e) => setAdminInput(e.target.value)}
                                    />
                                    <Button type="submit" variant="success" className="ms-2">Reply</Button>
                                </Form>
                            </Card.Footer>
                        </Card>
                    ) : (
                        <div className="text-center mt-5 text-muted">Select a conversation to start helping.</div>
                    )}
                </Col>
            </Row>
        </Container>
    );
};

export default AdminChatDashboard;