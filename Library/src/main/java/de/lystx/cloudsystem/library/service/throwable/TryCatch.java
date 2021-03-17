package de.lystx.cloudsystem.library.service.throwable;

import de.lystx.cloudsystem.library.service.util.Handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TryCatch {

    Class<? extends Handler<?>> handler();
}
