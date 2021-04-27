package com.github.wansors.quarkusconfigserver.rest;

/**
 * Enum Internal classification of service response.
 *
 */
public enum ErrorTypeCodeEnum implements ErrorTypeCode {

    /**
     * REQUEST_MISSING_REQUIRED_DATA: Faltan datos requeridos en la request
     */
    REQUEST_MISSING_REQUIRED_DATA("40", StateCodesHTTP.RESPONSE_400),

    /**
     * REQUEST_INVALID_DATA_INPUT: Datos enviados a la aplicación no son
     * válidos.
     */
    REQUEST_INVALID_DATA_INPUT("41", StateCodesHTTP.RESPONSE_400),

    /**
     * DATA_NOT_FOUND: Datos solicitados no encontrados.
     */
    DATA_NOT_FOUND("42", StateCodesHTTP.RESPONSE_400),

    /**
     * REQUEST_UNAUTHORIZED: No tienen permiso para acceder
     */
    REQUEST_UNAUTHORIZED("43", StateCodesHTTP.RESPONSE_403),

    /**
     * REQUEST_GENERIC_NOT_FOUND: Error genérico de recurso no encontrado
     */
    REQUEST_GENERIC_NOT_FOUND("44", StateCodesHTTP.RESPONSE_404),

    /**
     * REQUEST_UNDEFINED_ERROR: Error no contemplado
     */
    REQUEST_UNDEFINED_ERROR("50", StateCodesHTTP.RESPONSE_500),

    /**
     * REQUEST_METHOD_NOT_IMPLEMENTED: Método no implementado (la instalación no
     * soporta este método)
     */
    REQUEST_METHOD_NOT_IMPLEMENTED("51", StateCodesHTTP.RESPONSE_503),

    /**
     * REQUEST_SERVICE_REMOTE_ERROR: Error en servicio remoto
     */
    REQUEST_SERVICE_REMOTE_ERROR("52", StateCodesHTTP.RESPONSE_503),

    /**
     * REQUEST_SERVICE_UNAVAILABLE: Servicio remoto no disponible
     */
    REQUEST_SERVICE_UNAVAILABLE("53", StateCodesHTTP.RESPONSE_503),

    /**
     * REQUEST_TIME_OUT: Timeout en el servicio remoto
     */
    REQUEST_TIME_OUT("54", StateCodesHTTP.RESPONSE_504);

    private String code;
    private StateCodesHTTP httpCode;

    ErrorTypeCodeEnum(final String code, final StateCodesHTTP httpCode) {
	this.code = code;
	this.httpCode = httpCode;
    }

    /**
     * This method returns the {@code String} errorType.
     *
     * @return {@code String} with errorType
     */
    @Override
    public String getErrorType() {
	return this.code;
    }

    /**
     * This method returns the {@code StateCodesHTTP}.
     *
     * @return {@code StateCodesHTTP}
     */
    @Override
    public StateCodesHTTP getHttpCode() {
	return this.httpCode;
    }
}