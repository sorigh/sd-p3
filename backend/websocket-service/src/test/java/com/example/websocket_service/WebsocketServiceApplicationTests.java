package com.example.websocket_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "llm.api.key=test_key",
        "llm.api.url=http://localhost:8000",
        "llm.model=test_model"  // <-- Aceasta lipsea în ultima rulare
})
class WebsocketServiceApplicationTests {

    @Test
    void contextLoads() {
        //
    }

}