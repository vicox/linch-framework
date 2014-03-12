package com.linchproject.framework;

import com.github.mustachejava.TemplateFunction;
import com.linchproject.core.Result;
import com.linchproject.core.Route;
import com.linchproject.framework.assets.AssetService;
import com.linchproject.framework.db.ConnectionService;
import com.linchproject.framework.i18n.I18n;
import com.linchproject.framework.i18n.I18nService;
import com.linchproject.framework.view.RenderService;
import com.linchproject.http.CookieService;
import com.linchproject.http.LocaleService;
import com.linchproject.http.SessionService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Georg Schmidl
 */
public abstract class Controller extends com.linchproject.core.Controller {

    protected ConnectionService connectionService;
    protected RenderService renderService;
    protected I18nService i18nService;
    protected AssetService assetService;

    protected SessionService sessionService;
    protected CookieService cookieService;
    protected LocaleService localeService;

    private I18n i18n;

    protected Result render(String template) {
        return render(template, context());
    }

    protected Result render(String template, Map<String, Object> context) {
        return success(renderService.render(template, context));
    }

    protected Context context() {
        return new Context()
                .put("route", route)
                .put("path", new TemplateFunction() {
                    @Override
                    public String apply(String input) {
                        String url = null;
                        if (route != null) {
                            Route newRoute = route.copy();
                            if (input.length() > 0) {
                                newRoute.setPath(input);
                            }
                            url = newRoute.getUrl();
                        }
                        return url;
                    }
                });
    }

    public class Context extends HashMap<String, Object> {
        @Override
        public Context put(String key, Object value) {
            super.put(key, value);
            return this;
        }
    }

    protected Result asset() {
        String fileName = route.getAfterController();
        InputStream inputStream = assetService.getInputStream(fileName);
        return inputStream != null? binary(inputStream): error("");
    }

    protected I18n getI18n() {
        if (i18n == null) {
            Locale locale = localeService.getLocale();
            i18n = locale != null? i18nService.getI18n(locale): i18nService.getI18n(Locale.getDefault());
        }
        return i18n;
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void setRenderService(RenderService renderService) {
        this.renderService = renderService;
    }

    public void setI18nService(I18nService i18nService) {
        this.i18nService = i18nService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void setCookieService(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

}
