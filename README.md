# MazeRunner
MazeRunner is a Java Swing game where the goal is to escape as many randomly generated 10x10 labyrinths as possible in one minute.

## Run

With JDK 11 or later installed, run `MazeRunnerUI.java` from the project directory:

```text
java MazeRunnerUI.java
```

Select **Start Game** to begin a 60-second run. Move with `W`, `A`, `S`, and `D` or the arrow keys. Each escape generates a new labyrinth and increases the maze counter. Press `Q` to quit.

## Structure

- The game model and Swing UI are separate classes in the project root.
