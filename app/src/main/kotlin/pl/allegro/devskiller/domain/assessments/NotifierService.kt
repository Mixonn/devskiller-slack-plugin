package pl.allegro.devskiller.domain.assessments

import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsSummary
import pl.allegro.devskiller.domain.assessments.notifier.NoAssessmentsToEvaluate
import pl.allegro.devskiller.domain.assessments.provider.Assessment
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider

class NotifierService(
    private val assessmentsNotifier: AssessmentsNotifier,
    private val assessmentsProvider: AssessmentsProvider
) {
    fun notifyAboutAssessmentsToCheck() {
        val assessments = assessmentsProvider.getAssessmentsToEvaluate()
        assessmentsNotifier.notify(assessments.toStatistics())
    }

    private fun List<Assessment>.toStatistics(): AssessmentsSummary =
        if (isEmpty()) {
            NoAssessmentsToEvaluate
        } else {
            AssessmentsInEvaluation(
                this.size,
                this.minOf { it.finishDate }
            )
        }
}
