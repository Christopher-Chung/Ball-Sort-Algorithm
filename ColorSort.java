import java.util.*;

class Move{

  int from, to, vol;

  public Move(int f, int t, int v){
    from = f;
    to = t;
    vol = v;
  }

  public String toString(){
    return "Pour " + vol + " unit(s), " + from + " -> " + to + "\n";
  }
}

class State{
  int[][] state;

  public State(int length){
    state = new int[length][4];
  }

  private void printState(){
    for (int i = 0; i < state.length; i ++){
      for (int j = 0; j < 4; j ++){
        System.out.print(state[i][j]);
      }
      System.out.println("");
    }
  }

  private int getEmptySpaces(int target){
    //Returns 0 for all full, 1 for one empty space, 4 for all empty spaces
    int result = 0;
    while (result < 4 && state[target][result] == 0){
      result ++;
    }
    return result;
  }

  private boolean isEmpty(int target){
    return (getEmptySpaces(target) == 4);
  }

  private int getTopColor(int target){
    if (isEmpty(target)) return 0;
    return state[target][getEmptySpaces(target)];
  }

  private int maxVol(int from){
    int sp = getEmptySpaces(from);
    if (sp == 4) return 0;
    int result = 1;
    while (sp + result < 4 && state[from][sp + result] == state[from][sp]){
      result ++;
    }
    return result;
  }

  private boolean checkLegalMove(Move m){
    //From tube empty
    if (isEmpty(m.from)) return false;
    //Can't repeat state
    if (m.vol + getEmptySpaces(m.from) == 4 && isEmpty(m.to)) return false;
    //Check single color
    if (!isEmpty(m.to) && getTopColor(m.from) != getTopColor(m.to)) return false;
    return true;
  }

  private State executeMove(Move m){
    State newstate = new State(state.length);
    for (int i = 0; i < state.length; i ++){
      for (int j = 0; j < 4; j ++){
        newstate.state[i][j] = this.state[i][j];
      }
    }
    int c = getTopColor(m.from);
    int sp1 = getEmptySpaces(m.from);
    int sp2 = getEmptySpaces(m.to);
    for (int i = 0; i < m.vol; i ++){
      newstate.state[m.from][i + sp1] = 0;
      newstate.state[m.to][sp2 - 1 - i] = c;
    }
    return newstate;
  }

  private Vector<Move> moveList(){
    Vector<Move> v = new Vector<Move>();
    for (int i = 0; i < state.length; i ++){
      for (int j = 0; j < state.length; j ++){
        //Same tube or useless pour
        if (i == j || maxVol(i) > getEmptySpaces(j)) continue;
        Move m = new Move(i, j, maxVol(i));
        if (checkLegalMove(m)) v.add(m);
      }
    }
    return v;
  }

  private boolean isSolved(){
    for (int i = 0; i < state.length; i ++){
      if (maxVol(i) != 4 && getEmptySpaces(i) != 4) return false;
    }
    return true;
  }

  public boolean solver(Stack<Move> stack){
    if (isSolved()) return true;
    Vector<Move> vec = moveList();
    for (Move m : vec){
      State newstate = executeMove(m);
      stack.push(m);
      if (newstate.solver(stack)) return true;
      stack.pop();
    }
    return false;
  }

  public void parseInput(Scanner scanner, int numcolors){
    for (int i = 0; i < state.length; i ++){
      if (i < numcolors) System.out.println("\n" + "Input color sequence for tube " + i + ":");
      for (int j = 0; j < 4; j ++){
        //0 if empty
        state[i][j] = (i < numcolors) ? scanner.nextInt() : 0;
      }
    }
  }
}

public class ColorSort{

  public static void main(String[] args){
    Scanner scanner = new Scanner(System.in);
    System.out.println("Input number of tubes: ");
    int numtubes = scanner.nextInt();
    State state = new State(numtubes);
    state.parseInput(scanner, numtubes - 2);
    Stack<Move> solutionStack = new Stack<Move>();
    state.solver(solutionStack);
    System.out.println("\n" + "Solution:");
    System.out.println(solutionStack);
  }
}
