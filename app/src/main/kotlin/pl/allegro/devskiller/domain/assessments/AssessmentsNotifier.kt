package pl.allegro.devskiller.domain.assessments

interface AssessmentsNotifier {
    fun notify(assessmentsToEvaluate: AssessmentsToEvaluate)
}
