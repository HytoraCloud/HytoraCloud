package de.lystx.hytoracloud.driver.service.cloud.console.progressbar;


@FunctionalInterface
public interface ProgressBarRenderer {

    String render(ProgressState progress, int maxLength);

}
