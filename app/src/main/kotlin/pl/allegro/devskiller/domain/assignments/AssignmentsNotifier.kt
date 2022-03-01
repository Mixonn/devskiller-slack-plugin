package pl.allegro.devskiller.domain.assignments

interface AssignmentsNotifier {
    fun notify(assignmentsToEvaluate: AssignmentsToEvaluate)
}
