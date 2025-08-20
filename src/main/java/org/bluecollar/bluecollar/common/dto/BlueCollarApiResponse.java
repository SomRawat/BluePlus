package org.bluecollar.bluecollar.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.slf4j.MDC;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"result", "status", "traceId"})
public class BlueCollarApiResponse<T> {
    @JsonProperty("result")
    private T result;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("traceId")
    private String traceId;

    public BlueCollarApiResponse(T result, Integer status) {
        this.result = result;
        this.status = status;
        this.traceId = MDC.get("X-B3-TraceId");
    }
}