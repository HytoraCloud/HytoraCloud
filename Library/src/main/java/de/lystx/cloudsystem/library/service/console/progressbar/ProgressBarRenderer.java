package de.lystx.cloudsystem.library.service.console.progressbar;


@FunctionalInterface
public interface ProgressBarRenderer {

    String render(ProgressState progress, int maxLength);

}
