package kr.paragonnov.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CommonCodeBuilder Annotation
 *
 * <pre>{@code
 * @CommonCodeBuilder("<ResourcePathOfCommonCodesJson>/.../<FileName>.json")
 * public class CommonCodes {}
 * }</pre>
 *
 * @author paragonnov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CommonCodeBuilder {

    // Resource file path of Common code json
    String value();

}
