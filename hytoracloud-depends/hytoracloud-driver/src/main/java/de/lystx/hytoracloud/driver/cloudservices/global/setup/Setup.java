package de.lystx.hytoracloud.driver.cloudservices.global.setup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Setup {
    int id(); //ID of the question

    String[] forbiddenAnswers() default {}; //Answers that are not allowed
    String[] onlyAnswers() default {}; //Anwsers that are only allowe
    String[] changeAnswers() default {}; //Answers that change

    Class<? extends Enum> enumOnly() default Enum.class; //Only this enum is accepted

    String[] message() default {}; //Extra message that will be printed

    String[] exitAfterAnswer() default {}; //Exit after a given answer

    QuestionSkip skip() default @QuestionSkip(id = -1, value = "", elseID = -1); //Goto after a value

    String question(); //Given question
}
