# Архитектура интеграции 1

## Диаграмма классов

```mermaid
classDiagram
    class HelloApplication
    class HelloController
    class GameCatalog
    class GameMachine {
        <<interface>>
        +String id()
        +String title()
        +void open()
    }
    class FxGameMachine {
        <<abstract>>
        +void open()
        #void prepare(Stage)
    }
    class GambleMachine
    class GambleApplication
    class ThreeBagsAdapter
    class GameController
    HelloApplication ..> HelloController : загружает через FXML
    HelloController --> GameCatalog
    HelloController ..> GameMachine : открывает выбранный
    GameCatalog o-- GameMachine : находит через ServiceLoader
    GameMachine <|.. FxGameMachine
    FxGameMachine <|-- GambleMachine
    FxGameMachine <|-- ThreeBagsAdapter
    GambleMachine ..> GambleApplication
    ThreeBagsAdapter ..> GameController
```

На диаграмме показаны два адаптера. Остальные подключаются через тот же интерфейс.

## Открытие и возврат

```mermaid
sequenceDiagram
    actor Player as Игрок
    participant Hall as HelloController
    participant Machine as FxGameMachine
    participant Stage as Окно игры
    Player->>Hall: Выбирает игру и нажимает «Открыть автомат»
    Hall->>Machine: open()
    alt Сцена загружена
        Machine->>Stage: Показывает игровое окно
        Player->>Stage: Нажимает «В каталог» или закрывает окно
        Stage-->>Machine: Окно закрыто
        Machine-->>Hall: open() завершён
    else Ошибка подготовки
        Machine-->>Hall: Исключение с причиной
        Hall-->>Player: Сообщение об ошибке в каталоге
    end
```

## Проверка по методичке

| Проверки | Реализация |
| --- | --- |
| **T1.1** · каталог | `GameCatalogTest` проверяет идентификаторы всех семи автоматов. |
| **T1.2** · выбор и запуск | `HallWindowTest` выбирает каждую игру в настоящем окне JavaFX, вносит монету и запускает игру. |
| **T1.3** · возврат | `HallWindowTest` проверяет кнопку возврата, системное закрытие окна и переход к другой игре после завершённого раунда. |
| **T1.4** · подключение | `GameCatalogTest` проверяет обнаружение независимого модуля `tests/probe`, который подключается на время тестов. |
| **T1.5** · отказ | Тестовый автомат выбрасывает исключение при подготовке окна. `HallWindowTest` проверяет сообщение и последующий запуск рабочей игры. |
| **AT1.1** · сборка и запуск | Инструкции запуска и проверка состава рабочего каталога приведены в [README](../README.md). |
