package com.example.keycloak_app.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import java.io.IOException;

public class LenasDefaultRedirectStrategy implements RedirectStrategy {

    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean contextRelative;

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = this.calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        if (logger.isDebugEnabled()) {
            logger.debug(LogMessage.format("Redirecting to %s", redirectUrl));
        }
        // for first AUTH redirect on {host}/Intra/oauth2/authorization/keycloak
        // to fix ajax cross-domain FE issue about impossible redirect by 302 http status
        if (redirectUrl.contains("/oauth2/authorization/keycloak")) {
            response.setStatus(401);
            response.getWriter().write(redirectUrl);
            response.flushBuffer();
        } else {
            // for all other redirections should by default
            response.sendRedirect(redirectUrl);
        }
    }

    protected String calculateRedirectUrl(String contextPath, String url) {
        if (!UrlUtils.isAbsoluteUrl(url)) {
            return this.isContextRelative() ? url : contextPath + url;
        } else if (!this.isContextRelative()) {
            return url;
        } else {
            Assert.isTrue(url.contains(contextPath), "The fully qualified URL does not include context path.");
            url = url.substring(url.lastIndexOf("://") + 3);
            url = url.substring(url.indexOf(contextPath) + contextPath.length());
            if (url.length() > 1 && url.charAt(0) == '/') {
                url = url.substring(1);
            }
            return url;
        }
    }

    public void setContextRelative(boolean useRelativeContext) {
        this.contextRelative = useRelativeContext;
    }

    protected boolean isContextRelative() {
        return this.contextRelative;
    }
}
