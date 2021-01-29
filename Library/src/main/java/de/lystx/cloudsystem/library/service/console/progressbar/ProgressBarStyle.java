package de.lystx.cloudsystem.library.service.console.progressbar;


public enum ProgressBarStyle {

    COLORFUL_UNICODE_BLOCK("\r", "\u001b[33m│", "│\u001b[0m", '█', ' ', " ▏▎▍▌▋▊▉"),

    UNICODE_BLOCK("\r", "│", "│", '█', ' ', " ▏▎▍▌▋▊▉"),

    ASCII("\r", "[", "]", '=', ' ', ">");

    String refreshPrompt;
    String leftBracket;
    String rightBracket;
    char block;
    char space;
    String fractionSymbols;

    ProgressBarStyle(String refreshPrompt, String leftBracket, String rightBracket, char block, char space, String fractionSymbols) {
        this.refreshPrompt = refreshPrompt;
        this.leftBracket = leftBracket;
        this.rightBracket = rightBracket;
        this.block = block;
        this.space = space;
        this.fractionSymbols = fractionSymbols;
    }

}
