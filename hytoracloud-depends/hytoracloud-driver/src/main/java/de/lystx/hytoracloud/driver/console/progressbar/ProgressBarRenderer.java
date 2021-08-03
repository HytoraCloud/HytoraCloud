package de.lystx.hytoracloud.driver.console.progressbar;


@FunctionalInterface
public interface ProgressBarRenderer {

    String render(ProgressState progress, int maxLength);

}
