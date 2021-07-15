package com.github.wansors.lightweightconfigserver.utils;

import java.io.File;

import javax.ws.rs.core.MediaType;

public class MediaTypeDetector {
	private MediaTypeDetector() {

	}

	/**
	 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
	 *
	 * @param f
	 * @return
	 */
	public static MediaType getMediaType(File f) {
		String type = "application/octet-stream";
		String fileName = f.getName();
		String extension = null;
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		if (extension == null) {
			type = "text/plain";
		} else if ("js".equalsIgnoreCase(extension)) {
			type = "text/javascript";
		} else if ("css".equalsIgnoreCase(extension)) {
			type = "text/css";
		} else if ("json".equalsIgnoreCase(extension)) {
			type = "application/json";
		} else if ("xml".equalsIgnoreCase(extension)) {
			type = "application/xml";
		} else if ("txt".equalsIgnoreCase(extension)) {
			type = "text/txt";
		} else if ("csv".equalsIgnoreCase(extension)) {
			type = "text/csv";
		} else if ("html".equalsIgnoreCase(extension)) {
			type = "text/html";
		} else if ("pdf".equalsIgnoreCase(extension)) {
			type = "application/pdf";
		} else if ("woff".equalsIgnoreCase(extension)) {
			type = "font/woff";
		} else if ("ttf".equalsIgnoreCase(extension)) {
			type = "font/ttf";
		} else if ("otf".equalsIgnoreCase(extension)) {
			type = "font/otf";
		} else if ("png".equalsIgnoreCase(extension)) {
			type = "image/png";
		} else if ("jpeg".equalsIgnoreCase(extension)) {
			type = "image/jpeg";
		} else if ("jpg".equalsIgnoreCase(extension)) {
			type = "image/jpeg";
		} else if ("gif".equalsIgnoreCase(extension)) {
			type = "image/gif";
		} else if ("svg".equalsIgnoreCase(extension)) {
			type = "image/svg+xml";
		} else if ("rar".equalsIgnoreCase(extension)) {
			type = "application/x-rar-compressed";
		} else if ("zip".equalsIgnoreCase(extension)) {
			type = "application/zip";
		}

		return MediaType.valueOf(type);

	}

}
