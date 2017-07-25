package com.arcsoft.supervisor.model.domain.server;

public enum ComponentType {
	
	MEMORY(0),
	CPU(1),
    GPU(2),
    NETWORK(3),
    SDI(4);

    private final int value;

    private ComponentType(int type) {
        this.value = type;
    }

    public int getValue() {
        return value;
    }

    public static ComponentType getTypeEnum(int value){
        for (ComponentType componnentType : values()){
            if (value == componnentType.getValue()){
                return componnentType;
            }
        }
        return null;
    }
}
