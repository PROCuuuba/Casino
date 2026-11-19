package app;

import api.GameMachine;
import java.util.ArrayList;
import java.util.List;

public class GameCatalog {
    private final List<GameMachine> machines = new ArrayList<>();
    public void register(GameMachine machine){
        machines.add(machine);
    }
    List<GameMachine> getMachines(){
        return machines;
    }
}
