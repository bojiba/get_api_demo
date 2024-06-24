package com.example.gpt_api_demo.request;

import org.json.JSONObject;

import java.util.List;

public class GPTRequest {
    private final String model = "gpt-3.5-turbo";
    private List<ChatMessage> messages;
    private final Double temperature = 0.5;
    private final Integer max_tokens = 1000;
    private final Integer top_p = 1;
    private final Integer frequency_penalty = 0;
    private final Integer presence_penalty = 0;

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
