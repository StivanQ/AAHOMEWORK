public class Graph {
    private Integer matrix[][];
    private Integer dimension = 0;
    private Integer noEdges = 0;

    public Graph (int n) {
        matrix = new Integer[n + 1][n + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                matrix[i][j] = 0;
            }
        }
        dimension = n + 1;
    }

    public void addEdge (Integer v1, Integer v2) {
        matrix[v1][v2] = 1;
        matrix[v2][v1] = 1;
    }

    public Integer getEdge(Integer n1, Integer n2) {
        return matrix[n1][n2];
    }

    public boolean isEdge(Integer n1, Integer n2) {
        return matrix[n1][n2] == 1;
    }

    public Integer getDimension() {
        return dimension;
    }

    public boolean couldHaveHamyltonianCycle() {
        for(int i = 1; i < dimension; i++) {
            if(getNoEdgesOfVertex(i) == 1) {
                return false;
            }
        }

        return true;
    }

    public Integer getNoEdgesOfVertex(Integer n) {
        Integer number = 0;
        for(int i = 1; i < dimension; i++) {
            if(matrix[n][i] == 1) {
                number++;
            }
        }
        return number;
    }

    public Integer getMaxDistance() {
        return (dimension - 1) / 2 + 1;
    }

}
