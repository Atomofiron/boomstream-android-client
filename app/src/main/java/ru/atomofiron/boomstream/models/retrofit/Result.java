package ru.atomofiron.boomstream.models.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result extends Response {
	@SerializedName("Code")
	@Expose
	private String code;
	@SerializedName("Status")
	@Expose
	private String status;
	@SerializedName("Message")
	@Expose
	private String message;

	public String getCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
