package it.thecoder.rendcore.budget.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class JWTInspector {

    @Inject
    JsonWebToken jwt;

    public UUID getUserId(){
        if(jwt == null || StringUtils.isBlank(jwt.getSubject())) return null;
        return UUID.fromString(jwt.getSubject());
    }

}
