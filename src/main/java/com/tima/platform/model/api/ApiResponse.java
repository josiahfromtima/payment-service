package com.tima.platform.model.api;

import com.tima.platform.model.api.request.TokenAndIdRecord;
import com.tima.platform.util.ReportSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/14/23
 */
public class ApiResponse {
    private ApiResponse() {}

    public static Mono<ServerResponse> buildServerResponse(Mono<AppResponse> response) {
        try {
            return response
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        }catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public static String getPublicIdFromToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes()
                .getOrDefault("public_id", "")
                .toString();
    }

    public static String getToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getToken().getTokenValue();
    }

    public static TokenAndIdRecord getTokenAndId(JwtAuthenticationToken jwtToken) {
        return new TokenAndIdRecord( getToken(jwtToken), getPublicIdFromToken(jwtToken));
    }

    public static ReportSettings reportSettings(ServerRequest request, boolean useDate) {
        return (useDate) ? ReportSettings.instance()
                .page(Integer.parseInt(request.queryParam("page").orElse("0")))
                .size(Integer.parseInt(request.queryParam("size").orElse("10")))
                .sortIn(request.queryParam("sortIn").orElse("asc"))
                .sortBy(request.queryParam("sortBy").orElse("createdOn"))
                .start(request.queryParam("start").orElse(""))
                .end(request.queryParam("end").orElse("")) :
                ReportSettings.instance()
                        .page(Integer.parseInt(request.queryParam("page").orElse("0")))
                        .size(Integer.parseInt(request.queryParam("size").orElse("10")))
                        .sortIn(request.queryParam("sortIn").orElse("asc"))
                        .sortBy(request.queryParam("sortBy").orElse("createdOn"));
    }


}
