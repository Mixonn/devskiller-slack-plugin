package pl.allegro.devskiller.domain.assessments.notifier

interface AssessmentsNotifier {
    fun notify(assessmentsSummary: AssessmentsSummary)
}
