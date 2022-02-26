package pl.allegro.devskiller.domain.assignments

interface AssignmentsNotifier {
    fun notifyAboutCurrentAssignments(assignmentsStats: AssignmentsStatistics)
}

data class AssignmentsStatistics(val statistics: String)
