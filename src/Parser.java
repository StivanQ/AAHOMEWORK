import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    private FileWriter fileWriter;
    private StringBuilder stringBuilder;
    private File inputFile;
    private Scanner scanner;
    private Graph graph;
    // x[linie][coloana] = xlinie-coloana
    // = muchia de la linie la coloana
    private String x[][];
    // a[linie][coloana] = alinie-coloana
    // = distanta minima de la coloana la 1 este linia
    private String a[][];

    public Parser () {
        inputFile = new File("graph.in");
        try {
            fileWriter = new FileWriter("bexpr.out");
            scanner = new Scanner(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        stringBuilder = new StringBuilder();
    }

    private void xVariables(Integer n) {
        x = new String[n + 1][n + 1];
        for(int i = 1; i < n + 1; i++) {
            for(int j = 1; j < n + 1; j++) {
                x[i][j] = "x" + i + "-" + j;
            }
        }

    }

    private void aVariables(Integer n) {
        a = new String[n / 2 + 2][n + 1];
        for(int i = 1; i < n / 2 + 2; i++) {
            for(int j = 1; j < n + 1; j++) {
                a[i][j] = "a" + i + "-" + j;
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void doShit() {
        readGraph();
        printGraph();
        if(graph.couldHaveHamyltonianCycle()) {
            printClausesSucces();
            return;
        }
        printClauseFailure();
    }

    private void readGraph() {
        readDimensionsAndInstantiateGraph();
        readAndAddEdges();
    }

    private void printClauseFailure() {
        stringBuilder.append("x1-2&~x1-2\n");
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void printClausesSucces () {
        definedGraphCorrectnessClauses();
        vertexBelongingToCycleClauses();
        maximumTwoEdgesPerVertexInCycleClauses();
        noMinimumDistanceWhereThereIsNoEdgeToOneClauses();
        conditioningMinimumDistanceToExistingEdgeToOneClauses();
        conditioningMinimumDistanceToExistingPathToItClauses();
    }

    private void conditioningMinimumDistanceToExistingPathToItClauses () {
        for(int i = 2; i < graph.getMaxDistance() + 1; i++) {
            conditioningMinimumDistanceToExistingPathToItFixedDistanceClauses(i);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void conditioningMinimumDistanceToExistingPathToItFixedDistanceClauses(Integer distance) {
        for(int i = 2; i < graph.getDimension(); i++) {
            conditioningMinimumDistanceToExistingPathToItFixedDistanceAndVertexClauses(distance, i);
            conditioningMinimumDistanceToExistingPathToItFixedDistanceAndVertexComplementClauses(distance, i);
        }
    }
    private void conditioningMinimumDistanceToExistingPathToItFixedDistanceAndVertexClauses(Integer distance,
                                                                                            Integer vertex) {
        stringBuilder.append("((");
        stringBuilder.append(a[distance][vertex]);
        stringBuilder.append("|~((");
        for(int i = 2; i < graph.getDimension(); i++) {
            if(graph.isEdge(vertex, i)) {
                stringBuilder.append("(");
                stringBuilder.append(a[distance - 1][i] + "&" + x[i][vertex]);
                stringBuilder.append(")|");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")&~(");
        for(int i = 1; i < distance; i++) {
            stringBuilder.append(a[i][vertex]);
            stringBuilder.append("|");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")))&");
    }

    private void conditioningMinimumDistanceToExistingPathToItFixedDistanceAndVertexComplementClauses (Integer distance,
                                                                                                       Integer vertex) {
        stringBuilder.append("(~");
        stringBuilder.append(a[distance][vertex]);
        stringBuilder.append("|((");
        for(int i = 2; i < graph.getDimension(); i++) {
            if(graph.isEdge(vertex, i)) {
                stringBuilder.append("(");
                stringBuilder.append(a[distance - 1][i] + "&" + x[i][vertex]);
                stringBuilder.append(")|");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")&~(");
        for(int i = 1; i < distance; i++) {
            stringBuilder.append(a[i][vertex]);
            stringBuilder.append("|");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("))))&");
    }

    private void conditioningMinimumDistanceToExistingEdgeToOneClauses () {
        for(int i = 1; i < graph.getDimension(); i++) {
            if(graph.isEdge(1, i)) {
                stringBuilder.append("((" + a[1][i] + "|~" + x[1][i] + ")&(~" + a[1][i] + "|" +
                                     x[1][i] + "))&");
            }
        }
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void readDimensionsAndInstantiateGraph() {
        Integer n = scanner.nextInt();
        graph = new Graph(n);
        xVariables(n);
        aVariables(n);
    }

    private void readAndAddEdges() {
        while (scanner.hasNext()) {
            Integer n1 = scanner.nextInt();
            if(n1 == -1) {
                return;
            }
            Integer n2 = scanner.nextInt();
            graph.addEdge(n1, n2);
        }
    }


    private void printGraph() {
        if (graph == null) {
            System.out.println("no graph to be displayed");
            return;
        }
        for(int i = 1; i < graph.getDimension(); i ++) {
            for(int j = 1; j < graph.getDimension(); j++) {
                System.out.print(graph.getEdge(i, j) + " ");
            }
            System.out.println();
        }

    }

    private void definedGraphCorrectnessClauses () {
        for(int i = 1; i < graph.getDimension(); i++) {
            for(int j = i + 1; j < graph.getDimension(); j++) {
                if (graph.isEdge(i, j)) {
                    stringBuilder.append(
                            "((" + x[i][j] + "|~" + x[j][i] + ")&(~" + x[i][j] + "|" + x[j][i] +
                            "))&");
                }
            }
        }
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void vertexBelongingToCycleClauses () {
        for(int i = 2; i < graph.getDimension(); i ++) {
            stringBuilder.append("(");
            for(int j = 1; j < graph.getMaxDistance(); j++) {
                stringBuilder.append(a[j][i] + "|");
            }
            stringBuilder.append(a[graph.getMaxDistance()][i] + ")&");
        }
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void maximumTwoEdgesPerVertexInCycleClauses() {
        for(int i = 1; i < graph.getDimension(); i++) {
            stringBuilder.append(maximumTwoEdgesPerVertexInCycleClauseForVertex(i));
        }
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private String maximumTwoEdgesPerVertexInCycleClauseForVertex(Integer n) {
        StringBuilder internalStrinBuilder = new StringBuilder();
        internalStrinBuilder.append("(");
        for(int j = 1; j < graph.getDimension(); j++) {
            if(graph.isEdge(n, j)) {
                for(int k = j + 1; k < graph.getDimension(); k++) {
                    if(graph.isEdge(n, k)) {
                        internalStrinBuilder.append(maximumTwoEdgesPerVertexInCycleClauseForVertexLiteral(n, j, k) + "|");
                    }
                }
            }
        }
        internalStrinBuilder.deleteCharAt(internalStrinBuilder.length() - 1);
        internalStrinBuilder.append(")&");
        return internalStrinBuilder.toString();
    }

    private String maximumTwoEdgesPerVertexInCycleClauseForVertexLiteral(Integer n1, Integer n2,
                                                                         Integer n3) {
        StringBuilder internalStrinBuilder = new StringBuilder();
        internalStrinBuilder.append("(" + x[n1][n2] + "&" + x[n1][n3]);
        for(int i = 1; i < graph.getDimension(); i++) {
            if(i != n2 && i != n3) {
                if(graph.isEdge(n1, i)) {
                    internalStrinBuilder.append("&~" + x[n1][i]);
                }
            }
        }
        internalStrinBuilder.append(")");
        return internalStrinBuilder.toString();
    }

    private void noMinimumDistanceWhereThereIsNoEdgeToOneClauses() {
        for(int i = 1; i < graph.getDimension(); i++) {
            if(!graph.isEdge(1, i)) {
                stringBuilder.append("~" + a[1][i] + "&");
            }
        }
        writeAndFlushToOutputTheStringBuilder();
        clearStringBuilder();
    }

    private void clearStringBuilder () {
        stringBuilder.delete(0, stringBuilder.length());
    }

    private void writeAndFlushToOutputTheStringBuilder () {
        try {
            fileWriter.write(stringBuilder.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
