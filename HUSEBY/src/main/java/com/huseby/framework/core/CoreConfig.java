package com.huseby.framework.core;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:CoreConfig.properties")
public interface CoreConfig extends Config {

    @Key("browser.implicit.timeout")
    Integer browserImplicitWaitTimeOut();

    @Key("browser.explicit.timeout")
    Integer browserExplicitWaitTimeOut();

    @Key("user.admin.email")
    String userAdminEmail();

    @Key("user.admin.password")
    String userAdminPassword();

}
