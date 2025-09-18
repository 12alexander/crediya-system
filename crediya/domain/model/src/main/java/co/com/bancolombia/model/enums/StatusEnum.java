package co.com.bancolombia.model.enums;

public enum StatusEnum {
    PENDING("550e8400-e29b-41d4-a716-446655440001", "PENDING"),
    APPROVED("550e8400-e29b-41d4-a716-446655440002", "APPROVED"),
    REJECTED("550e8400-e29b-41d4-a716-446655440003", "REJECTED"),
    PROCESSING("550e8400-e29b-41d4-a716-446655440004", "PROCESSING"),
    COMPLETED("550e8400-e29b-41d4-a716-446655440005", "COMPLETED"),
    CANCELLED("550e8400-e29b-41d4-a716-446655440006", "CANCELLED"),
    MANUAL_REVIEW("550e8400-e29b-41d4-a716-446655440007", "MANUAL_REVIEW");
    
    private final String id;
    private final String name;

    StatusEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StatusEnum fromName(String name) {
        for (StatusEnum status : values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status name: " + name);
    }

    public static StatusEnum fromId(String id) {
        for (StatusEnum status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status id: " + id);
    }
}