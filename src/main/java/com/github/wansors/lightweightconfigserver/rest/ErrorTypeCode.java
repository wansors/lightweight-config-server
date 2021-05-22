package com.github.wansors.lightweightconfigserver.rest;
/**
 * Interface with ErrorType access methods and StateCodesHTTP
 *
 */
public interface ErrorTypeCode {

    /**
     * This method returns the {@code String} errorType.
     *
     * @return {@code String} with errorType
     */
    public String getErrorType();

    /**
     * This method returns the {@code StateCodesHTTP}.
     *
     * @return {@code StateCodesHTTP}
     */
    public StateCodesHTTP getHttpCode();

    /**
     * Nombre del error type
     *
     * @return name
     */
    public String name();
}