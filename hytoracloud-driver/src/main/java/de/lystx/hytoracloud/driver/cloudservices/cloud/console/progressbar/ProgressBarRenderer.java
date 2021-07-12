package de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar;


@FunctionalInterface
public interface ProgressBarRenderer {

    String render(ProgressState progress, int maxLength);

}
