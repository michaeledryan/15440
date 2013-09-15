package common;

import java.io.Serializable;

public enum ClientRequestType implements Serializable {
	START, MIGRATE, LIST, KILLALL;

	public static ClientRequestType fromString(String type) {
		return valueOf(type.toUpperCase());
	}
}
