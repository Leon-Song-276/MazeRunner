# MazeRunner
MazeRunner is a Java Swing game where the goal is to escape randomly generated 10x10 labyrinths.

## Run

With JDK 11 or later installed, run `MazeRunnerUI.java` from the project directory:

```text
java MazeRunnerUI.java
```

Select **Start Game** for a 60-second challenge or **Free Play** to complete mazes without a time limit. Move with `W`, `A`, `S`, and `D` or the arrow keys. Each escape generates a new labyrinth and increases the maze counter. Press `Q` to quit.

## Structure

- The game model and Swing UI are separate classes in the project root.
