package com.github.wansors.lightweightconfigserver.rest;

/**
 * Enum with standards with HTTP status codes.
 *
 */
public enum StateCodesHTTP {

    /**
     * RESPONSE_200: OK.
     */
    RESPONSE_200(200, "OK"),
    /**
     * RESPONSE_201: OK.
     */
    RESPONSE_201(201, "OK"),
    /**
     * RESPONSE_400: Bad Request.
     */
    RESPONSE_400(400, "Bad Request"),
    /**
     * RESPONSE_400: Unauthorized.
     */
    RESPONSE_401(401, "Unauthorized"),
    /**
     * Response 403 Forbidden
     */
    RESPONSE_403(403, "Forbidden "),
    /**
     * RESPONSE_404: Not found.
     */
    RESPONSE_404(404, "Not found"),
    /**
     * RESPONSE_500: Application error.
     */
    RESPONSE_500(500, "Appliction error"),
    /**
     * RESPONSE_503: Service Unavailable.
     */
    RESPONSE_503(503, "Service Unavailable"),
    /**
     * RESPONSE_504: Ggateway timeout.
     */
    RESPONSE_504(504, "Service Unavailable");

    private int code;
    private String description;

    StateCodesHTTP(final int code, final String description) {
	this.code = code;
	this.description = description;
    }

    /**
     * This method return one code of estate code HTTP.
     *
     * @return <code>int</code> with code of estate code HTTP.
     */
    public int getCode() {
	return this.code;
    }

    /**
     * This method return one description of estate code HTTP.
     *
     * @return <code>String</code> with description of estate code HTTP.
     */
    public String getDescription() {
	return this.description;
    }
}