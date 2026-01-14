import React, { useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs'; // <--- AM SCHIMBAT AICI (Importul nou)
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const WebSocketNotification = () => {
    useEffect(() => {
        // Conectare directă la portul 8084 (Bypass Traefik pentru test)
        const socket = new SockJS('http://localhost:8084/ws'); 
        
        // Folosim noua bibliotecă Stomp
        const stompClient = Stomp.over(socket);

        // Dezactivează logurile de debug din consolă (opțional)
        // stompClient.debug = () => {}; 

        stompClient.connect({}, () => {
            console.log('✅ Connected to WebSocket');

            stompClient.subscribe('/topic/alerts', (message) => {
                console.log('📩 RAW MESSAGE RECEIVED:', message.body);
                if (message.body) {
                    const notification = JSON.parse(message.body);
                    console.log('Parsed Object:', notification);
                    
                    toast.error(`⚠️ ${notification.message}`, {
                        position: "top-right",
                        autoClose: 10000,
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                    });
                }
            });
        }, (error) => {
            console.error('❌ WebSocket connection error:', error);
        });

        return () => {
            if (stompClient && stompClient.connected) {
                stompClient.disconnect();
            }
        };
    }, []);

    return null;
};

export default WebSocketNotification;