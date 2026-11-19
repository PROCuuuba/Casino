package ru.vstu.task14.chain;

/**
 * Базовый класс цепочки: помнит следующий обработчик
 * и по умолчанию просто передаёт ход ему.
 */
public abstract class Handler {

    private final Handler processor;

    public Handler(Handler processor) {
        this.processor = processor;
    }

    /**
     * Не свой случай передаётся следующему обработчику;
     * если цепочка кончилась — возвращает true.
     */
    public boolean process() {
        if (processor != null) {
            return processor.process();
        }
        return true; // конец цепочки
    }
}
