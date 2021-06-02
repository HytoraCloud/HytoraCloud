package de.lystx.hytoracloud.driver.service.console.progressbar;


@FunctionalInterface
public interface ProgressBarRenderer {

    String render(ProgressState progress, int maxLength);

}
