package com.example.gpt_api_demo.request;

import java.util.List;

public class GPTImageRequest {
    private String model = "gpt-4-vision-preview";
    private List<UserMessage> messages;
    private int max_tokens = 300;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<UserMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<UserMessage> messages) {
        this.messages = messages;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public static class UserMessage {
        private String role;
        private List<MessagePart> content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public List<MessagePart> getContent() {
            return content;
        }

        public void setContent(List<MessagePart> content) {
            this.content = content;
        }
    }

    public static class MessagePart {
        private String type;
        private String text;
        private ImageUrl image_url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ImageUrl getImage_url() {
            return image_url;
        }

        public void setImage_url(ImageUrl image_url) {
            this.image_url = image_url;
        }
    }

    public static class ImageUrl {
        private String url;
        private String detail = "low";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
