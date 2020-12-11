package CAFServer;

public class CAFWebSocketMessage {
    // Operation that is requested at client side
    private CAFWebSocketMessageOperation operation;

    // Property
    private String property;

    // Content
    private String content;

    public CAFWebSocketMessageOperation getOperation() {
        return operation;
    }

    public void setOperation(CAFWebSocketMessageOperation operation) {
        this.operation = operation;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
