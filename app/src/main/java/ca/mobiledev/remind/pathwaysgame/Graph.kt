package ca.mobiledev.remind.pathwaysgame


// Class representing a point in the grid
data class Point(val x: Int, val y: Int)

// Class representing a node in the graph
class Node(val point: Point) {
    val neighbors: MutableList<Node> = mutableListOf()

    fun addNeighbor(neighbor: Node) {
        neighbors.add(neighbor)
    }
}

// Class representing the graph
class Graph(private val rows: Int, private val cols: Int) {
     val grid: Array<Array<Node?>> = Array(rows) { arrayOfNulls<Node>(cols) }

    init {
        createNodes()
        connectNodes()
    }

    // Create nodes for all points in the grid
    private fun createNodes() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                grid[i][j] = Node(Point(i, j))
            }
        }
    }

    // Connect each node to its neighbors (up to 8 directions)
    private fun connectNodes() {
        val rowOffsets = intArrayOf(-1, -1, -1, 0, 0, 1, 1, 1)
        val colOffsets = intArrayOf(-1, 0, 1, -1, 1, -1, 0, 1)

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                for (k in rowOffsets.indices) {
                    val newRow = i + rowOffsets[k]
                    val newCol = j + colOffsets[k]
                    if (isValid(newRow, newCol)) {
                        grid[i][j]?.addNeighbor(grid[newRow][newCol]!!)
                    }
                }
            }
        }
    }

    // Check if the position is within the bounds of the grid
    private fun isValid(row: Int, col: Int): Boolean {
        return row in 0 until rows && col in 0 until cols
    }

    // Print the graph (for debugging)
    fun printGraph() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                print("Node ${grid[i][j]?.point} neighbors: ")
                grid[i][j]?.neighbors?.forEach { neighbor ->
                    print("${neighbor.point} ")
                }
                println()
            }
        }
    }
}