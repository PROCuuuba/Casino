package sample;

public class ActionChain {

    Handler chain;
    public static int SUCCESS = PositiveHandler.SUCCESS;
    public static int CHANCE = ChanceHandler.CHANCE;
    public static int LOSS = NegativeHandler.LOSS;

    final int NUMHANDLER = 4;

    public ActionChain() {
        buildChain();
    }

    private void buildChain() {
        chain = new NegativeHandler(
                new PositiveHandler(
                        new ChanceHandler(null)
                )
        );
    }

    public boolean process() {
        return chain.process(0);
    }

    public boolean process(Integer a) {
        return chain.process(a);
    }

    public int getVyigryshSumma() {
        return PositiveHandler.vyigryshSumma;
    }
}
