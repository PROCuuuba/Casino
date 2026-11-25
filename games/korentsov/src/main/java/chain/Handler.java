package chain;

import model.Player;

public abstract class Handler {

    @FunctionalInterface
    public interface ResultDisplay {
        void showResult(String reels, String resultTitle, String strategyName, String details);
    }

    private Handler processor;
    protected Player player;
    protected ResultDisplay display;

    public Handler(Handler processor, Player player) {
        this(processor, player, null);
    }

    public Handler(Handler processor, Player player, ResultDisplay display) {
        this.processor = processor;
        this.player = player;
        this.display = display;
    }

    public boolean process(Integer request) {
        if (processor != null) {
            return processor.process(request);
        }
        return true;
    }

    protected void updateScreen(String reels, String resultTitle, String strategyName, String details) {
        if (display != null) {
            display.showResult(reels, resultTitle, strategyName, details);
        }
    }
}
