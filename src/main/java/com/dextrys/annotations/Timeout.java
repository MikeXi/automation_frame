package com.dextrys.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timeout {
    double mupltiplier() default 1.0;
}
