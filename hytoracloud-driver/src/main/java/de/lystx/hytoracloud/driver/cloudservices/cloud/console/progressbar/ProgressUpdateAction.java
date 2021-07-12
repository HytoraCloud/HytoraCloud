package de.lystx.hytoracloud.driver.cloudservices.cloud.console.progressbar;


class ProgressUpdateAction implements Runnable {

    ProgressState progress;
    private ProgressBarRenderer renderer;
    private ProgressBarConsumer consumer;

    ProgressUpdateAction(
            ProgressState progress,
            ProgressBarRenderer renderer,
            ProgressBarConsumer consumer
    ) {
        this.progress = progress;
        this.renderer = renderer;
        this.consumer = consumer;
    }

    private void refresh() {
        String rendered = renderer.render(progress, consumer.getMaxRenderedLength());
        consumer.accept(rendered);
    }

    public void run() {
        if (!progress.paused) refresh();
        if (!progress.alive) {
            consumer.close();
            TerminalUtils.closeTerminal();
        }
    }

}
