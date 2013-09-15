package common;

import java.io.Serializable;

public enum ClientRequestType implements Serializable {
	START, MIGRATE, LIST;// , RESTART, STOP;

	public static ClientRequestType fromString(String type) {
		return valueOf(type.toUpperCase());
	}
}
