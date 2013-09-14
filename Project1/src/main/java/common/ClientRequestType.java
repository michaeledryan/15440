package common;

import java.io.Serializable;

public enum ClientRequestType implements Serializable{
	START, MIGRATE, STOP, LIST, RESTART;

	public static ClientRequestType fromString(String type) {
		return valueOf(type.toUpperCase());
	}
}
